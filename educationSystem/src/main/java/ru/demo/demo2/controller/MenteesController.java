package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.Mentee;
import ru.demo.demo2.repository.MenteeDao;
import java.util.Optional;

public class MenteesController {
    @FXML private TableView<Mentee> menteesTable;
    @FXML private TableColumn<Mentee, String> idColumn, fioColumn, emailColumn, goalsColumn, levelColumn;
    private final MenteeDao menteeDao = new MenteeDao();
    private final ObservableList<Mentee> list = FXCollections.observableArrayList();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        fioColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFio()));
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        goalsColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGoals() != null ? c.getValue().getGoals() : ""));
        levelColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurrentLevel() != null ? c.getValue().getCurrentLevel().toString() : ""));
        menteesTable.setItems(list);
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { Mentee m = menteesTable.getSelectionModel().getSelectedItem(); if (m != null) showDialog(m); }
    @FXML private void onDeleteClick() {
        Mentee m = menteesTable.getSelectionModel().getSelectedItem();
        if (m != null && new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + m.getFio() + "?").showAndWait().get() == ButtonType.OK) {
            menteeDao.delete(m); load();
        }
    }
    @FXML private void onRefreshClick() { load(); }
    private void load() { list.clear(); list.addAll(menteeDao.findAll()); }

    private void showDialog(Mentee mt) {
        Dialog<Mentee> dlg = new Dialog<>();
        dlg.setTitle(mt == null ? "Добавить подопечного" : "Редактировать подопечного");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        TextField fio = new TextField(), email = new TextField();
        TextArea goals = new TextArea(); goals.setPrefRowCount(3);
        Spinner<Integer> level = new Spinner<>(1, 5, mt != null && mt.getCurrentLevel() != null ? mt.getCurrentLevel() : 1);
        if (mt != null) {
            fio.setText(mt.getFio()); email.setText(mt.getEmail());
            goals.setText(mt.getGoals());
        }
        g.add(new Label("ФИО:"), 0, 0); g.add(fio, 1, 0);
        g.add(new Label("Email:"), 0, 1); g.add(email, 1, 1);
        g.add(new Label("Цели:"), 0, 2); g.add(goals, 1, 2);
        g.add(new Label("Уровень (1-5):"), 0, 3); g.add(level, 1, 3);
        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(b -> {
            if (b == save) {
                Mentee m = mt != null ? mt : new Mentee();
                String[] fioParts = fio.getText().trim().split(" +", 3);
                if (fioParts.length > 0) m.setLastName(fioParts[0]);
                if (fioParts.length > 1) m.setFirstName(fioParts[1]);
                if (fioParts.length > 2) m.setMiddleName(fioParts[2]);
                m.setEmail(email.getText());
                m.setGoals(goals.getText()); m.setCurrentLevel(level.getValue());
                return m;
            }
            return null;
        });
        Optional<Mentee> r = dlg.showAndWait();
        r.ifPresent(m -> {
            if (mt == null) {
                menteeDao.save(m);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена", ButtonType.OK).showAndWait();
            } else {
                menteeDao.update(m);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно обновлена", ButtonType.OK).showAndWait();
            }
            load();
        });
    }
}