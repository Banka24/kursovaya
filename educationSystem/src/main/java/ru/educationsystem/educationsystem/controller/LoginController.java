package ru.educationsystem.educationsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.context.AppContext;
import ru.educationsystem.educationsystem.repository.UserDao;
import ru.educationsystem.educationsystem.service.UserService;

public class LoginController {
    private final UserService userService;

    public LoginController() {
        this.userService = new UserService(new UserDao());
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();

            // Проверка учетных данных через UserService
            boolean isAuthenticated = userService.authenticate(email, password);

            if (isAuthenticated) {
                // Получение пользователя и сохранение его в AppContext
                ru.educationsystem.educationsystem.model.User user = userService.findByEmail(email);
                AppContext.setCurrentUserId(user.getId());
                AppContext.setCurrentUser(user);

                // Загрузка главной панели после успешного входа
                Launcher.setRoot("MainDashboardView");
            } else {
                // Показ сообщения об ошибке
                errorMessage.setText("Неверный email или пароль");
                errorMessage.setVisible(true);
            }
        } catch (IllegalArgumentException e) {
            // Показ сообщения об ошибке валидации
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Произошла ошибка при входе");
            errorMessage.setVisible(true);
        }
    }
}