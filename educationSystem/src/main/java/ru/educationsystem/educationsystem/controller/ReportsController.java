package ru.educationsystem.educationsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.repository.DevelopmentPlanDao;
import ru.educationsystem.educationsystem.repository.DirectionDao;
import ru.educationsystem.educationsystem.repository.MeetingDao;
import ru.educationsystem.educationsystem.repository.PairDao;
import ru.educationsystem.educationsystem.service.DirectionService;
import ru.educationsystem.educationsystem.service.PairService;
import ru.educationsystem.educationsystem.service.MeetingService;
import ru.educationsystem.educationsystem.service.DevelopmentPlanService;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<Direction> directionComboBox;

    @FXML
    private TextArea reportPreviewArea;

    private final DirectionService directionService = new DirectionService(new DirectionDao());
    private final PairService pairService = new PairService(new PairDao());
    private final MeetingService meetingService = new MeetingService(new MeetingDao());
    private final DevelopmentPlanService developmentPlanService = new DevelopmentPlanService(new DevelopmentPlanDao());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Инициализация типов отчетов
        reportTypeComboBox.getItems().addAll(
            "Эффективность наставничества",
            "Прогресс по планам развития",
            "Статистика встреч",
            "Сводный отчет"
        );

        // Загрузка направлений
        List<Direction> directions = directionService.findAll();
        directionComboBox.getItems().addAll(directions);
        directionComboBox.setConverter(new javafx.util.StringConverter<Direction>() {
            @Override
            public String toString(Direction direction) {
                return direction == null ? "" : direction.getName();
            }
            @Override
            public Direction fromString(String string) {
                return null;
            }
        });

        // Установка значений по умолчанию
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
    }

    @FXML
    public void generateReport() {
        String reportType = reportTypeComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        Direction direction = directionComboBox.getValue();

        if (reportType == null || startDate == null || endDate == null) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("ОТЧЕТ: ").append(reportType).append("\n\n");
        report.append("Период: с ").append(startDate).append(" по ").append(endDate).append("\n");

        if (direction != null) {
            report.append("Направление: ").append(direction.getName()).append("\n");
        }
        report.append("\n");

        switch (reportType) {
            case "Эффективность наставничества":
                generateEfficiencyReport(report, startDate, endDate, direction);
                break;
            case "Прогресс по планам развития":
                generateProgressReport(report, startDate, endDate, direction);
                break;
            case "Статистика встреч":
                generateMeetingsReport(report, startDate, endDate, direction);
                break;
            case "Сводный отчет":
                generateSummaryReport(report, startDate, endDate, direction);
                break;
        }

        reportPreviewArea.setText(report.toString());
    }

    private void generateEfficiencyReport(StringBuilder report, LocalDate startDate, LocalDate endDate, Direction direction) {
        var pairs = pairService.getAllPairsWithMentorAndMentee();
        var activePairs = pairs.stream().filter(p -> "active".equals(p.getStatus())).count();
        var completedPairs = pairs.stream().filter(p -> "completed".equals(p.getStatus())).count();
        
        report.append("ЭФФЕКТИВНОСТЬ НАСТАВНИЧЕСТВА").append("\n");
        report.append("Всего пар: ").append(pairs.size()).append("\n");
        report.append("Активных пар: ").append(activePairs).append("\n");
        report.append("Завершенных пар: ").append(completedPairs).append("\n");
        
        var meetings = meetingService.getAllMeetingsWithPair();
        if (!meetings.isEmpty()) {
            double avgMentorRating = meetings.stream()
                .filter(m -> m.getMentorRating() != null)
                .mapToInt(m -> m.getMentorRating())
                .average().orElse(0.0);
            double avgMenteeRating = meetings.stream()
                .filter(m -> m.getMenteeRating() != null)
                .mapToInt(m -> m.getMenteeRating())
                .average().orElse(0.0);
            report.append("Средний рейтинг наставников: ").append(String.format("%.2f", avgMentorRating)).append("\n");
            report.append("Средний рейтинг подопечных: ").append(String.format("%.2f", avgMenteeRating)).append("\n");
        }
        report.append("\n");
    }

    private void generateProgressReport(StringBuilder report, LocalDate startDate, LocalDate endDate, Direction direction) {
        var plans = developmentPlanService.getAllDevelopmentPlansWithPair();
        var completedPlans = plans.stream()
            .filter(p -> p.getDeadline() != null && p.getDeadline().isBefore(LocalDate.now()))
            .count();
        var activePlans = plans.stream()
            .filter(p -> p.getDeadline() == null || !p.getDeadline().isBefore(LocalDate.now()))
            .count();
        
        report.append("ПРОГРЕСС ПО ПЛАНАМ РАЗВИТИЯ").append("\n");
        report.append("Всего планов развития: ").append(plans.size()).append("\n");
        report.append("Активных планов: ").append(activePlans).append("\n");
        report.append("Завершенных планов: ").append(completedPlans).append("\n");
        
        if (plans.size() > 0) {
            double completionRate = (completedPlans * 100.0) / plans.size();
            report.append("Процент завершения: ").append(String.format("%.1f%%", completionRate)).append("\n");
        }
        report.append("\n");
    }

    private void generateMeetingsReport(StringBuilder report, LocalDate startDate, LocalDate endDate, Direction direction) {
        var meetings = meetingService.getAllMeetingsWithPair();
        var filteredMeetings = meetings.stream()
            .filter(m -> m.getDatetime() != null)
            .filter(m -> {
                LocalDate meetingDate = m.getDatetime().toLocalDate();
                return !meetingDate.isBefore(startDate) && !meetingDate.isAfter(endDate);
            })
            .toList();
        
        report.append("СТАТИСТИКА ВСТРЕЧ").append("\n");
        report.append("Всего встреч за период: ").append(filteredMeetings.size()).append("\n");
        
        if (!filteredMeetings.isEmpty()) {
            double avgRating = filteredMeetings.stream()
                .filter(m -> m.getMentorRating() != null && m.getMenteeRating() != null)
                .mapToDouble(m -> (m.getMentorRating() + m.getMenteeRating()) / 2.0)
                .average().orElse(0.0);
            report.append("Средняя оценка встреч: ").append(String.format("%.2f", avgRating)).append("\n");
            
            var topicsCount = filteredMeetings.stream()
                .filter(m -> m.getTopic() != null && !m.getTopic().isEmpty())
                .count();
            report.append("Встреч с темами: ").append(topicsCount).append("\n");
        }
        report.append("\n");
    }

    private void generateSummaryReport(StringBuilder report, LocalDate startDate, LocalDate endDate, Direction direction) {
        generateEfficiencyReport(report, startDate, endDate, direction);
        generateProgressReport(report, startDate, endDate, direction);
        generateMeetingsReport(report, startDate, endDate, direction);
    }

    @FXML
    public void exportToPdf() {
        if (reportPreviewArea.getText().isEmpty()) {
            showAlert("Ошибка", "Сначала сформируйте отчет");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчет в PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                ru.educationsystem.educationsystem.util.PdfExportUtil.exportReportToPdf(
                    reportPreviewArea.getText(), 
                    file.getAbsolutePath()
                );
                showAlert("Успех", "Отчет успешно сохранен в " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось сохранить отчет: " + e.getMessage());
            }
        }
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) reportPreviewArea.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}