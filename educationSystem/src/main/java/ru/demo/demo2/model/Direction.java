package ru.demo.demo2.model;
import jakarta.persistence.*;

@Entity @Table(name = "directions")
public class Direction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(nullable = false, unique = true) private String name;

    public Direction() {}
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    @Override public String toString() { return name; }
}
