package ru.educationsystem.educationsystem.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.MentorDao;
import ru.educationsystem.educationsystem.util.HibernateSessionFactoryUtil;
import java.util.List;

public class MentorService extends BaseService<Mentor, MentorDao> {
    public MentorService(MentorDao mentorDao) {
        super(mentorDao);
    }

    public List<Mentor> findMentorsByDirection(int directionId) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Mentor> result = dao.findMentorsByDirection(directionId);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public List<Mentor> findAvailableMentors() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Mentor> result = dao.findAvailableMentors();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public Mentor createMentor(String lastName, String firstName, String middleName, 
                              String email, String specialization, Boolean available) {
        Mentor mentor = new Mentor();
        mentor.setLastName(lastName);
        mentor.setFirstName(firstName);
        mentor.setMiddleName(middleName);
        mentor.setEmail(email);
        mentor.setSpecialization(specialization);
        mentor.setAvailable(available != null ? available : true);
        return save(mentor);
    }

    public Mentor updateMentor(Mentor mentor) {
        return update(mentor);
    }

    public void deleteMentor(Mentor mentor) {
        delete(mentor);
    }

    public Mentor getMentorById(int id) {
        return findOne(id);
    }

    public List<Mentor> getAllMentors() {
        return findAll();
    }
    
    public List<Mentor> getAllMentorsWithDirections() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Mentor> result = dao.findAllWithDirections();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}