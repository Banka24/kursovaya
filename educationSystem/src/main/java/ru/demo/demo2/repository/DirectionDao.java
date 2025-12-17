package ru.demo.demo2.repository;
import org.hibernate.Session;
import ru.demo.demo2.model.Direction;
import ru.demo.demo2.util.HibernateSession;
import java.util.List;

public class DirectionDao extends BaseDao<Direction> {
    public DirectionDao() {
        super(Direction.class);
    }

    public Direction findByName(String name) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            List<Direction> r = s.createQuery("FROM Direction WHERE name = :n", Direction.class)
                    .setParameter("n", name).getResultList();
            return r.isEmpty() ? null : r.getFirst();
        }
    }
}