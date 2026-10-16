package com.crms39.crms39.model;

public class ClassSession {

    private int id;
    private String day;
    private String startTime;
    private String endTime;
    private String subject;
    private String teacher;
    private String room;

    public ClassSession(String day, String startTime, String endTime,
                        String subject, String teacher, String room) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRoom() {
        return room;
    }

    public void updateDetails(String day, String startTime, String endTime,
                              String subject, String teacher, String room) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
    }

    @Override
    public String toString() {
        return day + " | " + startTime + " - " + endTime
                + " | " + subject
                + " | " + teacher
                + " | Room: " + room;
    }
}