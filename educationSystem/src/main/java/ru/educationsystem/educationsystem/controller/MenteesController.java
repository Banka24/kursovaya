package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.repository.MenteeDao;
import ru.educationsystem.educationsystem.service.MenteeService;

import java.util.List;
import java.util.Optional;

public class MenteesController {
    private final MenteeService menteeService;
    private final ObservableList<Mentee> menteesObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Mentee> menteesTable;

    @FXML
    private TableColumn<Mentee, Integer> idColumn;

    @FXML
    private TableColumn<Mentee, String> lastNameColumn;

    @FXML
    private TableColumn<Mentee, String> firstNameColumn;

    @FXML
    private TableColumn<Mentee, String> middleNameColumn;

    @FXML
    private TableColumn<Mentee, String> emailColumn;

    @FXML
    private TableColumn<Mentee, String> goalsColumn;

    @FXML
    private TableColumn<Mentee, Short> currentLevelColumn;

    @FXML
    private TableColumn<Mentee, Boolean> hasMentorColumn;

    @FXML
    private TextField searchField;

    public MenteesController() {
        this.menteeService = new MenteeService(new MenteeDao());
    }

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        goalsColumn.setCellValueFactory(new PropertyValueFactory<>("goals"));
        currentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("currentLevel"));

        // Настройка колонки наличия наставника
        hasMentorColumn.setCellValueFactory(cellData -> {
            Mentee mentee = cellData.getValue();
            boolean hasMentor = !mentee.getPairs().isEmpty();
            return new javafx.beans.property.SimpleBooleanProperty(hasMentor);
        });

        // Загрузка данных
        refreshMentees();
    }

    @FXML
    public void addMentee() {
        Dialog<Mentee> dialog = createMenteeDialog(null);
        dialog.showAndWait().ifPresent(mentee -> {
            menteeService.createMentee(
                    mentee.getLastName(),
                    mentee.getFirstName(),
                    mentee.getMiddleName(),
                    mentee.getEmail(),
                    mentee.getGoals(),
                    mentee.getCurrentLevel()
            );
            refreshMentees();
        });
    }

    @FXML
    public void editMentee() {
        Mentee selectedMentee = menteesTable.getSelectionModel().getSelectedItem();
        if (selectedMentee == null) {
            showAlert("Выберите подопечного для редактирования");
            return;
        }

        Dialog<Mentee> dialog = createMenteeDialog(selectedMentee);
        dialog.showAndWait().ifPresent(mentee -> {
            menteeService.updateMentee(mentee);
            refreshMentees();
        });
    }

    @FXML
    public void deleteMentee() {
        Mentee selectedMentee = menteesTable.getSelectionModel().getSelectedItem();
        if (selectedMentee == null) {
            showAlert("Выберите подопечного для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удаление подопечного");
        confirmation.setContentText("Вы уверены, что хотите удалить подопечного: " + 
                selectedMentee.getLastName() + " " + selectedMentee.getFirstName() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            menteeService.deleteMentee(selectedMentee);
            refreshMentees();
        }
    }

    @FXML
    public void refreshMentees() {
        List<Mentee> mentees = menteeService.getAllMenteesWithPairs();
        menteesObservableList.clear();
        menteesObservableList.addAll(mentees);
        menteesTable.setItems(menteesObservableList);
    }

    @FXML
    public void searchMentees() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshMentees();
            return;
        }

        List<Mentee> allMentees = menteeService.getAllMenteesWithPairs();
        List<Mentee> filteredMentees = allMentees.stream()
                .filter(mentee -> 
                    mentee.getLastName().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentee.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentee.getMiddleName() != null && mentee.getMiddleName().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentee.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                    mentee.getGoals() != null && mentee.getGoals().toLowerCase().contains(searchText.toLowerCase())
                )
                .toList();

        menteesObservableList.clear();
        menteesObservableList.addAll(filteredMentees);
        menteesTable.setItems(menteesObservableList);
    }

    private Dialog<Mentee> createMenteeDialog(Mentee mentee) {
        Dialog<Mentee> dialog = new Dialog<>();
        dialog.setTitle(mentee == null ? "Добавление подопечного" : "Редактирование подопечного");
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
        TextField goalsField = new TextField();
        TextField currentLevelField = new TextField();

        if (mentee != null) {
            lastNameField.setText(mentee.getLastName());
            firstNameField.setText(mentee.getFirstName());
            middleNameField.setText(mentee.getMiddleName());
            emailField.setText(mentee.getEmail());
            goalsField.setText(mentee.getGoals());
            currentLevelField.setText(mentee.getCurrentLevel() != null ? mentee.getCurrentLevel().toString() : "");
        }

        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Цели:"), 0, 4);
        grid.add(goalsField, 1, 4);
        grid.add(new Label("Текущий уровень (1-10):"), 0, 5);
        grid.add(currentLevelField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Конвертация результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Mentee resultMentee = mentee != null ? mentee : new Mentee();
                resultMentee.setLastName(lastNameField.getText());
                resultMentee.setFirstName(firstNameField.getText());
                resultMentee.setMiddleName(middleNameField.getText());
                resultMentee.setEmail(emailField.getText());
                resultMentee.setGoals(goalsField.getText());

                try {
                    String levelText = currentLevelField.getText();
                    if (!levelText.isEmpty()) {
                        short level = Short.parseShort(levelText);
                        if (level >= 1 && level <= 10) {
                            resultMentee.setCurrentLevel(level);
                        } else {
                            showAlert("Уровень должен быть в диапазоне от 1 до 10");
                            return null;
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert("Некорректный формат уровня. Введите число от 1 до 10");
                    return null;
                }

                return resultMentee;
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