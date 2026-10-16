package com.crms39.crms39.model;

public abstract class User {

    private final String username;
    private final String password;
    private final String displayName;

    protected User(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matchesPassword(String enteredPassword) {
        return password.equals(enteredPassword);
    }

    public abstract Role getRole();

    public abstract String viewSchedule();

    public boolean canEditSchedule() {
        return false;
    }

    public boolean canPostAnnouncements() {
        return false;
    }
}