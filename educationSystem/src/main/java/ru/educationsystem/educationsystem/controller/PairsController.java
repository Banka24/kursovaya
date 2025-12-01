package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
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
    private TableColumn<Pair, LocalDate> endDateColumn;

    @FXML
    private TextField searchField;

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

        // Загрузка данных
        refreshPairs();
    }

    @FXML
    public void addPair() {
        Dialog<Pair> dialog = createPairDialog(null);
        dialog.showAndWait().ifPresent(pair -> {
            pairService.createPair(
                    pair.getMentor(),
                    pair.getMentee(),
                    pair.getStatus()
            );
            refreshPairs();
        });
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
            pairService.updatePair(pair);
            refreshPairs();
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
            pairService.deletePair(selectedPair);
            refreshPairs();
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
        mentorComboBox.setItems(FXCollections.observableArrayList(mentorService.getAllMentors()));

        ComboBox<Mentee> menteeComboBox = new ComboBox<>();
        menteeComboBox.setItems(FXCollections.observableArrayList(menteeService.getAllMentees()));

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        if (pair != null) {
            mentorComboBox.setValue(pair.getMentor());
            menteeComboBox.setValue(pair.getMentee());
            startDatePicker.setValue(pair.getStartDate());
        }

        grid.add(new Label("Наставник:"), 0, 0);
        grid.add(mentorComboBox, 1, 0);
        grid.add(new Label("Подопечный:"), 0, 1);
        grid.add(menteeComboBox, 1, 1);
        grid.add(new Label("Дата начала:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("Дата окончания:"), 0, 3);
        grid.add(endDatePicker, 1, 3);

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
                LocalDate endDate = endDatePicker.getValue();

                if (startDate == null) {
                    showAlert("Выберите дату начала");
                    return null;
                }

                if (endDate != null && endDate.isBefore(startDate)) {
                    showAlert("Дата окончания не может быть раньше даты начала");
                    return null;
                }

                Pair resultPair = pair != null ? pair : new Pair();
                resultPair.setMentor(selectedMentor);
                resultPair.setMentee(selectedMentee);
                resultPair.setStartDate(startDate);
                resultPair.setStatus("Активна");

                return resultPair;
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}