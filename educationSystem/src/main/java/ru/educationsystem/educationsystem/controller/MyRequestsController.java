package ru.educationsystem.educationsystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.model.PairStatus;
import ru.educationsystem.educationsystem.service.PairService;
import ru.educationsystem.educationsystem.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyRequestsController {
    private final PairService pairService = new PairService();

    @FXML
    private TableView<Pair> requestsTableView;

    @FXML
    private Button refreshButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button backButton;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Настройка колонок таблицы для отображения запросов
        setupRequestsTable();
        
        // Загрузка запросов из соответствующего сервиса
        loadRequests();
    }
    
    private void setupRequestsTable() {
        // Колонка с именем наставника
        TableColumn<Pair, String> mentorNameCol = new TableColumn<>("Наставник");
        mentorNameCol.setCellValueFactory(cellData -> {
            String firstName = cellData.getValue().getMentor().getFirstName();
            String lastName = cellData.getValue().getMentor().getLastName();
            return new SimpleStringProperty(lastName + " " + firstName);
        });
        
        // Колонка с email наставника
        TableColumn<Pair, String> mentorEmailCol = new TableColumn<>("Email");
        mentorEmailCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMentor().getEmail())
        );
        
        // Колонка со статусом запроса
        TableColumn<Pair, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cellData -> {
            PairStatus status = cellData.getValue().getStatus();
            String statusText = switch (status) {
                case PAUSED -> "Приостановлен";
                case ACTIVE -> "Активен";
                case CANCELLED -> "Отменен";
                case COMPLETED -> "Завершен";
            };

            // Добавляем цветовое кодирование статуса

            return new SimpleStringProperty(statusText);
        });

        // Колонка с датой создания запроса
        TableColumn<Pair, String> dateCol = new TableColumn<>("Дата создания");
        dateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getStartDate();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
        });
        
        requestsTableView.getColumns().addAll(mentorNameCol, mentorEmailCol, statusCol, dateCol);
    }

    private void loadRequests() {
        try {
            // Получение текущего пользователя из UserService
            // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
            // Для примера используем пользователя с ID = 1
            Integer currentUserId = 1;
            
            // Загрузка запросов пользователя из соответствующего сервиса
            List<Pair> userRequests = pairService.getPairsByMenteeId(currentUserId);
            
            // Обновление таблицы
            ObservableList<Pair> requestsObservableList = FXCollections.observableArrayList(userRequests);
            requestsTableView.setItems(requestsObservableList);
            
            // Обновление статуса
            long pendingRequestsCount = userRequests.stream()
                .filter(pair -> pair.getStatus().equals(PairStatus.ACTIVE))
                .count();
            
            long activeRequestsCount = userRequests.stream()
                .filter(pair -> pair.getStatus().equals(PairStatus.ACTIVE))
                .count();
            
            statusLabel.setText(String.format("Ожидающие: %d | Активные: %d", pendingRequestsCount, activeRequestsCount));
            
            // Блокируем кнопку отмены, если нет ожидающих запросов
            cancelButton.setDisable(pendingRequestsCount == 0);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить запросы: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadRequests();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Проверка, что выбран запрос в таблице
        Pair selectedRequest = requestsTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите запрос для отмены");
            return;
        }
        
        if (!selectedRequest.getStatus().equals(PairStatus.ACTIVE)) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Можно отменить только ожидающие запросы");
            return;
        }
        
        // Подтверждение отмены
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение");
        confirmAlert.setHeaderText("Вы уверены, что хотите отменить запрос?");
        confirmAlert.setContentText("Наставник: " + 
            selectedRequest.getMentor().getLastName() + " " + 
            selectedRequest.getMentor().getFirstName());
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Отмена выбранного запроса через соответствующий сервис
                pairService.deletePair(selectedRequest.getId());
                
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Запрос отменен");
                
                // Обновление таблицы
                loadRequests();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось отменить запрос: " + e.getMessage());
            }
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