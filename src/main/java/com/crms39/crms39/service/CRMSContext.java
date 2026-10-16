package com.crms39.crms39.service;

public final class CRMSContext {

    private static final RoutineManager ROUTINE_MANAGER =
            new RoutineManager();

    private CRMSContext() {
    }

    public static RoutineManager getRoutineManager() {
        return ROUTINE_MANAGER;
    }
}