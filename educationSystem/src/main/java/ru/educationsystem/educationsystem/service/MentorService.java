package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.MentorDao;

import java.util.List;
import java.util.Optional;

public class MentorService {
    private final MentorDao mentorDao;

    public MentorService() {
        this.mentorDao = new MentorDao();
    }

    public MentorService(MentorDao mentorDao) {
        this.mentorDao = mentorDao;
    }

    public Mentor createMentor(Mentor mentor) {
        if (mentor == null) {
            throw new IllegalArgumentException("Наставник не может быть null");
        }
        if (mentor.getUser() == null) {
            throw new IllegalArgumentException("Пользователь наставника не может быть null");
        }
        mentorDao.save(mentor);
        return mentor;
    }

    public Mentor updateMentor(Mentor mentor) {
        if (mentor == null) {
            throw new IllegalArgumentException("Наставник не может быть null");
        }
        if (mentor.getId() == null || mentor.getId() <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }

        Optional<Mentor> existingMentor = mentorDao.findById(mentor.getId());
        if (existingMentor.isEmpty()) {
            throw new IllegalArgumentException("Наставник с таким ID не найден");
        }

        mentorDao.update(mentor);
        return mentor;
    }

    public Optional<Mentor> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return mentorDao.findById(id);
    }

    public Optional<Mentor> findByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
        return mentorDao.findByUserId(userId);
    }

    public List<Mentor> getAvailableMentors() {
        return mentorDao.findByAvailable(true);
    }

    public List<Mentor> getMentorsByDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Направление не может быть null");
        }
        return mentorDao.findByDirection(direction);
    }

    public Mentor addDirectionToMentor(Integer mentorId, Direction direction) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Направление не может быть null");
        }

        Optional<Mentor> mentorOpt = mentorDao.findById(mentorId);
        if (mentorOpt.isEmpty()) {
            throw new IllegalArgumentException("Наставник с таким ID не найден");
        }

        Mentor mentor = mentorOpt.get();
        if (mentor.getDirections() == null) {
            mentor.setDirections(java.util.HashSet.newHashSet(4));
        }

        mentor.getDirections().add(direction);
        mentorDao.update(mentor);
        return mentor;
    }

    public Mentor removeDirectionFromMentor(Integer mentorId, Direction direction) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Направление не может быть null");
        }

        Optional<Mentor> mentorOpt = mentorDao.findById(mentorId);
        if (mentorOpt.isEmpty()) {
            throw new IllegalArgumentException("Наставник с таким ID не найден");
        }

        Mentor mentor = mentorOpt.get();
        if (mentor.getDirections() != null) {
            mentor.getDirections().remove(direction);
            mentorDao.update(mentor);
        }

        return mentor;
    }

    public Mentor setAvailability(Integer mentorId, boolean available) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }

        Optional<Mentor> mentorOpt = mentorDao.findById(mentorId);
        if (mentorOpt.isEmpty()) {
            throw new IllegalArgumentException("Наставник с таким ID не найден");
        }

        Mentor mentor = mentorOpt.get();
        mentor.setAvailable(available);
        mentorDao.update(mentor);
        return mentor;
    }

    public List<Mentee> getMenteesByMentorId(Integer mentorId) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }

        Optional<Mentor> mentorOpt = mentorDao.findById(mentorId);
        if (mentorOpt.isEmpty()) {
            throw new IllegalArgumentException("Наставник с таким ID не найден");
        }

        // Получаем список пар для данного наставника и извлекаем подопечных
        Mentor mentor = mentorOpt.get();
        if (mentor.getPairs() == null) {
            return List.of();
        }

        return mentor.getPairs().stream()
                .map(pair -> pair.getMentee())
                .toList();
    }
}
