package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Direction;

import java.util.List;

public class DirectionDao extends BaseDao<Direction> {

    public DirectionDao() {
        super(Direction.class);
    }

    public List<Direction> findAllWithMentors() {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT DISTINCT d FROM Direction d LEFT JOIN FETCH d.mentors", Direction.class)
                .getResultList();
        }
    }
}