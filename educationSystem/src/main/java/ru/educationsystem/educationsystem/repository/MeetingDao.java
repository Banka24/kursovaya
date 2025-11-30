package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Meeting;

import java.util.List;

public class MeetingDao extends BaseDao<Meeting> {

    public MeetingDao() {
        super(Meeting.class);
    }

    public List<Meeting> findByPairId(Integer pairId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Meeting m WHERE m.pair.id = :pairId", Meeting.class)
                .setParameter("pairId", pairId)
                .getResultList();
        }
    }

    public List<Meeting> findByPairIdAndDateRange(Integer pairId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Meeting m WHERE m.pair.id = :pairId AND DATE(m.datetime) BETWEEN :startDate AND :endDate", Meeting.class)
                .setParameter("pairId", pairId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        }
    }

    public List<Meeting> findByPairIdAndFutureDate(Integer pairId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Meeting m WHERE m.pair.id = :pairId AND m.datetime >= CURRENT_TIMESTAMP", Meeting.class)
                .setParameter("pairId", pairId)
                .getResultList();
        }
    }

    public List<Meeting> findByPairIdAndPastDate(Integer pairId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Meeting m WHERE m.pair.id = :pairId AND m.datetime < CURRENT_TIMESTAMP", Meeting.class)
                .setParameter("pairId", pairId)
                .getResultList();
        }
    }
    
    public List<Meeting> findByPairIdAndDateTimeRange(Integer pairId, java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Meeting m WHERE m.pair.id = :pairId AND m.datetime BETWEEN :startDateTime AND :endDateTime", Meeting.class)
                .setParameter("pairId", pairId)
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .getResultList();
        }
    }
    
    public List<Meeting> findByDateTimeRange(java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT m FROM Meeting m WHERE m.datetime BETWEEN :startDateTime AND :endDateTime", Meeting.class)
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .getResultList();
        }
    }
}