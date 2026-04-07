package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ManagersPanelController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> departmentFilter;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TableView<ManagerRow> managerTable;

    @FXML
    private TableColumn<ManagerRow, String> managerNameColumn;

    @FXML
    private TableColumn<ManagerRow, String> departmentColumn;

    @FXML
    private TableColumn<ManagerRow, Number> projectsColumn;

    @FXML
    private TableColumn<ManagerRow, Number> teamSizeColumn;

    @FXML
    private TableColumn<ManagerRow, String> healthScoreColumn;

    @FXML
    private TableColumn<ManagerRow, String> managerStatusColumn;

    @FXML
    private Label managerCountLabel;

    private final EmployeeService employeeService = new EmployeeService();
    private final ProjectService projectService = new ProjectService();

    private final ObservableList<ManagerRow> allRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        managerNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        departmentColumn.setCellValueFactory(data -> data.getValue().departmentProperty());
        projectsColumn.setCellValueFactory(data -> data.getValue().projectsProperty());
        teamSizeColumn.setCellValueFactory(data -> data.getValue().teamSizeProperty());
        healthScoreColumn.setCellValueFactory(data -> data.getValue().healthProperty());
        managerStatusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        departmentFilter.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> applyFilters());
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> applyFilters());
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());

        loadManagers();
    }

    private void loadManagers() {
        departmentFilter.setItems(FXCollections.observableArrayList("All", "Engineering", "Product", "Operations"));
        statusFilter.setItems(FXCollections.observableArrayList("All", "Active", "On Leave"));
        departmentFilter.getSelectionModel().selectFirst();
        statusFilter.getSelectionModel().selectFirst();

        allRows.clear();

        try {
            List<Employee> managers = employeeService.getAllManagers();
            if (managers != null) {
                for (Employee manager : managers) {
                    int projectCount = projectService.getProjectByManager(manager.getEmp_id()) > 0 ? 1 : 0;
                    int teamSize = 0;
                    try {
                        List<Employee> team = employeeService.getEmployeesByManager(manager.getEmp_id());
                        teamSize = team == null ? 0 : team.size();
                    } catch (Exception ignored) {
                    }

                    double health = employeeService.calculateManagerWorkload(manager) * 100.0;
                    allRows.add(new ManagerRow(
                            manager.getName(),
                            manager.getDepartment(),
                            projectCount,
                            teamSize,
                            String.format(Locale.ROOT, "%.0f%%", health),
                            manager.getStatus()
                    ));
                }
            }
        } catch (Exception ignored) {
        }

        applyFilters();
    }

    private void applyFilters() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String dept = departmentFilter.getValue();
        String status = statusFilter.getValue();

        List<ManagerRow> filtered = allRows.stream()
                .filter(row -> keyword.isEmpty() || row.getName().toLowerCase(Locale.ROOT).contains(keyword))
                .filter(row -> dept == null || "All".equalsIgnoreCase(dept) || row.getDepartment().equalsIgnoreCase(dept))
                .filter(row -> status == null || "All".equalsIgnoreCase(status) || row.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());

        managerTable.setItems(FXCollections.observableArrayList(filtered));
        managerCountLabel.setText("Showing " + filtered.size() + " managers");
    }

    public static class ManagerRow {
        private final StringProperty name;
        private final StringProperty department;
        private final SimpleIntegerProperty projects;
        private final SimpleIntegerProperty teamSize;
        private final StringProperty health;
        private final StringProperty status;

        public ManagerRow(String name, String department, int projects, int teamSize, String health, String status) {
            this.name = new SimpleStringProperty(name);
            this.department = new SimpleStringProperty(department);
            this.projects = new SimpleIntegerProperty(projects);
            this.teamSize = new SimpleIntegerProperty(teamSize);
            this.health = new SimpleStringProperty(health);
            this.status = new SimpleStringProperty(status);
        }

        public String getName() {
            return name.get();
        }

        public String getDepartment() {
            return department.get();
        }

        public String getStatus() {
            return status.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty departmentProperty() {
            return department;
        }

        public SimpleIntegerProperty projectsProperty() {
            return projects;
        }

        public SimpleIntegerProperty teamSizeProperty() {
            return teamSize;
        }

        public StringProperty healthProperty() {
            return health;
        }

        public StringProperty statusProperty() {
            return status;
        }
    }
}
