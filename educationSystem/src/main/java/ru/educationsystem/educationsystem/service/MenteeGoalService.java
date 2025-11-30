package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.MenteeGoal;
import ru.educationsystem.educationsystem.repository.MenteeDao;
import ru.educationsystem.educationsystem.repository.MenteeGoalDao;

import java.util.List;
import java.util.Optional;

public class MenteeGoalService {
    private final MenteeGoalDao menteeGoalDao;
    private final MenteeDao menteeDao;

    public MenteeGoalService() {
        this.menteeGoalDao = new MenteeGoalDao();
        this.menteeDao = new MenteeDao();
    }

    public MenteeGoalService(MenteeGoalDao menteeGoalDao, MenteeDao menteeDao) {
        this.menteeGoalDao = menteeGoalDao;
        this.menteeDao = menteeDao;
    }

    public MenteeGoal createGoal(MenteeGoal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Цель не может быть null");
        }
        if (goal.getMentee() == null) {
            throw new IllegalArgumentException("Подопечный не может быть null");
        }
        if (goal.getGoalText() == null || goal.getGoalText().trim().isEmpty()) {
            throw new IllegalArgumentException("Текст цели не может быть пустым");
        }
        menteeGoalDao.save(goal);
        return goal;
    }

    public MenteeGoal updateGoal(MenteeGoal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Цель не может быть null");
        }
        if (goal.getId() == null || goal.getId() <= 0) {
            throw new IllegalArgumentException("ID цели должен быть положительным числом");
        }

        Optional<MenteeGoal> existingGoal = menteeGoalDao.findById(goal.getId());
        if (existingGoal.isEmpty()) {
            throw new IllegalArgumentException("Цель с таким ID не найдена");
        }

        menteeGoalDao.update(goal);
        return goal;
    }

    public Optional<MenteeGoal> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return menteeGoalDao.findById(id);
    }

    public List<MenteeGoal> getAllMenteeGoals() {
        return menteeGoalDao.findAll();
    }

    public List<MenteeGoal> getGoalsByMenteeId(Integer menteeId) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }
        return menteeGoalDao.findByMenteeId(menteeId);
    }

    public MenteeGoal markGoalAsCompleted(Integer goalId) {
        if (goalId == null || goalId <= 0) {
            throw new IllegalArgumentException("ID цели должен быть положительным числом");
        }

        Optional<MenteeGoal> goalOpt = menteeGoalDao.findById(goalId);
        if (goalOpt.isEmpty()) {
            throw new IllegalArgumentException("Цель с таким ID не найдена");
        }

        MenteeGoal goal = goalOpt.get();
        goal.setStatus("COMPLETED");
        menteeGoalDao.update(goal);
        return goal;
    }

    public boolean deleteGoal(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID цели должен быть положительным числом");
        }

        Optional<MenteeGoal> goalOpt = menteeGoalDao.findById(id);
        if (goalOpt.isEmpty()) {
            throw new IllegalArgumentException("Цель с таким ID не найдена");
        }

        menteeGoalDao.deleteById(id);
        return true;
    }

    public List<MenteeGoal> getGoalsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Статус не может быть пустым");
        }
        return menteeGoalDao.findByStatus(status);
    }
}
