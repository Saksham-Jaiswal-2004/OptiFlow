package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.utils.SessionManager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProjectListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<String> managerFilter;

    @FXML
    private TableView<ProjectRow> projectTable;

    @FXML
    private TableColumn<ProjectRow, String> nameColumn;

    @FXML
    private TableColumn<ProjectRow, String> statusColumn;

    @FXML
    private TableColumn<ProjectRow, Number> progressColumn;

    @FXML
    private TableColumn<ProjectRow, String> managerColumn;

    @FXML
    private TableColumn<ProjectRow, String> deadlineColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private ComboBox<Integer> rowsPerPage;

    @FXML
    private Button addProjectBtn;

    private final ObservableList<ProjectRow> allRows = FXCollections.observableArrayList();
    private final ObservableList<ProjectRow> filteredRows = FXCollections.observableArrayList();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getUser();
        seedData();
        configureColumns();
        configureFilters();
        configurePagination();
        applyRoleRestrictions();

        projectTable.setRowFactory(tv -> {
            TableRow<ProjectRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    openProjectDetail(row.getItem());
                }
            });
            return row;
        });

        applyFilters();
    }

    @FXML
    private void handleAddProject() {
        if (currentUser != null && currentUser.isManager()) {
            return;
        }
        System.out.println("Add Project clicked");
    }

    @FXML
    public void applyFilters() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedStatus = statusFilter.getValue();
        String selectedManager = managerFilter.getValue();

        List<ProjectRow> result = allRows.stream()
                .filter(row -> keyword.isEmpty() || row.getName().toLowerCase(Locale.ROOT).contains(keyword))
                .filter(row -> selectedStatus == null || "All".equals(selectedStatus) || row.getStatus().equalsIgnoreCase(selectedStatus))
                .filter(row -> selectedManager == null || "All".equals(selectedManager) || row.getManager().equalsIgnoreCase(selectedManager))
                .collect(Collectors.toList());

        filteredRows.setAll(result);
        refreshPagination();
    }

    @FXML
    public void resetFilters() {
        searchField.clear();
        statusFilter.getSelectionModel().select("All");
        managerFilter.getSelectionModel().select("All");
        applyFilters();
    }

    private void configureColumns() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        managerColumn.setCellValueFactory(data -> data.getValue().managerProperty());
        deadlineColumn.setCellValueFactory(data -> data.getValue().deadlineProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        progressColumn.setCellValueFactory(data -> data.getValue().progressProperty());

        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add("proj-status-badge");
                if ("On Track".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("proj-status-good");
                } else if ("At Risk".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("proj-status-warn");
                } else {
                    badge.getStyleClass().add("proj-status-bad");
                }
                setGraphic(badge);
            }
        });

        progressColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                ProgressBar progressBar = new ProgressBar(item.doubleValue() / 100.0);
                progressBar.getStyleClass().add("proj-progress");
                progressBar.setPrefWidth(130);

                Label pct = new Label((int) item.doubleValue() + "%");
                pct.getStyleClass().add("proj-progress-text");

                HBox box = new HBox(8, progressBar, pct);
                setGraphic(box);
            }
        });
    }

    private void configureFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("All", "On Track", "At Risk", "Delayed"));
        statusFilter.getSelectionModel().selectFirst();

        managerFilter.setItems(FXCollections.observableArrayList("All", "Ritika Mehra", "Arjun Reddy", "Nikhil Kumar", "Priya Menon"));
        managerFilter.getSelectionModel().selectFirst();

        rowsPerPage.setItems(FXCollections.observableArrayList(5, 10, 20));
        rowsPerPage.getSelectionModel().select(Integer.valueOf(10));
        rowsPerPage.valueProperty().addListener((obs, oldVal, newVal) -> refreshPagination());
    }

    private void applyRoleRestrictions() {
        if (currentUser == null || !currentUser.isManager()) {
            return;
        }

        if (addProjectBtn != null) {
            addProjectBtn.setVisible(false);
            addProjectBtn.setManaged(false);
        }

        String managerName = currentUser.getName() == null ? "" : currentUser.getName().trim();
        if (!managerName.isEmpty()) {
            managerFilter.setItems(FXCollections.observableArrayList(managerName));
            managerFilter.getSelectionModel().select(managerName);
            managerFilter.setDisable(true);

            allRows.removeIf(row -> !row.getManager().equalsIgnoreCase(managerName));
        }
    }

    private void configurePagination() {
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> showPage(newIndex.intValue()));
    }

    private void refreshPagination() {
        int pageSize = rowsPerPage.getValue() == null ? 10 : rowsPerPage.getValue();
        int pageCount = Math.max(1, (int) Math.ceil(filteredRows.size() / (double) pageSize));
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        showPage(0);
    }

    private void showPage(int pageIndex) {
        int pageSize = rowsPerPage.getValue() == null ? 10 : rowsPerPage.getValue();
        int from = pageIndex * pageSize;
        int to = Math.min(from + pageSize, filteredRows.size());
        if (from > to) {
            projectTable.setItems(FXCollections.observableArrayList());
            return;
        }
        projectTable.setItems(FXCollections.observableArrayList(filteredRows.subList(from, to)));
    }

    private void openProjectDetail(ProjectRow row) {
        System.out.println("Open Project Detail: " + row.getName());
    }

    private void seedData() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        allRows.setAll(
                new ProjectRow("OptiFlow Core", "On Track", 86, "Ritika Mehra", LocalDate.now().plusDays(8).format(fmt)),
                new ProjectRow("Retail BI", "At Risk", 61, "Arjun Reddy", LocalDate.now().plusDays(3).format(fmt)),
                new ProjectRow("HRMS Upgrade", "On Track", 73, "Nikhil Kumar", LocalDate.now().plusDays(14).format(fmt)),
                new ProjectRow("Client Portal", "Delayed", 48, "Priya Menon", LocalDate.now().plusDays(2).format(fmt)),
                new ProjectRow("Task Engine V2", "On Track", 81, "Ritika Mehra", LocalDate.now().plusDays(12).format(fmt)),
                new ProjectRow("Payroll Sync", "At Risk", 57, "Arjun Reddy", LocalDate.now().plusDays(6).format(fmt)),
                new ProjectRow("Audit Pipeline", "On Track", 69, "Nikhil Kumar", LocalDate.now().plusDays(10).format(fmt)),
                new ProjectRow("Partner API", "Delayed", 41, "Priya Menon", LocalDate.now().plusDays(1).format(fmt)),
                new ProjectRow("KPI Studio", "On Track", 88, "Ritika Mehra", LocalDate.now().plusDays(18).format(fmt)),
                new ProjectRow("Resource AI", "At Risk", 53, "Arjun Reddy", LocalDate.now().plusDays(5).format(fmt)),
                new ProjectRow("Notifications", "On Track", 76, "Nikhil Kumar", LocalDate.now().plusDays(9).format(fmt)),
                new ProjectRow("Doc Center", "On Track", 64, "Priya Menon", LocalDate.now().plusDays(11).format(fmt))
        );
    }

    public static class ProjectRow {
        private final StringProperty name;
        private final StringProperty status;
        private final IntegerProperty progress;
        private final StringProperty manager;
        private final StringProperty deadline;

        public ProjectRow(String name, String status, int progress, String manager, String deadline) {
            this.name = new SimpleStringProperty(name);
            this.status = new SimpleStringProperty(status);
            this.progress = new SimpleIntegerProperty(progress);
            this.manager = new SimpleStringProperty(manager);
            this.deadline = new SimpleStringProperty(deadline);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getStatus() {
            return status.get();
        }

        public StringProperty statusProperty() {
            return status;
        }

        public IntegerProperty progressProperty() {
            return progress;
        }

        public String getManager() {
            return manager.get();
        }

        public StringProperty managerProperty() {
            return manager;
        }

        public StringProperty deadlineProperty() {
            return deadline;
        }
    }
}
