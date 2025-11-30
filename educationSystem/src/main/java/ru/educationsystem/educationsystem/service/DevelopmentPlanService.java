package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.DevelopmentPlan;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.DevelopmentPlanDao;
import ru.educationsystem.educationsystem.repository.PairDao;

import java.util.List;
import java.util.Optional;

public class DevelopmentPlanService {
    private final DevelopmentPlanDao developmentPlanDao;
    private final PairDao pairDao;

    public DevelopmentPlanService() {
        this.developmentPlanDao = new DevelopmentPlanDao();
        this.pairDao = new PairDao();
    }

    public DevelopmentPlanService(DevelopmentPlanDao developmentPlanDao, PairDao pairDao) {
        this.developmentPlanDao = developmentPlanDao;
        this.pairDao = pairDao;
    }

    public DevelopmentPlan createPlanItem(DevelopmentPlan planItem) {
        if (planItem == null) {
            throw new IllegalArgumentException("Элемент плана развития не может быть null");
        }
        if (planItem.getPair() == null) {
            throw new IllegalArgumentException("Пара не может быть null");
        }
        if (planItem.getTitle() == null || planItem.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Заголовок не может быть пустым");
        }
        developmentPlanDao.save(planItem);
        return planItem;
    }

    public DevelopmentPlan updatePlanItem(DevelopmentPlan planItem) {
        if (planItem == null) {
            throw new IllegalArgumentException("Элемент плана развития не может быть null");
        }
        if (planItem.getId() == null || planItem.getId() <= 0) {
            throw new IllegalArgumentException("ID элемента плана должен быть положительным числом");
        }

        Optional<DevelopmentPlan> existingPlanItem = developmentPlanDao.findById(planItem.getId());
        if (existingPlanItem.isEmpty()) {
            throw new IllegalArgumentException("Элемент плана с таким ID не найден");
        }

        developmentPlanDao.update(planItem);
        return planItem;
    }

    public Optional<DevelopmentPlan> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return developmentPlanDao.findById(id);
    }

    public List<DevelopmentPlan> getAllDevelopmentPlans() {
        return developmentPlanDao.findAll();
    }

    public List<DevelopmentPlan> getPlanByPairId(Integer pairId) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        return developmentPlanDao.findByPairId(pairId);
    }

    public DevelopmentPlan markPlanItemAsCompleted(Integer planItemId) {
        if (planItemId == null || planItemId <= 0) {
            throw new IllegalArgumentException("ID элемента плана должен быть положительным числом");
        }

        Optional<DevelopmentPlan> planItemOpt = developmentPlanDao.findById(planItemId);
        if (planItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Элемент плана с таким ID не найден");
        }

        DevelopmentPlan planItem = planItemOpt.get();
        planItem.setCompleted(true);
        developmentPlanDao.update(planItem);
        return planItem;
    }

    public boolean deletePlanItem(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID элемента плана должен быть положительным числом");
        }

        Optional<DevelopmentPlan> planItemOpt = developmentPlanDao.findById(id);
        if (planItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Элемент плана с таким ID не найден");
        }

        developmentPlanDao.deleteById(id);
        return true;
    }

    public List<DevelopmentPlan> getCompletedPlanItems(Integer pairId) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        return developmentPlanDao.findByPairIdAndCompleted(pairId, true);
    }

    public List<DevelopmentPlan> getPendingPlanItems(Integer pairId) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        return developmentPlanDao.findByPairIdAndCompleted(pairId, false);
    }

    // Псевдоним для метода, используемого в контроллере
    public List<DevelopmentPlan> getPlansByPairId(Integer pairId) {
        return getPlanByPairId(pairId);
    }
}