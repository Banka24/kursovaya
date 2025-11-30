package ru.educationsystem.educationsystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.*;
import ru.educationsystem.educationsystem.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PairDetailController {
    private final PairService pairService = new PairService();
    private final MeetingService meetingService = new MeetingService();
    private final DevelopmentPlanService developmentPlanService = new DevelopmentPlanService();
    
    private Pair currentPair;
    
    @FXML
    private Label mentorNameLabel;

    @FXML
    private Label menteeNameLabel;

    @FXML
    private Label startDateLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label directionLabel;

    @FXML
    private TableView<Meeting> meetingsTableView;

    @FXML
    private TableView<DevelopmentPlan> developmentPlanTableView;

    @FXML
    private Button addMeetingButton;

    @FXML
    private Button addPlanItemButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Настройка колонок таблиц для отображения встреч и плана развития
        setupMeetingsTable();
        setupDevelopmentPlanTable();
        
        // Загрузка данных о паре из PairService
        loadPairDetails();
    }
    
    private void setupMeetingsTable() {
        // Колонка с датой и временем встречи
        TableColumn<Meeting, String> dateTimeCol = new TableColumn<>("Дата и время");
        dateTimeCol.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDatetime();
            return new SimpleStringProperty(
                dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : ""
            );
        });
        
        // Колонка с темой встречи
        TableColumn<Meeting, String> topicCol = new TableColumn<>("Тема");
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        
        // Колонка с оценками
        TableColumn<Meeting, String> ratingsCol = new TableColumn<>("Оценки");
        ratingsCol.setCellValueFactory(cellData -> {
            Meeting meeting = cellData.getValue();
            Integer mentorRating = meeting.getMentorRating();
            Integer menteeRating = meeting.getMenteeRating();
            
            if (mentorRating != null && menteeRating != null) {
                return new SimpleStringProperty(String.format("Н: %d/5, П: %d/5", mentorRating, menteeRating));
            } else if (mentorRating != null) {
                return new SimpleStringProperty(String.format("Н: %d/5", mentorRating));
            } else if (menteeRating != null) {
                return new SimpleStringProperty(String.format("П: %d/5", menteeRating));
            } else {
                return new SimpleStringProperty("Нет оценок");
            }
        });
        
        meetingsTableView.getColumns().addAll(dateTimeCol, topicCol, ratingsCol);
    }
    
    private void setupDevelopmentPlanTable() {
        // Колонка с датой создания
        TableColumn<DevelopmentPlan, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDeadline();
            return new SimpleStringProperty(
                date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : ""
            );
        });
        
        // Колонка с задачей
        TableColumn<DevelopmentPlan, String> taskCol = new TableColumn<>("Задача");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        
        // Колонка со статусом
        TableColumn<DevelopmentPlan, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cellData -> {
            Boolean completed = cellData.getValue().getCompleted();
            return new SimpleStringProperty(completed != null && completed ? "Выполнено" : "В процессе");
        });
        
        developmentPlanTableView.getColumns().addAll(dateCol, taskCol, statusCol);
    }

    private void loadPairDetails() {
        try {
            // Получение ID пары из параметров
            // В реальном приложении здесь должна быть логика получения ID пары из параметров навигации
            // Для примера используем пару с ID = 1
            Integer pairId = 1;
            
            // Загрузка данных о паре из PairService
            currentPair = pairService.findById(pairId)
                .orElseThrow(() -> new RuntimeException("Пара не найдена"));
            
            // Заполнение полей информацией
            updatePairInfo();
            
            // Загрузка списка встреч из MeetingService
            loadMeetings();
            
            // Загрузка плана развития из DevelopmentPlanService
            loadDevelopmentPlan();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
    }
    
    private void updatePairInfo() {
        if (currentPair == null) return;
        
        // Имя наставника
        String mentorName = currentPair.getMentor().getLastName() + " " + 
                           currentPair.getMentor().getFirstName();
        if (currentPair.getMentor().getMiddleName() != null && !currentPair.getMentor().getMiddleName().isEmpty()) {
            mentorName += " " + currentPair.getMentor().getMiddleName();
        }
        mentorNameLabel.setText(mentorName);
        
        // Имя подопечного
        String menteeName = currentPair.getMentee().getLastName() + " " + 
                          currentPair.getMentee().getFirstName();
        if (currentPair.getMentee().getMiddleName() != null && !currentPair.getMentee().getMiddleName().isEmpty()) {
            menteeName += " " + currentPair.getMentee().getMiddleName();
        }
        menteeNameLabel.setText(menteeName);
        
        // Дата начала
        LocalDate startDate = currentPair.getStartDate();
        startDateLabel.setText(startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
        
        // Статус
        PairStatus status = currentPair.getStatus();
        String statusText = status.toString();
        
        switch (status) {
            case PAUSED:
                statusText = "Приостановлена";
                break;
            case ACTIVE:
                statusText = "Активна";
                break;
            case CANCELLED:
                statusText = "Отменена";
                break;
            case COMPLETED:
                statusText = "Завершена";
                break;
        }
        
        statusLabel.setText(statusText);
        
        // Направления
        Set<Direction> directions = currentPair.getMentor().getDirections();
        if (directions != null && !directions.isEmpty()) {
            String directionNames = directions.stream()
                .map(Object::toString) // В реальном приложении здесь должно быть getName()
                .collect(Collectors.joining(", "));
            directionLabel.setText(directionNames);
        } else {
            directionLabel.setText("Не указаны");
        }
        
        // Блокируем кнопки, если пара не активна
        boolean isActive = status == PairStatus.ACTIVE;
        addMeetingButton.setDisable(!isActive);
        addPlanItemButton.setDisable(!isActive);
    }
    
    private void loadMeetings() {
        try {
            List<Meeting> meetings = meetingService.getMeetingsByPairId(currentPair.getId());
            ObservableList<Meeting> meetingsObservableList = FXCollections.observableArrayList(meetings);
            meetingsTableView.setItems(meetingsObservableList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить встречи: " + e.getMessage());
        }
    }
    
    private void loadDevelopmentPlan() {
        try {
            List<DevelopmentPlan> developmentPlans = developmentPlanService.getPlansByPairId(currentPair.getId());
            ObservableList<DevelopmentPlan> plansObservableList = FXCollections.observableArrayList(developmentPlans);
            developmentPlanTableView.setItems(plansObservableList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить план развития: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMeeting(ActionEvent event) {
        // Переход к форме добавления встречи
        try {
            // В реальном приложении здесь нужно передать ID пары в следующее представление
            Launcher.setRoot("MeetingEditView");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть страницу добавления встречи: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddPlanItem(ActionEvent event) {
        // Переход к форме добавления элемента плана развития
        // Открытие диалогового окна для ввода данных
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление задачи");
        dialog.setHeaderText("Введите новую задачу для плана развития");
        dialog.setContentText("Задача:");
        
        // Показываем диалог и обрабатываем результат
        dialog.showAndWait().ifPresent(taskText -> {
            if (taskText != null && !taskText.trim().isEmpty()) {
                try {
                    // Сохранение нового элемента через DevelopmentPlanService
                    DevelopmentPlan newPlanItem = new DevelopmentPlan();
                    newPlanItem.setPair(currentPair);
                    newPlanItem.setTitle(taskText.trim());
                    // newPlanItem.setCreationDate(LocalDate.now()); // В модели DevelopmentPlan нет поля creationDate
                    newPlanItem.setCompleted(false);
                    
                    developmentPlanService.createPlanItem(newPlanItem);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Задача добавлена в план развития");
                    
                    // Обновляем таблицу
                    loadDevelopmentPlan();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить задачу: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Launcher.setRoot("MyPairsView");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}