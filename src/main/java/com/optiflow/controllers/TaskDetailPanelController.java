package com.optiflow.controllers;

import com.optiflow.models.Comments;
import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.CommentService;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.services.UserService;
import com.optiflow.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskDetailPanelController {

    @FXML
    private VBox root;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label taskDescriptionLabel;

    @FXML
    private Label memberCountLabel;

    @FXML
    private Label commentCountLabel;

    @FXML
    private ListView<String> assignedEmployeesList;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ListView<Comments> commentsListView;

    @FXML
    private TextArea commentInputArea;

    private final TaskService taskService = new TaskService();
    private final EmployeeService employeeService = new EmployeeService();
    private final UserService userService = new UserService();
    private final CommentService commentService = new CommentService();

    private final ObservableList<String> assignedEmployees = FXCollections.observableArrayList();
    private final ObservableList<Comments> comments = FXCollections.observableArrayList();
    private final Map<Integer, String> userNameCache = new LinkedHashMap<>();

    private Tasks currentTask;

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("Pending", "In Progress", "Blocked", "Completed"));
        assignedEmployeesList.setItems(assignedEmployees);
        commentsListView.setItems(comments);

        commentsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Comments item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String author = resolveUserName(item.getUser_id());
                String content = item.getContent() == null ? "" : item.getContent().trim();
                setText(author + "\n" + content);
            }
        });

        tryLoadFallbackTask();
        refreshView();
    }

    public void setTask(Tasks task) {
        currentTask = task;
        refreshView();
    }

    @FXML
    private void handlePostComment() {
        if (currentTask == null) {
            return;
        }

        String content = commentInputArea.getText() == null ? "" : commentInputArea.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        try {
            User user = SessionManager.getUser();
            int userId = user == null ? 0 : user.getUserId();

            Comments payload = new Comments(currentTask.getTask_id(), userId, content);
            if (commentService.addComment(payload)) {
                commentInputArea.clear();
                loadComments();
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void handleSaveChanges() {
        if (currentTask == null) {
            return;
        }

        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus == null || selectedStatus.isBlank()) {
            return;
        }

        try {
            if (taskService.updateTaskStatus(currentTask.getTask_id(), selectedStatus)) {
                currentTask.setStatus(selectedStatus);
            }
        } catch (Exception ignored) {
        }
    }

    private void tryLoadFallbackTask() {
        if (currentTask != null) {
            return;
        }

        try {
            List<Tasks> allTasks = taskService.getAllTasks();
            if (allTasks != null && !allTasks.isEmpty()) {
                currentTask = allTasks.get(0);
            }
        } catch (SQLException ignored) {
        }
    }

    private void refreshView() {
        if (pageTitleLabel == null) {
            return;
        }

        if (currentTask == null) {
            pageTitleLabel.setText("Task Detail");
            dueDateLabel.setText("Due: --");
            taskDescriptionLabel.setText("No task selected.");
            memberCountLabel.setText("0 members");
            commentCountLabel.setText("0 messages");
            assignedEmployees.clear();
            comments.clear();
            return;
        }

        String title = currentTask.getTitle() == null || currentTask.getTitle().isBlank()
                ? "Untitled Task"
                : currentTask.getTitle();
        pageTitleLabel.setText("Task Detail - " + title);

        String due = currentTask.getEnd_date() == null ? "--" : currentTask.getEnd_date().toLocalDate().toString();
        dueDateLabel.setText("Due: " + due);

        String description = currentTask.getDescription() == null || currentTask.getDescription().isBlank()
                ? "No task description available."
                : currentTask.getDescription();
        taskDescriptionLabel.setText(description);

        statusComboBox.setValue(normalizeStatus(currentTask.getStatus()));

        loadAssignedEmployees();
        loadComments();
    }

    private void loadAssignedEmployees() {
        assignedEmployees.clear();
        if (currentTask == null) {
            memberCountLabel.setText("0 members");
            return;
        }

        try {
            List<Employee> allEmployees = employeeService.getAllEmployees();
            if (allEmployees != null) {
                for (Employee employee : allEmployees) {
                    if (currentTask.getAssigned_to() == employee.getEmp_id() || currentTask.getAssigned_to() == employee.getUser_id()) {
                        String designation = employee.getDesignation() == null ? "Unspecified" : employee.getDesignation();
                        assignedEmployees.add(employee.getName() + " - " + designation);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (assignedEmployees.isEmpty() && currentTask.getAssigned_to() > 0) {
            try {
                User fallback = userService.getUserById(currentTask.getAssigned_to());
                if (fallback != null) {
                    assignedEmployees.add(fallback.getName() + " - " + fallback.getRole());
                }
            } catch (Exception ignored) {
            }
        }

        memberCountLabel.setText(assignedEmployees.size() + (assignedEmployees.size() == 1 ? " member" : " members"));
    }

    private void loadComments() {
        comments.clear();
        if (currentTask == null) {
            commentCountLabel.setText("0 messages");
            return;
        }

        try {
            List<Comments> taskComments = commentService.getCommentsByTask(currentTask.getTask_id());
            if (taskComments != null) {
                comments.addAll(taskComments);
            }
        } catch (Exception ignored) {
        }

        commentCountLabel.setText(comments.size() + (comments.size() == 1 ? " message" : " messages"));
    }

    private String resolveUserName(int userId) {
        if (userId <= 0) {
            return "System";
        }

        if (userNameCache.containsKey(userId)) {
            return userNameCache.get(userId);
        }

        String name = "User " + userId;
        try {
            User user = userService.getUserById(userId);
            if (user != null && user.getName() != null && !user.getName().isBlank()) {
                name = user.getName();
            }
        } catch (Exception ignored) {
        }

        userNameCache.put(userId, name);
        return name;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Pending";
        }
        String normalized = status.trim().toLowerCase();
        if ("completed".equals(normalized)) {
            return "Completed";
        }
        if ("in progress".equals(normalized) || "active".equals(normalized)) {
            return "In Progress";
        }
        if ("blocked".equals(normalized)) {
            return "Blocked";
        }
        return "Pending";
    }
}
