package ru.educationsystem.educationsystem.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "mentor_directions")
public class MentorDirection implements Serializable {
    @EmbeddedId
    private MentorDirectionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mentorId")
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("directionId")
    @JoinColumn(name = "direction_id", nullable = false)
    private Direction direction;

    public MentorDirection() {
    }

    public MentorDirection(Mentor mentor, Direction direction) {
        this.mentor = mentor;
        this.direction = direction;
        this.id = new MentorDirectionId(mentor.getId(), direction.getId());
    }

    public MentorDirectionId getId() {
        return id;
    }

    public void setId(MentorDirectionId id) {
        this.id = id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MentorDirection that = (MentorDirection) o;
        return Objects.equals(mentor, that.mentor) &&
               Objects.equals(direction, that.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mentor, direction);
    }

    @Override
    public String toString() {
        return "MentorDirection{" +
                "mentor=" + mentor +
                ", direction=" + direction +
                '}';
    }
}
