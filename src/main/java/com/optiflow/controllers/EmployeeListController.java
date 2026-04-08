package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class EmployeeListController {

    @FXML
    private TableView<EmployeeRow> employeeTable;

    @FXML
    private TableColumn<EmployeeRow, String> userIdColumn;

    @FXML
    private TableColumn<EmployeeRow, String> empIdColumn;

    @FXML
    private TableColumn<EmployeeRow, String> nameColumn;

    @FXML
    private TableColumn<EmployeeRow, String> designationColumn;

    @FXML
    private TableColumn<EmployeeRow, String> departmentColumn;

    @FXML
    private TableColumn<EmployeeRow, String> statusColumn;

    @FXML
    private TableColumn<EmployeeRow, String> weeklyCapacityColumn;

    @FXML
    private Button exportBtn;

    private final EmployeeService employeeService = new EmployeeService();

    @FXML
    public void initialize() {
        userIdColumn.setCellValueFactory(data -> data.getValue().userIdProperty());
        empIdColumn.setCellValueFactory(data -> data.getValue().empIdProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        designationColumn.setCellValueFactory(data -> data.getValue().designationProperty());
        departmentColumn.setCellValueFactory(data -> data.getValue().departmentProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        weeklyCapacityColumn.setCellValueFactory(data -> data.getValue().weeklyCapacityProperty());

        userIdColumn.setStyle("-fx-alignment: CENTER;");
        empIdColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setStyle("-fx-alignment: CENTER;");
        designationColumn.setStyle("-fx-alignment: CENTER;");
        departmentColumn.setStyle("-fx-alignment: CENTER;");
        statusColumn.setStyle("-fx-alignment: CENTER;");
        weeklyCapacityColumn.setStyle("-fx-alignment: CENTER;");

        employeeTable.setRowFactory(tv -> {
            TableRow<EmployeeRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    openEmployeeProfile(row.getItem());
                }
            });
            return row;
        });

        loadEmployees();
    }

    private void openEmployeeProfile(EmployeeRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/EmployeeProfile.fxml"));
            Parent root = loader.load();

            Stage profileStage = new Stage();
            profileStage.setTitle("Employee Profile - " + row.getName());
            profileStage.setScene(new Scene(root, 980, 700));
            profileStage.initModality(Modality.APPLICATION_MODAL);

            Stage owner = (Stage) employeeTable.getScene().getWindow();
            profileStage.initOwner(owner);
            profileStage.showAndWait();
        } catch (Exception ignored) {
        }
    }

    private void loadEmployees() {
        ObservableList<EmployeeRow> rows = FXCollections.observableArrayList();

        try {
            List<Employee> employees;
            User currentUser = SessionManager.getUser();

            if (currentUser != null && currentUser.isManager()) {
                Employee managerProfile = employeeService.getEmployeeByUserId(currentUser.getUserId());
                if (managerProfile != null) {
                    employees = resolveManagerTeam(managerProfile);
                } else {
                    employees = FXCollections.observableArrayList();
                }
            } else {
                employees = employeeService.getAllEmployees();
            }

            if (employees != null) {
                for (Employee employee : employees) {
                    rows.add(new EmployeeRow(
                            String.valueOf(employee.getUser_id()),
                            String.valueOf(employee.getEmp_id()),
                            employee.getName(),
                            employee.getDesignation(),
                            employee.getDepartment(),
                            employee.getStatus(),
                            String.valueOf(employee.getWeeklyCapacity())
                    ));
                }
            }
        } catch (Exception ignored) {
        }

        employeeTable.setItems(rows);
    }

    private List<Employee> resolveManagerTeam(Employee managerProfile) {
        try {
            Map<Integer, Employee> unique = new LinkedHashMap<>();

            List<Employee> byEmpId = employeeService.getEmployeesByManager(managerProfile.getEmp_id());
            if (byEmpId != null) {
                for (Employee employee : byEmpId) {
                    unique.put(employee.getEmp_id(), employee);
                }
            }

            List<Employee> byUserId = employeeService.getEmployeesByManager(managerProfile.getUser_id());
            if (byUserId != null) {
                for (Employee employee : byUserId) {
                    unique.put(employee.getEmp_id(), employee);
                }
            }

            return FXCollections.observableArrayList(unique.values());
        } catch (Exception ignored) {
            return FXCollections.observableArrayList();
        }
    }

    @FXML
    private void handleExport() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CSV", "CSV", "Excel");
        dialog.setTitle("Export Employees");
        dialog.setHeaderText("Choose export format");
        dialog.setContentText("Format:");

        dialog.showAndWait().ifPresent(format -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Employees " + format);
                fileChooser.setInitialFileName("employees_" + java.time.LocalDate.now() + "." + (format.equals("CSV") ? "csv" : "xlsx"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(format.toUpperCase() + " Files", "*." + (format.equals("CSV") ? "csv" : "xlsx")));

                Stage stage = (Stage) exportBtn.getScene().getWindow();
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    String filePath = file.getAbsolutePath();
                    String savedFile;
                    if (format.equals("CSV")) {
                        savedFile = employeeService.exportEmployeesToCSV(filePath);
                    } else {
                        savedFile = employeeService.exportEmployeesToExcel(filePath);
                    }
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Export Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Employees exported successfully to " + savedFile);
                    alert.showAndWait();
                }
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to export employees: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    public static class EmployeeRow {
        private final StringProperty userId;
        private final StringProperty empId;
        private final StringProperty name;
        private final StringProperty designation;
        private final StringProperty department;
        private final StringProperty status;
        private final StringProperty weeklyCapacity;

        public EmployeeRow(String userId, String empId, String name, String designation, String department, String status, String weeklyCapacity) {
            this.userId = new SimpleStringProperty(userId == null ? "-" : userId);
            this.empId = new SimpleStringProperty(empId == null ? "-" : empId);
            this.name = new SimpleStringProperty(name);
            this.designation = new SimpleStringProperty(designation == null ? "-" : designation);
            this.department = new SimpleStringProperty(department == null ? "-" : department);
            this.status = new SimpleStringProperty(status == null ? "-" : status);
            this.weeklyCapacity = new SimpleStringProperty(weeklyCapacity == null ? "-" : weeklyCapacity);
        }

        public StringProperty userIdProperty() {
            return userId;
        }

        public StringProperty empIdProperty() {
            return empId;
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getName() {
            return name.get();
        }

        public StringProperty designationProperty() {
            return designation;
        }

        public StringProperty departmentProperty() {
            return department;
        }

        public StringProperty statusProperty() {
            return status;
        }

        public StringProperty weeklyCapacityProperty() {
            return weeklyCapacity;
        }
    }
}
