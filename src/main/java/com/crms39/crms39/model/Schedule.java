package com.crms39.crms39.model;

import com.crms39.crms39.service.InvalidScheduleException;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private final List<ClassSession> sessions;

    public Schedule() {
        sessions = new ArrayList<>();
    }

    public void addSession(ClassSession session)
            throws InvalidScheduleException {

        if (session == null) {
            throw new InvalidScheduleException(
                    "A class session cannot be empty."
            );
        }

        sessions.add(session);
    }

    public void addSession(
            String day,
            String startTime,
            String endTime,
            String subject,
            String teacher,
            String room
    ) throws InvalidScheduleException {

        ClassSession session = new ClassSession(
                day,
                startTime,
                endTime,
                subject,
                teacher,
                room
        );

        addSession(session);
    }

    public List<ClassSession> getSessions() {
        return new ArrayList<>(sessions);
    }

    public boolean removeSession(ClassSession session) {
        if (session == null) {
            return false;
        }

        return sessions.remove(session);
    }

    public boolean isEmpty() {
        return sessions.isEmpty();
    }
}