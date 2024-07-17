package com.fiap.hackathon.medico.controller.dto.doctor;

import com.fiap.hackathon.medico.domain.enums.Expertise;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Valid
public class DoctorFormDto {
    @NotNull
    private String name;
    @NotNull
    private String crm;
    @NotNull
    private Expertise expertise;
    @NotNull
    private Long distance;
}
