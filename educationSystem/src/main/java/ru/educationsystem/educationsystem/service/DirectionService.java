package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Direction;
import ru.educationsystem.educationsystem.repository.DirectionDao;
import java.util.List;

public class DirectionService extends BaseService<Direction, DirectionDao> {
    public DirectionService(DirectionDao directionDao) {
        super(directionDao);
    }

    public Direction createDirection(String name) {
        Direction direction = new Direction();
        direction.setName(name);
        return save(direction);
    }

    public Direction updateDirection(Direction direction) {
        return update(direction);
    }

    public void deleteDirection(Direction direction) {
        delete(direction);
    }

    public Direction getDirectionById(int id) {
        return findOne(id);
    }

    public List<Direction> getAllDirections() {
        return findAll();
    }
}