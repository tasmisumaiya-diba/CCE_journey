package com.crms39.crms39.service;

import com.crms39.crms39.model.ClassSession;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class ScheduleValidator {

    private ScheduleValidator() {
    }

    public static void validateSessionDetails(ClassSession session)
            throws InvalidScheduleException {

        if (session == null) {
            throw new InvalidScheduleException(
                    "Class session information is required."
            );
        }

        if (isBlank(session.getDay())
                || isBlank(session.getStartTime())
                || isBlank(session.getEndTime())
                || isBlank(session.getSubject())
                || isBlank(session.getTeacher())
                || isBlank(session.getRoom())) {
            throw new InvalidScheduleException(
                    "All class-session fields are required."
            );
        }

        LocalTime startTime = parseTime(
                session.getStartTime(),
                "Start time"
        );

        LocalTime endTime = parseTime(
                session.getEndTime(),
                "End time"
        );

        if (!endTime.isAfter(startTime)) {
            throw new InvalidScheduleException(
                    "End time must be later than start time."
            );
        }
    }

    public static void validateNoRoomConflict(
            ClassSession candidate,
            List<ClassSession> existingSessions
    ) throws InvalidScheduleException {

        LocalTime candidateStart = parseTime(
                candidate.getStartTime(),
                "Start time"
        );

        LocalTime candidateEnd = parseTime(
                candidate.getEndTime(),
                "End time"
        );

        for (ClassSession existing : existingSessions) {
            if (existing.getId() == candidate.getId()
                    && candidate.getId() != 0) {
                continue;
            }

            boolean sameDay = existing.getDay().equalsIgnoreCase(
                    candidate.getDay()
            );

            boolean sameRoom = existing.getRoom().equalsIgnoreCase(
                    candidate.getRoom()
            );

            if (!sameDay || !sameRoom) {
                continue;
            }

            LocalTime existingStart = parseTime(
                    existing.getStartTime(),
                    "Saved start time"
            );

            LocalTime existingEnd = parseTime(
                    existing.getEndTime(),
                    "Saved end time"
            );

            boolean timesOverlap =
                    candidateStart.isBefore(existingEnd)
                            && candidateEnd.isAfter(existingStart);

            if (timesOverlap) {
                throw new InvalidScheduleException(
                        "Room " + candidate.getRoom()
                                + " is already used on "
                                + existing.getDay()
                                + " from "
                                + existing.getStartTime()
                                + " to "
                                + existing.getEndTime()
                                + "."
                );
            }
        }
    }

    private static LocalTime parseTime(String time, String fieldName)
            throws InvalidScheduleException {

        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException exception) {
            throw new InvalidScheduleException(
                    fieldName
                            + " must use HH:mm format, for example 09:00."
            );
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}