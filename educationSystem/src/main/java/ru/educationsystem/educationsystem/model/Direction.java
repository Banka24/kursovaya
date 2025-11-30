package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "directions")
public class Direction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "directions")
    private Set<Mentor> mentors;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Mentor> getMentors() {
        return mentors;
    }

    public void setMentors(Set<Mentor> mentors) {
        this.mentors = mentors;
    }
}