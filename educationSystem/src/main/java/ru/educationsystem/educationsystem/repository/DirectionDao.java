package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Direction;

public class DirectionDao extends BaseDao<Direction> {

    public DirectionDao() {
        super(Direction.class);
    }

    // Найти направление по имени
    public Direction findByName(String name) {
        Session session = getCurrentSession();
        return session.createQuery(
                "FROM Direction d WHERE d.name = :name", Direction.class)
                .setParameter("name", name)
                .uniqueResult();
    }
}