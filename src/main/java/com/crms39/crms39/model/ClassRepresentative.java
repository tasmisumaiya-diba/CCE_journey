package com.crms39.crms39.model;

public class ClassRepresentative extends Student implements ScheduleEditable {

    public ClassRepresentative(String username, String password, String displayName) {
        super(username, password, displayName);
    }

    @Override
    public Role getRole() {
        return Role.CR;
    }

    @Override
    public String viewSchedule() {
        return "The CR can view and manage the routine.";
    }

    @Override
    public boolean canEditSchedule() {
        return true;
    }

    @Override
    public boolean canPostAnnouncements() {
        return true;
    }

    @Override
    public void updateSchedule(ClassSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }
    }

    @Override
    public boolean canAssignClassroom() {
        return true;
    }
}