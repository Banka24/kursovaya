package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Mentee;
import java.util.List;

public class MenteeDao extends BaseDao<Mentee> {

    public MenteeDao() {
        super(Mentee.class);
    }

    // Дополнительные методы для работы с подопечными
    public List<Mentee> findMenteesWithoutMentor() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Mentee> mentees = session.createQuery(
                "FROM Mentee m WHERE m.pair IS NULL", Mentee.class)
                .list();
        session.close();
        return mentees;
    }

    public List<Mentee> findMenteesByDirection(int directionId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Mentee> mentees = session.createQuery(
                "FROM Mentee m WHERE m.direction.id = :directionId", Mentee.class)
                .setParameter("directionId", directionId)
                .list();
        session.close();
        return mentees;
    }
    
    public List<Mentee> findAllWithPairs() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Mentee> mentees = session.createQuery(
                "SELECT DISTINCT m FROM Mentee m LEFT JOIN FETCH m.pairs", Mentee.class)
                .list();
        session.close();
        return mentees;
    }
}