package ru.educationsystem.educationsystem.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.educationsystem.educationsystem.util.HibernateSession;

import java.util.List;
import java.util.Optional;

public class BaseDao<T> {
    private final Class<T> clazz;

    public BaseDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Session getCurrentSession() {
        return HibernateSession.getSessionFactory().openSession();
    }

    public void save(final T entity) {
        Transaction tx1 = null;
        try (Session session = getCurrentSession()) {
            tx1 = session.beginTransaction();
            session.persist(entity);
            tx1.commit();
        } catch (Exception e) {
            if (tx1 != null) {
                tx1.rollback();
            }
            System.err.println("Ошибка при сохранении сущности: " + e.getMessage());
            throw new RuntimeException("Ошибка при сохранении сущности", e);
        }
    }

    public void update(final T entity) {
        Transaction tx1 = null;
        try (Session session = getCurrentSession()) {
            tx1 = session.beginTransaction();
            session.merge(entity);
            tx1.commit();
        } catch (Exception e) {
            if (tx1 != null) {
                tx1.rollback();
            }
            System.err.println("Ошибка при обновлении сущности: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении сущности", e);
        }
    }

    public void deleteById(final Integer id) {
        Transaction tx1 = null;
        try (Session session = getCurrentSession()) {
            tx1 = session.beginTransaction();
            T entity = session.find(clazz, id);
            if (entity != null) {
                session.remove(entity);
            }
            tx1.commit();
        } catch (Exception e) {
            if (tx1 != null) {
                tx1.rollback();
            }
            System.err.println("Ошибка при удалении сущности по ID: " + e.getMessage());
            throw new RuntimeException("Ошибка при удалении сущности по ID", e);
        }
    }

    public Optional<T> findById(final Integer id) {
        try (Session session = getCurrentSession()) {
            T entity = session.find(clazz, id);
            return Optional.ofNullable(entity);
        }
    }

    public List<T> findAll() {
        Session session = null;
        try {
            session = getCurrentSession();
            session.beginTransaction();
            List<T> items = session.createQuery("from " + clazz.getName(), clazz).list();
            session.getTransaction().commit();
            return items;
        } catch (Exception e) {
            System.err.println("Ошибка при получении списка сущностей: " + e.getMessage());
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return List.of();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public T findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        try (Session session = getCurrentSession()) {
            return session.createQuery(
                "SELECT e FROM " + clazz.getName() + " e WHERE e.name = :name", clazz)
                .setParameter("name", name)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}