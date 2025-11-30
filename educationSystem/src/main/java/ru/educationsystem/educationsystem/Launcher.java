package ru.educationsystem.educationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Launcher extends Application {
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Платформа наставничества");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void setRoot(String fxml) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Launcher.class.getResource(fxml + ".fxml")));
        primaryStage.getScene().setRoot(root);
    }
}
