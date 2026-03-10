package com.optiflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        if (loader.getLocation() == null)
        {
            throw new RuntimeException("FXML file not found!");
        }

        Scene scene = new Scene(loader.load());

        stage.setTitle("OptiFlow");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}
