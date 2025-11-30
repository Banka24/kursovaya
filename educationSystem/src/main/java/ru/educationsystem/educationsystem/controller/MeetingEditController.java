package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.Meeting;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.service.MeetingService;
import ru.educationsystem.educationsystem.service.PairService;
import ru.educationsystem.educationsystem.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MeetingEditController {
    private final MeetingService meetingService = new MeetingService();
    private final PairService pairService = new PairService();
    private final UserService userService = new UserService();
    
    private Meeting currentMeeting;
    private boolean isEditMode = false;
    
    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private TextArea topicTextArea;

    @FXML
    private TextArea notesTextArea;

    @FXML
    private ComboBox<Pair> pairComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Настройка timeComboBox с доступными временами
        timeComboBox.setItems(FXCollections.observableArrayList(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        ));
        
        // Загрузка пар пользователя из PairService в pairComboBox
        try {
            // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
            // Для примера используем пользователя с ID = 1
            Integer currentUserId = 1;
            List<Pair> userPairs = pairService.getPairsByMenteeId(currentUserId);
            
            // Фильтруем только активные пары
            List<Pair> activePairs = userPairs.stream()
                .filter(pair -> pair.getStatus().equals(ru.educationsystem.educationsystem.model.PairStatus.ACTIVE))
                .toList();
            
            pairComboBox.setItems(FXCollections.observableArrayList(activePairs));
            
            // Если есть активные пары, выбираем первую
            if (!activePairs.isEmpty()) {
                pairComboBox.setValue(activePairs.get(0));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
        
        // Установка сегодняшней даты по умолчанию
        datePicker.setValue(LocalDate.now());

        // Если редактирование существующей встречи, загрузка её данных
        // В реальном приложении здесь должна быть логика получения ID встречи из параметров навигации
        // Для примера используем ID = null (создание новой встречи)
        Integer meetingId = null;
        
        if (meetingId != null) {
            try {
                currentMeeting = meetingService.findById(meetingId)
                    .orElseThrow(() -> new RuntimeException("Встреча не найдена"));
                
                loadMeetingData();
                isEditMode = true;
                saveButton.setText("Обновить");
                statusLabel.setText("Режим редактирования встречи");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные встречи: " + e.getMessage());
            }
        } else {
            // Настройка для создания новой встречи
            statusLabel.setText("Создание новой встречи");
        }
    }
    
    private void loadMeetingData() {
        if (currentMeeting == null) return;
        
        // Дата и время
        LocalDateTime dateTime = currentMeeting.getDatetime();
        if (dateTime != null) {
            datePicker.setValue(dateTime.toLocalDate());
            timeComboBox.setValue(dateTime.toLocalTime().toString().substring(0, 5));
        }
        
        // Тема
        topicTextArea.setText(currentMeeting.getTopic());
        
        // Заметки
        notesTextArea.setText(currentMeeting.getTasksDone());
        
        // Пара
        if (currentMeeting.getPair() != null) {
            pairComboBox.setValue(currentMeeting.getPair());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Проверка корректности введенных данных
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeComboBox.getValue();
        String topic = topicTextArea.getText();
        
        if (selectedDate == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите дату встречи");
            return;
        }
        
        if (selectedTime == null || selectedTime.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите время встречи");
            return;
        }
        
        if (topic == null || topic.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите тему встречи");
            return;
        }
        
        Pair selectedPair = pairComboBox.getValue();
        if (selectedPair == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите пару");
            return;
        }
        
        try {
            // Создание или обновление встречи через MeetingService
            Meeting meeting;
            
            if (isEditMode) {
                meeting = currentMeeting;
            } else {
                meeting = new Meeting();
                meeting.setPair(selectedPair);
            }
            
            // Установка даты и времени
            LocalTime time = LocalTime.parse(selectedTime);
            meeting.setDatetime(LocalDateTime.of(selectedDate, time));
            
            // Установка темы и заметок
            meeting.setTopic(topic.trim());
            meeting.setTasksDone(notesTextArea.getText());
            
            // Сохранение встречи
            if (isEditMode) {
                meetingService.update(meeting);
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Встреча обновлена");
            } else {
                meetingService.create(meeting);
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Встреча создана");
            }
            
            // Возврат к предыдущему экрану
            Launcher.setRoot("PairDetailView");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сохранить встречу: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        // Возврат к предыдущему экрану без сохранения
        Launcher.setRoot("PairDetailView");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
