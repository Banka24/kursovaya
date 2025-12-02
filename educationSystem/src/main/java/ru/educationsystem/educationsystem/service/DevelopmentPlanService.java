package ru.educationsystem.educationsystem.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.educationsystem.educationsystem.model.DevelopmentPlan;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.DevelopmentPlanDao;
import ru.educationsystem.educationsystem.util.HibernateSessionFactoryUtil;
import java.time.LocalDate;
import java.util.List;

public class DevelopmentPlanService extends BaseService<DevelopmentPlan, DevelopmentPlanDao> {
    public DevelopmentPlanService(DevelopmentPlanDao developmentPlanDao) {
        super(developmentPlanDao);
    }

    public DevelopmentPlan createDevelopmentPlan(Pair pair, String title, String description, LocalDate deadline) {
        DevelopmentPlan developmentPlan = new DevelopmentPlan();
        developmentPlan.setPair(pair);
        developmentPlan.setTitle(title);
        developmentPlan.setDescription(description);
        developmentPlan.setDeadline(deadline);
        return save(developmentPlan);
    }

    public DevelopmentPlan updateDevelopmentPlan(DevelopmentPlan developmentPlan) {
        return update(developmentPlan);
    }

    public void deleteDevelopmentPlan(DevelopmentPlan developmentPlan) {
        delete(developmentPlan);
    }

    public DevelopmentPlan getDevelopmentPlanById(int id) {
        return findOne(id);
    }

    public List<DevelopmentPlan> getAllDevelopmentPlans() {
        return findAll();
    }
    
    public List<DevelopmentPlan> getAllDevelopmentPlansWithPair() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<DevelopmentPlan> result = dao.findAllWithPair();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // Метод для получения планов по конкретной паре
    public List<DevelopmentPlan> getPlansByPair(Pair pair) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<DevelopmentPlan> result = dao.findPlansByPair(pair.getId());
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}