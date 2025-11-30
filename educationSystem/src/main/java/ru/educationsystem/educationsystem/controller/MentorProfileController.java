package ru.educationsystem.educationsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.*;
import ru.educationsystem.educationsystem.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MentorProfileController {
    private final MentorService mentorService = new MentorService();
    private final PairService pairService = new PairService();
    private final UserService userService = new UserService();
    
    private Mentor currentMentor;
    private boolean isUserMentee = false;
    private boolean hasPendingRequest = false;
    
    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label directionsLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private Label menteesCountLabel;

    @FXML
    private TextArea bioTextArea;

    @FXML
    private Button backButton;

    @FXML
    private Button requestMentorshipButton;

    @FXML
    private Button scheduleMeetingButton;

    @FXML
    public void initialize() {
        // В реальном приложении здесь должна быть логика получения ID наставника из параметров навигации
        // Для примера используем наставника с ID = 1
        Integer mentorId = 1;
        
        try {
            // Загрузка данных о наставнике из MentorService
            currentMentor = mentorService.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Наставник не найден"));
            
            // Заполнение полей информацией о наставнике
            updateMentorInfo();
            
            // Настройка доступности кнопок в зависимости от статуса пользователя
            updateButtonStates();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
    }
    
    private void updateMentorInfo() {
        // Имя
        String fullName = currentMentor.getLastName() + " " + currentMentor.getFirstName();
        if (currentMentor.getMiddleName() != null && !currentMentor.getMiddleName().isEmpty()) {
            fullName += " " + currentMentor.getMiddleName();
        }
        nameLabel.setText(fullName);
        
        // Email
        emailLabel.setText(currentMentor.getEmail());
        
        // Направления
        Set<Direction> directions = currentMentor.getDirections();
        if (directions != null && !directions.isEmpty()) {
            String directionNames = directions.stream()
                .map(Direction::getName)
                .collect(Collectors.joining(", "));
            directionsLabel.setText(directionNames);
        } else {
            directionsLabel.setText("Не указаны");
        }
        
        // Опыт (заглушка, в реальном приложении можно добавить поле в модель)
        experienceLabel.setText("3 года");
        
        // Количество подопечных
        try {
            List<Pair> activePairs = pairService.getActivePairsByMentorId(currentMentor.getId());
            menteesCountLabel.setText(String.valueOf(activePairs.size()));
        } catch (Exception e) {
            menteesCountLabel.setText("0");
        }
        
        // Биография (заглушка, в реальном приложении можно добавить поле в модель)
        bioTextArea.setText("Опытный наставник с многолетним опытом в области " + 
            (directions != null && !directions.isEmpty() ? 
                directions.iterator().next().getName() : "развития карьеры") + 
            ". Помогает подопечным достигать профессиональных целей и развивать необходимые навыки.");
        bioTextArea.setEditable(false);
    }
    
    private void updateButtonStates() {
        try {
            // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
            // Для примера используем пользователя с ID = 1
            Integer currentUserId = 1;
            
            // Проверяем, является ли пользователь подопечным этого наставника
            List<Pair> userPairs = pairService.getPairsByMenteeId(currentUserId);
            isUserMentee = userPairs.stream()
                .anyMatch(pair -> pair.getMentor().getId().equals(currentMentor.getId()) && 
                                pair.getStatus() == PairStatus.ACTIVE);
            
            // Проверяем, есть ли уже отправленный запрос этому наставнику
            hasPendingRequest = userPairs.stream()
                .anyMatch(pair -> pair.getMentor().getId().equals(currentMentor.getId()) && 
                                pair.getStatus().equals(PairStatus.ACTIVE));
            
            // Обновляем состояние кнопок
            requestMentorshipButton.setDisable(hasPendingRequest || isUserMentee || !currentMentor.getAvailable());
            scheduleMeetingButton.setDisable(!isUserMentee);
            
            // Обновляем текст кнопки запроса
            if (hasPendingRequest) {
                requestMentorshipButton.setText("Запрос отправлен");
            } else if (!currentMentor.getAvailable()) {
                requestMentorshipButton.setText("Недоступен");
            } else {
                requestMentorshipButton.setText("Запросить наставничество");
            }
        } catch (Exception e) {
            // В случае ошибки отключаем кнопки
            requestMentorshipButton.setDisable(true);
            scheduleMeetingButton.setDisable(true);
        }
    }

    @FXML
    private void handleRequestMentorship(ActionEvent event) {
        try {
            // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
            // Для примера используем пользователя с ID = 1
            Integer currentUserId = 1;
            
            // Проверка, что пользователь еще не отправлял запрос этому наставнику
            if (hasPendingRequest) {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Вы уже отправили запрос этому наставнику");
                return;
            }
            
            if (isUserMentee) {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Вы уже являетесь подопечным этого наставника");
                return;
            }
            
            // Создание запроса на наставничество через соответствующий сервис
            Pair newPair = new Pair();
            newPair.setMentor(currentMentor);
            newPair.setMentee(userService.findMenteeByUserId(currentUserId));
            newPair.setStartDate(java.time.LocalDate.now());
            newPair.setStatus(PairStatus.ACTIVE);
            
            pairService.createPair(newPair);
            
            // Обновление состояния кнопок
            hasPendingRequest = true;
            updateButtonStates();
            
            // Отображение сообщения о результате операции
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Запрос на наставничество отправлен");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось отправить запрос: " + e.getMessage());
        }
    }

    @FXML
    private void handleScheduleMeeting(ActionEvent event) {
        // Проверка, что пользователь уже является подопечным этого наставника
        if (!isUserMentee) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Только подопечные могут планировать встречи с наставником");
            return;
        }
        
        // Переход к форме планирования встречи
        try {
            // В реальном приложении здесь нужно передать ID пары в следующее представление
            Launcher.setRoot("MeetingEditView");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть страницу планирования встречи: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Launcher.setRoot("MentorSearchView");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
