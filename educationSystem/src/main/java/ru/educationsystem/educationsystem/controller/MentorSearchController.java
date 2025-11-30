package ru.educationsystem.educationsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.model.*;
import ru.educationsystem.educationsystem.service.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.IOException;

public class MentorSearchController {
    private final MentorService mentorService = new MentorService();
    private final DirectionService directionService = new DirectionService();
    private final LevelService levelService = new LevelService();
    private final PairService pairService = new PairService();
    private final UserService userService = new UserService();

    @FXML
    private ComboBox<String> directionComboBox;

    @FXML
    private ComboBox<String> levelComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView mentorTableView;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button requestMentorshipButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Загрузка направлений из DirectionService в directionComboBox
        List<Direction> directions = directionService.findAll();
        directionComboBox.getItems().clear();
        for (Direction direction : directions) {
            directionComboBox.getItems().add(direction.getName());
        }
        
        // Загрузка уровней из LevelService в levelComboBox
        List<Level> levels = levelService.getAllLevels();
        levelComboBox.getItems().clear();
        for (Level level : levels) {
            levelComboBox.getItems().add(level.getName());
        }
        
        // Настройка колонок таблицы для отображения информации о наставниках
        // Предполагаем, что у Mentor есть поля: firstName, lastName, email, directions
        TableColumn<Mentor, String> firstNameCol = new TableColumn<>("Имя");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        
        TableColumn<Mentor, String> lastNameCol = new TableColumn<>("Фамилия");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        
        TableColumn<Mentor, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<Mentor, String> directionsCol = new TableColumn<>("Направления");
        directionsCol.setCellValueFactory(cellData -> {
            Set<Direction> dirs = cellData.getValue().getDirections();
            String dirNames = dirs.stream()
                .map(Direction::getName)
                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(dirNames);
        });
        
        mentorTableView.getColumns().addAll(firstNameCol, lastNameCol, emailCol, directionsCol);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        // Получение критериев поиска из полей
        String searchText = searchField.getText();
        String selectedDirectionName = directionComboBox.getValue();
        String selectedLevelName = levelComboBox.getValue();
        
        // Поиск наставников через MentorService
        List<Mentor> mentors;
        
        if (selectedDirectionName != null && !selectedDirectionName.isEmpty()) {
            // Поиск по направлению
            Direction direction = directionService.findByName(selectedDirectionName);
            if (direction != null) {
                mentors = mentorService.getMentorsByDirection(direction);
            } else {
                mentors = List.of();
            }
        } else {
            // Поиск всех доступных наставников
            mentors = mentorService.getAvailableMentors();
        }
        
        // Фильтрация по текстовому запросу (если есть)
        if (searchText != null && !searchText.trim().isEmpty()) {
            final String finalSearchText = searchText.toLowerCase().trim();
            mentors = mentors.stream()
                .filter(mentor -> 
                    (mentor.getFirstName() != null && mentor.getFirstName().toLowerCase().contains(finalSearchText)) ||
                    (mentor.getLastName() != null && mentor.getLastName().toLowerCase().contains(finalSearchText)) ||
                    (mentor.getEmail() != null && mentor.getEmail().toLowerCase().contains(finalSearchText))
                )
                .collect(Collectors.toList());
        }
        
        // Обновление таблицы результатами поиска
        ObservableList<Mentor> mentorObservableList = FXCollections.observableArrayList(mentors);
        mentorTableView.setItems(mentorObservableList);
    }

    @FXML
    private void handleClear(ActionEvent event) {
        // Очистка полей поиска
        searchField.clear();
        directionComboBox.setValue(null);
        levelComboBox.setValue(null);
        // Очистка таблицы результатов
        mentorTableView.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void handleRequestMentorship(ActionEvent event) {
        // Проверка, что выбран наставник в таблице
        Mentor selectedMentor = (Mentor) mentorTableView.getSelectionModel().getSelectedItem();
        if (selectedMentor == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите наставника для отправки запроса");
            return;
        }
        
        try {
            // В реальном приложении здесь должна быть логика получения текущего пользователя из сессии
            // Для примера используем пользователя с ID = 1
            Integer currentUserId = 1;
            
            // Проверка, что пользователь еще не отправлял запрос этому наставнику
            List<Pair> userPairs = pairService.getPairsByMenteeId(currentUserId);
            boolean hasPendingRequest = userPairs.stream()
                .anyMatch(pair -> pair.getMentor().getId().equals(selectedMentor.getId()) && 
                                pair.getStatus().equals(PairStatus.ACTIVE));
            
            boolean isAlreadyMentee = userPairs.stream()
                .anyMatch(pair -> pair.getMentor().getId().equals(selectedMentor.getId()) && 
                                pair.getStatus() == PairStatus.ACTIVE);
            
            if (hasPendingRequest) {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Вы уже отправили запрос этому наставнику");
                return;
            }
            
            if (isAlreadyMentee) {
                showAlert(Alert.AlertType.WARNING, "Внимание", "Вы уже являетесь подопечным этого наставника");
                return;
            }
            
            // Создание запроса на наставничество через соответствующий сервис
            Pair newPair = new Pair();
            newPair.setMentor(selectedMentor);
            newPair.setMentee(userService.findMenteeByUserId(currentUserId));
            newPair.setStartDate(java.time.LocalDate.now());
            newPair.setStatus(PairStatus.ACTIVE);
            
            pairService.createPair(newPair);
            
            // Отображение сообщения о результате операции
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Запрос на наставничество отправлен");
            
            // Обновление таблицы для отражения изменений
            handleSearch(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось отправить запрос: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Launcher.setRoot("MainDashboardView");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
