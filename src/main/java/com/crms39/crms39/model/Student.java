package com.crms39.crms39.model;

public class Student extends User {

    public Student(String username, String password, String displayName) {
        super(username, password, displayName);
    }

    @Override
    public Role getRole() {
        return Role.STUDENT;
    }

    @Override
    public String viewSchedule() {
        return "Students can view the routine only.";
    }
}