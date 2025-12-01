package ru.educationsystem.educationsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    public void openMentorsView() {
        openView("mentors-view.fxml", "Наставники");
    }

    @FXML
    public void openMenteesView() {
        openView("mentees-view.fxml", "Подопечные");
    }

    @FXML
    public void openDirectionsView() {
        openView("directions-view.fxml", "Направления");
    }

    @FXML
    public void openPairsView() {
        openView("pairs-view.fxml", "Управление парами");
    }

    @FXML
    public void openDevelopmentPlansView() {
        openView("development-plans-view.fxml", "Планы развития");
    }

    @FXML
    public void openMeetingsView() {
        openView("meetings-view.fxml", "Журнал встреч");
    }

    @FXML
    public void openReportsView() {
        openView("reports-view.fxml", "Генерация отчетов");
    }

    private void openView(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/educationsystem/educationsystem/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}