package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MeetingsController {
    @FXML private TableView<Meeting> meetingsTable;
    @FXML private TableColumn<Meeting, String> idColumn, pairColumn, datetimeColumn, topicColumn, tasksColumn, mentorRatingColumn, menteeRatingColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    private MeetingDao meetingDao = new MeetingDao();
    private PairDao pairDao = new PairDao();
    private ObservableList<Meeting> list = FXCollections.observableArrayList();
    private FilteredList<Meeting> filteredList;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML public void initialize() {
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
        
        filteredList = new FilteredList<>(list, p -> true);
        meetingsTable.setItems(filteredList);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(meeting -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    Pair p = meeting.getPair();
                    String pairText = p != null ? (p.getMentorFio() + " " + p.getMenteeFio()) : "";
                    return pairText.toLowerCase().contains(lowerCaseFilter) ||
                           (meeting.getTopic() != null && meeting.getTopic().toLowerCase().contains(lowerCaseFilter)) ||
                           meeting.getId().toString().contains(lowerCaseFilter);
                });
                updateStatusLabel();
            });
        }
        
        meetingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateStatusLabel());
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { 
        Meeting m = meetingsTable.getSelectionModel().getSelectedItem(); 
        if (m != null) showDialog(m); 
        else showError("Выберите запись для редактирования");
    }
    @FXML private void onDeleteClick() {
        Meeting m = meetingsTable.getSelectionModel().getSelectedItem();
        if (m == null) {
            showError("Выберите запись для удаления");
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить встречу?").showAndWait().get() == ButtonType.OK) {
            try {
                meetingDao.delete(m); 
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
            list.addAll(meetingDao.findAll());
            updateStatusLabel();
        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void updateStatusLabel() {
        if (statusLabel != null) {
            int total = filteredList.size();
            int selected = meetingsTable.getSelectionModel().getSelectedItems().size();
            statusLabel.setText("Всего записей: " + total + " | Выбрано: " + selected);
        }
    }

    private void showDialog(Meeting mt) {
        Dialog<Meeting> dlg = new Dialog<>();
        dlg.setTitle(mt == null ? "Добавить встречу" : "Редактировать встречу");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        ComboBox<Pair> pair = new ComboBox<>(FXCollections.observableArrayList(pairDao.findAll()));
        pair.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Pair p, boolean empty) {
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
                if (!validateMeeting(pair.getValue(), date.getValue(), topic.getText())) {
                    return null;
                }
                Meeting m = mt != null ? mt : new Meeting();
                m.setPair(pair.getValue());
                m.setDatetime(date.getValue().atTime(hour.getValue(), minute.getValue()));
                m.setTopic(topic.getText().trim()); 
                m.setTasksDone(tasks.getText().trim());
                m.setMentorRating(mentorRating.getValue()); 
                m.setMenteeRating(menteeRating.getValue());
                return m;
            }
            return null;
        });
        Optional<Meeting> r = dlg.showAndWait();
        r.ifPresent(m -> {
            try {
                if (mt == null) {
                    meetingDao.save(m);
                    showInfo("Запись успешно добавлена");
                } else {
                    meetingDao.update(m);
                    showInfo("Запись успешно обновлена");
                }
                load();
            } catch (Exception e) {
                showError("Ошибка при сохранении: " + e.getMessage());
            }
        });
    }
    
    private boolean validateMeeting(Pair pair, java.time.LocalDate date, String topic) {
        if (pair == null) {
            showError("Выберите пару");
            return false;
        }
        if (date == null) {
            showError("Выберите дату");
            return false;
        }
        if (date.isAfter(java.time.LocalDate.now())) {
            showError("Дата встречи не может быть в будущем");
            return false;
        }
        if (topic == null || topic.trim().isEmpty()) {
            showError("Тема не может быть пустой");
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