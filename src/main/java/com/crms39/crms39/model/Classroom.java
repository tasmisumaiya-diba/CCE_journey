package com.crms39.crms39.model;

public class Classroom {

    private int id;
    private String roomCode;
    private String location;

    public Classroom(String roomCode, String location) {
        this.roomCode = roomCode;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return roomCode + " (" + location + ")";
    }
}