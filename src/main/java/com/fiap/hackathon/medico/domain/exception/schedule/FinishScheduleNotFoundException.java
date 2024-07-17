package com.fiap.hackathon.medico.domain.exception.schedule;

import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;

import java.util.UUID;

public class FinishScheduleNotFoundException extends InvalidProcessException {
    private static final String TITTLE = "Schedule for finish not found exception";

    public FinishScheduleNotFoundException(UUID id) {
        super(TITTLE, String.format("The schedule %s was not found to finish", id));
    }
}
