package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "mentees")
public class Mentee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @OneToMany(mappedBy = "mentee")
    private List<MenteeGoal> menteeGoals;

    @OneToMany(mappedBy = "mentee")
    private List<Pair> pairs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Удобные методы для доступа к данным пользователя
    public String getLastName() {
        return user != null ? user.getLastName() : null;
    }

    public String getFirstName() {
        return user != null ? user.getFirstName() : null;
    }

    public String getMiddleName() {
        return user != null ? user.getMiddleName() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<MenteeGoal> getMenteeGoals() {
        return menteeGoals;
    }

    public void setMenteeGoals(List<MenteeGoal> menteeGoals) {
        this.menteeGoals = menteeGoals;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}