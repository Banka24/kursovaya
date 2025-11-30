package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pair_id", nullable = false)
    private Pair pair;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "tasks_done")
    private String tasksDone;

    @Column(name = "mentor_rating")
    private Integer mentorRating;

    @Column(name = "mentee_rating")
    private Integer menteeRating;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTasksDone() {
        return tasksDone;
    }

    public void setTasksDone(String tasksDone) {
        this.tasksDone = tasksDone;
    }

    public Integer getMentorRating() {
        return mentorRating;
    }

    public void setMentorRating(Integer mentorRating) {
        this.mentorRating = mentorRating;
    }

    public Integer getMenteeRating() {
        return menteeRating;
    }

    public void setMenteeRating(Integer menteeRating) {
        this.menteeRating = menteeRating;
    }
}