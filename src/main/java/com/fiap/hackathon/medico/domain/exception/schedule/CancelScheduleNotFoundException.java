package com.fiap.hackathon.medico.domain.exception.schedule;

import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;

import java.util.UUID;

public class CancelScheduleNotFoundException extends InvalidProcessException {
    private static final String TITTLE = "Schedule for cancel not found exception";

    public CancelScheduleNotFoundException(UUID id) {
        super(TITTLE, String.format("The schedule %s was not found to cancel", id));
    }
}
