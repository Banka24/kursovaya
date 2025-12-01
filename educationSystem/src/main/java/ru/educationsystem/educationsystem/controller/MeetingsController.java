package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.educationsystem.educationsystem.model.Meeting;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.MeetingDao;
import ru.educationsystem.educationsystem.repository.PairDao;
import ru.educationsystem.educationsystem.service.MeetingService;
import ru.educationsystem.educationsystem.service.PairService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class MeetingsController implements Initializable {

    @FXML
    private ComboBox<Pair> pairComboBox;

    @FXML
    private DatePicker meetingDatePicker;

    @FXML
    private TextField topicField;

    @FXML
    private TableView<Meeting> meetingsTable;

    @FXML
    private TableColumn<Meeting, String> pairColumn;

    @FXML
    private TableColumn<Meeting, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Meeting, String> topicColumn;

    @FXML
    private TableColumn<Meeting, String> tasksColumn;

    @FXML
    private TableColumn<Meeting, Integer> ratingColumn;

    @FXML
    private TableColumn<Meeting, Void> actionsColumn;

    private final PairService pairService = new PairService(new PairDao());
    private final MeetingService meetingService = new MeetingService(new MeetingDao());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка колонок таблицы
        pairColumn.setCellValueFactory(cellData -> {
            Pair pair = cellData.getValue().getPair();
            return new javafx.beans.property.SimpleStringProperty(
                pair.getMentor().getLastName() + " " + pair.getMentor().getFirstName() + " - " + 
                pair.getMentee().getLastName() + " " + pair.getMentee().getFirstName()
            );
        });

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        tasksColumn.setCellValueFactory(new PropertyValueFactory<>("tasksDone"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("mentorRating")); // Используем mentorRating вместо rating

        // Добавление кнопок действий в таблицу
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Изменить");
            private final Button deleteButton = new Button("Удалить");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Meeting meeting = getTableView().getItems().get(getIndex());
                    editMeeting(meeting);
                });

                deleteButton.setOnAction(event -> {
                    Meeting meeting = getTableView().getItems().get(getIndex());
                    deleteMeeting(meeting);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        // Загрузка пар
        List<Pair> pairs = pairService.findAll();
        pairComboBox.setItems(FXCollections.observableArrayList(pairs));

        // Загрузка встреч
        loadMeetings();
    }

    private void loadMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetingsWithPair();
        meetingsTable.setItems(FXCollections.observableArrayList(meetings));
    }

    @FXML
    public void addMeeting() {
        Pair pair = pairComboBox.getValue();
        LocalDate date = meetingDatePicker.getValue();
        String topic = topicField.getText();

        if (pair == null || date == null || topic.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        Meeting meeting = new Meeting();
        meeting.setPair(pair);
        meeting.setDatetime(date.atStartOfDay());
        meeting.setTopic(topic);

        meetingService.save(meeting);

        // Очистка полей
        pairComboBox.setValue(null);
        meetingDatePicker.setValue(null);
        topicField.clear();

        // Обновление таблицы
        loadMeetings();

        showAlert("Успех", "Встреча успешно добавлена");
    }

    private void editMeeting(Meeting meeting) {
        // Здесь будет логика редактирования встречи
        showAlert("Информация", "Функция редактирования будет реализована в следующей версии");
    }

    private void deleteMeeting(Meeting meeting) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Вы уверены, что хотите удалить эту встречу?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            meetingService.delete(meeting);
            loadMeetings();
            showAlert("Успех", "Встреча успешно удалена");
        }
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) meetingsTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}