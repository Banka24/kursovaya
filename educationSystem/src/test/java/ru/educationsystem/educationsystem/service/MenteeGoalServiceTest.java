package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.model.MenteeGoal;
import ru.educationsystem.educationsystem.repository.MenteeGoalDao;

import static org.junit.jupiter.api.Assertions.*;

class MenteeGoalServiceTest {
    private MenteeGoalDao menteeGoalDao;

    @BeforeEach
    void setUp() {
        menteeGoalDao = new MenteeGoalDao();
    }

    // TODO: Написать тест для метода createGoal(MenteeGoal goal) - создание новой цели

    // TODO: Написать тест для метода updateGoal(MenteeGoal goal) - обновление цели

    @Test
    public void testFindByIdReturnTrue(){
        var result = menteeGoalDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = menteeGoalDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> menteeGoalDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> menteeGoalDao.findById(0));
    }

    @Test
    public void testGetAllMenteeGoalsReturnList(){
        var result = menteeGoalDao.findAll();
        assertNotNull(result);
    }

    // TODO: Написать тест для метода getGoalsByMenteeId(Integer menteeId) - получение целей подопечного

    // TODO: Написать тест для метода markGoalAsCompleted(Integer goalId) - отметка цели как выполненной

    // TODO: Написать тест для метода deleteGoal(Integer id) - удаление цели

    // TODO: Написать тест для метода getGoalsByStatus(String status) - получение целей по статусу
}