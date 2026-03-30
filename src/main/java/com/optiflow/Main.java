package com.optiflow;

import com.optiflow.utils.AppContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.optiflow.database.DBConnection;
import java.sql.*;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
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
        Connection conn = DBConnection.getConnection();
        if(conn != null)
            System.out.println("DATABASE CONNECTED!");
        else
            System.out.println("FAILED TO CONNECT DATABASE!");
        launch();

        AppContext.getSocketClient().setListener(message -> {
            Platform.runLater(() -> {
                System.out.println(message.getMessageType());
            });
        });
    }
}
