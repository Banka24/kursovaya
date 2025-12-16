package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MeetingsController {
    @FXML
    private TableView<Meeting> meetingsTable;

    @FXML
    private TableColumn<Meeting, String> idColumn, pairColumn, datetimeColumn, topicColumn, tasksColumn, mentorRatingColumn, menteeRatingColumn;

    private final MeetingDao meetingDao = new MeetingDao();
    private final PairDao pairDao = new PairDao();
    private final ObservableList<Meeting> list = FXCollections.observableArrayList();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        pairColumn.setCellValueFactory(c -> {
            Pair p = c.getValue().getPair();
            return new SimpleStringProperty(p != null ? p.getMentorFio() + " - " + p.getMenteeFio() : "");
        });
        datetimeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDatetime() != null ? c.getValue().getDatetime().format(dtf) : ""));
        topicColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTopic() != null ? c.getValue().getTopic() : ""));
        tasksColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTasksDone() != null ? c.getValue().getTasksDone() : ""));
        mentorRatingColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMentorRating() != null ? c.getValue().getMentorRating().toString() : ""));
        menteeRatingColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMenteeRating() != null ? c.getValue().getMenteeRating().toString() : ""));
        meetingsTable.setItems(list);
        load();
    }

    @FXML
    private void onAddClick() { showDialog(null); }

    @FXML
    private void onEditClick() { Meeting m = meetingsTable.getSelectionModel().getSelectedItem(); if (m != null) showDialog(m); }

    @FXML
    private void onDeleteClick() {
        Meeting m = meetingsTable.getSelectionModel().getSelectedItem();
        if (m != null && new Alert(Alert.AlertType.CONFIRMATION, "Удалить встречу?").showAndWait().get() == ButtonType.OK) {
            meetingDao.delete(m); load();
        }
    }

    @FXML
    private void onRefreshClick() { load(); }

    private void load() { list.clear(); list.addAll(meetingDao.findAll()); }

    private void showDialog(Meeting mt) {
        Dialog<Meeting> dlg = new Dialog<>();
        dlg.setTitle(mt == null ? "Добавить встречу" : "Редактировать встречу");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        ComboBox<Pair> pair = new ComboBox<>(FXCollections.observableArrayList(pairDao.findAll()));
        pair.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Pair p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getMentorFio() + " - " + p.getMenteeFio());
            }
        });
        pair.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Pair p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getMentorFio() + " - " + p.getMenteeFio());
            }
        });
        DatePicker date = new DatePicker();
        Spinner<Integer> hour = new Spinner<>(0, 23, 12);
        Spinner<Integer> minute = new Spinner<>(0, 59, 0);
        TextField topic = new TextField();
        TextArea tasks = new TextArea(); tasks.setPrefRowCount(2);
        Spinner<Integer> mentorRating = new Spinner<>(1, 5, 5);
        Spinner<Integer> menteeRating = new Spinner<>(1, 5, 5);
        if (mt != null) {
            pair.setValue(mt.getPair());
            if (mt.getDatetime() != null) {
                date.setValue(mt.getDatetime().toLocalDate());
                hour.getValueFactory().setValue(mt.getDatetime().getHour());
                minute.getValueFactory().setValue(mt.getDatetime().getMinute());
            }
            topic.setText(mt.getTopic()); tasks.setText(mt.getTasksDone());
            if (mt.getMentorRating() != null) mentorRating.getValueFactory().setValue(mt.getMentorRating());
            if (mt.getMenteeRating() != null) menteeRating.getValueFactory().setValue(mt.getMenteeRating());
        } else {
            date.setValue(java.time.LocalDate.now());
        }
        g.add(new Label("Пара:"), 0, 0); g.add(pair, 1, 0);
        g.add(new Label("Дата:"), 0, 1); g.add(date, 1, 1);
        g.add(new Label("Час:"), 0, 2); g.add(hour, 1, 2);
        g.add(new Label("Минуты:"), 0, 3); g.add(minute, 1, 3);
        g.add(new Label("Тема:"), 0, 4); g.add(topic, 1, 4);
        g.add(new Label("Выполненные задачи:"), 0, 5); g.add(tasks, 1, 5);
        g.add(new Label("Оценка наставника:"), 0, 6); g.add(mentorRating, 1, 6);
        g.add(new Label("Оценка подопечного:"), 0, 7); g.add(menteeRating, 1, 7);
        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(b -> {
            if (b == save) {
                Meeting m = mt != null ? mt : new Meeting();
                m.setPair(pair.getValue());
                m.setDatetime(date.getValue().atTime(hour.getValue(), minute.getValue()));
                m.setTopic(topic.getText()); m.setTasksDone(tasks.getText());
                m.setMentorRating(mentorRating.getValue()); m.setMenteeRating(menteeRating.getValue());
                return m;
            }
            return null;
        });
        Optional<Meeting> r = dlg.showAndWait();
        r.ifPresent(m -> {
            if (mt == null) {
                meetingDao.save(m);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена", ButtonType.OK).showAndWait();
            } else {
                meetingDao.update(m);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно обновлена", ButtonType.OK).showAndWait();
            }
            load();
        });
    }
}