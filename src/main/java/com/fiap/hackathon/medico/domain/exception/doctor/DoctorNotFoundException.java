package com.fiap.hackathon.medico.domain.exception.doctor;

import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;

import java.util.UUID;

public class DoctorNotFoundException extends InvalidProcessException {
    private static final String TITTLE = "Doctor not found exception";


    public DoctorNotFoundException(UUID id) {
        super(TITTLE, String.format("Doctor with %s does not found", id));
    }
}
