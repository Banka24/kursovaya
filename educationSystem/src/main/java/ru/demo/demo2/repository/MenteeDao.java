package ru.demo.demo2.repository;
import ru.demo.demo2.model.Mentee;

public class MenteeDao extends BaseDao<Mentee> {
    public MenteeDao() {
        super(Mentee.class);
    }
}