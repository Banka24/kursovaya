package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.MentorDao;
import ru.educationsystem.educationsystem.service.MentorService;

import java.util.List;
import java.util.stream.Collectors;

public class MentorsController {
    private final MentorService mentorService;
    private final ObservableList<Mentor> mentorsObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Mentor> mentorsTable;

    @FXML
    private TableColumn<Mentor, Integer> idColumn;

    @FXML
    private TableColumn<Mentor, String> lastNameColumn;

    @FXML
    private TableColumn<Mentor, String> firstNameColumn;

    @FXML
    private TableColumn<Mentor, String> middleNameColumn;

    @FXML
    private TableColumn<Mentor, String> emailColumn;

    @FXML
    private TableColumn<Mentor, String> specializationColumn;

    @FXML
    private TableColumn<Mentor, Boolean> availableColumn;

    @FXML
    private TableColumn<Mentor, String> directionsColumn;

    @FXML
    private TextField searchField;

    public MentorsController() {
        this.mentorService = new MentorService(new MentorDao());
    }

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Настройка колонки направлений
        directionsColumn.setCellValueFactory(cellData -> {
            Mentor mentor = cellData.getValue();
            String directions = mentor.getDirections().stream()
                    .map(Direction::getName)
                    .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(directions);
        });

        // Загрузка данных
        refreshMentors();
    }

    @FXML
    public void addMentor() {
        Dialog<Mentor> dialog = createMentorDialog(null);
        dialog.showAndWait().ifPresent(mentor -> {
            mentorService.createMentor(
                    mentor.getLastName(),
                    mentor.getFirstName(),
                    mentor.getMiddleName(),
                    mentor.getEmail(),
                    mentor.getSpecialization(),
                    mentor.getAvailable()
            );
            refreshMentors();
        });
    }

    @FXML
    public void editMentor() {
        Mentor selectedMentor = mentorsTable.getSelectionModel().getSelectedItem();
        if (selectedMentor == null) {
            showAlert("Выберите наставника для редактирования");
            return;
        }

        Dialog<Mentor> dialog = createMentorDialog(selectedMentor);
        dialog.showAndWait().ifPresent(mentor -> {
            mentorService.updateMentor(mentor);
            refreshMentors();
        });
    }

    @FXML
    public void deleteMentor() {
        Mentor selectedMentor = mentorsTable.getSelectionModel().getSelectedItem();
        if (selectedMentor == null) {
            showAlert("Выберите наставника для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удаление наставника");
        confirmation.setContentText("Вы уверены, что хотите удалить наставника: " + 
                selectedMentor.getLastName() + " " + selectedMentor.getFirstName() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            mentorService.deleteMentor(selectedMentor);
            refreshMentors();
        }
    }

    @FXML
    public void refreshMentors() {
        List<Mentor> mentors = mentorService.getAllMentorsWithDirections();
        mentorsObservableList.clear();
        mentorsObservableList.addAll(mentors);
        mentorsTable.setItems(mentorsObservableList);
    }

    @FXML
    public void searchMentors() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshMentors();
            return;
        }

        List<Mentor> allMentors = mentorService.getAllMentorsWithDirections();
        List<Mentor> filteredMentors = allMentors.stream()
                .filter(mentor -> 
                    mentor.getLastName().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentor.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentor.getMiddleName() != null && mentor.getMiddleName().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentor.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentor.getSpecialization() != null && mentor.getSpecialization().toLowerCase().contains(searchText.toLowerCase())
                )
                .toList();

        mentorsObservableList.clear();
        mentorsObservableList.addAll(filteredMentors);
        mentorsTable.setItems(mentorsObservableList);
    }

    private Dialog<Mentor> createMentorDialog(Mentor mentor) {
        Dialog<Mentor> dialog = new Dialog<>();
        dialog.setTitle(mentor == null ? "Добавление наставника" : "Редактирование наставника");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Создание полей формы
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField emailField = new TextField();
        TextField specializationField = new TextField();
        CheckBox availableCheckBox = new CheckBox();

        if (mentor != null) {
            lastNameField.setText(mentor.getLastName());
            firstNameField.setText(mentor.getFirstName());
            middleNameField.setText(mentor.getMiddleName());
            emailField.setText(mentor.getEmail());
            specializationField.setText(mentor.getSpecialization());
            availableCheckBox.setSelected(mentor.getAvailable());
        }

        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Специализация:"), 0, 4);
        grid.add(specializationField, 1, 4);
        grid.add(new Label("Доступен:"), 0, 5);
        grid.add(availableCheckBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Конвертация результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Mentor resultMentor = mentor != null ? mentor : new Mentor();
                resultMentor.setLastName(lastNameField.getText());
                resultMentor.setFirstName(firstNameField.getText());
                resultMentor.setMiddleName(middleNameField.getText());
                resultMentor.setEmail(emailField.getText());
                resultMentor.setSpecialization(specializationField.getText());
                resultMentor.setAvailable(availableCheckBox.isSelected());
                return resultMentor;
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