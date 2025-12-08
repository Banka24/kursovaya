package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.educationsystem.educationsystem.util.HibernateSessionFactoryUtil;

import java.util.List;

public abstract class BaseDao<T> {
    private final Class<T> clazz;

    public BaseDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected Session getCurrentSession() {
        return HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
    }

    public void save(final T entity) {
        Session session = getCurrentSession();
        session.persist(entity);
    }

    public void update(final T entity) {
        Session session = getCurrentSession();
        session.merge(entity);
    }

    public void delete(final T entity) {
        Session session = getCurrentSession();
        // Re-attach entity if detached
        T managedEntity = session.merge(entity);
        session.remove(managedEntity);
    }

    public void deleteById(final long entityId) {
        Session session = getCurrentSession();
        T entity = session.get(clazz, entityId);
        if (entity != null) {
            session.remove(entity);
        }
    }

    public T findOne(final long id) {
        Session session = getCurrentSession();
        return session.get(clazz, id);
    }

    public List<T> findAll() {
        Session session = getCurrentSession();
        Query<T> query = session.createQuery("from " + clazz.getName(), clazz);
        return query.list();
    }
}