package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.Mentor;
import ru.demo.demo2.model.Direction;
import ru.demo.demo2.repository.MentorDao;
import ru.demo.demo2.repository.DirectionDao;
import java.util.Optional;
import java.util.stream.Collectors;

public class MentorsController {
    @FXML private TableView<Mentor> mentorsTable;
    @FXML private TableColumn<Mentor, String> idColumn, fioColumn, emailColumn, specColumn, availableColumn, directionsColumn;
    private final MentorDao mentorDao = new MentorDao();
    private DirectionDao directionDao = new DirectionDao();
    private final ObservableList<Mentor> list = FXCollections.observableArrayList();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        fioColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFio()));
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        specColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecialization() != null ? c.getValue().getSpecialization() : ""));
        availableColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAvailable() ? "Да" : "Нет"));
        directionsColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDirections().stream().map(Direction::getName).collect(Collectors.joining(", "))));
        mentorsTable.setItems(list);
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { Mentor m = mentorsTable.getSelectionModel().getSelectedItem(); if (m != null) showDialog(m); }
    @FXML private void onDeleteClick() {
        Mentor m = mentorsTable.getSelectionModel().getSelectedItem();
        if (m != null && new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + m.getFio() + "?").showAndWait().get() == ButtonType.OK) {
            mentorDao.delete(m); load();
        }
    }
    @FXML private void onRefreshClick() { load(); }
    private void load() { list.clear(); list.addAll(mentorDao.findAll()); }

    private void showDialog(Mentor mt) {
        Dialog<Mentor> dlg = new Dialog<>();
        dlg.setTitle(mt == null ? "Добавить наставника" : "Редактировать наставника");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        TextField fio = new TextField(), email = new TextField(), spec = new TextField();
        CheckBox available = new CheckBox();
        if (mt != null) {
            fio.setText(mt.getFio()); email.setText(mt.getEmail());
            spec.setText(mt.getSpecialization()); available.setSelected(mt.getAvailable());
        } else {
            available.setSelected(true);
        }
        g.add(new Label("ФИО:"), 0, 0); g.add(fio, 1, 0);
        g.add(new Label("Email:"), 0, 1); g.add(email, 1, 1);
        g.add(new Label("Специализация:"), 0, 2); g.add(spec, 1, 2);
        g.add(new Label("Доступен:"), 0, 3); g.add(available, 1, 3);
        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(b -> {
            if (b == save) {
                Mentor m = mt != null ? mt : new Mentor();
                String[] fioParts = fio.getText().trim().split(" +", 3);
                if (fioParts.length > 0) m.setLastName(fioParts[0]);
                if (fioParts.length > 1) m.setFirstName(fioParts[1]);
                if (fioParts.length > 2) m.setMiddleName(fioParts[2]);
                m.setEmail(email.getText());
                m.setSpecialization(spec.getText()); m.setAvailable(available.isSelected());
                return m;
            }
            return null;
        });
        Optional<Mentor> r = dlg.showAndWait();
        r.ifPresent(m -> {
            if (mt == null) {
                mentorDao.save(m);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена", ButtonType.OK).showAndWait();
            } else {
                mentorDao.update(m);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно обновлена", ButtonType.OK).showAndWait();
            }
            load();
        });
    }

    public DirectionDao getDirectionDao() {
        return directionDao;
    }

    public void setDirectionDao(DirectionDao directionDao) {
        this.directionDao = directionDao;
    }
}
