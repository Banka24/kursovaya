package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.model.DevelopmentPlan;
import ru.educationsystem.educationsystem.repository.DevelopmentPlanDao;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentPlanServiceTest {
    private DevelopmentPlanDao developmentPlanDao;

    @BeforeEach
    void setUp() {
        developmentPlanDao = new DevelopmentPlanDao();
    }

    // TODO: Написать тест для метода createPlanItem(DevelopmentPlan planItem) - создание элемента плана развития
    @Test
    public void testCreatePlanItemTr() {
        var planItem = new DevelopmentPlan();
    }

    // TODO: Написать тест для метода updatePlanItem(DevelopmentPlan planItem) - обновление элемента плана развития

    @Test
    public void testFindByIdReturnTrue(){
        var result = developmentPlanDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = developmentPlanDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> developmentPlanDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> developmentPlanDao.findById(0));
    }

    @Test
    public void testGetAllDevelopmentPlansReturnList(){
        var result = developmentPlanDao.findAll();
        assertNotNull(result);
    }

    // TODO: Написать тест для метода getPlanByPairId(Integer pairId) - получение плана развития по паре

    // TODO: Написать тест для метода markPlanItemAsCompleted(Integer planItemId) - отметка элемента плана как выполненного

    // TODO: Написать тест для метода deletePlanItem(Integer id) - удаление элемента плана

    // TODO: Написать тест для метода getCompletedPlanItems(Integer pairId) - получение выполненных элементов плана

    // TODO: Написать тест для метода getPendingPlanItems(Integer pairId) - получение невыполненных элементов плана
}