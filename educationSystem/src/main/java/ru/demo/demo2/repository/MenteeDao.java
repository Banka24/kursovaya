package ru.demo.demo2.repository;
import org.hibernate.Session;
import ru.demo.demo2.model.Mentee;
import ru.demo.demo2.util.HibernateSession;
import java.util.List;

public class MenteeDao extends BaseDao<Mentee> {
    public MenteeDao() { super(Mentee.class); }

    public Mentee findByEmail(String email) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            List<Mentee> r = s.createQuery("FROM Mentee WHERE email = :e", Mentee.class)
                    .setParameter("e", email).getResultList();
            return r.isEmpty() ? null : r.getFirst();
        }
    }

    public List<Mentee> findByLevel(int level) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM Mentee WHERE currentLevel = :l", Mentee.class)
                    .setParameter("l", level).getResultList();
        }
    }
}