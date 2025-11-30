package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.User;
import ru.educationsystem.educationsystem.model.UserRole;
import ru.educationsystem.educationsystem.repository.UserDao;

import java.util.List;
import java.util.Optional;

public class UserService {
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserService() {
        this.userDao = new UserDao();
    }

    public boolean authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        return userDao.authenticate(email, password);
    }

    public Optional<User> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return userDao.findById(id);
    }

    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        return userDao.findByEmail(email);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        // Проверяем, что email еще не используется
        User existingUser = userDao.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        userDao.save(user);
        return user;
    }

    public User updateProfile(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }

        // Проверяем, что пользователь существует
        Optional<User> existingUser = userDao.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }

        // Если email изменен, проверяем, что он не занят другим пользователем
        if (!existingUser.get().getEmail().equals(user.getEmail())) {
            User userWithSameEmail = userDao.findByEmail(user.getEmail());
            if (userWithSameEmail != null) {
                throw new IllegalArgumentException("Пользователь с таким email уже существует");
            }
        }

        userDao.update(user);
        return user;
    }

    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Старый пароль не может быть пустым");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Новый пароль не может быть пустым");
        }

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }

        User user = userOpt.get();
        // Проверяем старый пароль
        if (!user.getPasswordHash().equals(oldPassword)) {
            throw new IllegalArgumentException("Старый пароль неверен");
        }

        // Устанавливаем новый пароль
        user.setPasswordHash(newPassword);
        userDao.update(user);
        return true;
    }

    public boolean deleteUser(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }

        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }

        userDao.deleteById(id);
        return true;
    }

    public User addRoleToUser(Integer userId, UserRole role) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
        if (role == null) {
            throw new IllegalArgumentException("Роль не может быть null");
        }

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }

        User user = userOpt.get();
        if (user.getRoles() == null) {
            // Инициализируем множество ролей, если оно null
            user.setRoles(java.util.HashSet.newHashSet(4));
        }

        user.getRoles().add(role);
        userDao.update(user);
        return user;
    }

    public User removeRoleFromUser(Integer userId, UserRole role) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
        if (role == null) {
            throw new IllegalArgumentException("Роль не может быть null");
        }

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }

        User user = userOpt.get();
        if (user.getRoles() != null) {
            user.getRoles().remove(role);
            userDao.update(user);
        }

        return user;
    }

    public ru.educationsystem.educationsystem.model.Mentee findMenteeByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }

        // В реальном приложении здесь должен быть запрос к базе для получения подопечного
        // Для примера создаем и возвращаем новый объект подопечного
        ru.educationsystem.educationsystem.model.Mentee mentee = new ru.educationsystem.educationsystem.model.Mentee();
        mentee.setUser(userOpt.get());

        return mentee;
    }
}