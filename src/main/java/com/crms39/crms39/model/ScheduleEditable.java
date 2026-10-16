package com.crms39.crms39.model;

public interface ScheduleEditable {

    void updateSchedule(ClassSession session);

    boolean canAssignClassroom();
}