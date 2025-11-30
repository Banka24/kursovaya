package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.repository.BaseDao;

import static org.junit.jupiter.api.Assertions.*;

class DirectionServiceTest {

    private BaseDao<Direction> directionBaseDao;

    @BeforeEach
    void setUp() {
        directionBaseDao = new BaseDao<>(Direction.class);
    }

    @Test
    public void testFindByIdReturnTrue(){
        var result = directionBaseDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = directionBaseDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> directionBaseDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> directionBaseDao.findById(0));
    }

    @Test
    public void testFindByNameReturnNull(){
        var result = directionBaseDao.findByName("test");
        assertNull(result);
    }

    @Test
    public void testFindByNameReturnUser(){
        var result = directionBaseDao.findByName("IT");
        assertNotNull(result);
    }

    @Test
    public void testFindByNameNameIsEmptyReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> directionBaseDao.findByName(""));
    }

    @Test
    public void testFindByNameNameIsNullReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> directionBaseDao.findByName(null));
    }

    @Test
    public void testGetAllDirectionsReturnList(){
        var result = directionBaseDao.findAll();
        assertNotNull(result);
    }
}