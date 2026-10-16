package com.crms39.crms39.repository;

import com.crms39.crms39.database.DatabaseConnection;
import com.crms39.crms39.model.Announcement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementRepository {

    public List<Announcement> findAll() throws SQLException {
        String sql = "SELECT id, title, message, posted_by, posted_at "
                + "FROM announcements "
                + "ORDER BY posted_at DESC";

        List<Announcement> announcements = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                announcements.add(createAnnouncement(result));
            }
        }

        return announcements;
    }

    public void save(Announcement announcement) throws SQLException {
        String sql = "INSERT INTO announcements "
                + "(title, message, posted_by, posted_at) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, announcement.getTitle());
            statement.setString(2, announcement.getMessage());
            statement.setString(3, announcement.getPostedBy());
            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(announcement.getPostedAt())
            );

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    announcement.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public boolean deleteById(int announcementId) throws SQLException {
        if (announcementId <= 0) {
            return false;
        }

        String sql = "DELETE FROM announcements WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, announcementId);

            return statement.executeUpdate() == 1;
        }
    }

    private Announcement createAnnouncement(ResultSet result)
            throws SQLException {

        Announcement announcement = new Announcement(
                result.getString("title"),
                result.getString("message"),
                result.getString("posted_by"),
                result.getTimestamp("posted_at").toLocalDateTime()
        );

        announcement.setId(result.getInt("id"));
        return announcement;
    }
}