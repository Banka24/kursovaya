package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import ru.demo.demo2.service.ProgressTrackingService;
import java.util.Map;

public class ReportController {
    @FXML private TableView<Pair> pairsTable;
    @FXML private TableColumn<Pair, String> idColumn, mentorColumn, menteeColumn, statusColumn, meetingsColumn, plansColumn, avgRatingColumn;
    @FXML private Label summaryLabel;
    private final PairDao pairDao = new PairDao();
    private final ProgressTrackingService progressService = new ProgressTrackingService();
    private final ObservableList<Pair> list = FXCollections.observableArrayList();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        mentorColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMentorFio()));
        menteeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMenteeFio()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(getStatusText(c.getValue().getStatus())));
        meetingsColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(progressService.getMeetingsCount(c.getValue().getId()))));
        plansColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(progressService.getPlansCount(c.getValue().getId()))));
        avgRatingColumn.setCellValueFactory(c -> {
            Double avg = progressService.getAverageMentorRating(c.getValue().getId());
            return new SimpleStringProperty(String.format("%.1f", avg));
        });
        pairsTable.setItems(list);
        load();
    }

    private String getStatusText(String status) {
        if ("active".equals(status)) return "Активна";
        if ("paused".equals(status)) return "Приостановлена";
        if ("completed".equals(status)) return "Завершена";
        return status;
    }

    @FXML private void onRefreshClick() { load(); }

    private void load() {
        list.clear();
        list.addAll(pairDao.findAll());
        long active = list.stream().filter(p -> "active".equals(p.getStatus())).count();
        long completed = list.stream().filter(p -> "completed".equals(p.getStatus())).count();
        int totalMeetings = 0;
        int totalPlans = 0;
        for (Pair p : list) {
            totalMeetings += progressService.getMeetingsCount(p.getId());
            totalPlans += progressService.getPlansCount(p.getId());
        }
        summaryLabel.setText("Всего пар: " + list.size() + ", активных: " + active + ", завершенных: " + completed + ", встреч: " + totalMeetings + ", планов: " + totalPlans);
    }
}