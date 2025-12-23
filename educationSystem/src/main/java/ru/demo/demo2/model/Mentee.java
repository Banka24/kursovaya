package ru.demo.demo2.model;
import jakarta.persistence.*;

@Entity @Table(name = "mentees")
public class Mentee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(name = "last_name", nullable = false) private String lastName;
    @Column(name = "first_name", nullable = false) private String firstName;
    @Column(name = "middle_name") private String middleName;
    @Column(nullable = false, unique = true) private String email;
    @Column(columnDefinition = "TEXT") private String goals;
    @Column(name = "current_level") private Integer currentLevel;

    public Mentee() {}
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }
    public Integer getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }

    // Вспомогательный метод для получения полного ФИО
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (lastName != null) fullName.append(lastName);
        if (firstName != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(firstName);
        }
        if (middleName != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName);
        }
        return fullName.toString();
    }

    @Override public String toString() { return getFullName(); }
}
