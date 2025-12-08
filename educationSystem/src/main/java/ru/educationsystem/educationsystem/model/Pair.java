package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "pairs")
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column(name = "status", nullable = false)
    private String status;

    // Связи с другими сущностями
    @OneToMany(mappedBy = "pair")
    private Set<DevelopmentPlan> developmentPlans = new HashSet<>();

    @OneToMany(mappedBy = "pair")
    private Set<Meeting> meetings = new HashSet<>();

    public Pair() {
    }

    public Pair(Mentor mentor, Mentee mentee, String status) {
        this.mentor = mentor;
        this.mentee = mentee;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<DevelopmentPlan> getDevelopmentPlans() {
        return developmentPlans;
    }

    public void setDevelopmentPlans(Set<DevelopmentPlan> developmentPlans) {
        this.developmentPlans = developmentPlans;
    }

    public Set<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(Set<Meeting> meetings) {
        this.meetings = meetings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return id != null && Objects.equals(id, pair.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Pair{" +
                "id=" + id +
                ", mentor=" + mentor +
                ", mentee=" + mentee +
                ", startDate=" + startDate +
                ", status='" + status +
                '}';
    }
}