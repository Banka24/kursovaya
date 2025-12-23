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
import java.util.Optional;

public class PlansController {
    @FXML private TableView<DevelopmentPlan> plansTable;
    @FXML private TableColumn<DevelopmentPlan, String> idColumn, pairColumn, titleColumn, descriptionColumn, deadlineColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    private DevelopmentPlanDao planDao = new DevelopmentPlanDao();
    private PairDao pairDao = new PairDao();
    private ObservableList<DevelopmentPlan> list = FXCollections.observableArrayList();
    private FilteredList<DevelopmentPlan> filteredList;

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        pairColumn.setCellValueFactory(c -> {
            Pair p = c.getValue().getPair();
            return new SimpleStringProperty(p != null ? p.getMentorFio() + " - " + p.getMenteeFio() : "");
        });
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription() != null ? c.getValue().getDescription() : ""));
        deadlineColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeadline() != null ? c.getValue().getDeadline().toString() : ""));
        
        filteredList = new FilteredList<>(list, p -> true);
        plansTable.setItems(filteredList);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(plan -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    Pair p = plan.getPair();
                    String pairText = p != null ? (p.getMentorFio() + " " + p.getMenteeFio()) : "";
                    return pairText.toLowerCase().contains(lowerCaseFilter) ||
                           plan.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                           (plan.getDescription() != null && plan.getDescription().toLowerCase().contains(lowerCaseFilter)) ||
                           plan.getId().toString().contains(lowerCaseFilter);
                });
                updateStatusLabel();
            });
        }
        
        plansTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateStatusLabel());
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { 
        DevelopmentPlan p = plansTable.getSelectionModel().getSelectedItem(); 
        if (p != null) showDialog(p); 
        else showError("Выберите запись для редактирования");
    }
    @FXML private void onDeleteClick() {
        DevelopmentPlan p = plansTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            showError("Выберите запись для удаления");
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить план?").showAndWait().get() == ButtonType.OK) {
            try {
                planDao.delete(p); 
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
            list.addAll(planDao.findAll());
            updateStatusLabel();
        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void updateStatusLabel() {
        if (statusLabel != null) {
            int total = filteredList.size();
            int selected = plansTable.getSelectionModel().getSelectedItems().size();
            statusLabel.setText("Всего записей: " + total + " | Выбрано: " + selected);
        }
    }

    private void showDialog(DevelopmentPlan pt) {
        Dialog<DevelopmentPlan> dlg = new Dialog<>();
        dlg.setTitle(pt == null ? "Добавить план" : "Редактировать план");
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
        TextField title = new TextField();
        TextArea description = new TextArea(); description.setPrefRowCount(3);
        DatePicker deadline = new DatePicker();
        if (pt != null) {
            pair.setValue(pt.getPair()); title.setText(pt.getTitle());
            description.setText(pt.getDescription()); deadline.setValue(pt.getDeadline());
        }
        g.add(new Label("Пара:"), 0, 0); g.add(pair, 1, 0);
        g.add(new Label("Название:"), 0, 1); g.add(title, 1, 1);
        g.add(new Label("Описание:"), 0, 2); g.add(description, 1, 2);
        g.add(new Label("Дедлайн:"), 0, 3); g.add(deadline, 1, 3);
        dlg.getDialogPane().setContent(g);
        
        dlg.setResultConverter(b -> {
            if (b == save) {
                if (!validatePlan(pair.getValue(), title.getText(), deadline.getValue())) {
                    return null;
                }
                DevelopmentPlan p = pt != null ? pt : new DevelopmentPlan();
                p.setPair(pair.getValue()); 
                p.setTitle(title.getText().trim());
                p.setDescription(description.getText().trim()); 
                p.setDeadline(deadline.getValue());
                return p;
            }
            return null;
        });
        Optional<DevelopmentPlan> r = dlg.showAndWait();
        r.ifPresent(p -> {
            try {
                if (pt == null) {
                    planDao.save(p);
                    showInfo("Запись успешно добавлена");
                } else {
                    planDao.update(p);
                    showInfo("Запись успешно обновлена");
                }
                load();
            } catch (Exception e) {
                showError("Ошибка при сохранении: " + e.getMessage());
            }
        });
    }
    
    private boolean validatePlan(Pair pair, String title, java.time.LocalDate deadline) {
        if (pair == null) {
            showError("Выберите пару");
            return false;
        }
        if (title == null || title.trim().isEmpty()) {
            showError("Название не может быть пустым");
            return false;
        }
        if (title.trim().length() < 3) {
            showError("Название должно содержать минимум 3 символа");
            return false;
        }
        if (deadline == null) {
            showError("Выберите дедлайн");
            return false;
        }
        if (deadline.isBefore(java.time.LocalDate.now())) {
            showError("Дедлайн не может быть в прошлом");
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