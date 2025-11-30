package ru.educationsystem.educationsystem.model;

import java.time.LocalDateTime;

public class Request {
    private Integer id;
    private Mentee mentee;
    private Mentor mentor;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String description;

    public Request() {
        this.createdAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }

    public Request(Mentee mentee, Mentor mentor, String description) {
        this();
        this.mentee = mentee;
        this.mentor = mentor;
        this.description = description;
    }

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

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
