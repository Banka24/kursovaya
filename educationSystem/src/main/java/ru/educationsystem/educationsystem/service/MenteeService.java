package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Level;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.MenteeGoal;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.MenteeDao;

import java.util.List;
import java.util.Optional;

public class MenteeService {
    private final MenteeDao menteeDao;

    public MenteeService() {
        this.menteeDao = new MenteeDao();
    }

    public MenteeService(MenteeDao menteeDao) {
        this.menteeDao = menteeDao;
    }

    public Mentee createMentee(Mentee mentee) {
        if (mentee == null) {
            throw new IllegalArgumentException("Подопечный не может быть null");
        }
        if (mentee.getUser() == null) {
            throw new IllegalArgumentException("Пользователь подопечного не может быть null");
        }
        if (mentee.getLevel() == null) {
            throw new IllegalArgumentException("Уровень подопечного не может быть null");
        }
        menteeDao.save(mentee);
        return mentee;
    }

    public Mentee updateMentee(Mentee mentee) {
        if (mentee == null) {
            throw new IllegalArgumentException("Подопечный не может быть null");
        }
        if (mentee.getId() == null || mentee.getId() <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }

        Optional<Mentee> existingMentee = menteeDao.findById(mentee.getId());
        if (existingMentee.isEmpty()) {
            throw new IllegalArgumentException("Подопечный с таким ID не найден");
        }

        menteeDao.update(mentee);
        return mentee;
    }

    public Optional<Mentee> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return menteeDao.findById(id);
    }

    public List<Mentee> getMenteesByLevel(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }
        return menteeDao.findByLevel(level);
    }

    public Mentee setMenteeLevel(Integer menteeId, Level level) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }

        Optional<Mentee> menteeOpt = menteeDao.findById(menteeId);
        if (menteeOpt.isEmpty()) {
            throw new IllegalArgumentException("Подопечный с таким ID не найден");
        }

        Mentee mentee = menteeOpt.get();
        mentee.setLevel(level);
        menteeDao.update(mentee);
        return mentee;
    }

    public Mentee addGoalToMentee(Integer menteeId, MenteeGoal goal) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }
        if (goal == null) {
            throw new IllegalArgumentException("Цель не может быть null");
        }

        Optional<Mentee> menteeOpt = menteeDao.findById(menteeId);
        if (menteeOpt.isEmpty()) {
            throw new IllegalArgumentException("Подопечный с таким ID не найден");
        }

        Mentee mentee = menteeOpt.get();
        goal.setMentee(mentee);

        if (mentee.getMenteeGoals() == null) {
            mentee.setMenteeGoals(new java.util.ArrayList<>(10));
        }

        mentee.getMenteeGoals().add(goal);
        menteeDao.update(mentee);
        return mentee;
    }

    public Mentee removeGoalFromMentee(Integer menteeId, MenteeGoal goal) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }
        if (goal == null) {
            throw new IllegalArgumentException("Цель не может быть null");
        }

        Optional<Mentee> menteeOpt = menteeDao.findById(menteeId);
        if (menteeOpt.isEmpty()) {
            throw new IllegalArgumentException("Подопечный с таким ID не найден");
        }

        Mentee mentee = menteeOpt.get();
        if (mentee.getMenteeGoals() != null) {
            mentee.getMenteeGoals().remove(goal);
            menteeDao.update(mentee);
        }

        return mentee;
    }

    public List<MenteeGoal> getGoalsByMenteeId(Integer menteeId) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }

        Optional<Mentee> menteeOpt = menteeDao.findById(menteeId);
        if (menteeOpt.isEmpty()) {
            throw new IllegalArgumentException("Подопечный с таким ID не найден");
        }

        Mentee mentee = menteeOpt.get();
        return mentee.getMenteeGoals() != null ? mentee.getMenteeGoals() : List.of();
    }

    public List<Mentor> getMentorsByMenteeId(Integer menteeId) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }

        Optional<Mentee> menteeOpt = menteeDao.findById(menteeId);
        if (menteeOpt.isEmpty()) {
            throw new IllegalArgumentException("Подопечный с таким ID не найден");
        }

        Mentee mentee = menteeOpt.get();
        if (mentee.getPairs() == null) {
            return List.of();
        }

        return mentee.getPairs().stream()
                .map(pair -> pair.getMentor())
                .toList();
    }
}
