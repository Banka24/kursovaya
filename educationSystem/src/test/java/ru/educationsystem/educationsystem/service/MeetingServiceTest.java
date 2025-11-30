package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationsystem.educationsystem.model.Meeting;
import ru.educationsystem.educationsystem.repository.MeetingDao;

import static org.junit.jupiter.api.Assertions.*;

class MeetingServiceTest {
    private MeetingDao meetingDao;

    @BeforeEach
    void setUp() {
        meetingDao = new MeetingDao();
    }

    // TODO: Написать тест для метода createMeeting(Meeting meeting) - создание новой встречи

    // TODO: Написать тест для метода updateMeeting(Meeting meeting) - обновление встречи

    @Test
    public void testFindByIdReturnTrue(){
        var result = meetingDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = meetingDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> meetingDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> meetingDao.findById(0));
    }

    @Test
    public void testGetAllMeetingsReturnList(){
        var result = meetingDao.findAll();
        assertNotNull(result);
    }

    // TODO: Написать тест для метода getMeetingsByPairId(Integer pairId) - получение встреч по паре

    // TODO: Написать тест для метода getUpcomingMeetings(Integer pairId) - получение предстоящих встреч

    // TODO: Написать тест для метода getPastMeetings(Integer pairId) - получение прошедших встреч

    // TODO: Написать тест для метода cancelMeeting(Integer meetingId) - отмена встречи

    // TODO: Написать тест для метода rescheduleMeeting(Integer meetingId, LocalDateTime newDateTime) - перенос встречи

    // TODO: Написать тест для метода deleteMeeting(Integer id) - удаление встречи

    // TODO: Написать тест для метода getMeetingsByDateRange(Integer pairId, LocalDate startDate, LocalDate endDate) - получение встреч за период
}