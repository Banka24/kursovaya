package ru.demo.demo2.repository;
import org.junit.jupiter.api.Test;
import ru.demo.demo2.model.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DaoTest {
    private MentorDao mentorDao = new MentorDao();
    private MenteeDao menteeDao = new MenteeDao();
    private DirectionDao directionDao = new DirectionDao();
    private PairDao pairDao = new PairDao();
    private DevelopmentPlanDao planDao = new DevelopmentPlanDao();
    private MeetingDao meetingDao = new MeetingDao();

    @Test void shouldFindAllMentors() {
        List<Mentor> mentors = mentorDao.findAll();
        assertNotNull(mentors);
        assertFalse(mentors.isEmpty());
    }

    @Test void shouldFindMentorById() {
        Mentor m = mentorDao.findAll().get(0);
        Mentor found = mentorDao.findById(m.getId());
        assertNotNull(found);
        assertEquals(m.getFullName(), found.getFullName());
    }

    @Test void shouldFindAvailableMentors() {
        List<Mentor> available = mentorDao.findAvailable();
        assertNotNull(available);
        for (Mentor m : available) {
            assertTrue(m.getAvailable());
        }
    }

    @Test void shouldFindAllMentees() {
        List<Mentee> mentees = menteeDao.findAll();
        assertNotNull(mentees);
        assertFalse(mentees.isEmpty());
    }

    @Test void shouldFindMenteeById() {
        Mentee m = menteeDao.findAll().get(0);
        Mentee found = menteeDao.findById(m.getId());
        assertNotNull(found);
        assertEquals(m.getFullName(), found.getFullName());
    }

    @Test void shouldFindAllDirections() {
        List<Direction> directions = directionDao.findAll();
        assertNotNull(directions);
        assertFalse(directions.isEmpty());
    }

    @Test void shouldFindDirectionByName() {
        Direction d = directionDao.findByName("IT");
        assertNotNull(d);
        assertEquals("IT", d.getName());
    }

    @Test void shouldFindAllPairs() {
        List<Pair> pairs = pairDao.findAll();
        assertNotNull(pairs);
        assertFalse(pairs.isEmpty());
    }

    @Test void shouldFindPairsByStatus() {
        List<Pair> active = pairDao.findByStatus("active");
        assertNotNull(active);
        for (Pair p : active) {
            assertEquals("active", p.getStatus());
        }
    }

    @Test void shouldFindAllPlans() {
        List<DevelopmentPlan> plans = planDao.findAll();
        assertNotNull(plans);
        assertFalse(plans.isEmpty());
    }

    @Test void shouldFindPlansByPairId() {
        Pair p = pairDao.findAll().get(0);
        List<DevelopmentPlan> plans = planDao.findByPairId(p.getId());
        assertNotNull(plans);
    }

    @Test void shouldFindAllMeetings() {
        List<Meeting> meetings = meetingDao.findAll();
        assertNotNull(meetings);
        assertFalse(meetings.isEmpty());
    }

    @Test void shouldFindMeetingsByPairId() {
        Pair p = pairDao.findAll().get(0);
        List<Meeting> meetings = meetingDao.findByPairId(p.getId());
        assertNotNull(meetings);
    }

    @Test void shouldCalculateAverageRating() {
        Pair p = pairDao.findAll().get(0);
        Double avg = meetingDao.getAverageMentorRating(p.getId());
        assertNotNull(avg);
    }
}
