package com.optiflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WorkLogController {

    @FXML
    private Button addEntryBtn;

    @FXML
    public void initialize() {
        addEntryBtn.setOnAction(e -> handleAddEntry());
    }

    private void handleAddEntry() {
        try {
            // Load the AddWorklog.fxml dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddWorklog.fxml"));
            Parent root = loader.load();

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Worklog Entry");
            Scene scene = new Scene(root, 500, 650);
            scene.setFill(Color.web("#05020a"));
            dialogStage.setScene(scene);
            
            // Make it a modal dialog
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            // Set owner to the main window if available
            Stage mainStage = (Stage) addEntryBtn.getScene().getWindow();
            dialogStage.initOwner(mainStage);

            // Show the dialog
            dialogStage.showAndWait();

        } catch (Exception e) {
            showAlert("Error", "Failed to open add worklog dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}