package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.MentorDao;
import java.util.List;

public class MentorService extends BaseService<Mentor, MentorDao> {
    public MentorService(MentorDao mentorDao) {
        super(mentorDao);
    }

    public List<Mentor> findMentorsByDirection(int directionId) {
        return dao.findMentorsByDirection(directionId);
    }

    public List<Mentor> findAvailableMentors() {
        return dao.findAvailableMentors();
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
        return dao.findAllWithDirections();
    }
}