package ru.demo.demo2.service;
import org.junit.jupiter.api.Test;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ProgressTrackingServiceTest {
    private final ProgressTrackingService service = new ProgressTrackingService();
    private final PairDao pairDao = new PairDao();

    @Test void shouldGetMeetingsForPair() {
        Pair p = pairDao.findAll().get(0);
        List<Meeting> meetings = service.getMeetingsForPair(p.getId());
        assertNotNull(meetings);
    }

    @Test void shouldGetPlansForPair() {
        Pair p = pairDao.findAll().get(0);
        List<DevelopmentPlan> plans = service.getPlansForPair(p.getId());
        assertNotNull(plans);
    }

    @Test void shouldCalculateAverageMentorRating() {
        Pair p = pairDao.findAll().get(0);
        Double avg = service.getAverageMentorRating(p.getId());
        assertNotNull(avg);
        assertTrue(avg >= 0 && avg <= 5);
    }

    @Test void shouldCalculateAverageMenteeRating() {
        Pair p = pairDao.findAll().get(0);
        Double avg = service.getAverageMenteeRating(p.getId());
        assertNotNull(avg);
        assertTrue(avg >= 0 && avg <= 5);
    }

    @Test void shouldGetPairStatistics() {
        Pair p = pairDao.findAll().get(0);
        Map<String, Object> stats = service.getPairStatistics(p.getId());
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalMeetings"));
        assertTrue(stats.containsKey("totalPlans"));
        assertTrue(stats.containsKey("avgMentorRating"));
        assertTrue(stats.containsKey("avgMenteeRating"));
    }

    @Test void shouldGetMeetingsCount() {
        Pair p = pairDao.findAll().get(0);
        int count = service.getMeetingsCount(p.getId());
        assertTrue(count >= 0);
    }

    @Test void shouldGetPlansCount() {
        Pair p = pairDao.findAll().get(0);
        int count = service.getPlansCount(p.getId());
        assertTrue(count >= 0);
    }

    @Test void shouldGetOverduePlans() {
        List<DevelopmentPlan> overdue = service.getOverduePlans();
        assertNotNull(overdue);
    }

    @Test void shouldAddMeeting() {
        Pair p = pairDao.findByStatus("active").get(0);
        Meeting m = service.addMeeting(p, "Тестовая тема", "Задачи выполнены", 4, 5);
        assertNotNull(m);
        assertNotNull(m.getId());
        assertEquals("Тестовая тема", m.getTopic());
        assertEquals(4, m.getMentorRating());
        assertEquals(5, m.getMenteeRating());
    }

    @Test void shouldAddPlan() {
        Pair p = pairDao.findByStatus("active").get(0);
        DevelopmentPlan plan = service.addPlan(p, "Тестовый план", "Описание плана", LocalDate.now().plusMonths(1));
        assertNotNull(plan);
        assertNotNull(plan.getId());
        assertEquals("Тестовый план", plan.getTitle());
    }
}
