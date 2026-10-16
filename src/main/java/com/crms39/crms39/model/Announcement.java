package com.crms39.crms39.model;

import java.time.LocalDateTime;

public class Announcement {

    private int id;
    private String title;
    private String message;
    private String postedBy;
    private LocalDateTime postedAt;

    public Announcement(String title, String message,
                        String postedBy, LocalDateTime postedAt) {
        this.title = title;
        this.message = message;
        this.postedBy = postedBy;
        this.postedAt = postedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    @Override
    public String toString() {
        return title + "\n"
                + message + "\n"
                + "Posted by: " + postedBy
                + " at " + postedAt;
    }
}