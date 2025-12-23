package ru.demo.demo2.model;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    @Test void testMentor() {
        Mentor m = new Mentor();
        m.setId(1);
        m.setLastName("Иванов");
        m.setFirstName("Сергей");
        m.setEmail("ivanov@mail.ru");
        m.setSpecialization("Java");
        m.setAvailable(true);
        assertEquals(1, m.getId());
        assertEquals("Иванов Сергей", m.getFullName());
        assertEquals("ivanov@mail.ru", m.getEmail());
        assertEquals("Java", m.getSpecialization());
        assertTrue(m.getAvailable());
        assertEquals("Иванов Сергей", m.toString());
    }

    @Test void testMentee() {
        Mentee m = new Mentee();
        m.setId(1);
        m.setLastName("Новиков");
        m.setFirstName("Артем");
        m.setEmail("novikov@mail.ru");
        m.setGoals("Освоить Java");
        m.setCurrentLevel(2);
        assertEquals(1, m.getId());
        assertEquals("Новиков Артем", m.getFullName());
        assertEquals("novikov@mail.ru", m.getEmail());
        assertEquals("Освоить Java", m.getGoals());
        assertEquals(2, m.getCurrentLevel());
        assertEquals("Новиков Артем", m.toString());
    }

    @Test void testDirection() {
        Direction d = new Direction();
        d.setId(1);
        d.setName("IT");
        assertEquals(1, d.getId());
        assertEquals("IT", d.getName());
        assertEquals("IT", d.toString());
    }

    @Test void testPair() {
        Pair p = new Pair();
        p.setId(1);
        p.setStartDate(LocalDate.of(2024, 9, 1));
        p.setStatus("active");
        assertEquals(1, p.getId());
        assertEquals(LocalDate.of(2024, 9, 1), p.getStartDate());
        assertEquals("active", p.getStatus());
        assertEquals("", p.getMentorFio());
        assertEquals("", p.getMenteeFio());
        assertTrue(p.toString().contains("Пара"));
    }

    @Test void testPairWithMentorMentee() {
        Mentor mentor = new Mentor();
        mentor.setLastName("Иванов");
        Mentee mentee = new Mentee();
        mentee.setLastName("Новиков");
        Pair p = new Pair();
        p.setMentor(mentor);
        p.setMentee(mentee);
        assertEquals("Иванов", p.getMentorFio());
        assertEquals("Новиков", p.getMenteeFio());
    }

    @Test void testDevelopmentPlan() {
        DevelopmentPlan dp = new DevelopmentPlan();
        dp.setId(1);
        dp.setTitle("Основы Java");
        dp.setDescription("Изучение синтаксиса");
        dp.setDeadline(LocalDate.of(2024, 12, 1));
        assertEquals(1, dp.getId());
        assertEquals("Основы Java", dp.getTitle());
        assertEquals("Изучение синтаксиса", dp.getDescription());
        assertEquals(LocalDate.of(2024, 12, 1), dp.getDeadline());
        assertEquals("Основы Java", dp.toString());
    }

    @Test void testMeeting() {
        Meeting m = new Meeting();
        m.setId(1);
        m.setDatetime(LocalDateTime.of(2024, 9, 10, 14, 0));
        m.setTopic("Введение в Java");
        m.setTasksDone("Установка IDE");
        m.setMentorRating(5);
        m.setMenteeRating(4);
        assertEquals(1, m.getId());
        assertNotNull(m.getDatetime());
        assertEquals("Введение в Java", m.getTopic());
        assertEquals("Установка IDE", m.getTasksDone());
        assertEquals(5, m.getMentorRating());
        assertEquals(4, m.getMenteeRating());
        assertTrue(m.toString().contains("Встреча"));
    }

    @Test void testMentorDirections() {
        Mentor m = new Mentor();
        Direction d = new Direction();
        d.setName("IT");
        m.getDirections().add(d);
        assertFalse(m.getDirections().isEmpty());
        assertEquals(1, m.getDirections().size());
    }
}
