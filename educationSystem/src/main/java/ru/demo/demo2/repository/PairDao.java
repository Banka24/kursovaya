package ru.demo.demo2.repository;
import org.hibernate.Session;
import ru.demo.demo2.model.Pair;
import ru.demo.demo2.util.HibernateSession;
import java.util.List;

public class PairDao extends BaseDao<Pair> {
    public PairDao() { super(Pair.class); }

    public List<Pair> findByStatus(String status) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM Pair WHERE status = :st", Pair.class)
                    .setParameter("st", status).getResultList();
        }
    }

    public List<Pair> findByMentorId(Integer mentorId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM Pair WHERE mentor.id = :id", Pair.class)
                    .setParameter("id", mentorId).getResultList();
        }
    }

    public List<Pair> findByMenteeId(Integer menteeId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM Pair WHERE mentee.id = :id", Pair.class)
                    .setParameter("id", menteeId).getResultList();
        }
    }

    public Pair findActivePairByMentee(Integer menteeId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            List<Pair> r = s.createQuery("FROM Pair WHERE mentee.id = :id AND status = 'active'", Pair.class)
                    .setParameter("id", menteeId).getResultList();

            return r.isEmpty() ? null : r.getFirst();
        }
    }
}