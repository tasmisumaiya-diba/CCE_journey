package com.crms39.crms39.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomBookingTest {

    @Test
    void detectsOverlappingBookingsInTheSameRoom() {
        Classroom room = new Classroom("CR-302", "Campus");

        RoomBooking firstBooking = new RoomBooking(
                room,
                "Sunday",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Programming class",
                "teacher"
        );

        RoomBooking secondBooking = new RoomBooking(
                room,
                "Sunday",
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                "Meeting",
                "cr"
        );

        assertTrue(firstBooking.conflictsWith(secondBooking));
    }

    @Test
    void allowsBackToBackBookings() {
        Classroom room = new Classroom("CR-302", "Campus");

        RoomBooking firstBooking = new RoomBooking(
                room,
                "Monday",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "First class",
                "teacher"
        );

        RoomBooking secondBooking = new RoomBooking(
                room,
                "Monday",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Second class",
                "teacher"
        );

        assertFalse(firstBooking.conflictsWith(secondBooking));
    }

    @Test
    void allowsSameTimeInDifferentRooms() {
        Classroom firstRoom = new Classroom("CR-302", "Campus");
        Classroom secondRoom = new Classroom("CR-303", "Campus");

        RoomBooking firstBooking = new RoomBooking(
                firstRoom,
                "Tuesday",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Class",
                "teacher"
        );

        RoomBooking secondBooking = new RoomBooking(
                secondRoom,
                "Tuesday",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Meeting",
                "cr"
        );

        assertFalse(firstBooking.conflictsWith(secondBooking));
    }
}