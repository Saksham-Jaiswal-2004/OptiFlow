package com.optiflow.controllers;

import com.optiflow.models.*;
import com.optiflow.services.*;
import com.optiflow.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class ViewTasksController {

    // 🔹 UI ELEMENTS
    @FXML private Label taskTitle;
    @FXML private Label projectName;
    @FXML private Label statusBadge;

    @FXML private Label employeeName;
    @FXML private Label employeeId;
    @FXML private Label skills;

    @FXML private Label startDate;
    @FXML private Label deadline;
    @FXML private Label estimatedHours;
    @FXML private Label currentHours;

    @FXML private Label description;

    @FXML private Label roleBadge;

    @FXML private Button closeBtn;

    @FXML private TextField taskTitleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField estimatedHoursField;
    @FXML private Button saveBtn;


    private boolean isEditMode = false;

    // 🔹 SERVICES
    private EmployeeService employeeService = new EmployeeService();
    private ProjectService projectService = new ProjectService();
    private WorkLogService workLogService = new WorkLogService();

    private Tasks task;

    // 🔹 RECEIVED FROM DASHBOARD
    public void setTask(Tasks task) {
        this.task = task;
        loadTaskData();
    }

    // 🔹 LOAD ALL DATA INTO UI
    private void loadTaskData() {
        if (task == null) return;

        try {
            // 🔹 TASK BASIC
            taskTitle.setText(task.getTitle());
            description.setText(task.getDescription());

            setStatus(task.getStatus());

            // 🔹 PROJECT
            Projects project = projectService.getProjectById(task.getProject_id());
            if (project != null) {
                projectName.setText(project.getName());
            } else {
                projectName.setText("-");
            }

            // 🔹 EMPLOYEE
            Employee emp = employeeService.getEmployeeById(task.getAssigned_to());
            if (emp != null) {
                employeeName.setText(emp.getName());
                employeeId.setText(String.valueOf(emp.getEmp_id()));
//                skills.setText(emp.getSkill());
            } else {
                employeeName.setText("-");
                employeeId.setText("-");
                skills.setText("-");
            }

            // 🔹 DATES
            startDate.setText(
                    task.getStart_date() != null ? task.getStart_date().toString() : "-"
            );

            deadline.setText(
                    task.getEnd_date() != null ? task.getEnd_date().toString() : "-"
            );

            // 🔹 HOURS
            estimatedHours.setText(
                    task.getEstimated_hours() != 0
                            ? String.valueOf(task.getActual_hours())
                            : "-"
            );

            int totalWorked = workLogService.getTaskProgress(task.getTask_id());
            currentHours.setText(String.valueOf(totalWorked));

            // 🔹 ROLE BADGE
            User user = SessionManager.getUser();
            if (user != null) {
                setRoleBadge(user.getRole());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 STATUS BADGE
    private void setStatus(String status) {
        statusBadge.setText(status.toUpperCase());

        String color;

        switch (status.toLowerCase()) {
            case "active":
            case "in progress":
                color = "#8e2de2, #c471ed"; // purple
                break;
            case "completed":
                color = "#11998e, #38ef7d"; // green
                break;
            default:
                color = "#6B7280, #6B7280"; // grey
        }

        statusBadge.setStyle(
                "-fx-background-color: linear-gradient(to right, " + color + ");" +
                        "-fx-background-radius: 50;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5 12;"
        );
    }

    // 🔹 ROLE BADGE
    private void setRoleBadge(String role) {
        roleBadge.setText(role.toUpperCase());

        String color;

        switch (role.toLowerCase()) {
            case "admin":
                color = "#8e2de2, #c471ed";
                break;
            case "manager":
                color = "#11998e, #38ef7d";
                break;
            case "employee":
                color = "#f12711, #f5af19";
                break;
            default:
                color = "#6B7280, #6B7280";
        }

        roleBadge.setStyle(
                "-fx-background-color: linear-gradient(to right, " + color + ");" +
                        "-fx-background-radius: 50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10;"
        );
    }

    // 🔴 CLOSE BUTTON → BACK TO DASHBOARD
    @FXML
    private void handleClose() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/DashboardLayout.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void enableEditMode() {
        isEditMode = true;

        // 🔥 show edit fields
        taskTitleField.setVisible(true);
        descriptionArea.setVisible(true);
        startDatePicker.setVisible(true);
        deadlinePicker.setVisible(true);
        estimatedHoursField.setVisible(true);
        saveBtn.setVisible(true);

        // 🔥 hide labels
        taskTitle.setVisible(false);
        description.setVisible(false);
        startDate.setVisible(false);
        deadline.setVisible(false);
        estimatedHours.setVisible(false);

        populateEditableFields();
    }
    private void populateEditableFields() {
        if (task == null) return;

        taskTitleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());

        if (task.getStart_date() != null)
            startDatePicker.setValue(task.getStart_date().toLocalDate());

        if (task.getEnd_date() != null)
            deadlinePicker.setValue(task.getEnd_date().toLocalDate());

        estimatedHoursField.setText(String.valueOf(task.getEstimated_hours()));
    }
    @FXML
    private void handleSave() {
        try {
            task.setTitle(taskTitleField.getText());
            task.setDescription(descriptionArea.getText());

            if (startDatePicker.getValue() != null)
                task.setStart_date(java.sql.Date.valueOf(startDatePicker.getValue()));

            if (deadlinePicker.getValue() != null)
                task.setEnd_date(java.sql.Date.valueOf(deadlinePicker.getValue()));

            task.setEstimated_hours(
                    Integer.parseInt(estimatedHoursField.getText())
            );

            new TaskService().updateTask(task);

            System.out.println("Task Updated");

            // 🔥 back to view mode
            isEditMode = false;
            saveBtn.setVisible(false);

            taskTitleField.setVisible(false);
            descriptionArea.setVisible(false);
            startDatePicker.setVisible(false);
            deadlinePicker.setVisible(false);
            estimatedHoursField.setVisible(false);

            taskTitle.setVisible(true);
            description.setVisible(true);
            startDate.setVisible(true);
            deadline.setVisible(true);
            estimatedHours.setVisible(true);

            loadTaskData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}