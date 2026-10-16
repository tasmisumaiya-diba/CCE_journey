package com.crms39.crms39.repository;

import com.crms39.crms39.database.DatabaseConnection;
import com.crms39.crms39.model.ClassSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassSessionRepository {

    public List<ClassSession> findAll() throws SQLException {
        String sql = "SELECT id, class_day, start_time, end_time, "
                + "subject, teacher, room "
                + "FROM class_sessions";

        List<ClassSession> sessions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                sessions.add(createSession(result));
            }
        }

        return sessions;
    }

    public Optional<ClassSession> findRoomConflict(ClassSession candidate)
            throws SQLException {

        String sql = "SELECT id, class_day, start_time, end_time, "
                + "subject, teacher, room "
                + "FROM class_sessions "
                + "WHERE LOWER(TRIM(class_day)) = LOWER(?) "
                + "AND LOWER(TRIM(room)) = LOWER(?) "
                + "AND start_time < ? "
                + "AND end_time > ? "
                + "AND id <> ? "
                + "LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, candidate.getDay().trim());
            statement.setString(2, candidate.getRoom().trim());

            statement.setTime(
                    3,
                    Time.valueOf(
                            LocalTime.parse(candidate.getEndTime())
                    )
            );

            statement.setTime(
                    4,
                    Time.valueOf(
                            LocalTime.parse(candidate.getStartTime())
                    )
            );

            statement.setInt(5, candidate.getId());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(createSession(result));
                }
            }
        }

        return Optional.empty();
    }

    public void save(ClassSession session) throws SQLException {
        String sql = "INSERT INTO class_sessions "
                + "(class_day, start_time, end_time, subject, teacher, room) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, session.getDay());
            statement.setString(2, session.getStartTime());
            statement.setString(3, session.getEndTime());
            statement.setString(4, session.getSubject());
            statement.setString(5, session.getTeacher());
            statement.setString(6, session.getRoom());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    session.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(ClassSession session) throws SQLException {
        String sql = "UPDATE class_sessions "
                + "SET class_day = ?, start_time = ?, end_time = ?, "
                + "subject = ?, teacher = ?, room = ? "
                + "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, session.getDay());
            statement.setString(2, session.getStartTime());
            statement.setString(3, session.getEndTime());
            statement.setString(4, session.getSubject());
            statement.setString(5, session.getTeacher());
            statement.setString(6, session.getRoom());
            statement.setInt(7, session.getId());

            statement.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM class_sessions WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private ClassSession createSession(ResultSet result) throws SQLException {
        ClassSession session = new ClassSession(
                result.getString("class_day"),
                result.getString("start_time"),
                result.getString("end_time"),
                result.getString("subject"),
                result.getString("teacher"),
                result.getString("room")
        );

        session.setId(result.getInt("id"));
        return session;
    }
}