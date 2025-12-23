package ru.demo.demo2.service;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.time.LocalDate;
import java.util.*;

public class ProgressTrackingService {
    private final MeetingDao meetingDao = new MeetingDao();
    private final DevelopmentPlanDao planDao = new DevelopmentPlanDao();

    public List<Meeting> getMeetingsForPair(Integer pairId) {
        return meetingDao.findByPairId(pairId);
    }

    public List<DevelopmentPlan> getPlansForPair(Integer pairId) {
        return planDao.findByPairId(pairId);
    }

    public Meeting addMeeting(Pair pair, String topic, String tasksDone, Integer mentorRating, Integer menteeRating) {
        Meeting meeting = new Meeting();
        meeting.setPair(pair);
        meeting.setDatetime(java.time.LocalDateTime.now());
        meeting.setTopic(topic);
        meeting.setTasksDone(tasksDone);
        meeting.setMentorRating(mentorRating);
        meeting.setMenteeRating(menteeRating);
        meetingDao.save(meeting);
        return meeting;
    }

    public DevelopmentPlan addPlan(Pair pair, String title, String description, LocalDate deadline) {
        DevelopmentPlan plan = new DevelopmentPlan();
        plan.setPair(pair);
        plan.setTitle(title);
        plan.setDescription(description);
        plan.setDeadline(deadline);
        planDao.save(plan);
        return plan;
    }

    public Double getAverageMentorRating(Integer pairId) {
        return meetingDao.getAverageMentorRating(pairId);
    }

    public Double getAverageMenteeRating(Integer pairId) {
        return meetingDao.getAverageMenteeRating(pairId);
    }

    public Map<String, Object> getPairStatistics(Integer pairId) {
        Map<String, Object> stats = new HashMap<>();
        List<Meeting> meetings = meetingDao.findByPairId(pairId);
        List<DevelopmentPlan> plans = planDao.findByPairId(pairId);

        stats.put("totalMeetings", meetings.size());
        stats.put("totalPlans", plans.size());
        stats.put("avgMentorRating", getAverageMentorRating(pairId));
        stats.put("avgMenteeRating", getAverageMenteeRating(pairId));

        long completedPlans = plans.stream().filter(p -> p.getDeadline() != null && p.getDeadline().isBefore(LocalDate.now())).count();
        stats.put("completedPlans", completedPlans);

        return stats;
    }

    public List<DevelopmentPlan> getOverduePlans() {
        return planDao.findOverdue();
    }

    public int getMeetingsCount(Integer pairId) {
        return meetingDao.findByPairId(pairId).size();
    }

    public int getPlansCount(Integer pairId) {
        return planDao.findByPairId(pairId).size();
    }
}