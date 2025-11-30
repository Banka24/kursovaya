package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pairs")
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PairStatus status = PairStatus.ACTIVE;

    @OneToMany(mappedBy = "pair")
    private List<DevelopmentPlan> developmentPlans;

    @OneToMany(mappedBy = "pair")
    private List<Meeting> meetings;

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

    public PairStatus getStatus() {
        return status;
    }

    public void setStatus(PairStatus status) {
        this.status = status;
    }

    public List<DevelopmentPlan> getDevelopmentPlans() {
        return developmentPlans;
    }

    public void setDevelopmentPlans(List<DevelopmentPlan> developmentPlans) {
        this.developmentPlans = developmentPlans;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }
}