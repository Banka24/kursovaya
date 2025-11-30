package ru.educationsystem.educationsystem.repository;

import ru.educationsystem.educationsystem.model.Request;
import ru.educationsystem.educationsystem.model.RequestStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestDao {
    private static final List<Request> requests = new ArrayList<>();
    private static int nextId = 1;

    public Request save(Request request) {
        if (request.getId() == null) {
            request.setId(nextId++);
            requests.add(request);
        } else {
            // В реальном приложении здесь была бы логика обновления в БД
            int index = -1;
            for (int i = 0; i < requests.size(); i++) {
                if (requests.get(i).getId().equals(request.getId())) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                requests.set(index, request);
            }
        }
        return request;
    }

    public Optional<Request> findById(Integer id) {
        return requests.stream()
                .filter(request -> request.getId().equals(id))
                .findFirst();
    }

    public List<Request> findAll() {
        return new ArrayList<>(requests);
    }

    public List<Request> findByMenteeId(Integer menteeId) {
        return requests.stream()
                .filter(request -> request.getMentee() != null && request.getMentee().getId().equals(menteeId))
                .collect(Collectors.toList());
    }

    public List<Request> findByMentorId(Integer mentorId) {
        return requests.stream()
                .filter(request -> request.getMentor() != null && request.getMentor().getId().equals(mentorId))
                .collect(Collectors.toList());
    }

    public List<Request> findByStatus(RequestStatus status) {
        return requests.stream()
                .filter(request -> request.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        requests.removeIf(request -> request.getId().equals(id));
    }
}
