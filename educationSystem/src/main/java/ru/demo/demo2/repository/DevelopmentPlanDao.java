package ru.demo.demo2.repository;
import org.hibernate.Session;
import ru.demo.demo2.model.DevelopmentPlan;
import ru.demo.demo2.util.HibernateSession;
import java.time.LocalDate;
import java.util.List;

public class DevelopmentPlanDao extends BaseDao<DevelopmentPlan> {
    public DevelopmentPlanDao() {
        super(DevelopmentPlan.class);
    }

    public List<DevelopmentPlan> findByPairId(Integer pairId) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM DevelopmentPlan WHERE pair.id = :id", DevelopmentPlan.class)
                    .setParameter("id", pairId).getResultList();
        }
    }

    public List<DevelopmentPlan> findOverdue() {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            return s.createQuery("FROM DevelopmentPlan WHERE deadline < :d", DevelopmentPlan.class)
                    .setParameter("d", LocalDate.now()).getResultList();
        }
    }
}