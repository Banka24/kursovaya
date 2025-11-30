package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.model.PairStatus;

import java.util.List;
import java.util.Optional;

public class PairDao extends BaseDao<Pair> {

    public PairDao() {
        super(Pair.class);
    }

    public List<Pair> findByStatus(PairStatus status) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT p FROM Pair p LEFT JOIN FETCH p.mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.mentee me LEFT JOIN FETCH me.user WHERE p.status = :status", Pair.class)
                    .setParameter("status", status)
                    .getResultList();
        }
    }

    public List<Pair> findByMentorId(Integer mentorId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT p FROM Pair p LEFT JOIN FETCH p.mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.mentee me LEFT JOIN FETCH me.user WHERE p.mentor.id = :mentorId", Pair.class)
                    .setParameter("mentorId", mentorId)
                    .getResultList();
        }
    }

    public List<Pair> findByMenteeId(Integer menteeId) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT p FROM Pair p LEFT JOIN FETCH p.mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.mentee me LEFT JOIN FETCH me.user WHERE p.mentee.id = :menteeId", Pair.class)
                    .setParameter("menteeId", menteeId)
                    .getResultList();
        }
    }

    public List<Pair> findByMentorIdAndStatus(Integer mentorId, PairStatus status) {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT p FROM Pair p LEFT JOIN FETCH p.mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.mentee me LEFT JOIN FETCH me.user WHERE p.mentor.id = :mentorId AND p.status = :status", Pair.class)
                    .setParameter("mentorId", mentorId)
                    .setParameter("status", status)
                    .getResultList();
        }
    }

    @Override
    public List<Pair> findAll() {
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                            "SELECT p FROM Pair p LEFT JOIN FETCH p.mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.mentee me LEFT JOIN FETCH me.user", Pair.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<Pair> findById(Integer id) {
        try (Session session = getCurrentSession()) {
            Pair pair = session.createQuery(
                            "SELECT p FROM Pair p LEFT JOIN FETCH p.mentor m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.mentee me LEFT JOIN FETCH me.user WHERE p.id = :id", Pair.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(pair);
        }
    }
}