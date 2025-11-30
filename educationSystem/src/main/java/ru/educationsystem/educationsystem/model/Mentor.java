package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "mentors")
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "mentor_directions",
        joinColumns = @JoinColumn(name = "mentor_id"),
        inverseJoinColumns = @JoinColumn(name = "direction_id")
    )
    private Set<Direction> directions;

    @OneToMany(mappedBy = "mentor")
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

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Set<Direction> getDirections() {
        return directions;
    }

    public void setDirections(Set<Direction> directions) {
        this.directions = directions;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}