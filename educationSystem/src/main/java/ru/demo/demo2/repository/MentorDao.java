package ru.demo.demo2.repository;
import org.hibernate.Session;
import ru.demo.demo2.model.Mentor;
import ru.demo.demo2.util.HibernateSession;
import java.util.List;

public class MentorDao extends BaseDao<Mentor> {
    public MentorDao() { super(Mentor.class); }

    public List<Mentor> findAvailable() {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM Mentor WHERE available = true", Mentor.class).getResultList();
        }
    }

    public Mentor findByEmail(String email) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            List<Mentor> r = s.createQuery("FROM Mentor WHERE email = :e", Mentor.class)
                    .setParameter("e", email).getResultList();

            return r.isEmpty() ? null : r.getFirst();
        }
    }
}