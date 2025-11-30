package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Meeting;
import ru.educationsystem.educationsystem.repository.MeetingDao;
import ru.educationsystem.educationsystem.repository.PairDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MeetingService {
    private final MeetingDao meetingDao;
    private final PairDao pairDao;

    public MeetingService() {
        this.meetingDao = new MeetingDao();
        this.pairDao = new PairDao();
    }

    public MeetingService(MeetingDao meetingDao, PairDao pairDao) {
        this.meetingDao = meetingDao;
        this.pairDao = pairDao;
    }

    public Meeting createMeeting(Meeting meeting) {
        if (meeting == null) {
            throw new IllegalArgumentException("Встреча не может быть null");
        }
        if (meeting.getPair() == null) {
            throw new IllegalArgumentException("Пара не может быть null");
        }
        if (meeting.getDatetime() == null) {
            throw new IllegalArgumentException("Дата и время встречи не могут быть null");
        }
        if (meeting.getTopic() == null || meeting.getTopic().trim().isEmpty()) {
            throw new IllegalArgumentException("Тема встречи не может быть пустой");
        }
        meetingDao.save(meeting);
        return meeting;
    }

    public Meeting updateMeeting(Meeting meeting) {
        if (meeting == null) {
            throw new IllegalArgumentException("Встреча не может быть null");
        }
        if (meeting.getId() == null || meeting.getId() <= 0) {
            throw new IllegalArgumentException("ID встречи должен быть положительным числом");
        }

        Optional<Meeting> existingMeeting = meetingDao.findById(meeting.getId());
        if (existingMeeting.isEmpty()) {
            throw new IllegalArgumentException("Встреча с таким ID не найдена");
        }

        meetingDao.update(meeting);
        return meeting;
    }

    public Optional<Meeting> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return meetingDao.findById(id);
    }

    public List<Meeting> getAllMeetings() {
        return meetingDao.findAll();
    }

    public List<Meeting> getMeetingsByPairId(Integer pairId) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        return meetingDao.findByPairId(pairId);
    }

    public List<Meeting> getUpcomingMeetings(Integer pairId) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        return meetingDao.findByPairIdAndFutureDate(pairId);
    }

    public List<Meeting> getPastMeetings(Integer pairId) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        return meetingDao.findByPairIdAndPastDate(pairId);
    }

    public Meeting cancelMeeting(Integer meetingId) {
        if (meetingId == null || meetingId <= 0) {
            throw new IllegalArgumentException("ID встречи должен быть положительным числом");
        }

        Optional<Meeting> meetingOpt = meetingDao.findById(meetingId);
        if (meetingOpt.isEmpty()) {
            throw new IllegalArgumentException("Встреча с таким ID не найдена");
        }

        Meeting meeting = meetingOpt.get();
        // Отменяем встречу, устанавливая дату в прошлое
        meeting.setDatetime(LocalDateTime.now().minusDays(1));
        meetingDao.update(meeting);
        return meeting;
    }

    public Meeting rescheduleMeeting(Integer meetingId, LocalDateTime newDateTime) {
        if (meetingId == null || meetingId <= 0) {
            throw new IllegalArgumentException("ID встречи должен быть положительным числом");
        }
        if (newDateTime == null) {
            throw new IllegalArgumentException("Новая дата и время не могут быть null");
        }

        Optional<Meeting> meetingOpt = meetingDao.findById(meetingId);
        if (meetingOpt.isEmpty()) {
            throw new IllegalArgumentException("Встреча с таким ID не найдена");
        }

        Meeting meeting = meetingOpt.get();
        meeting.setDatetime(newDateTime);
        meetingDao.update(meeting);
        return meeting;
    }

    public boolean deleteMeeting(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID встречи должен быть положительным числом");
        }

        Optional<Meeting> meetingOpt = meetingDao.findById(id);
        if (meetingOpt.isEmpty()) {
            throw new IllegalArgumentException("Встреча с таким ID не найдена");
        }

        meetingDao.deleteById(id);
        return true;
    }

    public List<Meeting> getMeetingsByDateRange(Integer pairId, LocalDate startDate, LocalDate endDate) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Начальная дата не может быть null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Конечная дата не может быть null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Начальная дата не может быть позже конечной");
        }

        return meetingDao.findByPairIdAndDateRange(pairId, startDate, endDate);
    }

    public List<Meeting> getMeetingsByPairIdInPeriod(Integer pairId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (pairId == null || pairId <= 0) {
            throw new IllegalArgumentException("ID пары должен быть положительным числом");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("Начальная дата и время не могут быть null");
        }
        if (endDateTime == null) {
            throw new IllegalArgumentException("Конечная дата и время не могут быть null");
        }
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Начальная дата и время не могут быть позже конечных");
        }

        return meetingDao.findByPairIdAndDateTimeRange(pairId, startDateTime, endDateTime);
    }

    public List<Meeting> getMeetingsInPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null) {
            throw new IllegalArgumentException("Начальная дата и время не могут быть null");
        }
        if (endDateTime == null) {
            throw new IllegalArgumentException("Конечная дата и время не могут быть null");
        }
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Начальная дата и время не могут быть позже конечных");
        }

        return meetingDao.findByDateTimeRange(startDateTime, endDateTime);
    }

    // Псевдонимы для методов, используемых в контроллерах
    public Meeting create(Meeting meeting) {
        return createMeeting(meeting);
    }

    public Meeting update(Meeting meeting) {
        return updateMeeting(meeting);
    }
}