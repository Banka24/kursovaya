package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.model.Mentor;

import java.util.List;

public class MentorDao extends BaseDao<Mentor> {

    public MentorDao() {
        super(Mentor.class);
    }

    public List<Mentor> findByAvailable(Boolean available) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.directions WHERE m.available = :available", Mentor.class)
                .setParameter("available", available)
                .getResultList();
        }
    }

    public List<Mentor> findByDirection(Direction direction) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT DISTINCT m FROM Mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.directions JOIN m.directions d WHERE d = :direction", Mentor.class)
                .setParameter("direction", direction)
                .getResultList();
        }
    }

    public java.util.Optional<Mentor> findByUserId(Integer userId) {
        try (Session session = getCurrentSession()) {
            List<Mentor> mentors = session.createQuery(
                "SELECT m FROM Mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.directions WHERE m.user.id = :userId", Mentor.class)
                .setParameter("userId", userId)
                .getResultList();

            return mentors.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(mentors.get(0));
        }
    }

    @Override
    public java.util.Optional<Mentor> findById(Integer id) {
        try (Session session = getCurrentSession()) {
            Mentor mentor = session.createQuery(
                            "SELECT m FROM Mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.directions WHERE m.id = :id", Mentor.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return java.util.Optional.ofNullable(mentor);
        }
    }

    @Override
    public java.util.List<Mentor> findAll() {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT DISTINCT m FROM Mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.directions", Mentor.class)
                    .getResultList();
        }
    }
}