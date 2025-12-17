package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.Direction;
import ru.demo.demo2.repository.DirectionDao;
import java.util.Optional;

public class DirectionsController {
    @FXML
    private TableView<Direction> directionsTable;

    @FXML
    private TableColumn<Direction, String> idColumn, nameColumn;

    private final DirectionDao directionDao = new DirectionDao();
    private final ObservableList<Direction> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        directionsTable.setItems(list);
        load();
    }

    @FXML
    private void onAddClick() {
        showDialog(null);
    }

    @FXML
    private void onEditClick() {
        Direction d = directionsTable.getSelectionModel().getSelectedItem(); if (d != null) showDialog(d);
    }

    @FXML
    private void onDeleteClick() {
        Direction d = directionsTable.getSelectionModel().getSelectedItem();
        if (d != null && new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + d.getName() + "?")
                .showAndWait().get() == ButtonType.OK) {
            directionDao.delete(d); load();
        }
    }

    @FXML
    private void onRefreshClick() { load(); }

    private void load() { list.clear(); list.addAll(directionDao.findAll()); }

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
                Direction d = dt != null ? dt : new Direction();
                d.setName(name.getText());
                return d;
            }
            return null;
        });
        Optional<Direction> r = dlg.showAndWait();
        r.ifPresent(d -> {
            if (dt == null) {
                directionDao.save(d);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена", ButtonType.OK).showAndWait();
            } else {
                directionDao.update(d);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно обновлена", ButtonType.OK).showAndWait();
            }
            load();
        });
    }
}