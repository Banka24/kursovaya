package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Pair;
import java.util.List;

public class PairDao extends BaseDao<Pair> {

    public PairDao() {
        super(Pair.class);
    }

    // Дополнительные методы для работы с парами
    public List<Pair> findPairsByMentor(long mentorId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Pair> pairs = session.createQuery(
                "FROM Pair p WHERE p.mentor.id = :mentorId", Pair.class)
                .setParameter("mentorId", mentorId)
                .list();
        session.close();
        return pairs;
    }

    public List<Pair> findPairsByMentee(long menteeId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Pair> pairs = session.createQuery(
                "FROM Pair p WHERE p.mentee.id = :menteeId", Pair.class)
                .setParameter("menteeId", menteeId)
                .list();
        session.close();
        return pairs;
    }

    public List<Pair> findActivePairs() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Pair> pairs = session.createQuery(
                "FROM Pair p WHERE p.active = true", Pair.class)
                .list();
        session.close();
        return pairs;
    }
    
    public List<Pair> findAllWithMentorAndMentee() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Pair> pairs = session.createQuery(
                "SELECT DISTINCT p FROM Pair p LEFT JOIN FETCH p.mentor LEFT JOIN FETCH p.mentee", Pair.class)
                .list();
        session.close();
        return pairs;
    }
}