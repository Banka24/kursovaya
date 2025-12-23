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
import ru.demo.demo2.service.PairMatchingService;
import java.util.Optional;

public class PairsController {
    @FXML private TableView<Pair> pairsTable;
    @FXML private TableColumn<Pair, String> idColumn, mentorColumn, menteeColumn, startDateColumn, statusColumn;
    @FXML private TextField searchField;
    @FXML private Label resultLabel;
    private PairDao pairDao = new PairDao();
    private MentorDao mentorDao = new MentorDao();
    private MenteeDao menteeDao = new MenteeDao();
    private PairMatchingService pairService = new PairMatchingService();
    private ObservableList<Pair> list = FXCollections.observableArrayList();
    private FilteredList<Pair> filteredList;

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        mentorColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMentorFio()));
        menteeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMenteeFio()));
        startDateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(getStatusText(c.getValue().getStatus())));
        
        filteredList = new FilteredList<>(list, p -> true);
        pairsTable.setItems(filteredList);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(pair -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    return pair.getMentorFio().toLowerCase().contains(lowerCaseFilter) ||
                           pair.getMenteeFio().toLowerCase().contains(lowerCaseFilter) ||
                           getStatusText(pair.getStatus()).toLowerCase().contains(lowerCaseFilter) ||
                           pair.getId().toString().contains(lowerCaseFilter);
                });
                updateResultLabel();
            });
        }
        
        pairsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateResultLabel());
        load();
    }

    private String getStatusText(String status) {
        if ("active".equals(status)) return "Активна";
        if ("paused".equals(status)) return "Приостановлена";
        if ("completed".equals(status)) return "Завершена";
        return status;
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { 
        Pair p = pairsTable.getSelectionModel().getSelectedItem(); 
        if (p != null) showDialog(p); 
        else showError("Выберите запись для редактирования");
    }
    @FXML private void onDeleteClick() {
        Pair p = pairsTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            showError("Выберите запись для удаления");
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить пару?").showAndWait().get() == ButtonType.OK) {
            try {
                pairDao.delete(p); 
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
            list.addAll(pairDao.findAll());
            updateResultLabel();
        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void updateResultLabel() {
        long active = list.stream().filter(p -> "active".equals(p.getStatus())).count();
        int total = filteredList.size();
        int selected = pairsTable.getSelectionModel().getSelectedItems().size();
        resultLabel.setText("Всего пар: " + total + ", активных: " + active + " | Выбрано: " + selected);
    }

    private void showDialog(Pair pt) {
        Dialog<Pair> dlg = new Dialog<>();
        dlg.setTitle(pt == null ? "Создать пару" : "Редактировать пару");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        ComboBox<Mentor> mentor = new ComboBox<>(FXCollections.observableArrayList(mentorDao.findAll()));
        ComboBox<Mentee> mentee = new ComboBox<>(FXCollections.observableArrayList(menteeDao.findAll()));
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList("active", "paused", "completed"));
        DatePicker startDate = new DatePicker();
        if (pt != null) {
            mentor.setValue(pt.getMentor()); mentee.setValue(pt.getMentee());
            status.setValue(pt.getStatus()); startDate.setValue(pt.getStartDate());
        } else {
            status.setValue("active"); startDate.setValue(java.time.LocalDate.now());
        }
        g.add(new Label("Наставник:"), 0, 0); g.add(mentor, 1, 0);
        g.add(new Label("Подопечный:"), 0, 1); g.add(mentee, 1, 1);
        g.add(new Label("Дата начала:"), 0, 2); g.add(startDate, 1, 2);
        g.add(new Label("Статус:"), 0, 3); g.add(status, 1, 3);
        dlg.getDialogPane().setContent(g);
        
        dlg.setResultConverter(b -> {
            if (b == save) {
                if (!validatePair(mentor.getValue(), mentee.getValue(), startDate.getValue())) {
                    return null;
                }
                Pair p = pt != null ? pt : new Pair();
                p.setMentor(mentor.getValue()); p.setMentee(mentee.getValue());
                p.setStartDate(startDate.getValue()); p.setStatus(status.getValue());
                return p;
            }
            return null;
        });
        Optional<Pair> r = dlg.showAndWait();
        r.ifPresent(p -> {
            try {
                if (pt == null) {
                    pairDao.save(p);
                    showInfo("Запись успешно добавлена");
                } else {
                    pairDao.update(p);
                    showInfo("Запись успешно обновлена");
                }
                load();
            } catch (Exception e) {
                showError("Ошибка при сохранении: " + e.getMessage());
            }
        });
    }
    
    private boolean validatePair(Mentor mentor, Mentee mentee, java.time.LocalDate startDate) {
        if (mentor == null) {
            showError("Выберите наставника");
            return false;
        }
        if (mentee == null) {
            showError("Выберите подопечного");
            return false;
        }
        if (startDate == null) {
            showError("Выберите дату начала");
            return false;
        }
        if (startDate.isAfter(java.time.LocalDate.now())) {
            showError("Дата начала не может быть в будущем");
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