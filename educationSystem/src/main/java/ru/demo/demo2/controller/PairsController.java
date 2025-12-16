package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import ru.demo.demo2.service.PairMatchingService;
import java.util.Optional;

public class PairsController {
    @FXML
    private TableView<Pair> pairsTable;

    @FXML
    private TableColumn<Pair, String> idColumn, mentorColumn, menteeColumn, startDateColumn, statusColumn;

    @FXML
    private Label resultLabel;

    private final PairDao pairDao = new PairDao();
    private final MentorDao mentorDao = new MentorDao();
    private final MenteeDao menteeDao = new MenteeDao();
    private PairMatchingService pairService = new PairMatchingService();
    private final ObservableList<Pair> list = FXCollections.observableArrayList();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        mentorColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMentorFio()));
        menteeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMenteeFio()));
        startDateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(getStatusText(c.getValue().getStatus())));
        pairsTable.setItems(list);
        load();
    }

    private String getStatusText(String status) {
        if ("active".equals(status)) return "Активна";
        if ("paused".equals(status)) return "Приостановлена";
        if ("completed".equals(status)) return "Завершена";
        return status;
    }

    @FXML
    private void onAddClick() { showDialog(null); }

    @FXML
    private void onEditClick() { Pair p = pairsTable.getSelectionModel().getSelectedItem(); if (p != null) showDialog(p); }

    @FXML
    private void onDeleteClick() {
        Pair p = pairsTable.getSelectionModel().getSelectedItem();
        if (p != null && new Alert(Alert.AlertType.CONFIRMATION, "Удалить пару?").showAndWait().get() == ButtonType.OK) {
            pairDao.delete(p); load();
        }
    }

    @FXML
    private void onRefreshClick() { load(); }

    private void load() {
        list.clear(); list.addAll(pairDao.findAll());
        long active = list.stream().filter(p -> "active".equals(p.getStatus())).count();
        resultLabel.setText("Всего пар: " + list.size() + ", активных: " + active);
    }

    private void showDialog(Pair pt) {
        Dialog<Pair> dlg = new Dialog<>();
        dlg.setTitle(pt == null ? "Создать пару" : "Редактировать пару");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        ComboBox<Mentor> mentor = new ComboBox<>(FXCollections.observableArrayList(mentorDao.findAll()));
        ComboBox<Mentee> mentee = new ComboBox<>(FXCollections.observableArrayList(menteeDao.findAll()));
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList("active", "paused", "completed"));
        DatePicker startDate = new DatePicker();
        if (pt != null) {
            mentor.setValue(pt.getMentor()); mentee.setValue(pt.getMentee());
            status.setValue(pt.getStatus()); startDate.setValue(pt.getStartDate());
        } else {
            status.setValue("active"); startDate.setValue(java.time.LocalDate.now());
        }
        g.add(new Label("Наставник:"), 0, 0); g.add(mentor, 1, 0);
        g.add(new Label("Подопечный:"), 0, 1); g.add(mentee, 1, 1);
        g.add(new Label("Дата начала:"), 0, 2); g.add(startDate, 1, 2);
        g.add(new Label("Статус:"), 0, 3); g.add(status, 1, 3);
        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(b -> {
            if (b == save) {
                Pair p = pt != null ? pt : new Pair();
                p.setMentor(mentor.getValue()); p.setMentee(mentee.getValue());
                p.setStartDate(startDate.getValue()); p.setStatus(status.getValue());
                return p;
            }
            return null;
        });
        Optional<Pair> r = dlg.showAndWait();
        r.ifPresent(p -> {
            if (pt == null) {
                pairDao.save(p);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена", ButtonType.OK).showAndWait();
            } else {
                pairDao.update(p);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно обновлена", ButtonType.OK).showAndWait();
            }
            load();
        });
    }

    public PairMatchingService getPairService() {
        return pairService;
    }

    public void setPairService(PairMatchingService pairService) {
        this.pairService = pairService;
    }
}