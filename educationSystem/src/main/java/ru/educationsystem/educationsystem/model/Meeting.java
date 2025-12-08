package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pair_id", nullable = false)
    private Pair pair;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "topic")
    private String topic;

    @Column(name = "tasks_done", columnDefinition = "TEXT")
    private String tasksDone;

    @Column(name = "mentor_rating")
    private Short mentorRating;

    @Column(name = "mentee_rating")
    private Short menteeRating;

    public Meeting() {
    }

    public Meeting(Pair pair, LocalDateTime datetime) {
        this.pair = pair;
        this.datetime = datetime;
    }

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

    public Short getMentorRating() {
        return mentorRating;
    }

    public void setMentorRating(Short mentorRating) {
        this.mentorRating = mentorRating;
    }

    public Short getMenteeRating() {
        return menteeRating;
    }

    public void setMenteeRating(Short menteeRating) {
        this.menteeRating = menteeRating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return id != null && Objects.equals(id, meeting.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", pair=" + pair +
                ", datetime=" + datetime +
                ", topic='" + topic +
                ", tasksDone='" + tasksDone +
                ", mentorRating=" + mentorRating +
                ", menteeRating=" + menteeRating +
                '}';
    }
}