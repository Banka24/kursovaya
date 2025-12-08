package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Mentee;
import java.util.List;

public class MenteeDao extends BaseDao<Mentee> {

    public MenteeDao() {
        super(Mentee.class);
    }

    // Найти подопечных без наставника
    public List<Mentee> findMenteesWithoutMentor() {
        Session session = getCurrentSession();
        List<Mentee> mentees = session.createQuery(
                "FROM Mentee m WHERE m.pairs IS EMPTY", Mentee.class)
                .list();
        return mentees;
    }
    
    public List<Mentee> findAllWithPairs() {
        Session session = getCurrentSession();
        List<Mentee> mentees = session.createQuery(
                "SELECT DISTINCT m FROM Mentee m LEFT JOIN FETCH m.pairs", Mentee.class)
                .list();
        return mentees;
    }
}