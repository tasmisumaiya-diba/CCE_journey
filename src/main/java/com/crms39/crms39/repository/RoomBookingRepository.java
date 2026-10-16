package com.crms39.crms39.repository;

import com.crms39.crms39.database.DatabaseConnection;
import com.crms39.crms39.model.Classroom;
import com.crms39.crms39.model.RoomBooking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class RoomBookingRepository {

    public List<RoomBooking> findAll() throws SQLException {
        String sql = "SELECT rb.id, rb.booking_day, rb.start_time, "
                + "rb.end_time, rb.purpose, rb.booked_by, "
                + "c.id AS classroom_id, c.room_code, c.location "
                + "FROM room_bookings rb "
                + "JOIN classrooms c ON rb.classroom_id = c.id "
                + "ORDER BY rb.booking_day, rb.start_time";

        List<RoomBooking> bookings = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                bookings.add(createBooking(result));
            }
        }

        return bookings;
    }

    public void save(RoomBooking booking) throws SQLException {
        String sql = "INSERT INTO room_bookings "
                + "(classroom_id, booking_day, start_time, end_time, "
                + "purpose, booked_by) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            int classroomId = findClassroomId(
                    connection,
                    booking.getClassroom().getRoomCode()
            );

            statement.setInt(1, classroomId);
            statement.setString(2, booking.getDay());
            statement.setTime(3, Time.valueOf(booking.getStartTime()));
            statement.setTime(4, Time.valueOf(booking.getEndTime()));
            statement.setString(5, booking.getPurpose());
            statement.setString(6, booking.getBookedBy());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public boolean deleteById(int bookingId) throws SQLException {
        if (bookingId <= 0) {
            return false;
        }

        String sql = "DELETE FROM room_bookings WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            return statement.executeUpdate() == 1;
        }
    }

    private int findClassroomId(Connection connection, String roomCode)
            throws SQLException {

        String sql = "SELECT id FROM classrooms WHERE room_code = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roomCode);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id");
                }
            }
        }

        throw new SQLException(
                "Classroom '" + roomCode + "' does not exist in MySQL."
        );
    }

    private RoomBooking createBooking(ResultSet result)
            throws SQLException {

        Classroom classroom = new Classroom(
                result.getString("room_code"),
                result.getString("location")
        );
        classroom.setId(result.getInt("classroom_id"));

        RoomBooking booking = new RoomBooking(
                classroom,
                result.getString("booking_day"),
                result.getTime("start_time").toLocalTime(),
                result.getTime("end_time").toLocalTime(),
                result.getString("purpose"),
                result.getString("booked_by")
        );

        booking.setId(result.getInt("id"));
        return booking;
    }
}