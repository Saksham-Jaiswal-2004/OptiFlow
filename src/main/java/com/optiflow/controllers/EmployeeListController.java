package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.services.EmployeeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class EmployeeListController {

    @FXML
    private TableView<EmployeeRow> employeeTable;

    @FXML
    private TableColumn<EmployeeRow, String> nameColumn;

    @FXML
    private TableColumn<EmployeeRow, String> roleColumn;

    @FXML
    private TableColumn<EmployeeRow, String> skillsColumn;

    @FXML
    private TableColumn<EmployeeRow, String> workloadColumn;

    @FXML
    private TableColumn<EmployeeRow, String> performanceColumn;

    private final EmployeeService employeeService = new EmployeeService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        roleColumn.setCellValueFactory(data -> data.getValue().roleProperty());
        skillsColumn.setCellValueFactory(data -> data.getValue().skillsProperty());
        workloadColumn.setCellValueFactory(data -> data.getValue().workloadProperty());
        performanceColumn.setCellValueFactory(data -> data.getValue().performanceProperty());

        nameColumn.setStyle("-fx-alignment: CENTER;");
        roleColumn.setStyle("-fx-alignment: CENTER;");
        skillsColumn.setStyle("-fx-alignment: CENTER;");
        workloadColumn.setStyle("-fx-alignment: CENTER;");
        performanceColumn.setStyle("-fx-alignment: CENTER;");

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
            List<Employee> employees = employeeService.getAllEmployees();
            if (employees != null) {
                for (Employee employee : employees) {
                    int capacity = Math.max(employee.getWeeklyCapacity(), 1);
                    int allocated = Math.max(employee.getAllocated_hours(), 0);
                    int workloadPct = Math.min(100, (int) Math.round((allocated * 100.0) / capacity));

                    rows.add(new EmployeeRow(
                            employee.getName(),
                            employee.getDesignation(),
                            "Skills data linked in Employee Skills",
                            workloadPct + "%",
                            employee.getStatus()
                    ));
                }
            }
        } catch (Exception ignored) {
        }

        employeeTable.setItems(rows);
    }

    public static class EmployeeRow {
        private final StringProperty name;
        private final StringProperty role;
        private final StringProperty skills;
        private final StringProperty workload;
        private final StringProperty performance;

        public EmployeeRow(String name, String role, String skills, String workload, String performance) {
            this.name = new SimpleStringProperty(name);
            this.role = new SimpleStringProperty(role);
            this.skills = new SimpleStringProperty(skills);
            this.workload = new SimpleStringProperty(workload);
            this.performance = new SimpleStringProperty(performance);
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getName() {
            return name.get();
        }

        public StringProperty roleProperty() {
            return role;
        }

        public StringProperty skillsProperty() {
            return skills;
        }

        public StringProperty workloadProperty() {
            return workload;
        }

        public StringProperty performanceProperty() {
            return performance;
        }
    }
}
