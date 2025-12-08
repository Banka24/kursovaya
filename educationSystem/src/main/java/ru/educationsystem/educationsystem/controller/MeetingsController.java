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

    @FXML
    private ComboBox<Pair> pairComboBox;

    @FXML
    private DatePicker meetingDatePicker;

    @FXML
    private TextField topicField;

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

        // Инициализация ComboBox для пар
        List<Pair> pairs = pairService.getAllPairsWithMentorAndMentee();
        pairComboBox.setItems(FXCollections.observableArrayList(pairs));
        pairComboBox.setConverter(new javafx.util.StringConverter<Pair>() {
            @Override
            public String toString(Pair pair) {
                if (pair == null) return "";
                return pair.getMentor().getLastName() + " - " + pair.getMentee().getLastName();
            }
            @Override
            public Pair fromString(String string) {
                return null;
            }
        });

        // Загрузка встреч
        loadMeetings();
    }

    private void loadMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetingsWithPair();
        meetingsTable.setItems(FXCollections.observableArrayList(meetings));
    }

    @FXML
    public void addMeeting() {
        // Проверяем выбор из формы
        Pair selectedPair = pairComboBox.getValue();
        LocalDate selectedDate = meetingDatePicker.getValue();
        String topic = topicField != null ? topicField.getText() : null;
        
        if (selectedPair != null && selectedDate != null && topic != null && !topic.trim().isEmpty()) {
            // Если заполнены все поля - создаем встречу сразу
            try {
                Meeting meeting = new Meeting();
                meeting.setPair(selectedPair);
                meeting.setDatetime(selectedDate.atStartOfDay());
                meeting.setTopic(topic);
                meetingService.save(meeting);
                loadMeetings();
                // Очищаем поля
                pairComboBox.setValue(null);
                meetingDatePicker.setValue(null);
                if (topicField != null) topicField.clear();
                showAlert("Успех", "Встреча успешно добавлена");
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка при добавлении встречи.");
            }
        } else {
            // Иначе открываем диалог
            Dialog<Meeting> dialog = createMeetingDialog(null);
            dialog.showAndWait().ifPresent(meeting -> {
                try {
                    meetingService.save(meeting);
                    loadMeetings();
                    showAlert("Успех", "Встреча успешно добавлена");
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при добавлении встречи.");
                }
            });
        }
    }

    private void editMeeting(Meeting meeting) {
        Dialog<Meeting> dialog = createMeetingDialog(meeting);
        dialog.showAndWait().ifPresent(editedMeeting -> {
            try {
                meetingService.update(editedMeeting);
                loadMeetings();
                showAlert("Успех", "Встреча успешно обновлена");
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка при редактировании встречи.");
            }
        });
    }

    private Dialog<Meeting> createMeetingDialog(Meeting meeting) {
        Dialog<Meeting> dialog = new Dialog<>();
        dialog.setTitle(meeting == null ? "Добавление встречи" : "Редактирование встречи");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Pair> pairCombo = new ComboBox<>();
        List<Pair> pairsList = pairService.getAllPairsWithMentorAndMentee();
        pairCombo.setItems(FXCollections.observableArrayList(pairsList));
        pairCombo.setConverter(new javafx.util.StringConverter<Pair>() {
            @Override
            public String toString(Pair pair) {
                if (pair == null) return "";
                return pair.getMentor().getLastName() + " - " + pair.getMentee().getLastName();
            }
            @Override
            public Pair fromString(String string) {
                return null;
            }
        });

        DatePicker datePicker = new DatePicker();
        TextField topicField = new TextField();
        TextField tasksField = new TextField();
        TextField mentorRatingField = new TextField();
        TextField menteeRatingField = new TextField();

        if (meeting != null) {
            // Находим соответствующую пару по ID
            Integer pairId = meeting.getPair().getId();
            pairsList.stream()
                    .filter(p -> p.getId().equals(pairId))
                    .findFirst()
                    .ifPresent(pairCombo::setValue);
            
            if (meeting.getDatetime() != null) {
                datePicker.setValue(meeting.getDatetime().toLocalDate());
            }
            topicField.setText(meeting.getTopic());
            tasksField.setText(meeting.getTasksDone());
            if (meeting.getMentorRating() != null) {
                mentorRatingField.setText(meeting.getMentorRating().toString());
            }
            if (meeting.getMenteeRating() != null) {
                menteeRatingField.setText(meeting.getMenteeRating().toString());
            }
        }

        grid.add(new Label("Пара:"), 0, 0);
        grid.add(pairCombo, 1, 0);
        grid.add(new Label("Дата:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Тема:"), 0, 2);
        grid.add(topicField, 1, 2);
        grid.add(new Label("Выполненные задачи:"), 0, 3);
        grid.add(tasksField, 1, 3);
        grid.add(new Label("Рейтинг наставника (1-5):"), 0, 4);
        grid.add(mentorRatingField, 1, 4);
        grid.add(new Label("Рейтинг подопечного (1-5):"), 0, 5);
        grid.add(menteeRatingField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Pair selectedPair = pairCombo.getValue();
                LocalDate selectedDate = datePicker.getValue();
                String topic = topicField.getText();

                if (selectedPair == null || selectedDate == null || topic == null || topic.trim().isEmpty()) {
                    showAlert("Ошибка", "Заполните обязательные поля: Пара, Дата и Тема");
                    return null;
                }

                Meeting resultMeeting = meeting != null ? meeting : new Meeting();
                resultMeeting.setPair(selectedPair);
                resultMeeting.setDatetime(selectedDate.atStartOfDay());
                resultMeeting.setTopic(topic);
                resultMeeting.setTasksDone(tasksField.getText());

                try {
                    if (!mentorRatingField.getText().isEmpty()) {
                        short rating = Short.parseShort(mentorRatingField.getText());
                        if (rating >= 1 && rating <= 5) {
                            resultMeeting.setMentorRating(rating);
                        }
                    }
                    if (!menteeRatingField.getText().isEmpty()) {
                        short rating = Short.parseShort(menteeRatingField.getText());
                        if (rating >= 1 && rating <= 5) {
                            resultMeeting.setMenteeRating(rating);
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Рейтинг должен быть числом от 1 до 5");
                    return null;
                }

                return resultMeeting;
            }
            return null;
        });

        return dialog;
    }

    private void deleteMeeting(Meeting meeting) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Вы уверены, что хотите удалить эту встречу?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                meetingService.delete(meeting);
                loadMeetings();
                showAlert("Успех", "Встреча успешно удалена");
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка при удалении встречи.");
            }
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