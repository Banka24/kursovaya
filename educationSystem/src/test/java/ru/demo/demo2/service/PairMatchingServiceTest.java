package ru.demo.demo2.service;
import org.junit.jupiter.api.Test;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PairMatchingServiceTest {
    private final PairMatchingService service = new PairMatchingService();
    private final MentorDao mentorDao = new MentorDao();
    private final MenteeDao menteeDao = new MenteeDao();
    private final PairDao pairDao = new PairDao();

    @Test void shouldFindAvailableMentors() {
        List<Mentor> available = service.findAvailableMentors();
        assertNotNull(available);
        for (Mentor m : available) {
            assertTrue(m.getAvailable());
        }
    }

    @Test void shouldGetActivePairs() {
        List<Pair> active = service.getActivePairs();
        assertNotNull(active);
        for (Pair p : active) {
            assertEquals("active", p.getStatus());
        }
    }

    @Test void shouldGetCompletedPairs() {
        List<Pair> completed = service.getCompletedPairs();
        assertNotNull(completed);
        for (Pair p : completed) {
            assertEquals("completed", p.getStatus());
        }
    }

    @Test void shouldGetAllPairs() {
        List<Pair> all = service.getAllPairs();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    @Test void shouldNotCreatePairWithUnavailableMentor() {
        Mentor unavailable = mentorDao.findAll().stream().filter(m -> !m.getAvailable()).findFirst().orElse(null);
        if (unavailable != null) {
            Mentee mentee = menteeDao.findAll().get(0);
            Exception e = assertThrows(RuntimeException.class, () -> service.createPair(unavailable, mentee));
            assertTrue(e.getMessage().contains("недоступен"));
        }
    }

    @Test void shouldValidatePairStatus() {
        Pair p = pairDao.findAll().get(0);
        String originalStatus = p.getStatus();
        Exception e = assertThrows(RuntimeException.class, () -> service.updatePairStatus(p, "invalid"));
        assertTrue(e.getMessage().contains("Неверный статус"));
        p.setStatus(originalStatus);
    }

    @Test void shouldGetMenteesWithoutPair() {
        List<Mentee> withoutPair = service.getMenteesWithoutPair();
        assertNotNull(withoutPair);
    }
}
