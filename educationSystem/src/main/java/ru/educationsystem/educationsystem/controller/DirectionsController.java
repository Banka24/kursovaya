package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.DirectionDao;
import ru.educationsystem.educationsystem.repository.MentorDao;
import ru.educationsystem.educationsystem.service.DirectionService;
import ru.educationsystem.educationsystem.service.MentorService;

import java.util.List;
import java.util.stream.Collectors;

public class DirectionsController {
    private final DirectionService directionService;
    private final MentorService mentorService;
    private final ObservableList<Direction> directionsObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Direction> directionsTable;

    @FXML
    private TableColumn<Direction, Integer> idColumn;

    @FXML
    private TableColumn<Direction, String> nameColumn;

    @FXML
    private TableColumn<Direction, Integer> mentorsCountColumn;

    @FXML
    private TextField searchField;

    public DirectionsController() {
        this.directionService = new DirectionService(new DirectionDao());
        this.mentorService = new MentorService(new MentorDao());
    }

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Настройка колонки количества наставников
        mentorsCountColumn.setCellValueFactory(cellData -> {
            Direction direction = cellData.getValue();
            List<Mentor> mentors = mentorService.findMentorsByDirection(direction.getId());
            return new javafx.beans.property.SimpleIntegerProperty(mentors.size()).asObject();
        });

        // Загрузка данных
        refreshDirections();
    }

    @FXML
    public void addDirection() {
        Dialog<Direction> dialog = createDirectionDialog(null);
        dialog.showAndWait().ifPresent(direction -> {
            directionService.createDirection(direction.getName());
            refreshDirections();
        });
    }

    @FXML
    public void editDirection() {
        Direction selectedDirection = directionsTable.getSelectionModel().getSelectedItem();
        if (selectedDirection == null) {
            showAlert("Выберите направление для редактирования");
            return;
        }

        Dialog<Direction> dialog = createDirectionDialog(selectedDirection);
        dialog.showAndWait().ifPresent(direction -> {
            directionService.updateDirection(direction);
            refreshDirections();
        });
    }

    @FXML
    public void deleteDirection() {
        Direction selectedDirection = directionsTable.getSelectionModel().getSelectedItem();
        if (selectedDirection == null) {
            showAlert("Выберите направление для удаления");
            return;
        }

        // Проверка, есть ли наставники с этим направлением
        List<Mentor> mentors = mentorService.findMentorsByDirection(selectedDirection.getId());
        if (!mentors.isEmpty()) {
            showAlert("Нельзя удалить направление, так как оно используется наставниками");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удаление направления");
        confirmation.setContentText("Вы уверены, что хотите удалить направление: " + selectedDirection.getName() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            directionService.deleteDirection(selectedDirection);
            refreshDirections();
        }
    }

    @FXML
    public void refreshDirections() {
        List<Direction> directions = directionService.getAllDirections();
        directionsObservableList.clear();
        directionsObservableList.addAll(directions);
        directionsTable.setItems(directionsObservableList);
    }

    @FXML
    public void searchDirections() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshDirections();
            return;
        }

        List<Direction> allDirections = directionService.getAllDirections();
        List<Direction> filteredDirections = allDirections.stream()
                .filter(direction -> 
                    direction.getName().toLowerCase().contains(searchText.toLowerCase())
                )
                .toList();

        directionsObservableList.clear();
        directionsObservableList.addAll(filteredDirections);
        directionsTable.setItems(directionsObservableList);
    }

    private Dialog<Direction> createDirectionDialog(Direction direction) {
        Dialog<Direction> dialog = new Dialog<>();
        dialog.setTitle(direction == null ? "Добавление направления" : "Редактирование направления");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Создание полей формы
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();

        if (direction != null) {
            nameField.setText(direction.getName());
        }

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Конвертация результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Direction resultDirection = direction != null ? direction : new Direction();
                resultDirection.setName(nameField.getText());
                return resultDirection;
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}