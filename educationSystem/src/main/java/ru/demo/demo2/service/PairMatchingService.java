package ru.demo.demo2.service;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.time.LocalDate;
import java.util.*;

public class PairMatchingService {
    private final MentorDao mentorDao = new MentorDao();
    private final MenteeDao menteeDao = new MenteeDao();
    private final PairDao pairDao = new PairDao();
    private DirectionDao directionDao = new DirectionDao();

    public List<Mentor> findAvailableMentors() {
        return mentorDao.findAvailable();
    }

    public List<Mentor> findMentorsByDirection(Direction direction) {
        List<Mentor> result = new ArrayList<>();
        for (Mentor m : mentorDao.findAll()) {
            if (m.getAvailable() && m.getDirections().contains(direction)) {
                result.add(m);
            }
        }
        return result;
    }

    public void createPair(Mentor mentor, Mentee mentee) {
        if (!mentor.getAvailable()) {
            throw new RuntimeException("Наставник недоступен");
        }
        Pair existing = pairDao.findActivePairByMentee(mentee.getId());
        if (existing != null) {
            throw new RuntimeException("У подопечного уже есть активная пара");
        }
        Pair pair = new Pair();
        pair.setMentor(mentor);
        pair.setMentee(mentee);
        pair.setStartDate(LocalDate.now());
        pair.setStatus("active");
        pairDao.save(pair);
    }

    public void updatePairStatus(Pair pair, String newStatus) {
        if (!newStatus.equals("active") && !newStatus.equals("paused") && !newStatus.equals("completed")) {
            throw new RuntimeException("Неверный статус");
        }
        pair.setStatus(newStatus);
        pairDao.update(pair);
    }

    public List<Pair> getActivePairs() {
        return pairDao.findByStatus("active");
    }

    public List<Pair> getCompletedPairs() {
        return pairDao.findByStatus("completed");
    }

    public List<Pair> getAllPairs() {
        return pairDao.findAll();
    }

    public List<Mentee> getMenteesWithoutPair() {
        List<Mentee> result = new ArrayList<>();
        for (Mentee m : menteeDao.findAll()) {
            if (pairDao.findActivePairByMentee(m.getId()) == null) {
                result.add(m);
            }
        }
        return result;
    }

    public DirectionDao getDirectionDao() {
        return directionDao;
    }

    public void setDirectionDao(DirectionDao directionDao) {
        this.directionDao = directionDao;
    }
}