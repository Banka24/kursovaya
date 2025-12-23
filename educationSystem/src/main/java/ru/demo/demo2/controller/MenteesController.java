package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.Mentee;
import ru.demo.demo2.repository.MenteeDao;
import java.util.Optional;
import java.util.regex.Pattern;

public class MenteesController {
    @FXML private TableView<Mentee> menteesTable;
    @FXML private TableColumn<Mentee, String> idColumn, fioColumn, emailColumn, goalsColumn, levelColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    private MenteeDao menteeDao = new MenteeDao();
    private ObservableList<Mentee> list = FXCollections.observableArrayList();
    private FilteredList<Mentee> filteredList;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        fioColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        goalsColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGoals() != null ? c.getValue().getGoals() : ""));
        levelColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurrentLevel() != null ? c.getValue().getCurrentLevel().toString() : ""));
        
        filteredList = new FilteredList<>(list, p -> true);
        menteesTable.setItems(filteredList);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(mentee -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    return mentee.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                           mentee.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                           (mentee.getGoals() != null && mentee.getGoals().toLowerCase().contains(lowerCaseFilter)) ||
                           mentee.getId().toString().contains(lowerCaseFilter);
                });
                updateStatusLabel();
            });
        }
        
        menteesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateStatusLabel());
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { 
        Mentee m = menteesTable.getSelectionModel().getSelectedItem(); 
        if (m != null) showDialog(m); 
        else showError("Выберите запись для редактирования");
    }
    @FXML private void onDeleteClick() {
        Mentee m = menteesTable.getSelectionModel().getSelectedItem();
        if (m == null) {
            showError("Выберите запись для удаления");
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + m.getFullName() + "?").showAndWait().get() == ButtonType.OK) {
            try {
                menteeDao.delete(m); 
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
            list.addAll(menteeDao.findAll());
            updateStatusLabel();
        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void updateStatusLabel() {
        if (statusLabel != null) {
            int total = filteredList.size();
            int selected = menteesTable.getSelectionModel().getSelectedItems().size();
            statusLabel.setText("Всего записей: " + total + " | Выбрано: " + selected);
        }
    }

    private void showDialog(Mentee mt) {
        Dialog<Mentee> dlg = new Dialog<>();
        dlg.setTitle(mt == null ? "Добавить подопечного" : "Редактировать подопечного");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        TextField fio = new TextField(), email = new TextField();
        TextArea goals = new TextArea(); goals.setPrefRowCount(3);
        Spinner<Integer> level = new Spinner<>(1, 5, mt != null && mt.getCurrentLevel() != null ? mt.getCurrentLevel() : 1);
        if (mt != null) {
            fio.setText(mt.getFullName()); email.setText(mt.getEmail());
            goals.setText(mt.getGoals());
        }
        g.add(new Label("ФИО:"), 0, 0); g.add(fio, 1, 0);
        g.add(new Label("Email:"), 0, 1); g.add(email, 1, 1);
        g.add(new Label("Цели:"), 0, 2); g.add(goals, 1, 2);
        g.add(new Label("Уровень (1-5):"), 0, 3); g.add(level, 1, 3);
        dlg.getDialogPane().setContent(g);
        
        dlg.setResultConverter(b -> {
            if (b == save) {
                if (!validateMentee(fio.getText(), email.getText(), goals.getText())) {
                    return null;
                }
                Mentee m = mt != null ? mt : new Mentee();
                // Разбираем ФИО на компоненты
                String[] nameParts = fio.getText().trim().split("\\s+", 3);
                m.setLastName(nameParts.length > 0 ? nameParts[0] : "");
                m.setFirstName(nameParts.length > 1 ? nameParts[1] : "");
                m.setMiddleName(nameParts.length > 2 ? nameParts[2] : null); 
                m.setEmail(email.getText().trim());
                m.setGoals(goals.getText().trim()); 
                m.setCurrentLevel(level.getValue());
                return m;
            }
            return null;
        });
        Optional<Mentee> r = dlg.showAndWait();
        r.ifPresent(m -> {
            try {
                if (mt == null) {
                    menteeDao.save(m);
                    showInfo("Запись успешно добавлена");
                } else {
                    menteeDao.update(m);
                    showInfo("Запись успешно обновлена");
                }
                load();
            } catch (Exception e) {
                showError("Ошибка при сохранении: " + e.getMessage());
            }
        });
    }
    
    private boolean validateMentee(String fio, String email, String goals) {
        if (fio == null || fio.trim().isEmpty()) {
            showError("ФИО не может быть пустым");
            return false;
        }
        if (fio.trim().length() < 3) {
            showError("ФИО должно содержать минимум 3 символа");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            showError("Email не может быть пустым");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            showError("Некорректный формат email");
            return false;
        }
        if (goals == null || goals.trim().isEmpty()) {
            showError("Цели не могут быть пустыми");
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