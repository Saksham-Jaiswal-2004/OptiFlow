import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GanttController {

    @FXML
    private HBox dateHeader;

    @FXML
    private VBox ganttContainer;

    private final int DAY_WIDTH = 25;

    @FXML
    public void initialize() {

        // Define timeline
        LocalDate start = LocalDate.of(2026, 3, 25);
        LocalDate end = LocalDate.of(2026, 4, 5);

        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        // ================= HEADER =================
        for (int i = 0; i < totalDays; i++) {
            LocalDate date = start.plusDays(i);

            Label day = new Label(String.valueOf(date.getDayOfMonth()));
            day.setPrefWidth(DAY_WIDTH);
            day.setStyle("-fx-font-size: 11px;");

            dateHeader.getChildren().add(day);
        }

        // ================= EMPLOYEES =================
        addEmployee("John", start,
                new Task("T1", LocalDate.of(2026,3,25), LocalDate.of(2026,3,28), "#3B82F6"));

        addEmployee("Alice", start,
                new Task("T1", LocalDate.of(2026,3,25), LocalDate.of(2026,3,28), "#F59E0B"),
                new Task("T2", LocalDate.of(2026,3,28), LocalDate.of(2026,4,5), "#F59E0B"));
    }

    private void addEmployee(String name, LocalDate timelineStart, Task... tasks) {

        HBox row = new HBox(5);
        row.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #E5E7EB;");

        Label label = new Label(name);
        label.setPrefWidth(120);
        label.setStyle("-fx-font-weight: bold;");

        Pane timeline = new Pane();
        timeline.setPrefHeight(40);
        timeline.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 6;");

        for (Task t : tasks) {

            long startOffset = ChronoUnit.DAYS.between(timelineStart, t.start);
            long duration = ChronoUnit.DAYS.between(t.start, t.end) + 1;

            double x = startOffset * DAY_WIDTH;
            double width = duration * DAY_WIDTH;

            Rectangle rect = new Rectangle(x, 8, width, 24);
            rect.setArcWidth(10);
            rect.setArcHeight(10);
            rect.setFill(Color.web(t.color));

            Label taskLabel = new Label(t.name);
            taskLabel.setLayoutX(x + 5);
            taskLabel.setLayoutY(10);
            taskLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");

            timeline.getChildren().addAll(rect, taskLabel);
        }

        row.getChildren().addAll(label, timeline);
        ganttContainer.getChildren().add(row);
    }

    static class Task {
        String name;
        LocalDate start;
        LocalDate end;
        String color;

        Task(String name, LocalDate start, LocalDate end, String color) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }
}