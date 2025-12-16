package ru.demo.demo2.repository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.demo.demo2.util.HibernateSession;
import java.util.List;

public abstract class BaseDao<T> {
    private final Class<T> clazz;
    public BaseDao(Class<T> clazz) { this.clazz = clazz; }
    protected Session getSession() { return HibernateSession.getSessionFactory().openSession(); }
    
    public void save(T entity) {
        Session s = getSession(); Transaction tx = s.beginTransaction();
        s.persist(entity); tx.commit(); s.close();
    }

    public T findById(Integer id) {
        Session s = getSession(); T item = s.find(clazz, id); s.close(); return item;
    }

    public void delete(T entity) {
        Session s = getSession(); Transaction tx = s.beginTransaction();
        s.remove(entity); tx.commit(); s.close();
    }

    public void update(T entity) {
        Session s = getSession(); Transaction tx = s.beginTransaction();
        s.merge(entity); tx.commit(); s.close();
    }

    public void deleteById(long id) {
        delete(findById((int) id));
    }

    public List<T> findAll() {
        Session s = getSession(); Transaction tx = s.beginTransaction();
        List<T> items = s.createQuery("from " + clazz.getName(), clazz).list();
        tx.commit(); s.close(); return items;
    }
}