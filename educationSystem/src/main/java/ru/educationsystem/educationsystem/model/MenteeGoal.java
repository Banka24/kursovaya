package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mentee_goals")
public class MenteeGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @Column(name = "goal_text", nullable = false)
    private String goalText;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public String getGoalText() {
        return goalText;
    }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}