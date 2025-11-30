package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.service.DirectionService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DirectionController {
    private final DirectionService directionService = new DirectionService();
    private final ObservableList<Direction> directionObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Direction> directionTableView;

    @FXML
    private TableColumn<Direction, Integer> idColumn;

    @FXML
    private TableColumn<Direction, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Загрузка данных из сервиса
        loadDirections();

        // Установка данных в таблицу
        directionTableView.setItems(directionObservableList);

        // Обработка выбора элемента в таблице
        directionTableView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newSelection) -> {
                    if (newSelection != null) {
                        nameField.setText(newSelection.getName());
                    }
                });
    }

    private void loadDirections() {
        List<Direction> directions = directionService.findAll();
        directionObservableList.clear();
        directionObservableList.addAll(directions);
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Название направления не может быть пустым");
            return;
        }

        try {
            Direction direction = new Direction();
            direction.setName(name.trim());
            directionService.createDirection(direction);
            loadDirections();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Направление успешно добавлено");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить направление: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        Direction selectedDirection = directionTableView.getSelectionModel().getSelectedItem();
        if (selectedDirection == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите направление для обновления");
            return;
        }

        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Название направления не может быть пустым");
            return;
        }

        try {
            selectedDirection.setName(name.trim());
            directionService.updateDirection(selectedDirection);
            loadDirections();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Направление успешно обновлено");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить направление: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        Direction selectedDirection = directionTableView.getSelectionModel().getSelectedItem();
        if (selectedDirection == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите направление для удаления");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение");
        confirmAlert.setHeaderText("Вы уверены, что хотите удалить направление?");
        confirmAlert.setContentText("Направление: " + selectedDirection.getName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                directionService.deleteDirection(selectedDirection.getId());
                loadDirections();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Направление успешно удалено");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить направление: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearAction(ActionEvent event) {
        clearFields();
        directionTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        Launcher.setRoot("MainDashboardView");
    }

    private void clearFields() {
        nameField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}