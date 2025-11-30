package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.model.Mentor;
import ru.educationsystem.educationsystem.repository.DirectionDao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DirectionService {
    private final DirectionDao directionDao;

    public DirectionService() {
        this.directionDao = new DirectionDao();
    }

    public DirectionService(DirectionDao directionDao) {
        this.directionDao = directionDao;
    }

    public Direction createDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Направление не может быть null");
        }
        if (direction.getName() == null || direction.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название направления не может быть пустым");
        }

        // Проверяем, что направление с таким названием еще не существует
        Direction existingDirection = directionDao.findByName(direction.getName());
        if (existingDirection != null) {
            throw new IllegalArgumentException("Направление с таким названием уже существует");
        }

        directionDao.save(direction);
        return direction;
    }

    public Direction updateDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Направление не может быть null");
        }
        if (direction.getId() == null || direction.getId() <= 0) {
            throw new IllegalArgumentException("ID направления должен быть положительным числом");
        }
        if (direction.getName() == null || direction.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название направления не может быть пустым");
        }

        Optional<Direction> existingDirection = directionDao.findById(direction.getId());
        if (existingDirection.isEmpty()) {
            throw new IllegalArgumentException("Направление с таким ID не найдено");
        }

        directionDao.update(direction);
        return direction;
    }

    public Optional<Direction> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return directionDao.findById(id);
    }

    public List<Direction> findAll() {
        return directionDao.findAllWithMentors();
    }

    public Direction findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        return directionDao.findByName(name);
    }

    public void deleteDirection(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }

        Optional<Direction> direction = directionDao.findById(id);
        if (direction.isEmpty()) {
            throw new IllegalArgumentException("Направление с таким ID не найдено");
        }

        // Проверяем, есть ли у направления связанные наставники
        Set<Mentor> mentors = direction.get().getMentors();
        if (mentors != null && !mentors.isEmpty()) {
            throw new IllegalStateException("Невозможно удалить направление, так как с ним связаны наставники");
        }

        directionDao.deleteById(id);
    }

    public Set<Mentor> getMentorsByDirection(Integer directionId) {
        if (directionId == null || directionId <= 0) {
            throw new IllegalArgumentException("ID направления должен быть положительным числом");
        }

        Optional<Direction> direction = directionDao.findById(directionId);
        if (direction.isEmpty()) {
            throw new IllegalArgumentException("Направление с таким ID не найдено");
        }

        return direction.get().getMentors();
    }
}
