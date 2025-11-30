package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.*;
import ru.educationsystem.educationsystem.repository.MenteeDao;

import static org.junit.jupiter.api.Assertions.*;

class MenteeServiceTest {
    private MenteeDao menteeDao;

    @BeforeEach
    void setUp() {
        menteeDao = new MenteeDao();
    }

    // TODO: Написать тест для метода createMentee(Mentee mentee) - создание нового подопечного
    
    // TODO: Написать тест для метода updateMentee(Mentee mentee) - обновление данных подопечного

    @Test
    public void testFindByIdReturnTrue(){
        var result = menteeDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = menteeDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> menteeDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> menteeDao.findById(0));
    }

    @Test
    public void testFindByUserIdReturnTrue(){
        var result = menteeDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByUserIdReturnFalse(){
        var result = menteeDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUserIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> menteeDao.findById(-1));
    }

    @Test
    public void testFindByUserIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> menteeDao.findById(0));
    }
    
    // TODO: Написать тест для метода getMenteesByLevel(Level level) - получение подопечных по уровню
    
    // TODO: Написать тест для метода setMenteeLevel(Integer menteeId, Level level) - установка уровня подопечного
    
    // TODO: Написать тест для метода addGoalToMentee(Integer menteeId, MenteeGoal goal) - добавление цели подопечному
    
    // TODO: Написать тест для метода removeGoalFromMentee(Integer menteeId, MenteeGoal goal) - удаление цели у подопечного
    
    // TODO: Написать тест для метода getGoalsByMenteeId(Integer menteeId) - получение целей подопечного
    
    // TODO: Написать тест для метода getMentorsByMenteeId(Integer menteeId) - получение наставников подопечного
}