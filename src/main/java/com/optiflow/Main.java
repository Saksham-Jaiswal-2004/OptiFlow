package com.optiflow;

import com.optiflow.database.DBConnection;
import com.optiflow.utils.AppContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Register.fxml"));
        if (loader.getLocation() == null)
        {
            throw new RuntimeException("FXML file not found!");
        }

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("OptiFlow");
        stage.setScene(scene);
//        stage.setMaximized(true);
//        stage.setResizable(false);
        stage.show();

        if(AppContext.getSocketClient() != null)
        {
            AppContext.getSocketClient().setListener(message ->
            {
                Platform.runLater(() -> {
                    System.out.println(message.getMessageType());
                });
            });
        }
    }

    public static void main(String[] args) throws Exception
    {
//        try(Connection conn = DBConnection.getConnection())
//        {
//            if (conn != null)
//                System.out.println("DATABASE CONNECTED!");
//            else
//                System.out.println("FAILED TO CONNECT DATABASE!");
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }

        launch();
    }
}
