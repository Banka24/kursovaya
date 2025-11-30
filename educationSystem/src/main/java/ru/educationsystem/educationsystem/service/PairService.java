package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.model.PairStatus;
import ru.educationsystem.educationsystem.repository.PairDao;

import java.util.List;
import java.util.Optional;

public class PairService {
    private final PairDao pairDao;

    public PairService() {
        this.pairDao = new PairDao();
    }

    public PairService(PairDao pairDao) {
        this.pairDao = pairDao;
    }

    public Pair createPair(Pair pair) {
        if (pair == null) {
            throw new IllegalArgumentException("Пара не может быть null");
        }
        if (pair.getMentor() == null) {
            throw new IllegalArgumentException("Наставник не может быть null");
        }
        if (pair.getMentee() == null) {
            throw new IllegalArgumentException("Подопечный не может быть null");
        }
        if (pair.getStartDate() == null) {
            throw new IllegalArgumentException("Дата начала не может быть null");
        }
        pairDao.save(pair);
        return pair;
    }

    public Pair updatePair(Pair pair) {
        if (pair == null) {
            throw new IllegalArgumentException("Пара не может быть null");
        }
        if (pair.getId() == null || pair.getId() <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }

        Optional<Pair> existingPair = pairDao.findById(pair.getId());
        if (existingPair.isEmpty()) {
            throw new IllegalArgumentException("Пара с таким ID не найдена");
        }

        pairDao.update(pair);
        return pair;
    }

    public Optional<Pair> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return pairDao.findById(id);
    }

    public List<Pair> getAllPairs() {
        return pairDao.findAll();
    }

    public List<Pair> getPairsByMentorId(Integer mentorId) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }
        return pairDao.findByMentorId(mentorId);
    }

    public List<Pair> getPairsByMenteeId(Integer menteeId) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }
        return pairDao.findByMenteeId(menteeId);
    }

    public List<Pair> getPairsByStatus(PairStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null");
        }
        return pairDao.findByStatus(status);
    }

    public Pair changePairStatus(Integer pairId, PairStatus status) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null");
        }

        Optional<Pair> pairOpt = pairDao.findById(pairId);
        if (pairOpt.isEmpty()) {
            throw new IllegalArgumentException("Пара с таким ID не найдена");
        }

        Pair pair = pairOpt.get();
        pair.setStatus(status);
        pairDao.update(pair);
        return pair;
    }

    public List<Pair> getActivePairs() {
        return pairDao.findByStatus(PairStatus.ACTIVE);
    }

    public List<Pair> getCompletedPairs() {
        return pairDao.findByStatus(PairStatus.COMPLETED);
    }

    public boolean deletePair(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }

        Optional<Pair> pairOpt = pairDao.findById(id);
        if (pairOpt.isEmpty()) {
            throw new IllegalArgumentException("Пара с таким ID не найдена");
        }

        pairDao.deleteById(id);
        return true;
    }

    public List<Pair> getActivePairsByMentorId(Integer mentorId) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }
        return pairDao.findByMentorIdAndStatus(mentorId, PairStatus.ACTIVE);
    }

    // Псевдонимы для методов, используемых в контроллерах
    public Pair create(Pair pair) {
        return createPair(pair);
    }
}