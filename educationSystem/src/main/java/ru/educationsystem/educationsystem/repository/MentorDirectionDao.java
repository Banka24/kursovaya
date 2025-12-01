package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.MentorDirection;
import ru.educationsystem.educationsystem.model.MentorDirectionId;
import java.util.List;

public class MentorDirectionDao extends BaseDao<MentorDirection> {

    public MentorDirectionDao() {
        super(MentorDirection.class);
    }

    // Дополнительные методы для работы со связями наставников и направлений
    public MentorDirection findById(MentorDirectionId id) {
        Session session = getCurrentSession();
        session.beginTransaction();
        MentorDirection mentorDirection = session.get(MentorDirection.class, id);
        session.close();
        return mentorDirection;
    }

    public List<MentorDirection> findByMentorId(long mentorId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<MentorDirection> mentorDirections = session.createQuery(
                "FROM MentorDirection md WHERE md.id.mentorId = :mentorId", MentorDirection.class)
                .setParameter("mentorId", mentorId)
                .list();
        session.close();
        return mentorDirections;
    }

    public List<MentorDirection> findByDirectionId(int directionId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<MentorDirection> mentorDirections = session.createQuery(
                "FROM MentorDirection md WHERE md.id.directionId = :directionId", MentorDirection.class)
                .setParameter("directionId", directionId)
                .list();
        session.close();
        return mentorDirections;
    }
}