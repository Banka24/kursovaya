package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.MenteeGoal;

import java.util.List;

public class MenteeGoalDao extends BaseDao<MenteeGoal> {

    public MenteeGoalDao() {
        super(MenteeGoal.class);
    }

    public List<MenteeGoal> findByMenteeId(Integer menteeId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT mg FROM MenteeGoal mg WHERE mg.mentee.id = :menteeId", MenteeGoal.class)
                .setParameter("menteeId", menteeId)
                .getResultList();
        }
    }

    public List<MenteeGoal> findByStatus(String status) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT mg FROM MenteeGoal mg WHERE mg.status = :status", MenteeGoal.class)
                .setParameter("status", status)
                .getResultList();
        }
    }
}