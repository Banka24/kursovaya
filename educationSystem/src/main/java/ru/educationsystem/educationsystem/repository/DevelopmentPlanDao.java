package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.DevelopmentPlan;

import java.util.List;

public class DevelopmentPlanDao extends BaseDao<DevelopmentPlan> {

    public DevelopmentPlanDao() {
        super(DevelopmentPlan.class);
    }

    public List<DevelopmentPlan> findByPairId(Integer pairId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT dp FROM DevelopmentPlan dp WHERE dp.pair.id = :pairId", DevelopmentPlan.class)
                .setParameter("pairId", pairId)
                .getResultList();
        }
    }

    public List<DevelopmentPlan> findByPairIdAndCompleted(Integer pairId, boolean completed) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT dp FROM DevelopmentPlan dp WHERE dp.pair.id = :pairId AND dp.completed = :completed", DevelopmentPlan.class)
                .setParameter("pairId", pairId)
                .setParameter("completed", completed)
                .getResultList();
        }
    }
}