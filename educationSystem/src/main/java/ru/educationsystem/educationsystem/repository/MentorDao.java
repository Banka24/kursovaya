package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Mentor;
import java.util.List;

public class MentorDao extends BaseDao<Mentor> {

    public MentorDao() {
        super(Mentor.class);
    }

    // Дополнительные методы для работы с наставниками
    public List<Mentor> findMentorsByDirection(int directionId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Mentor> mentors = session.createQuery(
                "SELECT DISTINCT m FROM Mentor m " +
                "JOIN m.directions md " +
                "WHERE md.direction.id = :directionId", Mentor.class)
                .setParameter("directionId", directionId)
                .list();
        session.close();
        return mentors;
    }

    public List<Mentor> findAvailableMentors() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Mentor> mentors = session.createQuery(
                "FROM Mentor m WHERE m.available = true", Mentor.class)
                .list();
        session.close();
        return mentors;
    }
    
    public List<Mentor> findAllWithDirections() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Mentor> mentors = session.createQuery(
                "SELECT DISTINCT m FROM Mentor m LEFT JOIN FETCH m.directions", Mentor.class)
                .list();
        session.close();
        return mentors;
    }
}