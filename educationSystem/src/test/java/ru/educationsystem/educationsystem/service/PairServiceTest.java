package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.PairDao;

import static org.junit.jupiter.api.Assertions.*;

class PairServiceTest {
    private PairDao pairDao;

    @BeforeEach
    void setUp() {
        pairDao = new PairDao();
    }

    // TODO: Написать тест для метода createPair(Pair pair) - создание новой пары

    // TODO: Написать тест для метода updatePair(Pair pair) - обновление данных пары

    @Test
    public void testFindByIdReturnTrue(){
        var result = pairDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = pairDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> pairDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> pairDao.findById(0));
    }

    @Test
    public void testGetAllPairsReturnList(){
        var result = pairDao.findAll();
        assertNotNull(result);
    }

    // TODO: Написать тест для метода getPairsByMentorId(Integer mentorId) - получение пар по ID наставника

    // TODO: Написать тест для метода getPairsByMenteeId(Integer menteeId) - получение пар по ID подопечного

    // TODO: Написать тест для метода getPairsByStatus(PairStatus status) - получение пар по статусу

    // TODO: Написать тест для метода changePairStatus(Integer pairId, PairStatus status) - изменение статуса пары

    // TODO: Написать тест для метода getActivePairs() - получение активных пар

    // TODO: Написать тест для метода getCompletedPairs() - получение завершенных пар

    // TODO: Написать тест для метода deletePair(Integer id) - удаление пары
}