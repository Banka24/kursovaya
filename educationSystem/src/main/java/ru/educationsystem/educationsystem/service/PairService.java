package ru.educationsystem.educationsystem.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.PairDao;
import ru.educationsystem.educationsystem.util.HibernateSessionFactoryUtil;
import java.time.LocalDate;
import java.util.List;

public class PairService extends BaseService<Pair, PairDao> {
    public PairService(PairDao pairDao) {
        super(pairDao);
    }

    public List<Pair> findPairsByMentor(long mentorId) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Pair> result = dao.findPairsByMentor(mentorId);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public List<Pair> findPairsByMentee(long menteeId) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Pair> result = dao.findPairsByMentee(menteeId);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public List<Pair> findActivePairs() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Pair> result = dao.findActivePairs();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
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
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Pair> result = dao.findAllWithMentorAndMentee();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}