package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.Mentor;
import ru.demo.demo2.model.Direction;
import ru.demo.demo2.repository.MentorDao;
import ru.demo.demo2.repository.DirectionDao;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class MentorsController {
    @FXML private TableView<Mentor> mentorsTable;
    @FXML private TableColumn<Mentor, String> idColumn, fioColumn, emailColumn, specColumn, availableColumn, directionsColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    private MentorDao mentorDao = new MentorDao();
    private DirectionDao directionDao = new DirectionDao();
    private ObservableList<Mentor> list = FXCollections.observableArrayList();
    private FilteredList<Mentor> filteredList;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        fioColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        specColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecialization() != null ? c.getValue().getSpecialization() : ""));
        availableColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAvailable() ? "Да" : "Нет"));
        directionsColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDirections().stream().map(Direction::getName).collect(Collectors.joining(", "))));
        
        filteredList = new FilteredList<>(list, p -> true);
        mentorsTable.setItems(filteredList);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(mentor -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    return mentor.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                           mentor.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                           (mentor.getSpecialization() != null && mentor.getSpecialization().toLowerCase().contains(lowerCaseFilter)) ||
                           mentor.getId().toString().contains(lowerCaseFilter);
                });
                updateStatusLabel();
            });
        }
        
        mentorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateStatusLabel());
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { 
        Mentor m = mentorsTable.getSelectionModel().getSelectedItem(); 
        if (m != null) showDialog(m); 
        else showError("Выберите запись для редактирования");
    }
    @FXML private void onDeleteClick() {
        Mentor m = mentorsTable.getSelectionModel().getSelectedItem();
        if (m == null) {
            showError("Выберите запись для удаления");
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + m.getFullName() + "?").showAndWait().get() == ButtonType.OK) {
            try {
                mentorDao.delete(m); 
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
            list.addAll(mentorDao.findAll());
            updateStatusLabel();
        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void updateStatusLabel() {
        if (statusLabel != null) {
            int total = filteredList.size();
            int selected = mentorsTable.getSelectionModel().getSelectedItems().size();
            statusLabel.setText("Всего записей: " + total + " | Выбрано: " + selected);
        }
    }

    private void showDialog(Mentor mt) {
        Dialog<Mentor> dlg = new Dialog<>();
        dlg.setTitle(mt == null ? "Добавить наставника" : "Редактировать наставника");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        TextField fio = new TextField(), email = new TextField(), spec = new TextField();
        CheckBox available = new CheckBox();
        if (mt != null) {
            fio.setText(mt.getFullName()); email.setText(mt.getEmail());
            spec.setText(mt.getSpecialization()); available.setSelected(mt.getAvailable());
        } else {
            available.setSelected(true);
        }
        g.add(new Label("ФИО:"), 0, 0); g.add(fio, 1, 0);
        g.add(new Label("Email:"), 0, 1); g.add(email, 1, 1);
        g.add(new Label("Специализация:"), 0, 2); g.add(spec, 1, 2);
        g.add(new Label("Доступен:"), 0, 3); g.add(available, 1, 3);
        dlg.getDialogPane().setContent(g);
        
        dlg.setResultConverter(b -> {
            if (b == save) {
                if (!validateMentor(fio.getText(), email.getText(), spec.getText())) {
                    return null;
                }
                Mentor m = mt != null ? mt : new Mentor();
                // Разбираем ФИО на компоненты
                String[] nameParts = fio.getText().trim().split("\\s+", 3);
                m.setLastName(nameParts.length > 0 ? nameParts[0] : "");
                m.setFirstName(nameParts.length > 1 ? nameParts[1] : "");
                m.setMiddleName(nameParts.length > 2 ? nameParts[2] : null); 
                m.setEmail(email.getText().trim());
                m.setSpecialization(spec.getText().trim()); 
                m.setAvailable(available.isSelected());
                return m;
            }
            return null;
        });
        Optional<Mentor> r = dlg.showAndWait();
        r.ifPresent(m -> {
            try {
                if (mt == null) {
                    mentorDao.save(m);
                    showInfo("Запись успешно добавлена");
                } else {
                    mentorDao.update(m);
                    showInfo("Запись успешно обновлена");
                }
                load();
            } catch (Exception e) {
                showError("Ошибка при сохранении: " + e.getMessage());
            }
        });
    }
    
    private boolean validateMentor(String fio, String email, String spec) {
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
        if (spec == null || spec.trim().isEmpty()) {
            showError("Специализация не может быть пустой");
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