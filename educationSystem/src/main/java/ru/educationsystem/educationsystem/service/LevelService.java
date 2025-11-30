package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Level;
import ru.educationsystem.educationsystem.model.Mentee;
import ru.educationsystem.educationsystem.repository.BaseDao;
import ru.educationsystem.educationsystem.repository.MenteeDao;

import java.util.List;
import java.util.Optional;

public class LevelService {
    private final BaseDao<Level> levelDao;
    private final MenteeDao menteeDao;

    public LevelService() {
        this.levelDao = new BaseDao<>(Level.class);
        this.menteeDao = new MenteeDao();
    }

    public LevelService(BaseDao<Level> levelDao, MenteeDao menteeDao) {
        this.levelDao = levelDao;
        this.menteeDao = menteeDao;
    }

    public Level createLevel(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }
        if (level.getName() == null || level.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название уровня не может быть пустым");
        }

        // Проверяем, что название еще не используется
        Level existingLevel = levelDao.findByName(level.getName());
        if (existingLevel != null) {
            throw new IllegalArgumentException("Уровень с таким названием уже существует");
        }

        levelDao.save(level);
        return level;
    }

    public Level updateLevel(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Уровень не может быть null");
        }
        if (level.getId() == null || level.getId() <= 0) {
            throw new IllegalArgumentException("ID уровня должен быть положительным числом");
        }

        Optional<Level> existingLevel = levelDao.findById(level.getId());
        if (existingLevel.isEmpty()) {
            throw new IllegalArgumentException("Уровень с таким ID не найден");
        }

        // Если название изменено, проверяем, что оно не занято другим уровнем
        if (!existingLevel.get().getName().equals(level.getName())) {
            Level levelWithSameName = levelDao.findByName(level.getName());
            if (levelWithSameName != null) {
                throw new IllegalArgumentException("Уровень с таким названием уже существует");
            }
        }

        levelDao.update(level);
        return level;
    }

    public Optional<Level> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return levelDao.findById(id);
    }

    public Level findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        return levelDao.findByName(name);
    }

    public List<Level> getAllLevels() {
        return levelDao.findAll();
    }

    public boolean deleteLevel(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID уровня должен быть положительным числом");
        }

        Optional<Level> levelOpt = levelDao.findById(id);
        if (levelOpt.isEmpty()) {
            throw new IllegalArgumentException("Уровень с таким ID не найден");
        }

        // Проверяем, что нет подопечных с этим уровнем
        List<Mentee> mentees = menteeDao.findByLevel(levelOpt.get());
        if (!mentees.isEmpty()) {
            throw new IllegalArgumentException("Нельзя удалить уровень, так как существуют подопечные с этим уровнем");
        }

        levelDao.deleteById(id);
        return true;
    }

    public List<Mentee> getMenteesByLevelId(Integer levelId) {
        if (levelId == null || levelId <= 0) {
            throw new IllegalArgumentException("ID уровня должен быть положительным числом");
        }

        Optional<Level> levelOpt = levelDao.findById(levelId);
        if (levelOpt.isEmpty()) {
            throw new IllegalArgumentException("Уровень с таким ID не найден");
        }

        return menteeDao.findByLevel(levelOpt.get());
    }
}
