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
        report.append("ОТЧЕТ: ").append(reportType);
        report.append("Период: с ").append(startDate).append(" по ").append(endDate);

        if (direction != null) {
            report.append("Направление: ").append(direction.getName());
        }

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
        // Здесь будет логика расчета эффективности наставничества
        report.append("Средний рейтинг наставников: 4.5");
        report.append("% завершенных планов: 78%");
        report.append("Количество активных пар: 12");
    }

    private void generateProgressReport(StringBuilder report, LocalDate startDate, LocalDate endDate, Direction direction) {
        // Здесь будет логика отчета по прогрессу
        report.append("Всего планов развития: 15");
        report.append("Завершено в срок: 12");
        report.append("Отложено: 3");
    }

    private void generateMeetingsReport(StringBuilder report, LocalDate startDate, LocalDate endDate, Direction direction) {
        // Здесь будет логика отчета по встречам
        report.append("Всего встреч: 45");
        report.append("Средняя оценка: 4.3");
        report.append("Самые частые темы: Java, Spring, Hibernate");
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