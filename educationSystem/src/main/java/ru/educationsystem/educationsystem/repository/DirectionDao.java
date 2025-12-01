package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Direction;
import java.util.List;

public class DirectionDao extends BaseDao<Direction> {

    public DirectionDao() {
        super(Direction.class);
    }

    // Дополнительные методы для работы с направлениями
    public Direction findByName(String name) {
        Session session = getCurrentSession();
        session.beginTransaction();
        Direction direction = session.createQuery(
                "FROM Direction d WHERE d.name = :name", Direction.class)
                .setParameter("name", name)
                .uniqueResult();
        session.close();
        return direction;
    }

    public List<Direction> findAllActive() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<Direction> directions = session.createQuery(
                "FROM Direction d WHERE d.active = true", Direction.class)
                .list();
        session.close();
        return directions;
    }
}