
package ru.educationsystem.educationsystem.context;

import ru.educationsystem.educationsystem.model.User;
import ru.educationsystem.educationsystem.model.Request;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.Pair;

import java.util.List;

/**
 * Класс для хранения глобального контекста приложения.
 * Используется для передачи данных между контроллерами.
 */
public class AppContext {
    private static Integer currentUserId;
    private static User currentUser;
    private static List<Request> requests;
    private static List<Mentee> mentees;
    private static List<Pair> pairs;
    private static List<Pair> activePairs;
    private static List<Pair> completedPairs;


    private AppContext() {
        // Приватный конструктор для предотвращения создания экземпляров
    }

    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(Integer currentUserId) {
        AppContext.currentUserId = currentUserId;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        AppContext.currentUser = currentUser;
    }

    public static List<Request> getRequests() {
        return requests;
    }

    public static void setRequests(List<Request> requests) {
        AppContext.requests = requests;
    }

    public static List<Mentee> getMentees() {
        return mentees;
    }

    public static void setMentees(List<Mentee> mentees) {
        AppContext.mentees = mentees;
    }

    public static List<Pair> getPairs() {
        return pairs;
    }

    public static void setPairs(List<Pair> pairs) {
        AppContext.pairs = pairs;
    }

    public static void clear() {
        currentUserId = null;
        currentUser = null;
        requests = null;
        mentees = null;
        pairs = null;
    }

    public static void setActivePairs(List<Pair> activePairs) {
        AppContext.activePairs = activePairs;
    }

    public static void setCompletedPairs(List<Pair> completedPairs) {
        AppContext.completedPairs = completedPairs;
    }
}