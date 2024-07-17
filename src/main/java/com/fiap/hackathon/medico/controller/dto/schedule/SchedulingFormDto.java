package com.fiap.hackathon.medico.controller.dto.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Valid
public class SchedulingFormDto {
    @NotNull
    private String patientEmail;
    @NotNull
    private String patientName;

    public SchedulingFormDto() {
    }

    public SchedulingFormDto(String patientEmail, String patientName) {
        this.patientEmail = patientEmail;
        this.patientName = patientName;
    }
}
