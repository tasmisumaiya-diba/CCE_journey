package com.crms39.crms39.model;

import com.crms39.crms39.service.InvalidScheduleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScheduleTest {

    @Test
    void addsSessionObject() throws InvalidScheduleException {
        Schedule schedule = new Schedule();

        ClassSession session = new ClassSession(
                "Friday",
                "09:00",
                "09:55",
                "OOP-101",
                "Test Teacher",
                "CR-302"
        );

        schedule.addSession(session);

        assertEquals(1, schedule.getSessions().size());
    }

    @Test
    void addsSessionUsingOverloadedMethod()
            throws InvalidScheduleException {

        Schedule schedule = new Schedule();

        schedule.addSession(
                "Friday",
                "10:00",
                "10:55",
                "OOP-102",
                "Test Teacher",
                "CR-302"
        );

        assertEquals(1, schedule.getSessions().size());
        assertEquals(
                "OOP-102",
                schedule.getSessions().get(0).getSubject()
        );
    }

    @Test
    void returnsCopyOfSessionList()
            throws InvalidScheduleException {

        Schedule schedule = new Schedule();

        schedule.addSession(
                "Friday",
                "11:00",
                "11:55",
                "OOP-103",
                "Test Teacher",
                "CR-302"
        );

        schedule.getSessions().clear();

        assertEquals(1, schedule.getSessions().size());
    }
}