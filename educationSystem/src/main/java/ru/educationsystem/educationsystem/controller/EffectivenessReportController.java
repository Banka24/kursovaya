package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.model.Meeting;
import ru.educationsystem.educationsystem.service.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class EffectivenessReportController {
    private final PairService pairService = new PairService();
    private final MeetingService meetingService = new MeetingService();
    private final UserService userService = new UserService();
    
    @FXML
    private ComboBox<String> periodComboBox;

    @FXML
    private ComboBox<String> pairComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private PieChart statusPieChart;

    @FXML
    private BarChart<String, Number> meetingsBarChart;

    @FXML
    private LineChart<String, Number> progressLineChart;

    @FXML
    private TableView detailedDataTableView;

    @FXML
    private Button generateButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button backButton;
    
    private List<Pair> userPairs;
    private List<Meeting> filteredMeetings;

    @FXML
    public void initialize() {
        // Настройка periodComboBox с доступными периодами
        periodComboBox.getItems().addAll(
            "Последний месяц",
            "Последние 3 месяца",
            "Последние 6 месяцев",
            "Последний год",
            "Произвольный период"
        );
        periodComboBox.setValue("Последний месяц");
        
        // Загрузка пар пользователя из PairService в pairComboBox
        try {
            // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
            // Для примера используем пользователя с ID = 1
            Integer currentUserId = 1;
            userPairs = pairService.getPairsByMenteeId(currentUserId);
            
            ObservableList<String> pairNames = FXCollections.observableArrayList();
            for (Pair pair : userPairs) {
                String mentorName = pair.getMentor().getLastName() + " " + pair.getMentor().getFirstName();
                pairNames.add(mentorName);
            }
            pairComboBox.setItems(pairNames);
            if (!pairNames.isEmpty()) {
                pairComboBox.setValue(pairNames.get(0));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
        
        // Установка периода по умолчанию
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);
        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);
        
        // Настройка графиков
        setupCharts();

        // Настройка колонок таблицы для детальных данных
        setupDetailedDataTable();
        
        // Генерация отчета при инициализации
        handleGenerate(null);
    }

    private void setupCharts() {
        // Настройка заголовков и осей графиков
        statusPieChart.setTitle("Статус пар");
        
        meetingsBarChart.setTitle("Количество встреч по месяцам");
        meetingsBarChart.getXAxis().setLabel("Месяц");
        meetingsBarChart.getYAxis().setLabel("Количество встреч");
        
        progressLineChart.setTitle("Прогресс развития");
        progressLineChart.getXAxis().setLabel("Месяц");
        progressLineChart.getYAxis().setLabel("Средняя оценка");
    }

    private void setupDetailedDataTable() {
        // Настройка колонок таблицы
        TableColumn<Meeting, String> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDatetime();
            return new javafx.beans.property.SimpleStringProperty(
                dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : ""
            );
        });
        
        TableColumn<Meeting, String> topicColumn = new TableColumn<>("Тема");
        topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        
        TableColumn<Meeting, String> tasksColumn = new TableColumn<>("Выполненные задачи");
        tasksColumn.setCellValueFactory(new PropertyValueFactory<>("tasksDone"));
        
        TableColumn<Meeting, Number> mentorRatingColumn = new TableColumn<>("Оценка наставника");
        mentorRatingColumn.setCellValueFactory(new PropertyValueFactory<>("mentorRating"));
        
        TableColumn<Meeting, Number> menteeRatingColumn = new TableColumn<>("Оценка подопечного");
        menteeRatingColumn.setCellValueFactory(new PropertyValueFactory<>("menteeRating"));
        
        detailedDataTableView.getColumns().addAll(
            dateColumn, topicColumn, tasksColumn, mentorRatingColumn, menteeRatingColumn
        );
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        // Получение выбранных фильтров
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String selectedPairName = pairComboBox.getValue();
        
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите даты начала и окончания периода");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Дата начала не может быть позже даты окончания");
            return;
        }
        
        // Загрузка данных из сервисов
        try {
            // Определение выбранной пары
            Pair selectedPair = null;
            if (selectedPairName != null && !selectedPairName.isEmpty()) {
                for (Pair pair : userPairs) {
                    String mentorName = pair.getMentor().getLastName() + " " + pair.getMentor().getFirstName();
                    if (mentorName.equals(selectedPairName)) {
                        selectedPair = pair;
                        break;
                    }
                }
            }
            
            // Загрузка встреч
            if (selectedPair != null) {
                filteredMeetings = meetingService.getMeetingsByPairIdInPeriod(
                    selectedPair.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59)
                );
            } else {
                filteredMeetings = meetingService.getMeetingsInPeriod(
                    startDate.atStartOfDay(), endDate.atTime(23, 59)
                );
            }
            
            // Обновление графиков и таблицы
            updateCharts(selectedPair);
            updateDetailedTable();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
    }

    private void updateCharts(Pair selectedPair) {
        // Обновление данных на графиках на основе выбранных фильтров
        
        // График статусов пар
        ObservableList<PieChart.Data> statusData = FXCollections.observableArrayList();
        if (selectedPair != null) {
            statusData.add(new PieChart.Data(selectedPair.getStatus().toString(), 1));
        } else {
            Map<String, Long> statusCounts = userPairs.stream()
                .collect(Collectors.groupingBy(pair -> pair.getStatus().toString(), Collectors.counting()));
            
            statusCounts.forEach((status, count) -> 
                statusData.add(new PieChart.Data(status, count))
            );
        }
        statusPieChart.setData(statusData);
        
        // График количества встреч по месяцам
        XYChart.Series<String, Number> meetingsSeries = new XYChart.Series<>();
        meetingsSeries.setName("Количество встреч");
        
        Map<String, Long> meetingsByMonth = filteredMeetings.stream()
            .collect(Collectors.groupingBy(
                meeting -> meeting.getDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.counting()
            ));
        
        // Сортировка по месяцам
        meetingsByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> meetingsSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        
        meetingsBarChart.getData().clear();
        meetingsBarChart.getData().add(meetingsSeries);
        
        // График прогресса (средние оценки)
        XYChart.Series<String, Number> mentorRatingSeries = new XYChart.Series<>();
        mentorRatingSeries.setName("Оценка наставника");
        
        XYChart.Series<String, Number> menteeRatingSeries = new XYChart.Series<>();
        menteeRatingSeries.setName("Оценка подопечного");
        
        Map<String, Double> mentorRatingsByMonth = filteredMeetings.stream()
            .filter(meeting -> meeting.getMentorRating() != null)
            .collect(Collectors.groupingBy(
                meeting -> meeting.getDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.averagingInt(Meeting::getMentorRating)
            ));
        
        Map<String, Double> menteeRatingsByMonth = filteredMeetings.stream()
            .filter(meeting -> meeting.getMenteeRating() != null)
            .collect(Collectors.groupingBy(
                meeting -> meeting.getDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.averagingInt(Meeting::getMenteeRating)
            ));
        
        // Сортировка по месяцам
        mentorRatingsByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> mentorRatingSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        
        menteeRatingsByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> menteeRatingSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        
        progressLineChart.getData().clear();
        progressLineChart.getData().addAll(mentorRatingSeries, menteeRatingSeries);
    }

    private void updateDetailedTable() {
        // Обновление данных в таблице на основе выбранных фильтров
        ObservableList<Meeting> meetingsData = FXCollections.observableArrayList(filteredMeetings);
        detailedDataTableView.setItems(meetingsData);
    }

    @FXML
    private void handleExportPdf(ActionEvent event) {
        // Генерация PDF отчета на основе текущих данных
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить отчет");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("Отчет_эффективности_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy")) + ".pdf");
            
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                // Создание PDF документа
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                // Заголовок
                document.add(new Paragraph("Отчет об эффективности наставничества"));
                document.add(new Paragraph("Период: " + 
                    startDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                    endDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                
                if (pairComboBox.getValue() != null) {
                    document.add(new Paragraph("Наставник: " + pairComboBox.getValue()));
                }
                
                document.add(new Paragraph(" ")); // Пустая строка
                
                // Таблица со встречами
                if (!filteredMeetings.isEmpty()) {
                    PdfPTable table = new PdfPTable(5);
                    table.addCell("Дата");
                    table.addCell("Тема");
                    table.addCell("Выполненные задачи");
                    table.addCell("Оценка наставника");
                    table.addCell("Оценка подопечного");
                    
                    for (Meeting meeting : filteredMeetings) {
                        table.addCell(meeting.getDatetime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                        table.addCell(meeting.getTopic() != null ? meeting.getTopic() : "");
                        table.addCell(meeting.getTasksDone() != null ? meeting.getTasksDone() : "");
                        table.addCell(meeting.getMentorRating() != null ? meeting.getMentorRating().toString() : "");
                        table.addCell(meeting.getMenteeRating() != null ? meeting.getMenteeRating().toString() : "");
                    }
                    
                    document.add(table);
                } else {
                    document.add(new Paragraph("За выбранный период встреч не проводилось"));
                }
                
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Отчет успешно сохранен: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось создать PDF отчет: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Launcher.setRoot("MainDashboardView");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
