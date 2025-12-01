package ru.educationsystem.educationsystem.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MentorDirectionId implements Serializable {
    private Integer mentorId;
    private Integer directionId;

    public MentorDirectionId() {
    }

    public MentorDirectionId(Integer mentorId, Integer directionId) {
        this.mentorId = mentorId;
        this.directionId = directionId;
    }

    public Integer getMentorId() {
        return mentorId;
    }

    public void setMentorId(Integer mentorId) {
        this.mentorId = mentorId;
    }

    public Integer getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Integer directionId) {
        this.directionId = directionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MentorDirectionId that = (MentorDirectionId) o;
        return Objects.equals(mentorId, that.mentorId) &&
               Objects.equals(directionId, that.directionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mentorId, directionId);
    }
}