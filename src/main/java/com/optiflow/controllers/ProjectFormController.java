package com.optiflow.controllers;

import com.optiflow.models.Projects;
import com.optiflow.models.Skills;
import com.optiflow.models.Tasks;
import com.optiflow.models.Employee;
import com.optiflow.services.ProjectService;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ReferenceDataService;
import com.optiflow.services.SkillService;
import com.optiflow.services.TaskService;
import com.optiflow.services.TaskSkillService;
import com.optiflow.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProjectFormController {

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final ReferenceDataService referenceDataService = new ReferenceDataService();
    private final TaskService taskService = new TaskService();
    private final TaskSkillService taskSkillService = new TaskSkillService();
    private final SkillService skillService = new SkillService();

    @FXML
    private TextField projectNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> priorityCombo;

    @FXML
    private Label projectNameError;

    @FXML
    private Label descriptionError;

    @FXML
    private Label startDateError;

    @FXML
    private Label endDateError;

    @FXML
    private Label priorityError;

    @FXML
    private Label formSuccessMessage;

    @FXML
    public void initialize() {
        priorityCombo.setItems(FXCollections.observableArrayList(referenceDataService.getTaskPriorities()));
    }

    @FXML
    private void handleGenerateTasks(ActionEvent event) {
        hideMessages();
        if (!validateForm()) {
            return;
        }

        try {
            Projects project = buildProjectFromForm();
            int projectId = projectService.createProjectAndReturnId(project);

            if (projectId <= 0) {
                showError(formSuccessMessage, "Unable to save project. Check required data.");
                return;
            }

            closeCurrentComponent(event);
            generateAndPersistTasksInBackground(projectId, project.getName(), project.getDescription(), project.getStart_date(), project.getDeadline());
        } catch (Exception e) {
            showError(formSuccessMessage, "Failed to save project before AI generation.");
        }
    }

    @FXML
    private void handleAddTaskManually(ActionEvent event) {
        hideMessages();
        if (!validateForm()) {
            return;
        }

        try {
            Projects project = buildProjectFromForm();
            int projectId = projectService.createProjectAndReturnId(project);

            if (projectId <= 0) {
                showError(formSuccessMessage, "Unable to save project. Check required data.");
                return;
            }

            closeCurrentComponent(event);
        } catch (Exception e) {
            showError(formSuccessMessage, "Failed to save project.");
        }
    }

    @FXML
    private void handleSaveProject() {
        hideMessages();

        boolean valid = validateForm();
        if (!valid) {
            return;
        }

        try {
            Projects project = buildProjectFromForm();

            boolean saved = projectService.createProject(project);
            if (!saved) {
                showError(formSuccessMessage, "Unable to save project. Check required data.");
                return;
            }

            formSuccessMessage.setText("Project saved successfully.");
            formSuccessMessage.setVisible(true);
            formSuccessMessage.setManaged(true);
        } catch (Exception e) {
            showError(formSuccessMessage, "Failed to save project.");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (isBlank(projectNameField.getText())) {
            showError(projectNameError, "Project name is required");
            valid = false;
        }

        if (isBlank(descriptionField.getText())) {
            showError(descriptionError, "Description is required");
            valid = false;
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null) {
            showError(startDateError, "Start date is required");
            valid = false;
        }

        if (end == null) {
            showError(endDateError, "End date is required");
            valid = false;
        }

        if (start != null && end != null && end.isBefore(start)) {
            showError(endDateError, "Deadline must be after start date");
            valid = false;
        }

        if (priorityCombo.getValue() == null) {
            showError(priorityError, "Please select a priority");
            valid = false;
        }

        return valid;
    }

    private void hideMessages() {
        hideError(projectNameError);
        hideError(descriptionError);
        hideError(startDateError);
        hideError(endDateError);
        hideError(priorityError);

        formSuccessMessage.setVisible(false);
        formSuccessMessage.setManaged(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int resolveCurrentManagerId() {
        try {
            if (SessionManager.getUser() == null) {
                return 0;
            }

            Employee manager = employeeService.getEmployeeByUserId(SessionManager.getUser().getUserId());
            return manager == null ? 0 : manager.getEmp_id();
        } catch (Exception ignored) {
            return 0;
        }
    }

    private Projects buildProjectFromForm() {
        Projects project = new Projects();
        project.setName(projectNameField.getText().trim());
        project.setDescription(descriptionField.getText().trim());
        project.setStart_date(Date.valueOf(startDatePicker.getValue()));
        project.setEnd_date(Date.valueOf(endDatePicker.getValue()));
        project.setDeadline(Date.valueOf(endDatePicker.getValue()));
        project.setStatus(referenceDataService.getDefaultProjectStatus());
        project.setManager_id(resolveCurrentManagerId());
        return project;
    }

    private void closeCurrentComponent(ActionEvent event) {
        if (event == null || event.getSource() == null) {
            return;
        }

        Node source = (Node) event.getSource();
        if (source.getScene() != null && source.getScene().getWindow() != null) {
            source.getScene().getWindow().hide();
        }
    }

    private void generateAndPersistTasksInBackground(int projectId, String projectTitle, String projectDescription, Date startDate, Date deadline) {
        Thread worker = new Thread(() -> {
            try {
                List<String> totalSkillList = fetchAllSkillNames();
                List<Tasks> generated = projectService.generateTasksForProjects(projectTitle, projectDescription, totalSkillList);
                if (generated == null || generated.isEmpty()) {
                    return;
                }

                Map<String, Integer> skillIdsByName = buildSkillIdMap();
                for (Tasks generatedTask : generated) {
                    Tasks taskToSave = new Tasks();
                    taskToSave.setProject_id(projectId);
                    taskToSave.setAssigned_to(0);
                    taskToSave.setTitle(generatedTask.getTitle());
                    taskToSave.setDescription(generatedTask.getDescription());
                    taskToSave.setStatus(referenceDataService.getDefaultTaskStatus());
                    taskToSave.setPriority(normalizePriority(generatedTask.getPriority()));
                    taskToSave.setEstimated_hours(Math.max(generatedTask.getEstimated_hours(), 1));
                    taskToSave.setStart_date(startDate);
                    taskToSave.setEnd_date(deadline);

                    int taskId = taskService.createTaskAndReturnId(taskToSave);
                    if (taskId <= 0 || generatedTask.getSkillsList() == null) {
                        continue;
                    }

                    for (String skillName : generatedTask.getSkillsList()) {
                        if (skillName == null) {
                            continue;
                        }

                        Integer skillId = skillIdsByName.get(skillName.trim().toLowerCase(Locale.ROOT));
                        if (skillId != null) {
                            taskSkillService.addSkillToTask(SessionManager.getUser(), taskId, skillId);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        worker.setDaemon(true);
        worker.start();
    }

    private List<String> fetchAllSkillNames() {
        List<String> names = new ArrayList<>();
        try {
            List<Skills> allSkills = skillService.getAllSkills();
            if (allSkills == null) {
                return names;
            }

            for (Skills skill : allSkills) {
                if (skill != null && !isBlank(skill.getName())) {
                    names.add(skill.getName().trim());
                }
            }
        } catch (Exception ignored) {
        }
        return names;
    }

    private Map<String, Integer> buildSkillIdMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            List<Skills> allSkills = skillService.getAllSkills();
            if (allSkills == null) {
                return map;
            }

            for (Skills skill : allSkills) {
                if (skill != null && !isBlank(skill.getName())) {
                    map.put(skill.getName().trim().toLowerCase(Locale.ROOT), skill.getSkill_id());
                }
            }
        } catch (Exception ignored) {
        }
        return map;
    }

    private String normalizePriority(String aiPriority) {
        if (isBlank(aiPriority)) {
            return referenceDataService.getDefaultTaskPriority();
        }

        String normalized = aiPriority.trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(normalized)) {
            return "High";
        }
        if ("LOW".equals(normalized)) {
            return "Low";
        }
        if ("MEDIUM".equals(normalized)) {
            return "Medium";
        }
        if ("CRITICAL".equals(normalized)) {
            return "Critical";
        }
        return referenceDataService.getDefaultTaskPriority();
    }
}
