package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "mentees")
public class Mentee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "goals", columnDefinition = "TEXT")
    private String goals;

    @Column(name = "current_level")
    private Short currentLevel;

    // Связи с другими сущностями
    @OneToMany(mappedBy = "mentee")
    private Set<Pair> pairs = new HashSet<>();

    public Mentee() {
    }

    public Mentee(String lastName, String firstName, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public Short getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Short currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Set<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(Set<Pair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mentee mentee = (Mentee) o;
        return id != null && Objects.equals(id, mentee.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Mentee{" +
                "id=" + id +
                ", lastName='" + lastName +
                ", firstName='" + firstName +
                ", middleName='" + middleName +
                ", email='" + email +
                ", goals='" + goals +
                ", currentLevel=" + currentLevel +
                '}';
    }
}