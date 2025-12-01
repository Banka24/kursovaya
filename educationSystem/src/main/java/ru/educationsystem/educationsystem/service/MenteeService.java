package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.repository.MenteeDao;
import java.util.List;

public class MenteeService extends BaseService<Mentee, MenteeDao> {
    public MenteeService(MenteeDao menteeDao) {
        super(menteeDao);
    }

    public Mentee createMentee(String lastName, String firstName, String middleName, 
                              String email, String goals, Short currentLevel) {
        Mentee mentee = new Mentee();
        mentee.setLastName(lastName);
        mentee.setFirstName(firstName);
        mentee.setMiddleName(middleName);
        mentee.setEmail(email);
        mentee.setGoals(goals);
        mentee.setCurrentLevel(currentLevel);
        return save(mentee);
    }

    public Mentee updateMentee(Mentee mentee) {
        return update(mentee);
    }

    public void deleteMentee(Mentee mentee) {
        delete(mentee);
    }

    public Mentee getMenteeById(int id) {
        return findOne(id);
    }

    public List<Mentee> getAllMentees() {
        return findAll();
    }
    
    public List<Mentee> getAllMenteesWithPairs() {
        return dao.findAllWithPairs();
    }
}