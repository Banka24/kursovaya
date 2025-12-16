package ru.demo.demo2.controller;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainMenuController {
    @FXML
    private BorderPane mainContainer;

    @FXML
    private VBox drawer;

    private boolean menuOpen = false;

    @FXML
    public void initialize() { loadView("/ru/demo/demo2/pairs-view.fxml"); }

    @FXML
    public void toggleMenu() { if (menuOpen) closeMenu(); else openMenu(); }

    private void openMenu() { menuOpen = true; drawer.setManaged(true); drawer.toFront();
        TranslateTransition t = new TranslateTransition(Duration.millis(250), drawer); t.setToX(0); t.play(); }
    private void closeMenu() { menuOpen = false;
        TranslateTransition t = new TranslateTransition(Duration.millis(250), drawer); t.setToX(-200); t.setOnFinished(_ -> drawer.setManaged(false)); t.play(); }

    @FXML
    private void onPairsClick() { closeMenu(); loadView("/ru/demo/demo2/pairs-view.fxml"); }

    @FXML
    private void onMeetingsClick() { closeMenu(); loadView("/ru/demo/demo2/meetings-view.fxml"); }

    @FXML
    private void onPlansClick() { closeMenu(); loadView("/ru/demo/demo2/plans-view.fxml"); }

    @FXML
    private void onMentorsClick() { closeMenu(); loadView("/ru/demo/demo2/mentors-view.fxml"); }

    @FXML
    private void onMenteesClick() { closeMenu(); loadView("/ru/demo/demo2/mentees-view.fxml"); }

    @FXML
    private void onDirectionsClick() { closeMenu(); loadView("/ru/demo/demo2/directions-view.fxml"); }

    @FXML
    private void onReportClick() { closeMenu(); loadView("/ru/demo/demo2/report-view.fxml"); }

    private void loadView(String fxml) { try { mainContainer.setCenter(new FXMLLoader(getClass().getResource(fxml)).load()); } catch (Exception e) { e.printStackTrace(); } }
}