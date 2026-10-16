package com.crms39.crms39.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPermissionTest {

    @Test
    void teacherCanEditRoutineButCannotPostAnnouncements() {
        User teacher = new Teacher(
                "teacher",
                "demo",
                "Dr. Rahman"
        );

        assertEquals(Role.TEACHER, teacher.getRole());
        assertTrue(teacher.canEditSchedule());
        assertFalse(teacher.canPostAnnouncements());
    }

    @Test
    void classRepresentativeCanEditAndPostAnnouncements() {
        User cr = new ClassRepresentative(
                "cr",
                "demo",
                "Ayesha Ahmed"
        );

        assertEquals(Role.CR, cr.getRole());
        assertTrue(cr.canEditSchedule());
        assertTrue(cr.canPostAnnouncements());
    }

    @Test
    void studentHasReadOnlyPermissions() {
        User student = new Student(
                "student",
                "demo",
                "Tanvir Hasan"
        );

        assertEquals(Role.STUDENT, student.getRole());
        assertFalse(student.canEditSchedule());
        assertFalse(student.canPostAnnouncements());
    }
}