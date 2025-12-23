package ru.demo.demo2.repository;
import org.hibernate.Session;
import ru.demo.demo2.model.Meeting;
import ru.demo.demo2.util.HibernateSession;
import java.util.List;

public class MeetingDao extends BaseDao<Meeting> {
    public MeetingDao() { super(Meeting.class); }

    public List<Meeting> findByPairId(Integer pairId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM Meeting WHERE pair.id = :id ORDER BY datetime DESC", Meeting.class).setParameter("id", pairId).getResultList();
        }
    }

    public Double getAverageMentorRating(Integer pairId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            Double r = s.createQuery("SELECT AVG(mentorRating) FROM Meeting WHERE pair.id = :id AND mentorRating IS NOT NULL", Double.class).setParameter("id", pairId).uniqueResult();
            return r != null ? r : 0.0;
        }
    }

    public Double getAverageMenteeRating(Integer pairId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            Double r = s.createQuery("SELECT AVG(menteeRating) FROM Meeting WHERE pair.id = :id AND menteeRating IS NOT NULL", Double.class).setParameter("id", pairId).uniqueResult();
            return r != null ? r : 0.0;
        }
    }
}