package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.repository.MentorDao;

import static org.junit.jupiter.api.Assertions.*;

class MentorServiceTest {

    private MentorDao mentorDao;

    @BeforeEach
    void setUp() {
        mentorDao = new MentorDao();
    }

    // TODO: Написать тест для метода createMentor(Mentor mentor) - создание нового наставника
    
    // TODO: Написать тест для метода updateMentor(Mentor mentor) - обновление данных наставника

    @Test
    public void testFindByIdReturnTrue(){
        var result = mentorDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = mentorDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> mentorDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> mentorDao.findById(0));
    }

    @Test
    public void testFindByUserIdReturnTrue(){
        var result = mentorDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByUserIdReturnFalse(){
        var result = mentorDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUserIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> mentorDao.findById(-1));
    }

    @Test
    public void testFindByUserIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> mentorDao.findById(0));
    }
    
    // TODO: Написать тест для метода getAvailableMentors() - получение списка доступных наставников
    
    // TODO: Написать тест для метода getMentorsByDirection(Direction direction) - получение наставников по направлению
    
    // TODO: Написать тест для метода addDirectionToMentor(Integer mentorId, Direction direction) - добавление направления наставнику
    
    // TODO: Написать тест для метода removeDirectionFromMentor(Integer mentorId, Direction direction) - удаление направления у наставника
    
    // TODO: Написать тест для метода setAvailability(Integer mentorId, boolean available) - установка доступности наставника
    
    // TODO: Написать тест для метода getMenteesByMentorId(Integer mentorId) - получение подопечных наставника
}