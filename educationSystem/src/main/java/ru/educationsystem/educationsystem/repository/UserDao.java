package ru.educationsystem.educationsystem.repository;

import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.User;

public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    public User findByEmail(String email) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean authenticate(String email, String password) {
        try (Session session = getCurrentSession()) {
            User user = session.createQuery(
                            "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email AND u.passwordHash = :passwordHash", User.class)
                    .setParameter("email", email)
                    .setParameter("passwordHash", password)
                    .getSingleResult();
            return user != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public java.util.Optional<User> findById(Integer id) {
        try (Session session = getCurrentSession()) {
            User user = session.createQuery(
                            "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return java.util.Optional.ofNullable(user);
        }
    }

    @Override
    public java.util.List<User> findAll() {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class)
                    .getResultList();
        }
    }
}