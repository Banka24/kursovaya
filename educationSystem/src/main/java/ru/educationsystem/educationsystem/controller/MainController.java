package ru.educationsystem.educationsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void openMentorsView() {
        loadView("mentors-view.fxml");
    }

    @FXML
    public void openMenteesView() {
        loadView("mentees-view.fxml");
    }

    @FXML
    public void openDirectionsView() {
        loadView("directions-view.fxml");
    }

    @FXML
    public void openPairsView() {
        loadView("pairs-view.fxml");
    }

    @FXML
    public void openDevelopmentPlansView() {
        loadView("development-plans-view.fxml");
    }

    @FXML
    public void openMeetingsView() {
        loadView("meetings-view.fxml");
    }

    @FXML
    public void openReportsView() {
        loadView("reports-view.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/educationsystem/educationsystem/" + fxmlFile));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}