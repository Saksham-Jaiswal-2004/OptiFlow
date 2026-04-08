package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.services.ReferenceDataService;
import com.optiflow.services.SkillService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTasksController {

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> skillsDropdown;
    @FXML private ComboBox<String> employeeDropdown;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextArea descriptionArea;

    private final TaskService taskService = new TaskService();
    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final SkillService skillService = new SkillService();
    private final ReferenceDataService referenceDataService = new ReferenceDataService();

    private final Map<String, Integer> employeeIds = new HashMap<>();
    private int selectedProjectId;

    @FXML
    public void initialize() {
        if (deadlinePicker != null) {
            deadlinePicker.setValue(LocalDate.now().plusDays(7));
        }
        loadSkills();
        loadProjectContext();
        loadEmployees();
    }

    @FXML
    private void handleAutoAssign() {
        try {
            List<Employee> candidates = resolveAssignableEmployees();
            if (candidates.isEmpty() || employeeDropdown == null) {
                showAlert("Auto Assign", "No employees are available for assignment.");
                return;
            }

            Employee best = candidates.get(0);
            for (Employee employee : candidates) {
                if (employee.getAllocated_hours() < best.getAllocated_hours()) {
                    best = employee;
                }
            }

            employeeDropdown.getSelectionModel().select(employeeLabel(best));
        } catch (Exception e) {
            showAlert("Auto Assign", "Unable to auto assign employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateTask() {
        String title = safeText(taskNameField == null ? null : taskNameField.getText());
        String description = safeText(descriptionArea == null ? null : descriptionArea.getText());

        if (title.isBlank() || description.isBlank() || deadlinePicker == null || deadlinePicker.getValue() == null) {
            showAlert("Validation Error", "Task name, description, and deadline are required.");
            return;
        }

        try {
            Tasks task = new Tasks();
            task.setProject_id(resolveProjectId());
            task.setAssigned_to(resolveEmployeeId());
            task.setTitle(title);
            task.setDescription(buildDescription(description));
            task.setStatus(referenceDataService.getDefaultTaskStatus());
            task.setPriority(safePriority());
            task.setEstimated_hours(0);
            task.setActual_hours(0);
            task.setStart_date(Date.valueOf(LocalDate.now()));
            task.setEnd_date(Date.valueOf(deadlinePicker.getValue()));

            if (!taskService.createTask(task)) {
                showAlert("Save Failed", "The task could not be created.");
                return;
            }

            showAlert("Success", "Task created successfully.");
            resetForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to create task: " + e.getMessage());
        }
    }

    private void loadSkills() {
        if (skillsDropdown == null) {
            return;
        }

        skillsDropdown.getItems().clear();
        skillsDropdown.getItems().addAll(referenceDataService.getTaskPriorities());
        skillsDropdown.getSelectionModel().select(referenceDataService.getDefaultTaskPriority());

        try {
            if (skillService.getAllSkills() != null && !skillService.getAllSkills().isEmpty()) {
                skillsDropdown.getItems().clear();
                skillService.getAllSkills().forEach(skill -> skillsDropdown.getItems().add(skill.getName()));
                skillsDropdown.getSelectionModel().selectFirst();
            }
        } catch (Exception ignored) {
        }
    }

    private void loadProjectContext() {
        selectedProjectId = 0;

        try {
            List<Projects> projects = projectService.getAllProjects();
            if (projects == null || projects.isEmpty()) {
                return;
            }

            User user = SessionManager.getUser();
            if (user != null) {
                Employee manager = employeeService.getEmployeeByUserId(user.getUserId());
                if (manager != null) {
                    for (Projects project : projects) {
                        if (project.getManager_id() == manager.getEmp_id()) {
                            selectedProjectId = project.getProject_id();
                            return;
                        }
                    }
                }
            }

            selectedProjectId = projects.get(0).getProject_id();
        } catch (Exception ignored) {
        }
    }

    private void loadEmployees() {
        if (employeeDropdown == null) {
            return;
        }

        employeeIds.clear();
        employeeDropdown.getItems().clear();

        try {
            List<Employee> employees = resolveAssignableEmployees();
            for (Employee employee : employees) {
                String label = employeeLabel(employee);
                employeeIds.put(label, employee.getEmp_id());
                employeeDropdown.getItems().add(label);
            }

            if (!employeeDropdown.getItems().isEmpty()) {
                employeeDropdown.getSelectionModel().selectFirst();
            }
        } catch (Exception ignored) {
        }
    }

    private List<Employee> resolveAssignableEmployees() throws Exception {
        User user = SessionManager.getUser();
        if (user == null) {
            List<Employee> all = employeeService.getAllEmployees();
            return all == null ? new ArrayList<>() : all;
        }

        Employee manager = employeeService.getEmployeeByUserId(user.getUserId());
        if (manager != null) {
            List<Employee> team = employeeService.getEmployeesByManager(manager.getEmp_id());
            if (team != null && !team.isEmpty()) {
                return team;
            }
        }

        List<Employee> all = employeeService.getAllEmployees();
        return all == null ? new ArrayList<>() : all;
    }

    private int resolveProjectId() {
        return selectedProjectId;
    }

    private int resolveEmployeeId() {
        if (employeeDropdown == null || employeeDropdown.getValue() == null) {
            return 0;
        }
        return employeeIds.getOrDefault(employeeDropdown.getValue(), 0);
    }

    private String safePriority() {
        String selected = skillsDropdown == null ? null : skillsDropdown.getValue();
        return selected == null || selected.isBlank() ? referenceDataService.getDefaultTaskPriority() : selected;
    }

    private String buildDescription(String description) {
        String selectedSkill = skillsDropdown == null ? null : skillsDropdown.getValue();
        if (selectedSkill == null || selectedSkill.isBlank()) {
            return description.trim();
        }

        return description.trim() + System.lineSeparator() + "Skill focus: " + selectedSkill;
    }

    private void resetForm() {
        if (taskNameField != null) {
            taskNameField.clear();
        }
        if (descriptionArea != null) {
            descriptionArea.clear();
        }
        if (deadlinePicker != null) {
            deadlinePicker.setValue(LocalDate.now().plusDays(7));
        }
        if (skillsDropdown != null && !skillsDropdown.getItems().isEmpty()) {
            skillsDropdown.getSelectionModel().selectFirst();
        }
        if (employeeDropdown != null && !employeeDropdown.getItems().isEmpty()) {
            employeeDropdown.getSelectionModel().selectFirst();
        }
    }

    private String employeeLabel(Employee employee) {
        return employee.getName() + " (#" + employee.getEmp_id() + ")";
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}