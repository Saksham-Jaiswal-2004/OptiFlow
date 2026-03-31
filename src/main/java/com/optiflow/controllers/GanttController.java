package com.optiflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;

public class GanttController {

    @FXML
    private VBox ganttContainer;

    private final int DAY_WIDTH = 80;
    private final int EMP_WIDTH = 140;

    @FXML
    public void initialize() {

        addEmployeeRow("Emp 1", new Task[]{
                new Task(4, 6, "green", "T1"),
                new Task(7, 7, "green", "T2")
        });

        addEmployeeRow("Emp 2", new Task[]{
                new Task(4, 6, "red", "T3")
        });

        addEmployeeRow("Emp 3", new Task[]{
                new Task(4, 6, "blue", "T4")
        });
    }

    private void addEmployeeRow(String name, Task[] tasks) {

        // MATCH HEADER STRUCTURE EXACTLY
        HBox row = new HBox();
        row.setSpacing(10);

        // Employee label
        Label empLabel = new Label(name);
        empLabel.setPrefWidth(EMP_WIDTH);
        empLabel.setMinWidth(EMP_WIDTH);
        empLabel.setMaxWidth(EMP_WIDTH);
        empLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        // Separator (match header)
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPrefHeight(30);

        // Timeline container
        HBox timeline = new HBox();
        timeline.setSpacing(0);

        // Create 7 day cells
        StackPane[] dayCells = new StackPane[7];

        for (int i = 0; i < 7; i++) {

            StackPane cell = new StackPane();
            cell.setPrefWidth(DAY_WIDTH);
            cell.setPrefHeight(30);
            cell.setStyle("-fx-background-color: #6B7280; -fx-background-radius: 6;");

            dayCells[i] = cell;
            timeline.getChildren().add(cell);
        }

        // Add tasks inside cells
        for (Task t : tasks) {

            StackPane bar = new StackPane();
            bar.setPrefHeight(30);
            bar.setPrefWidth((t.endDay - t.startDay + 1) * DAY_WIDTH);

            String color;
            switch (t.status) {
                case "green": color = "#10B981"; break;
                case "red": color = "#EF4444"; break;
                case "blue": color = "#3B82F6"; break;
                default: color = "#9CA3AF";
            }

            bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 6;");

            Label taskLabel = new Label(t.name);
            taskLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            bar.getChildren().add(taskLabel);

            // place bar in correct starting cell
            dayCells[t.startDay - 1].getChildren().add(bar);
        }

        row.getChildren().addAll(empLabel, separator, timeline);
        ganttContainer.getChildren().add(row);
    }

    static class Task {
        int startDay, endDay;
        String status, name;

        Task(int s, int e, String status, String name) {
            this.startDay = s;
            this.endDay = e;
            this.status = status;
            this.name = name;
        }
    }
}