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
import ru.educationsystem.educationsystem.repository.UserDao;
import ru.educationsystem.educationsystem.service.PairService;
import ru.educationsystem.educationsystem.service.UserService;

import java.io.IOException;
import java.util.List;

public class MyPairsController {
    private final PairService pairService = new PairService();
    private final UserService userService = new UserService(new UserDao());
    @FXML
    private TableView pairsTableView;

    @FXML
    private Button refreshButton;

    @FXML
    private Button viewDetailsButton;

    @FXML
    private Button scheduleMeetingButton;

    @FXML
    private Button backButton;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Настройка колонок таблицы для отображения пар
        TableColumn<Pair, String> mentorNameCol = new TableColumn<>("Наставник");
        mentorNameCol.setCellValueFactory(cellData -> {
            String firstName = cellData.getValue().getMentor().getFirstName();
            String lastName = cellData.getValue().getMentor().getLastName();
            return new SimpleStringProperty(lastName + " " + firstName);
        });
        
        TableColumn<Pair, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cellData -> {
            PairStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status.toString());
        });
        
        TableColumn<Pair, String> startDateCol = new TableColumn<>("Дата начала");
        startDateCol.setCellValueFactory(cellData -> {
            java.time.LocalDate date = cellData.getValue().getStartDate();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });
        
        pairsTableView.getColumns().addAll(mentorNameCol, statusCol, startDateCol);
        
        // Загрузка пар пользователя из PairService
        loadPairs();
    }

    private void loadPairs() {
        // Получение текущего пользователя из UserService
        // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
        // Для примера используем пользователя с ID = 1
        Integer currentUserId = 1;
        
        // Загрузка пар пользователя из PairService
        List<Pair> userPairs = pairService.getPairsByMenteeId(currentUserId);
        
        // Обновление таблицы
        ObservableList<Pair> pairsObservableList = FXCollections.observableArrayList(userPairs);
        pairsTableView.setItems(pairsObservableList);
        
        // Обновление статуса
        long activePairsCount = userPairs.stream()
            .filter(pair -> pair.getStatus().equals(PairStatus.ACTIVE))
            .count();
        statusLabel.setText("Активных пар: " + activePairsCount);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadPairs();
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        // Проверка, что выбрана пара в таблице
        Pair selectedPair = (Pair) pairsTableView.getSelectionModel().getSelectedItem();
        if (selectedPair == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите пару для просмотра");
            return;
        }
        
        // В реальном приложении здесь нужно передать ID выбранной пары в следующее представление
        // Например, через глобальный контекст или параметры навигации
        
        // Переход к детальной информации о паре
        try {
            Launcher.setRoot("PairDetailView");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть страницу деталей: " + e.getMessage());
        }
    }

    @FXML
    private void handleScheduleMeeting(ActionEvent event) {
        // Проверка, что выбрана активная пара в таблице
        Pair selectedPair = (Pair) pairsTableView.getSelectionModel().getSelectedItem();
        if (selectedPair == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите пару для планирования встречи");
            return;
        }
        
        if (!selectedPair.getStatus().equals(PairStatus.ACTIVE)) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Встречи можно планировать только для активных пар");
            return;
        }
        
        // В реальном приложении здесь нужно передать ID выбранной пары в следующее представление
        // Например, через глобальный контекст или параметры навигации
        
        // Переход к форме планирования встречи
        try {
            Launcher.setRoot("MeetingEditView");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть страницу планирования встречи: " + e.getMessage());
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
