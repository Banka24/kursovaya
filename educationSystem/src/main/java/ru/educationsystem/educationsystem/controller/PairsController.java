package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.MenteeDao;
import ru.educationsystem.educationsystem.repository.MentorDao;
import ru.educationsystem.educationsystem.repository.PairDao;
import ru.educationsystem.educationsystem.service.MenteeService;
import ru.educationsystem.educationsystem.service.MentorService;
import ru.educationsystem.educationsystem.service.PairService;

import java.time.LocalDate;
import java.util.List;

public class PairsController {
    private final PairService pairService;
    private final MentorService mentorService;
    private final MenteeService menteeService;
    private final ObservableList<Pair> pairsObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Pair> pairsTable;

    @FXML
    private TableColumn<Pair, Integer> idColumn;

    @FXML
    private TableColumn<Pair, String> mentorColumn;

    @FXML
    private TableColumn<Pair, String> menteeColumn;

    @FXML
    private TableColumn<Pair, LocalDate> startDateColumn;

    @FXML
    private TableColumn<Pair, String> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Mentor> mentorComboBox;

    @FXML
    private ComboBox<Mentee> menteeComboBox;

    public PairsController() {
        this.pairService = new PairService(new PairDao());
        this.mentorService = new MentorService(new MentorDao());
        this.menteeService = new MenteeService(new MenteeDao());
    }

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Настройка колонки наставника
        mentorColumn.setCellValueFactory(cellData -> {
            Pair pair = cellData.getValue();
            Mentor mentor = pair.getMentor();
            return new javafx.beans.property.SimpleStringProperty(
                    mentor.getLastName() + " " + mentor.getFirstName() + 
                    (mentor.getMiddleName() != null ? " " + mentor.getMiddleName() : "")
            );
        });

        // Настройка колонки подопечного
        menteeColumn.setCellValueFactory(cellData -> {
            Pair pair = cellData.getValue();
            Mentee mentee = pair.getMentee();
            return new javafx.beans.property.SimpleStringProperty(
                    mentee.getLastName() + " " + mentee.getFirstName() + 
                    (mentee.getMiddleName() != null ? " " + mentee.getMiddleName() : "")
            );
        });

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Инициализация ComboBox для наставников
        List<Mentor> mentors = mentorService.getAllMentors();
        mentorComboBox.setItems(FXCollections.observableArrayList(mentors));
        mentorComboBox.setConverter(new javafx.util.StringConverter<Mentor>() {
            @Override
            public String toString(Mentor mentor) {
                return mentor == null ? "" : mentor.getLastName() + " " + mentor.getFirstName();
            }
            @Override
            public Mentor fromString(String string) {
                return null;
            }
        });

        // Инициализация ComboBox для подопечных
        List<Mentee> mentees = menteeService.getAllMentees();
        menteeComboBox.setItems(FXCollections.observableArrayList(mentees));
        menteeComboBox.setConverter(new javafx.util.StringConverter<Mentee>() {
            @Override
            public String toString(Mentee mentee) {
                return mentee == null ? "" : mentee.getLastName() + " " + mentee.getFirstName();
            }
            @Override
            public Mentee fromString(String string) {
                return null;
            }
        });

