package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.model.Level;
import ru.educationsystem.educationsystem.repository.BaseDao;

import static org.junit.jupiter.api.Assertions.*;

class LevelServiceTest {

    private BaseDao<Level> levelBaseDao;

    @BeforeEach
    void setUp() {
        levelBaseDao = new BaseDao<>(Level.class);
    }

    // TODO: Написать тест для метода createLevel(Level level) - создание нового уровня

    // TODO: Написать тест для метода updateLevel(Level level) - обновление уровня

    @Test
    public void testFindByIdReturnTrue(){
        var result = levelBaseDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = levelBaseDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> levelBaseDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> levelBaseDao.findById(0));
    }

    @Test
    public void testFindByNameReturnNull(){
        var result = levelBaseDao.findByName("test");
        assertNull(result);
    }

    @Test
    public void testFindByNameReturnLevel(){
        var result = levelBaseDao.findByName("Junior");
        assertNotNull(result);
    }

    @Test
    public void testFindByNameNameIsEmptyReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> levelBaseDao.findByName(""));
    }

    @Test
    public void testFindByNameNameIsNullReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> levelBaseDao.findByName(null));
    }

    @Test
    public void testGetAllLevelsReturnList(){
        var result = levelBaseDao.findAll();
        assertNotNull(result);
    }

    // TODO: Написать тест для метода deleteLevel(Integer id) - удаление уровня

    // TODO: Написать тест для метода getMenteesByLevelId(Integer levelId) - получение подопечных по уровню
}