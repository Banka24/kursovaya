package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.PairDao;
import java.time.LocalDate;
import java.util.List;

public class PairService extends BaseService<Pair, PairDao> {
    public PairService(PairDao pairDao) {
        super(pairDao);
    }

    public List<Pair> findPairsByMentor(long mentorId) {
        return dao.findPairsByMentor(mentorId);
    }

    public List<Pair> findPairsByMentee(long menteeId) {
        return dao.findPairsByMentee(menteeId);
    }

    public List<Pair> findActivePairs() {
        return dao.findActivePairs();
    }

    public Pair createPair(Mentor mentor, Mentee mentee, String status) {
        Pair pair = new Pair();
        pair.setMentor(mentor);
        pair.setMentee(mentee);
        pair.setStatus(status);
        pair.setStartDate(LocalDate.now());
        return save(pair);
    }

    public Pair updatePair(Pair pair) {
        return update(pair);
    }

    public void deletePair(Pair pair) {
        delete(pair);
    }

    public Pair getPairById(int id) {
        return findOne(id);
    }

    public List<Pair> getAllPairs() {
        return findAll();
    }
    
    public List<Pair> getAllPairsWithMentorAndMentee() {
        return dao.findAllWithMentorAndMentee();
    }
}