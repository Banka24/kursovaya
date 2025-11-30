package ru.educationsystem.educationsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ru.educationsystem.educationsystem.Launcher;
import ru.educationsystem.educationsystem.context.AppContext;
import ru.educationsystem.educationsystem.service.UserService;
import ru.educationsystem.educationsystem.service.MentorService;
import ru.educationsystem.educationsystem.service.PairService;
import ru.educationsystem.educationsystem.service.RequestService;
import ru.educationsystem.educationsystem.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainDashboardController {
    private final UserService userService = new UserService();
    private final MentorService mentorService = new MentorService();
    private final PairService pairService = new PairService();
    private final RequestService requestService = new RequestService();

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Button findMentorButton;

    @FXML
    private Button myMenteesButton;

    @FXML
    private Button myRequestsButton;

    @FXML
    private Button myPairsButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        // Получение данных о текущем пользователе из AppContext
        // В реальном приложении здесь должна быть логика получения ID текущего пользователя из сессии
        // Для примера используем пользователя с ID = 1
        if (AppContext.getCurrentUserId() == null) {
            AppContext.setCurrentUserId(1);
        }

        try {
            ru.educationsystem.educationsystem.model.User currentUser = userService.findById(AppContext.getCurrentUserId()).orElse(null);
            AppContext.setCurrentUser(currentUser);

            if (currentUser != null) {
                // Настройка видимости кнопок в зависимости от роли пользователя
                boolean isMentor = currentUser.getRoles() != null &&
                        currentUser.getRoles().contains(UserRole.MENTOR);
                boolean isAdmin = currentUser.getRoles() != null &&
                        currentUser.getRoles().contains(UserRole.ADMIN);

                // Настройка видимости кнопок в зависимости от роли
                myMenteesButton.setVisible(isMentor);
                findMentorButton.setVisible(!isMentor);
                reportsButton.setVisible(isAdmin);

                // Установка текущей даты
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                dateLabel.setText("Платформа наставничества • " + LocalDate.now().format(formatter));

                // Установка приветствия с именем пользователя
                String userName = currentUser.getFirstName() != null ? currentUser.getFirstName() : "";
                if (currentUser.getLastName() != null && !currentUser.getLastName().isEmpty()) {
                    userName = userName.isEmpty() ? currentUser.getLastName() :
                            userName + " " + currentUser.getLastName();
                }
                welcomeLabel.setText("Здравствуйте, " + (userName.isEmpty() ? "Пользователь" : userName) + "!");
            } else {
                // Если пользователь не найден, используем значения по умолчанию
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                dateLabel.setText("Платформа наставничества • " + LocalDate.now().format(formatter));
                welcomeLabel.setText("Здравствуйте, Гость!");

                // Скрываем все специфичные для пользователя кнопки
                myMenteesButton.setVisible(false);
                findMentorButton.setVisible(false);
                reportsButton.setVisible(false);
            }
        } catch (Exception e) {
            // В случае ошибки используем значения по умолчанию
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            dateLabel.setText("Платформа наставничества • " + LocalDate.now().format(formatter));
            welcomeLabel.setText("Здравствуйте, Гость!");

            // Скрываем все специфичные для пользователя кнопки
            myMenteesButton.setVisible(false);
            findMentorButton.setVisible(false);
            reportsButton.setVisible(false);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        try {
            // Логирование действия выхода
            System.out.println("Пользователь " + AppContext.getCurrentUserId() + " выходит из системы");

            // Очистка данных о текущем пользователе через AppContext
            AppContext.clear();

            // В реальном приложении здесь должна быть логика очистки сессии
            // Например, HttpSession.invalidate() для веб-приложения

            // Переход к экрану входа
            Launcher.setRoot("LoginView");
        } catch (Exception e) {
            System.err.println("Ошибка при выходе из системы: " + e.getMessage());
            // Показать сообщение об ошибке пользователю
            // В реальном приложении здесь может быть диалоговое окно с ошибкой
        }
    }

    @FXML
    private void handleFindMentor(ActionEvent event) throws IOException {
        try {
            // Проверяем, что пользователь авторизован
            if (AppContext.getCurrentUserId() == null) {
                System.err.println("Пользователь не авторизован");
                Launcher.setRoot("LoginView");
                return;
            }

            // Проверяем, что пользователь не является наставником
            if (AppContext.getCurrentUser() != null &&
                    AppContext.getCurrentUser().getRoles() != null &&
                    AppContext.getCurrentUser().getRoles().contains(UserRole.MENTOR)) {
                System.err.println("Наставники не могут искать других наставников");
                // В реальном приложении здесь может быть диалоговое окно с сообщением
                return;
            }

            // Логирование действия
            System.out.println("Пользователь " + AppContext.getCurrentUserId() + " переходит к поиску наставников");

            // Переход к поиску наставников
            // ID текущего пользователя уже доступен через AppContext.getCurrentUserId()
            Launcher.setRoot("MentorSearchView");
        } catch (Exception e) {
            System.err.println("Ошибка при переходе к поиску наставников: " + e.getMessage());
            // Показать сообщение об ошибке пользователю
            // В реальном приложении здесь может быть диалоговое окно с ошибкой
        }
    }

    @FXML
    private void handleMyMentees(ActionEvent event) throws IOException {
        try {
            // Проверяем, что пользователь авторизован
            if (AppContext.getCurrentUserId() == null) {
                System.err.println("Пользователь не авторизован");
                Launcher.setRoot("LoginView");
                return;
            }

            // Проверяем, что пользователь является наставником
            if (AppContext.getCurrentUser() == null ||
                    AppContext.getCurrentUser().getRoles() == null ||
                    !AppContext.getCurrentUser().getRoles().contains(UserRole.MENTOR)) {
                System.err.println("Только наставники могут просматривать своих подопечных");
                // В реальном приложении здесь может быть диалоговое окно с сообщением
                return;
            }

            // Получаем информацию о наставнике
            Optional<Mentor> mentorOpt = mentorService.findByUserId(AppContext.getCurrentUserId());
            if (mentorOpt.isEmpty()) {
                System.err.println("Наставник не найден");
                return;
            }

            Mentor mentor = mentorOpt.get();

            // Проверяем наличие подопечных
            java.util.List<Mentee> mentees = mentorService.getMenteesByMentorId(mentor.getId());
            if (mentees.isEmpty()) {
                System.out.println("У наставника " + AppContext.getCurrentUserId() + " нет подопечных");
                // В реальном приложении здесь может быть информационное сообщение
            }

            // Логирование действия
            System.out.println("Наставник " + AppContext.getCurrentUserId() + " просматривает список подопечных");

            // Сохраняем данные в контексте для использования на следующем экране
            AppContext.setMentees(mentees);

            // Переход к списку подопечных
            Launcher.setRoot("MyMenteesView");
        } catch (Exception e) {
            System.err.println("Ошибка при переходе к списку подопечных: " + e.getMessage());
            // Показать сообщение об ошибке пользователю
            // В реальном приложении здесь может быть диалоговое окно с ошибкой
        }
    }

    @FXML
    private void handleMyRequests(ActionEvent event) throws IOException {
        try {
            // Проверяем, что пользователь авторизован
            if (AppContext.getCurrentUserId() == null) {
                System.err.println("Пользователь не авторизован");
                Launcher.setRoot("LoginView");
                return;
            }

            // Получаем список запросов пользователя
            java.util.List<Request> requests;

            // Проверяем роль пользователя и получаем соответствующие запросы
            if (AppContext.getCurrentUser() != null &&
                    AppContext.getCurrentUser().getRoles() != null &&
                    AppContext.getCurrentUser().getRoles().contains(UserRole.MENTOR)) {
                // Для наставников получаем запросы на наставничество
                Optional<Mentor> mentorOpt = mentorService.findByUserId(AppContext.getCurrentUserId());
                if (mentorOpt.isEmpty()) {
                    System.err.println("Наставник не найден");
                    return;
                }

                ru.educationsystem.educationsystem.model.Mentor mentor = mentorOpt.get();
                requests = requestService.findByMentorId(mentor.getId());
            } else {
                // Для подопечных получаем их запросы
                Mentee mentee = userService.findMenteeByUserId(AppContext.getCurrentUserId());
                requests = requestService.findByMenteeId(mentee.getId());
            }

            // Логирование действия
            System.out.println("Пользователь " + AppContext.getCurrentUserId() + " просматривает список запросов");

            // Сохраняем данные в контексте для использования на следующем экране
            AppContext.setRequests(requests);

            // Переход к списку запросов
            Launcher.setRoot("MyRequestsView");
        } catch (Exception e) {
            System.err.println("Ошибка при переходе к списку запросов: " + e.getMessage());
            // Показать сообщение об ошибке пользователю
            // В реальном приложении здесь может быть диалоговое окно с ошибкой
        }
    }

    @FXML
    private void handleMyPairs(ActionEvent event) throws IOException {
        try {
            // Проверяем, что пользователь авторизован
            if (AppContext.getCurrentUserId() == null) {
                System.err.println("Пользователь не авторизован");
                Launcher.setRoot("LoginView");
                return;
            }

            // Получаем список пар пользователя
            java.util.List<Pair> pairs;

            // Проверяем роль пользователя и получаем соответствующие пары
            if (AppContext.getCurrentUser() != null &&
                    AppContext.getCurrentUser().getRoles() != null &&
                    AppContext.getCurrentUser().getRoles().contains(UserRole.MENTOR)) {
                // Для наставников получаем их пары
                Optional<Mentor> mentorOpt = mentorService.findByUserId(AppContext.getCurrentUserId());
                if (mentorOpt.isEmpty()) {
                    System.err.println("Наставник не найден");
                    return;
                }

                Mentor mentor = mentorOpt.get();
                pairs = pairService.getPairsByMentorId(mentor.getId());
            } else {
                // Для подопечных получаем их пары
                Mentee mentee = userService.findMenteeByUserId(AppContext.getCurrentUserId());
                pairs = pairService.getPairsByMenteeId(mentee.getId());
            }

            // Логирование действия
            System.out.println("Пользователь " + AppContext.getCurrentUserId() + " просматривает список пар");

            // Сохраняем данные в контексте для использования на следующем экране
            AppContext.setPairs(pairs);

            // Переход к списку пар
            Launcher.setRoot("MyPairsView");
        } catch (Exception e) {
            System.err.println("Ошибка при переходе к списку пар: " + e.getMessage());
            // Показать сообщение об ошибке пользователю
            // В реальном приложении здесь может быть диалоговое окно с ошибкой
        }
    }

    @FXML
    private void handleReports(ActionEvent event) throws IOException {
        try {
            // Проверяем, что пользователь авторизован
            if (AppContext.getCurrentUserId() == null) {
                System.err.println("Пользователь не авторизован");
                Launcher.setRoot("LoginView");
                return;
            }

            // Проверяем, что пользователь является администратором
            if (AppContext.getCurrentUser() == null ||
                    AppContext.getCurrentUser().getRoles() == null ||
                    !AppContext.getCurrentUser().getRoles().contains(UserRole.ADMIN)) {
                System.err.println("Только администраторы могут просматривать отчеты");
                // В реальном приложении здесь может быть диалоговое окно с сообщением
                return;
            }

            // Получаем статистику для отчета
            java.util.List<Pair> activePairs = pairService.getActivePairs();
            java.util.List<Pair> completedPairs = pairService.getCompletedPairs();

            // Логирование действия
            System.out.println("Администратор " + AppContext.getCurrentUserId() + " просматривает отчеты");

            // Сохраняем данные в контексте для использования на следующем экране
            AppContext.setActivePairs(activePairs);
            AppContext.setCompletedPairs(completedPairs);

            // Переход к отчетам
            Launcher.setRoot("EffectivenessReportView");
        } catch (Exception e) {
            System.err.println("Ошибка при переходе к отчетам: " + e.getMessage());
            // Показать сообщение об ошибке пользователю
            // В реальном приложении здесь может быть диалоговое окно с ошибкой
        }
    }
}