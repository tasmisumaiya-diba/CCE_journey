package com.crms39.crms39.service;

import com.crms39.crms39.model.ClassSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduleValidatorTest {

    @Test
    void rejectsEndTimeBeforeStartTime() {
        ClassSession session = new ClassSession(
                "Friday",
                "10:00",
                "09:00",
                "TEST-TIME",
                "Test Teacher",
                "CR-302"
        );

        assertThrows(
                InvalidScheduleException.class,
                () -> ScheduleValidator.validateSessionDetails(session)
        );
    }

    @Test
    void rejectsOverlappingSessionsInTheSameRoom() {
        ClassSession existingSession = new ClassSession(
                "Monday",
                "10:00",
                "10:55",
                "PHY-122",
                "DKH/ORC",
                "CR-302"
        );

        ClassSession newSession = new ClassSession(
                "Monday",
                "10:05",
                "10:30",
                "NEW-CONFLICT",
                "Test Teacher",
                "CR-302"
        );

        assertThrows(
                InvalidScheduleException.class,
                () -> ScheduleValidator.validateNoRoomConflict(
                        newSession,
                        List.of(existingSession)
                )
        );
    }

    @Test
    void allowsSameTimeInDifferentRooms() {
        ClassSession existingSession = new ClassSession(
                "Monday",
                "10:00",
                "10:55",
                "PHY-122",
                "DKH/ORC",
                "CR-302"
        );

        ClassSession newSession = new ClassSession(
                "Monday",
                "10:05",
                "10:30",
                "NEW-ROOM",
                "Test Teacher",
                "CR-303"
        );

        assertDoesNotThrow(
                () -> ScheduleValidator.validateNoRoomConflict(
                        newSession,
                        List.of(existingSession)
                )
        );
    }
}