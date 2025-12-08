package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import ru.educationsystem.educationsystem.model.DevelopmentPlan;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.DevelopmentPlanDao;
import ru.educationsystem.educationsystem.repository.PairDao;
import ru.educationsystem.educationsystem.service.DevelopmentPlanService;
import ru.educationsystem.educationsystem.service.PairService;

import java.time.LocalDate;
import java.util.List;

public class DevelopmentPlansController {
    private final DevelopmentPlanService developmentPlanService;
    private final PairService pairService;
    private final ObservableList<DevelopmentPlan> developmentPlansObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<DevelopmentPlan> developmentPlansTable;

    @FXML
    private TableColumn<DevelopmentPlan, Integer> idColumn;

    @FXML
    private TableColumn<DevelopmentPlan, String> pairColumn;

    @FXML
    private TableColumn<DevelopmentPlan, String> titleColumn;

    @FXML
    private TableColumn<DevelopmentPlan, String> descriptionColumn;

    @FXML
    private TableColumn<DevelopmentPlan, LocalDate> deadlineColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Pair> pairComboBox;

    public DevelopmentPlansController() {
        this.developmentPlanService = new DevelopmentPlanService(new DevelopmentPlanDao());
        this.pairService = new PairService(new PairDao());
    }

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Настройка колонки пары
        pairColumn.setCellValueFactory(cellData -> {
            DevelopmentPlan plan = cellData.getValue();
            Pair pair = plan.getPair();
            if (pair != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        pair.getMentor().getLastName() + " " + pair.getMentor().getFirstName() + " - " +
                        pair.getMentee().getLastName() + " " + pair.getMentee().getFirstName()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

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

        refreshDevelopmentPlans();
    }

    @FXML
    public void addDevelopmentPlan() {
        Dialog<DevelopmentPlan> dialog = createDevelopmentPlanDialog(null);
        dialog.showAndWait().ifPresent(plan -> {
            try {
                developmentPlanService.createDevelopmentPlan(
                        plan.getPair(),
                        plan.getTitle(),
                        plan.getDescription(),
                        plan.getDeadline()
                );
                refreshDevelopmentPlans();
            } catch (Exception e) {
                showAlert("Ошибка при добавлении плана развития.");
            }
        });
    }

    @FXML
    public void editDevelopmentPlan() {
        DevelopmentPlan selectedPlan = developmentPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Выберите план развития для редактирования");
            return;
        }

        Dialog<DevelopmentPlan> dialog = createDevelopmentPlanDialog(selectedPlan);
        dialog.showAndWait().ifPresent(plan -> {
            try {
                developmentPlanService.updateDevelopmentPlan(plan);
                refreshDevelopmentPlans();
            } catch (Exception e) {
                showAlert("Ошибка при редактировании плана развития.");
            }
        });
    }

    @FXML
    public void deleteDevelopmentPlan() {
        DevelopmentPlan selectedPlan = developmentPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Выберите план развития для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Удаление плана развития");
        confirmation.setContentText("Вы уверены, что хотите удалить выбранный план развития?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                developmentPlanService.deleteDevelopmentPlan(selectedPlan);
                refreshDevelopmentPlans();
            } catch (Exception e) {
                showAlert("Ошибка при удалении плана развития.");
            }
        }
    }

    @FXML
    public void refreshDevelopmentPlans() {
        List<DevelopmentPlan> plans = developmentPlanService.getAllDevelopmentPlansWithPair();
        developmentPlansObservableList.clear();
        developmentPlansObservableList.addAll(plans);
        developmentPlansTable.setItems(developmentPlansObservableList);
    }

    @FXML
    public void searchDevelopmentPlans() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshDevelopmentPlans();
            return;
        }

        List<DevelopmentPlan> allPlans = developmentPlanService.getAllDevelopmentPlansWithPair();
        List<DevelopmentPlan> filteredPlans = allPlans.stream()
                .filter(plan -> {
                    String title = plan.getTitle() != null ? plan.getTitle().toLowerCase() : "";
                    String description = plan.getDescription() != null ? plan.getDescription().toLowerCase() : "";

                    Pair pair = plan.getPair();
                    String pairInfo = "";
                    if (pair != null) {
                        pairInfo = pair.getMentor().getLastName() + " " + pair.getMentor().getFirstName() + " - " +
                                pair.getMentee().getLastName() + " " + pair.getMentee().getFirstName();
                        pairInfo = pairInfo.toLowerCase();
                    }

                    return title.contains(searchText.toLowerCase()) ||
                           description.contains(searchText.toLowerCase()) ||
                           pairInfo.contains(searchText.toLowerCase());
                })
                .toList();

        developmentPlansObservableList.clear();
        developmentPlansObservableList.addAll(filteredPlans);
        developmentPlansTable.setItems(developmentPlansObservableList);
    }

    private Dialog<DevelopmentPlan> createDevelopmentPlanDialog(DevelopmentPlan plan) {
        Dialog<DevelopmentPlan> dialog = new Dialog<>();
        dialog.setTitle(plan == null ? "Добавление плана развития" : "Редактирование плана развития");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Создание полей формы
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Pair> pairComboBox = new ComboBox<>();
        List<Pair> pairsList = pairService.getAllPairsWithMentorAndMentee();
        pairComboBox.setItems(FXCollections.observableArrayList(pairsList));
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

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        DatePicker endDatePicker = new DatePicker();

        if (plan != null) {
            // Находим соответствующую пару по ID
            Integer pairId = plan.getPair().getId();
            pairsList.stream()
                    .filter(p -> p.getId().equals(pairId))
                    .findFirst()
                    .ifPresent(pairComboBox::setValue);
            
            titleField.setText(plan.getTitle());
            descriptionField.setText(plan.getDescription());
            endDatePicker.setValue(plan.getDeadline());
        }

        grid.add(new Label("Пара:"), 0, 0);
        grid.add(pairComboBox, 1, 0);
        grid.add(new Label("Название:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Описание:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Срок выполнения:"), 0, 3);
        grid.add(endDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Конвертация результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Pair selectedPair = pairComboBox.getValue();

                if (selectedPair == null) {
                    showAlert("Выберите пару");
                    return null;
                }

                String title = titleField.getText();
                if (title == null || title.trim().isEmpty()) {
                    showAlert("Введите название плана");
                    return null;
                }

                LocalDate endDate = endDatePicker.getValue();

                DevelopmentPlan resultPlan = plan != null ? plan : new DevelopmentPlan();
                resultPlan.setPair(selectedPair);
                resultPlan.setTitle(title);
                resultPlan.setDescription(descriptionField.getText());
                resultPlan.setDeadline(endDate); // Используем endDate как deadline

                return resultPlan;
            }
            return null;
        });

        return dialog;
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) developmentPlansTable.getScene().getWindow();
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