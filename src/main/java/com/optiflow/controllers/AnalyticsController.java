package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AnalyticsController {

    @FXML
    private ComboBox<String> timeRangeCombo;

    @FXML
    private ComboBox<String> projectCombo;

    @FXML
    private ComboBox<String> teamCombo;

    @FXML
    private Label updatedLabel;

    @FXML
    private LineChart<String, Number> progressLineChart;

    @FXML
    private PieChart taskDistributionChart;

    @FXML
    private BarChart<String, Number> performanceBarChart;

    private final ProjectService projectService = new ProjectService();
    private final TaskService taskService = new TaskService();
    private final EmployeeService employeeService = new EmployeeService();

    private Employee currentManager;
    private final Map<String, Projects> projectByName = new LinkedHashMap<>();
    private final Map<Integer, Employee> employeeById = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        currentManager = resolveCurrentManager();
        loadFiltersFromServices();
        refreshCharts();
    }

    @FXML
    private void handleFiltersChanged() {
        refreshCharts();
    }

    private void loadFiltersFromServices() {
        timeRangeCombo.setItems(FXCollections.observableArrayList("Last 7 Days", "Last 30 Days", "Last Quarter"));
        timeRangeCombo.setValue("Last 30 Days");

        projectByName.clear();
        Set<String> projectOptions = new LinkedHashSet<>();
        projectOptions.add("All Projects");

        try {
            List<Projects> projects = resolveManagerProjects();
            for (Projects project : projects) {
                String name = project.getName() == null || project.getName().isBlank()
                        ? "Project #" + project.getProject_id()
                        : project.getName();
                projectOptions.add(name);
                projectByName.put(name, project);
            }
        } catch (Exception ignored) {
        }

        projectCombo.setItems(FXCollections.observableArrayList(projectOptions));
        projectCombo.setValue("All Projects");

        Set<String> teamOptions = new LinkedHashSet<>();
        teamOptions.add("All Teams");
        try {
            for (Employee member : resolveTeamMembers()) {
                if (member.getDepartment() != null && !member.getDepartment().isBlank()) {
                    teamOptions.add(member.getDepartment());
                }
            }
        } catch (Exception ignored) {
        }
        teamCombo.setItems(FXCollections.observableArrayList(teamOptions));
        teamCombo.setValue("All Teams");
    }

    private void refreshCharts() {
        List<Tasks> scopedTasks = resolveScopedTasks();
        List<Employee> teamMembers = resolveTeamMembers();

        configureTaskDistribution(scopedTasks);
        configurePerformanceOverview(scopedTasks, teamMembers);
        configureProgressTrend(scopedTasks);

        if (updatedLabel != null) {
            updatedLabel.setText("Updated " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private void configureTaskDistribution(List<Tasks> tasks) {
        int completed = 0;
        int inProgress = 0;
        int pending = 0;

        for (Tasks task : tasks) {
            String status = normalizeStatus(task.getStatus());
            if ("Completed".equals(status)) {
                completed++;
            } else if ("In Progress".equals(status)) {
                inProgress++;
            } else {
                pending++;
            }
        }

        if (tasks.isEmpty()) {
            taskDistributionChart.setData(FXCollections.observableArrayList(new PieChart.Data("No Data", 1)));
            return;
        }

        taskDistributionChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Completed", completed),
                new PieChart.Data("In Progress", inProgress),
                new PieChart.Data("Pending", pending)
        ));
    }

    private void configurePerformanceOverview(List<Tasks> tasks, List<Employee> teamMembers) {
        BarChart.Series<String, Number> series = new BarChart.Series<>();
        series.setName("Completion %");

        String selectedTeam = teamCombo.getValue();
        Map<String, Integer> totalByDepartment = new LinkedHashMap<>();
        Map<String, Integer> doneByDepartment = new LinkedHashMap<>();

        for (Employee member : teamMembers) {
            String department = (member.getDepartment() == null || member.getDepartment().isBlank())
                    ? "Unknown"
                    : member.getDepartment();

            if (selectedTeam != null && !"All Teams".equals(selectedTeam) && !selectedTeam.equals(department)) {
                continue;
            }

            totalByDepartment.putIfAbsent(department, 0);
            doneByDepartment.putIfAbsent(department, 0);
        }

        for (Tasks task : tasks) {
            Employee assignee = resolveAssignee(task.getAssigned_to(), teamMembers);
            if (assignee == null) {
                continue;
            }

            String department = (assignee.getDepartment() == null || assignee.getDepartment().isBlank())
                    ? "Unknown"
                    : assignee.getDepartment();
            if (!totalByDepartment.containsKey(department)) {
                continue;
            }

            totalByDepartment.put(department, totalByDepartment.get(department) + 1);
            if ("Completed".equals(normalizeStatus(task.getStatus()))) {
                doneByDepartment.put(department, doneByDepartment.get(department) + 1);
            }
        }

        if (totalByDepartment.isEmpty()) {
            series.getData().add(new BarChart.Data<>("No Team", 0));
        } else {
            for (Map.Entry<String, Integer> entry : totalByDepartment.entrySet()) {
                int total = entry.getValue();
                int done = doneByDepartment.getOrDefault(entry.getKey(), 0);
                double score = total == 0 ? 0 : (done * 100.0) / total;
                series.getData().add(new BarChart.Data<>(entry.getKey(), score));
            }
        }

        performanceBarChart.getData().setAll(series);
    }

    private void configureProgressTrend(List<Tasks> tasks) {
        LineChart.Series<String, Number> series = new LineChart.Series<>();
        series.setName("Completion %");

        int windowDays = resolveWindowDays();
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(windowDays - 1L);

        for (int i = 0; i < windowDays; i += 7) {
            LocalDate bucketStart = start.plusDays(i);
            LocalDate bucketEnd = bucketStart.plusDays(6);
            if (bucketEnd.isAfter(end)) {
                bucketEnd = end;
            }

            int total = 0;
            int done = 0;
            for (Tasks task : tasks) {
                LocalDate referenceDate = resolveReferenceDate(task);
                if (referenceDate == null || referenceDate.isBefore(bucketStart) || referenceDate.isAfter(bucketEnd)) {
                    continue;
                }

                total++;
                if ("Completed".equals(normalizeStatus(task.getStatus()))) {
                    done++;
                }
            }

            double completion = total == 0 ? 0 : (done * 100.0) / total;
            String label = bucketStart.getMonth().name().substring(0, 3) + " " + bucketStart.getDayOfMonth();
            series.getData().add(new LineChart.Data<>(label, completion));
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new LineChart.Data<>("No Data", 0));
        }

        progressLineChart.getData().setAll(series);
    }

    private List<Projects> resolveManagerProjects() {
        try {
            List<Projects> projects = projectService.getAllProjects();
            if (projects == null) {
                return List.of();
            }

            if (currentManager == null) {
                return projects;
            }

            return projects.stream()
                    .filter(p -> p.getManager_id() == currentManager.getEmp_id() || p.getManager_id() == currentManager.getUser_id())
                    .toList();
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<Tasks> resolveScopedTasks() {
        try {
            String selectedProject = projectCombo.getValue();
            List<Projects> projects = resolveManagerProjects();
            Map<Integer, Tasks> byId = new LinkedHashMap<>();

            for (Projects project : projects) {
                if (selectedProject != null && !"All Projects".equals(selectedProject)) {
                    Projects fromCombo = projectByName.get(selectedProject);
                    if (fromCombo == null || project.getProject_id() != fromCombo.getProject_id()) {
                        continue;
                    }
                }

                List<Tasks> projectTasks = taskService.getTaskByProject(project.getProject_id());
                if (projectTasks != null) {
                    for (Tasks task : projectTasks) {
                        byId.put(task.getTask_id(), task);
                    }
                }
            }

            String selectedTeam = teamCombo.getValue();
            if (selectedTeam != null && !"All Teams".equals(selectedTeam)) {
                List<Employee> teamMembers = resolveTeamMembers();
                Set<Integer> allowedIds = new LinkedHashSet<>();
                for (Employee member : teamMembers) {
                    if (selectedTeam.equals(member.getDepartment())) {
                        allowedIds.add(member.getEmp_id());
                        allowedIds.add(member.getUser_id());
                    }
                }
                byId.entrySet().removeIf(entry -> !allowedIds.contains(entry.getValue().getAssigned_to()));
            }

            return List.copyOf(byId.values());
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<Employee> resolveTeamMembers() {
        try {
            employeeById.clear();
            if (currentManager == null) {
                List<Employee> all = employeeService.getAllEmployees();
                if (all != null) {
                    for (Employee employee : all) {
                        employeeById.put(employee.getEmp_id(), employee);
                    }
                    return all;
                }
                return List.of();
            }

            List<Employee> byEmpId = employeeService.getEmployeesByManager(currentManager.getEmp_id());
            if (byEmpId != null) {
                for (Employee employee : byEmpId) {
                    employeeById.put(employee.getEmp_id(), employee);
                }
            }

            List<Employee> byUserId = employeeService.getEmployeesByManager(currentManager.getUser_id());
            if (byUserId != null) {
                for (Employee employee : byUserId) {
                    employeeById.put(employee.getEmp_id(), employee);
                }
            }

            return List.copyOf(employeeById.values());
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private Employee resolveCurrentManager() {
        try {
            User user = SessionManager.getUser();
            if (user == null) {
                return null;
            }
            return employeeService.getEmployeeByUserId(user.getUserId());
        } catch (Exception ignored) {
            return null;
        }
    }

    private Employee resolveAssignee(int assignedTo, List<Employee> teamMembers) {
        for (Employee member : teamMembers) {
            if (assignedTo == member.getEmp_id() || assignedTo == member.getUser_id()) {
                return member;
            }
        }
        return null;
    }

    private int resolveWindowDays() {
        String value = timeRangeCombo.getValue();
        if ("Last 7 Days".equals(value)) {
            return 7;
        }
        if ("Last Quarter".equals(value)) {
            return 90;
        }
        return 30;
    }

    private LocalDate resolveReferenceDate(Tasks task) {
        if (task.getEnd_date() != null) {
            return task.getEnd_date().toLocalDate();
        }
        if (task.getStart_date() != null) {
            return task.getStart_date().toLocalDate();
        }
        return null;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Pending";
        }
        String normalized = status.toLowerCase(Locale.ROOT);
        if (normalized.contains("complete")) {
            return "Completed";
        }
        if (normalized.contains("progress") || normalized.contains("active")) {
            return "In Progress";
        }
        return "Pending";
    }
}
