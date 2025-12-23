package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.Direction;
import ru.demo.demo2.repository.DirectionDao;
import java.util.Optional;

public class DirectionsController {
    @FXML private TableView<Direction> directionsTable;
    @FXML private TableColumn<Direction, String> idColumn, nameColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    private DirectionDao directionDao = new DirectionDao();
    private ObservableList<Direction> list = FXCollections.observableArrayList();
    private FilteredList<Direction> filteredList;

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        
        filteredList = new FilteredList<>(list, p -> true);
        directionsTable.setItems(filteredList);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(direction -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    return direction.getName().toLowerCase().contains(lowerCaseFilter) ||
                           direction.getId().toString().contains(lowerCaseFilter);
                });
                updateStatusLabel();
            });
        }
        
        directionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateStatusLabel());
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { 
        Direction d = directionsTable.getSelectionModel().getSelectedItem(); 
        if (d != null) showDialog(d); 
        else showError("Выберите запись для редактирования");
    }
    @FXML private void onDeleteClick() {
        Direction d = directionsTable.getSelectionModel().getSelectedItem();
        if (d == null) {
            showError("Выберите запись для удаления");
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + d.getName() + "?").showAndWait().get() == ButtonType.OK) {
            try {
                directionDao.delete(d); 
                load();
                showInfo("Запись успешно удалена");
            } catch (Exception e) {
                showError("Ошибка при удалении: " + e.getMessage());
            }
        }
    }
    @FXML private void onRefreshClick() { load(); }
    
    private void load() { 
        try {
            list.clear(); 
            list.addAll(directionDao.findAll());
            updateStatusLabel();
        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void updateStatusLabel() {
        if (statusLabel != null) {
            int total = filteredList.size();
            int selected = directionsTable.getSelectionModel().getSelectedItems().size();
            statusLabel.setText("Всего записей: " + total + " | Выбрано: " + selected);
        }
    }

    private void showDialog(Direction dt) {
        Dialog<Direction> dlg = new Dialog<>();
        dlg.setTitle(dt == null ? "Добавить направление" : "Редактировать направление");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        TextField name = new TextField();
        if (dt != null) { name.setText(dt.getName()); }
        g.add(new Label("Название:"), 0, 0); g.add(name, 1, 0);
        dlg.getDialogPane().setContent(g);
        
        dlg.setResultConverter(b -> {
            if (b == save) {
                if (!validateDirection(name.getText())) {
                    return null;
                }
                Direction d = dt != null ? dt : new Direction();
                d.setName(name.getText().trim());
                return d;
            }
            return null;
        });
        Optional<Direction> r = dlg.showAndWait();
        r.ifPresent(d -> {
            try {
                if (dt == null) {
                    directionDao.save(d);
                    showInfo("Запись успешно добавлена");
                } else {
                    directionDao.update(d);
                    showInfo("Запись успешно обновлена");
                }
                load();
            } catch (Exception e) {
                showError("Ошибка при сохранении: " + e.getMessage());
            }
        });
    }
    
    private boolean validateDirection(String name) {
        if (name == null || name.trim().isEmpty()) {
            showError("Название не может быть пустым");
            return false;
        }
        if (name.trim().length() < 3) {
            showError("Название должно содержать минимум 3 символа");
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}