package com.crms39.crms39.service;

import com.crms39.crms39.model.Announcement;
import com.crms39.crms39.model.ClassRepresentative;
import com.crms39.crms39.model.ClassSession;
import com.crms39.crms39.model.RoomBooking;
import com.crms39.crms39.model.Schedule;
import com.crms39.crms39.model.ScheduleEditable;
import com.crms39.crms39.model.Teacher;
import com.crms39.crms39.model.User;
import com.crms39.crms39.repository.AnnouncementRepository;
import com.crms39.crms39.repository.ClassSessionRepository;
import com.crms39.crms39.repository.RoomBookingRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoutineManager {

    private final Schedule schedule;
    private final List<Announcement> announcements;
    private final List<RoomBooking> roomBookings;

    private final ClassSessionRepository classSessionRepository;
    private final AnnouncementRepository announcementRepository;
    private final RoomBookingRepository roomBookingRepository;

    public RoutineManager() {
        schedule = new Schedule();
        announcements = new ArrayList<>();
        roomBookings = new ArrayList<>();

        classSessionRepository = new ClassSessionRepository();
        announcementRepository = new AnnouncementRepository();
        roomBookingRepository = new RoomBookingRepository();

        loadSessionsFromDatabase();
        loadAnnouncementsFromDatabase();
        loadRoomBookingsFromDatabase();
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public List<Announcement> getAnnouncements() {
        return new ArrayList<>(announcements);
    }

    public List<RoomBooking> getRoomBookings() {
        return new ArrayList<>(roomBookings);
    }

    public void addSession(User user, ClassSession session)
            throws InvalidScheduleException {

        checkSchedulePermission(user);
        validateSession(session);

        try {
            classSessionRepository.save(session);
            schedule.addSession(session);
        } catch (SQLException exception) {
            throw new InvalidScheduleException(
                    "Could not save the class session to MySQL."
            );
        }
    }

    public void updateSession(User user, ClassSession selectedSession,
                              String day, String startTime, String endTime,
                              String subject, String teacher, String room)
            throws InvalidScheduleException {

        checkSchedulePermission(user);

        if (selectedSession == null) {
            throw new InvalidScheduleException(
                    "Please select a class session to update."
            );
        }

        ClassSession updatedSession = new ClassSession(
                day, startTime, endTime, subject, teacher, room
        );
        updatedSession.setId(selectedSession.getId());

        validateSession(updatedSession);

        try {
            classSessionRepository.update(updatedSession);

            selectedSession.updateDetails(
                    day, startTime, endTime, subject, teacher, room
            );
        } catch (SQLException exception) {
            throw new InvalidScheduleException(
                    "Could not update the class session in MySQL."
            );
        }
    }

    public void deleteSession(User user, ClassSession session)
            throws InvalidScheduleException {

        checkSchedulePermission(user);

        if (session == null) {
            throw new InvalidScheduleException(
                    "Please select a class session to delete."
            );
        }

        try {
            classSessionRepository.deleteById(session.getId());

            if (!schedule.removeSession(session)) {
                throw new InvalidScheduleException(
                        "The selected class session was not found."
                );
            }
        } catch (SQLException exception) {
            throw new InvalidScheduleException(
                    "Could not delete the class session from MySQL."
            );
        }
    }

    public void postAnnouncement(User user, Announcement announcement) {
        if (!(user instanceof ClassRepresentative)) {
            throw new IllegalStateException(
                    "Only the Class Representative can post announcements."
            );
        }

        if (announcement == null
                || announcement.getTitle().isBlank()
                || announcement.getMessage().isBlank()) {
            throw new IllegalArgumentException(
                    "Announcement title and message are required."
            );
        }

        try {
            announcementRepository.save(announcement);
            announcements.add(announcement);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not save the announcement to MySQL.",
                    exception
            );
        }
    }
    public void deleteAnnouncement(User user, Announcement announcement) {
        if (user == null || !user.canPostAnnouncements()) {
            throw new IllegalStateException(
                    "Only the Class Representative can delete announcements."
            );
        }

        if (announcement == null || announcement.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Please select an announcement to delete."
            );
        }

        try {
            boolean wasDeleted = announcementRepository
                    .deleteById(announcement.getId());

            if (!wasDeleted) {
                throw new IllegalStateException(
                        "The selected announcement was not found."
                );
            }

            announcements.removeIf(
                    item -> item.getId() == announcement.getId()
            );

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not delete the announcement from MySQL.",
                    exception
            );
        }
    }

    public void addRoomBooking(User user, RoomBooking newBooking)
            throws ClassroomUnavailableException {

        if (!(user instanceof ScheduleEditable editable)
                || !editable.canAssignClassroom()) {
            throw new ClassroomUnavailableException(
                    "You do not have permission to book a classroom."
            );
        }

        if (newBooking == null) {
            throw new ClassroomUnavailableException(
                    "Room booking information is required."
            );
        }

        ClassSession bookingAsSession = new ClassSession(
                newBooking.getDay(),
                newBooking.getStartTime().toString(),
                newBooking.getEndTime().toString(),
                newBooking.getPurpose(),
                newBooking.getBookedBy(),
                newBooking.getClassroom().getRoomCode()
        );

        try {
            ClassSession classConflict = classSessionRepository
                    .findRoomConflict(bookingAsSession)
                    .orElse(null);

            if (classConflict != null) {
                throw new ClassroomUnavailableException(
                        "Room " + classConflict.getRoom()
                                + " already has "
                                + classConflict.getSubject()
                                + " on " + classConflict.getDay()
                                + " from "
                                + classConflict.getStartTime()
                                + " to "
                                + classConflict.getEndTime()
                                + "."
                );
            }
        } catch (SQLException exception) {
            throw new ClassroomUnavailableException(
                    "Could not check the class routine for room availability."
            );
        }

        for (RoomBooking existingBooking : roomBookings) {
            if (newBooking.conflictsWith(existingBooking)) {
                throw new ClassroomUnavailableException(
                        "This classroom is already booked during that time."
                );
            }
        }

        try {
            roomBookingRepository.save(newBooking);
            roomBookings.add(newBooking);
        } catch (SQLException exception) {
            throw new ClassroomUnavailableException(
                    "Could not save the classroom booking to MySQL."
            );
        }
    }
    public void cancelRoomBooking(User user, RoomBooking selectedBooking)
            throws ClassroomUnavailableException {

        if (!(user instanceof ScheduleEditable editable)
                || !editable.canAssignClassroom()) {
            throw new ClassroomUnavailableException(
                    "You do not have permission to cancel a classroom booking."
            );
        }

        if (selectedBooking == null || selectedBooking.getId() <= 0) {
            throw new ClassroomUnavailableException(
                    "Please select a classroom booking to cancel."
            );
        }

        try {
            boolean wasDeleted = roomBookingRepository
                    .deleteById(selectedBooking.getId());

            if (!wasDeleted) {
                throw new ClassroomUnavailableException(
                        "The selected classroom booking was not found."
                );
            }

            roomBookings.removeIf(
                    booking -> booking.getId() == selectedBooking.getId()
            );

        } catch (SQLException exception) {
            throw new ClassroomUnavailableException(
                    "Could not cancel the classroom booking from MySQL."
            );
        }
    }
    private void loadSessionsFromDatabase() {
        try {
            List<ClassSession> savedSessions =
                    classSessionRepository.findAll();

            for (ClassSession session : savedSessions) {
                schedule.addSession(session);
            }
        } catch (SQLException | InvalidScheduleException exception) {
            throw new IllegalStateException(
                    "Could not load the routine from MySQL.",
                    exception
            );
        }
    }

    private void loadAnnouncementsFromDatabase() {
        try {
            announcements.addAll(announcementRepository.findAll());
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not load announcements from MySQL.",
                    exception
            );
        }
    }

    private void loadRoomBookingsFromDatabase() {
        try {
            roomBookings.addAll(roomBookingRepository.findAll());
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not load classroom bookings from MySQL.",
                    exception
            );
        }
    }

    private void checkSchedulePermission(User user)
            throws InvalidScheduleException {

        if (!(user instanceof Teacher)
                && !(user instanceof ClassRepresentative)) {
            throw new InvalidScheduleException(
                    "Only a Teacher or Class Representative can edit the routine."
            );
        }
    }

    private void validateSession(ClassSession session)
            throws InvalidScheduleException {

        ScheduleValidator.validateSessionDetails(session);

        ScheduleValidator.validateNoRoomConflict(
                session,
                schedule.getSessions()
        );

        try {
            ClassSession databaseConflict = classSessionRepository
                    .findRoomConflict(session)
                    .orElse(null);

            if (databaseConflict != null) {
                throw new InvalidScheduleException(
                        "Room " + databaseConflict.getRoom()
                                + " is already used on "
                                + databaseConflict.getDay()
                                + " from "
                                + databaseConflict.getStartTime()
                                + " to "
                                + databaseConflict.getEndTime()
                                + "."
                );
            }
        } catch (SQLException exception) {
            throw new InvalidScheduleException(
                    "Could not check MySQL for a room-time conflict."
            );
        }
    }
}