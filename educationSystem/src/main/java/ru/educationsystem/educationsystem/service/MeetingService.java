package ru.educationsystem.educationsystem.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.educationsystem.educationsystem.model.Meeting;
import ru.educationsystem.educationsystem.model.Pair;
import ru.educationsystem.educationsystem.repository.MeetingDao;
import ru.educationsystem.educationsystem.util.HibernateSessionFactoryUtil;
import java.time.LocalDateTime;
import java.util.List;

public class MeetingService extends BaseService<Meeting, MeetingDao> {
    public MeetingService(MeetingDao meetingDao) {
        super(meetingDao);
    }

    public Meeting createMeeting(Pair pair, LocalDateTime datetime, String topic, 
                               String tasksDone, Short mentorRating, Short menteeRating) {
        Meeting meeting = new Meeting();
        meeting.setPair(pair);
        meeting.setDatetime(datetime);
        meeting.setTopic(topic);
        meeting.setTasksDone(tasksDone);
        meeting.setMentorRating(mentorRating);
        meeting.setMenteeRating(menteeRating);
        return save(meeting);
    }

    public Meeting updateMeeting(Meeting meeting) {
        return update(meeting);
    }

    public void deleteMeeting(Meeting meeting) {
        delete(meeting);
    }

    public Meeting getMeetingById(int id) {
        return findOne(id);
    }

    public List<Meeting> getAllMeetings() {
        return findAll();
    }
    
    public List<Meeting> getAllMeetingsWithPair() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Meeting> result = dao.findAllWithPair();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // Метод для получения встреч по конкретной паре
    public List<Meeting> getMeetingsByPair(Pair pair) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Meeting> result = dao.findMeetingsByPair(pair.getId());
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // Метод для получения встреч по диапазону дат
    public List<Meeting> getMeetingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            java.util.Date start = java.sql.Timestamp.valueOf(startDate);
            java.util.Date end = java.sql.Timestamp.valueOf(endDate);
            List<Meeting> result = dao.findMeetingsByDateRange(start, end);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // Метод для получения среднего рейтинга наставника
    public Double getAverageMentorRating(int mentorId) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Meeting> meetings = dao.findAll();
            double sum = 0;
            int count = 0;

            for (Meeting meeting : meetings) {
                if (meeting.getPair().getMentor().getId() == mentorId && meeting.getMentorRating() != null) {
                    sum += meeting.getMentorRating();
                    count++;
                }
            }

            transaction.commit();
            return count > 0 ? sum / count : 0.0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // Метод для получения среднего рейтинга подопечного
    public Double getAverageMenteeRating(int menteeId) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Meeting> meetings = dao.findAll();
            double sum = 0;
            int count = 0;

            for (Meeting meeting : meetings) {
                if (meeting.getPair().getMentee().getId() == menteeId && meeting.getMenteeRating() != null) {
                    sum += meeting.getMenteeRating();
                    count++;
                }
            }

            transaction.commit();
            return count > 0 ? sum / count : 0.0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}