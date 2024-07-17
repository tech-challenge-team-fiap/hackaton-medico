package com.fiap.hackathon.medico.domain.exception.schedule;

import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;

import java.util.UUID;

public class ScheduleNotFoundException extends InvalidProcessException {
    private static final String TITTLE = "Schedule not found exception";

    public ScheduleNotFoundException(UUID id) {
        super(TITTLE, String.format("Schedule with %s does not found", id));
    }
}
