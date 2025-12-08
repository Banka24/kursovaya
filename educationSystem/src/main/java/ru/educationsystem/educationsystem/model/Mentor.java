package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "mentors")
public class Mentor {
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

    @Column(name = "specialization", columnDefinition = "TEXT")
    private String specialization;

    @Column(name = "available")
    private Boolean available = true;

    // Связи с другими сущностями
    @ManyToMany
    @JoinTable(
        name = "mentor_directions",
        joinColumns = @JoinColumn(name = "mentor_id"),
        inverseJoinColumns = @JoinColumn(name = "direction_id")
    )
    private Set<Direction> directions = new HashSet<>();

    @OneToMany(mappedBy = "mentor")
    private Set<Pair> pairs = new HashSet<>();

    public Mentor() {
    }

    public Mentor(String lastName, String firstName, String email) {
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
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
        Mentor mentor = (Mentor) o;
        return id != null && Objects.equals(id, mentor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "id=" + id +
                ", lastName='" + lastName +
                ", firstName='" + firstName +
                ", middleName='" + middleName +
                ", email='" + email +
                ", specialization='" + specialization +
                ", available=" + available +
                '}';
    }
}