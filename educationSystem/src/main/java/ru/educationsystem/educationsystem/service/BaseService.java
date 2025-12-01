package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.repository.BaseDao;
import java.util.List;

public abstract class BaseService<T, D extends BaseDao<T>> {
    protected final D dao;

    public BaseService(D dao) {
        this.dao = dao;
    }

    public T save(T entity) {
        dao.save(entity);
        return entity;
    }

    public T update(T entity) {
        dao.update(entity);
        return entity;
    }

    public void delete(T entity) {
        dao.delete(entity);
    }

    public void deleteById(long entityId) {
        dao.deleteById(entityId);
    }

    public T findOne(long id) {
        return dao.findOne(id);
    }

    public List<T> findAll() {
        return dao.findAll();
    }
}