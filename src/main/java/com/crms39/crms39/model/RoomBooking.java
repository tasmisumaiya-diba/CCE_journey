package com.crms39.crms39.model;

import java.time.LocalTime;

public class RoomBooking {

    private int id;
    private Classroom classroom;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    private String bookedBy;

    public RoomBooking(Classroom classroom, String day,
                       LocalTime startTime, LocalTime endTime,
                       String purpose, String bookedBy) {
        this.classroom = classroom;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.bookedBy = bookedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public String getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public boolean conflictsWith(RoomBooking otherBooking) {
        boolean sameRoom = classroom.getRoomCode()
                .equalsIgnoreCase(otherBooking.getClassroom().getRoomCode());

        boolean sameDay = day.equalsIgnoreCase(otherBooking.getDay());

        boolean timeOverlaps = startTime.isBefore(otherBooking.getEndTime())
                && otherBooking.getStartTime().isBefore(endTime);

        return sameRoom && sameDay && timeOverlaps;
    }

    @Override
    public String toString() {
        return classroom.getRoomCode()
                + " | " + day
                + " | " + startTime + " - " + endTime
                + " | " + purpose
                + " | Booked by: " + bookedBy;
    }
}