        // Загрузка данных
        refreshPairs();
    }

    @FXML
    public void addPair() {
        // Проверяем выбор из ComboBox на форме
        Mentor selectedMentor = mentorComboBox.getValue();
        Mentee selectedMentee = menteeComboBox.getValue();
        
        if (selectedMentor != null && selectedMentee != null) {
            // Если выбраны оба - создаем пару сразу
            try {
                pairService.createPair(selectedMentor, selectedMentee, "active");
                refreshPairs();
                mentorComboBox.setValue(null);
                menteeComboBox.setValue(null);
            } catch (Exception e) {
                showAlert("Ошибка при добавлении пары. Возможно, такая пара уже существует.");
            }
        } else {
            // Иначе открываем диалог
            Dialog<Pair> dialog = createPairDialog(null);
            dialog.showAndWait().ifPresent(pair -> {
                try {
                    pairService.createPair(
                            pair.getMentor(),
                            pair.getMentee(),
                            pair.getStatus()
                    );
                    refreshPairs();
                } catch (Exception e) {
                    showAlert("Ошибка при добавлении пары. Возможно, такая пара уже существует.");
                }
            });
        }
    }

    @FXML
    public void editPair() {
        Pair selectedPair = pairsTable.getSelectionModel().getSelectedItem();
        if (selectedPair == null) {
            showAlert("Выберите пару для редактирования");
            return;
        }

        Dialog<Pair> dialog = createPairDialog(selectedPair);
        dialog.showAndWait().ifPresent(pair -> {
            try {
                pairService.updatePair(pair);
                refreshPairs();
            } catch (Exception e) {
                showAlert("Ошибка при редактировании пары.");
            }
        });
    }

    @FXML
    public void deletePair() {
        Pair selectedPair = pairsTable.getSelectionModel().getSelectedItem();
        if (selectedPair == null) {
            showAlert("Выберите пару для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удаление пары");
        confirmation.setContentText("Вы уверены, что хотите удалить выбранную пару?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                pairService.deletePair(selectedPair);
                refreshPairs();
            } catch (Exception e) {
                showAlert("Невозможно удалить пару. Возможно, существуют связанные встречи или планы развития.");
            }
        }
    }

    @FXML
    public void refreshPairs() {
        List<Pair> pairs = pairService.getAllPairsWithMentorAndMentee();
        pairsObservableList.clear();
        pairsObservableList.addAll(pairs);
        pairsTable.setItems(pairsObservableList);
    }

    @FXML
    public void searchPairs() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshPairs();
            return;
        }

        List<Pair> allPairs = pairService.getAllPairsWithMentorAndMentee();
        List<Pair> filteredPairs = allPairs.stream()
                .filter(pair -> {
                    Mentor mentor = pair.getMentor();
                    Mentee mentee = pair.getMentee();

                    String mentorFullName = mentor.getLastName() + " " + mentor.getFirstName() + 
                            (mentor.getMiddleName() != null ? " " + mentor.getMiddleName() : "");
                    String menteeFullName = mentee.getLastName() + " " + mentee.getFirstName() + 
                            (mentee.getMiddleName() != null ? " " + mentee.getMiddleName() : "");

                    return mentorFullName.toLowerCase().contains(searchText.toLowerCase()) ||
                           menteeFullName.toLowerCase().contains(searchText.toLowerCase());
                })
                .toList();

        pairsObservableList.clear();
        pairsObservableList.addAll(filteredPairs);
        pairsTable.setItems(pairsObservableList);
    }

    private Dialog<Pair> createPairDialog(Pair pair) {
        Dialog<Pair> dialog = new Dialog<>();
        dialog.setTitle(pair == null ? "Добавление пары" : "Редактирование пары");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Создание полей формы
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Mentor> mentorComboBox = new ComboBox<>();
        List<Mentor> mentorsList = mentorService.getAllMentors();
        mentorComboBox.setItems(FXCollections.observableArrayList(mentorsList));
        mentorComboBox.setConverter(new javafx.util.StringConverter<Mentor>() {
            @Override
            public String toString(Mentor mentor) {
                return mentor == null ? "" : mentor.getLastName() + " " + mentor.getFirstName();
            }
            @Override
            public Mentor fromString(String string) {
                return null;
            }
        });

        ComboBox<Mentee> menteeComboBox = new ComboBox<>();
        List<Mentee> menteesList = menteeService.getAllMentees();
        menteeComboBox.setItems(FXCollections.observableArrayList(menteesList));
        menteeComboBox.setConverter(new javafx.util.StringConverter<Mentee>() {
            @Override
            public String toString(Mentee mentee) {
                return mentee == null ? "" : mentee.getLastName() + " " + mentee.getFirstName();
            }
            @Override
            public Mentee fromString(String string) {
                return null;
            }
        });

        DatePicker startDatePicker = new DatePicker();
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList("active", "paused", "completed"));

        if (pair != null) {
            // Находим соответствующие элементы по ID
            Integer mentorId = pair.getMentor().getId();
            Integer menteeId = pair.getMentee().getId();
            
            mentorsList.stream()
                    .filter(m -> m.getId().equals(mentorId))
                    .findFirst()
                    .ifPresent(mentorComboBox::setValue);
            
            menteesList.stream()
                    .filter(m -> m.getId().equals(menteeId))
                    .findFirst()
                    .ifPresent(menteeComboBox::setValue);
            
            startDatePicker.setValue(pair.getStartDate());
            statusComboBox.setValue(pair.getStatus());
        } else {
            statusComboBox.setValue("active");
        }

        grid.add(new Label("Наставник:"), 0, 0);
        grid.add(mentorComboBox, 1, 0);
        grid.add(new Label("Подопечный:"), 0, 1);
        grid.add(menteeComboBox, 1, 1);
        grid.add(new Label("Дата начала:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("Статус:"), 0, 3);
        grid.add(statusComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Mentor selectedMentor = mentorComboBox.getValue();
                Mentee selectedMentee = menteeComboBox.getValue();

                if (selectedMentor == null) {
                    showAlert("Выберите наставника");
                    return null;
                }

                if (selectedMentee == null) {
                    showAlert("Выберите подопечного");
                    return null;
                }

                LocalDate startDate = startDatePicker.getValue();
                String status = statusComboBox.getValue();

                if (startDate == null) {
                    startDate = LocalDate.now();
                }

                if (status == null || status.isEmpty()) {
                    showAlert("Выберите статус");
                    return null;
                }

                Pair resultPair = pair != null ? pair : new Pair();
                resultPair.setMentor(selectedMentor);
                resultPair.setMentee(selectedMentee);
                resultPair.setStartDate(startDate);
                resultPair.setStatus(status);

                return resultPair;
            }
            return null;
        });

        return dialog;
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) pairsTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}