package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Meeting;
import java.util.List;
import java.util.Date;

public class MeetingDao extends BaseDao<Meeting> {

    public MeetingDao() {
        super(Meeting.class);
    }

    // Дополнительные методы для работы со встречами
    public List<Meeting> findMeetingsByPair(long pairId) {
        Session session = getCurrentSession();
        List<Meeting> meetings = session.createQuery(
                "FROM Meeting m WHERE m.pair.id = :pairId ORDER BY m.datetime", Meeting.class)
                .setParameter("pairId", pairId)
                .list();
        return meetings;
    }

    public List<Meeting> findMeetingsByDateRange(Date startDate, Date endDate) {
        Session session = getCurrentSession();
        List<Meeting> meetings = session.createQuery(
                "FROM Meeting m WHERE m.datetime BETWEEN :startDate AND :endDate", Meeting.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .list();
        return meetings;
    }

    public List<Meeting> findUpcomingMeetings() {
        Session session = getCurrentSession();
        List<Meeting> meetings = session.createQuery(
                "FROM Meeting m WHERE m.datetime >= CURRENT_DATE", Meeting.class)
                .list();
        return meetings;
    }

    public List<Meeting> findPastMeetings() {
        Session session = getCurrentSession();
        List<Meeting> meetings = session.createQuery(
                "FROM Meeting m WHERE m.datetime < CURRENT_DATE", Meeting.class)
                .list();
        return meetings;
    }
    
    public List<Meeting> findAllWithPair() {
        Session session = getCurrentSession();
        List<Meeting> meetings = session.createQuery(
                "SELECT DISTINCT m FROM Meeting m LEFT JOIN FETCH m.pair p LEFT JOIN FETCH p.mentor LEFT JOIN FETCH p.mentee", Meeting.class)
                .list();
        return meetings;
    }
}