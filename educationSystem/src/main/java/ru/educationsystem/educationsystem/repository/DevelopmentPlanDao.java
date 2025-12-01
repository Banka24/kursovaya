package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.DevelopmentPlan;
import java.util.List;

public class DevelopmentPlanDao extends BaseDao<DevelopmentPlan> {

    public DevelopmentPlanDao() {
        super(DevelopmentPlan.class);
    }

    // Дополнительные методы для работы с планами развития
    public List<DevelopmentPlan> findPlansByPair(long pairId) {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<DevelopmentPlan> plans = session.createQuery(
                "FROM DevelopmentPlan dp WHERE dp.pair.id = :pairId", DevelopmentPlan.class)
                .setParameter("pairId", pairId)
                .list();
        session.close();
        return plans;
    }

    public List<DevelopmentPlan> findActivePlans() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<DevelopmentPlan> plans = session.createQuery(
                "FROM DevelopmentPlan dp WHERE dp.completed = false", DevelopmentPlan.class)
                .list();
        session.close();
        return plans;
    }

    public List<DevelopmentPlan> findCompletedPlans() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<DevelopmentPlan> plans = session.createQuery(
                "FROM DevelopmentPlan dp WHERE dp.completed = true", DevelopmentPlan.class)
                .list();
        session.close();
        return plans;
    }
    
    public List<DevelopmentPlan> findAllWithPair() {
        Session session = getCurrentSession();
        session.beginTransaction();
        List<DevelopmentPlan> plans = session.createQuery(
                "SELECT DISTINCT dp FROM DevelopmentPlan dp LEFT JOIN FETCH dp.pair p LEFT JOIN FETCH p.mentor LEFT JOIN FETCH p.mentee", DevelopmentPlan.class)
                .list();
        session.close();
        return plans;
    }
}