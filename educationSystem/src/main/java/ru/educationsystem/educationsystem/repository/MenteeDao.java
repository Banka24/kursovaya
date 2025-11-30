package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Level;
import ru.educationsystem.educationsystem.model.Mentee;

import java.util.List;

public class MenteeDao extends BaseDao<Mentee> {

    public MenteeDao() {
        super(Mentee.class);
    }

    public List<Mentee> findByLevel(Level level) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Mentee m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.level WHERE m.level = :level", Mentee.class)
                .setParameter("level", level)
                .getResultList();
        }
    }

    public List<Mentee> findByUserId(Integer userId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Mentee m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.level WHERE m.user.id = :userId", Mentee.class)
                .setParameter("userId", userId)
                .getResultList();
        }
    }

    @Override
    public java.util.Optional<Mentee> findById(Integer id) {
        try (Session session = getCurrentSession()) {
            Mentee mentee = session.createQuery(
                            "SELECT m FROM Mentee m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.level WHERE m.id = :id", Mentee.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return java.util.Optional.ofNullable(mentee);
        }
    }

    @Override
    public java.util.List<Mentee> findAll() {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT DISTINCT m FROM Mentee m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.user.roles LEFT JOIN FETCH m.level", Mentee.class)
                    .getResultList();
        }
    }
}