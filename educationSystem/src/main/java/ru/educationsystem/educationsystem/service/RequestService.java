package ru.educationsystem.educationsystem.service;

import ru.educationsystem.educationsystem.model.Request;
import ru.educationsystem.educationsystem.model.RequestStatus;
import ru.educationsystem.educationsystem.repository.RequestDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RequestService {
    private final RequestDao requestDao;

    public RequestService() {
        this.requestDao = new RequestDao();
    }

    public RequestService(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    public Request createRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос не может быть null");
        }
        if (request.getMentee() == null) {
            throw new IllegalArgumentException("Подопечный не может быть null");
        }
        if (request.getMentor() == null) {
            throw new IllegalArgumentException("Наставник не может быть null");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание запроса не может быть пустым");
        }

        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        return requestDao.save(request);
    }

    public Request updateRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос не может быть null");
        }
        if (request.getId() == null || request.getId() <= 0) {
            throw new IllegalArgumentException("ID запроса должен быть положительным числом");
        }

        Optional<Request> existingRequest = requestDao.findById(request.getId());
        if (existingRequest.isEmpty()) {
            throw new IllegalArgumentException("Запрос с таким ID не найден");
        }

        return requestDao.save(request);
    }

    public Optional<Request> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return requestDao.findById(id);
    }

    public List<Request> findAll() {
        return requestDao.findAll();
    }

    public List<Request> findByMenteeId(Integer menteeId) {
        if (menteeId == null || menteeId <= 0) {
            throw new IllegalArgumentException("ID подопечного должен быть положительным числом");
        }
        return requestDao.findByMenteeId(menteeId);
    }

    public List<Request> findByMentorId(Integer mentorId) {
        if (mentorId == null || mentorId <= 0) {
            throw new IllegalArgumentException("ID наставника должен быть положительным числом");
        }
        return requestDao.findByMentorId(mentorId);
    }

    public List<Request> findByStatus(RequestStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null");
        }
        return requestDao.findByStatus(status);
    }

    public Request approveRequest(Integer requestId) {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("ID запроса должен быть положительным числом");
        }

        Optional<Request> requestOpt = requestDao.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new IllegalArgumentException("Запрос с таким ID не найден");
        }

        Request request = requestOpt.get();
        request.setStatus(RequestStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());

        return requestDao.save(request);
    }

    public Request rejectRequest(Integer requestId) {
        if (requestId == null || requestId <= 0) {
            throw new IllegalArgumentException("ID запроса должен быть положительным числом");
        }

        Optional<Request> requestOpt = requestDao.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new IllegalArgumentException("Запрос с таким ID не найден");
        }

        Request request = requestOpt.get();
        request.setStatus(RequestStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());

        return requestDao.save(request);
    }

    public boolean deleteRequest(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID запроса должен быть положительным числом");
        }

        Optional<Request> requestOpt = requestDao.findById(id);
        if (requestOpt.isEmpty()) {
            throw new IllegalArgumentException("Запрос с таким ID не найден");
        }

        requestDao.deleteById(id);
        return true;
    }
}
