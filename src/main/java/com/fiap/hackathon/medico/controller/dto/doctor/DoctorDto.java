package com.fiap.hackathon.medico.controller.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.hackathon.medico.domain.enums.Expertise;
import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class DoctorDto {

    private UUID id;
    private String name;
    private String crm;
    private Expertise expertise;
    private Long distance;

    public static DoctorDto doctorOf(DoctorDB doctor) {
        return DoctorDto.builder()
                        .id(doctor.getId())
                        .name(doctor.getName())
                        .crm(doctor.getCrm())
                        .expertise(doctor.getExpertise())
                        .distance(doctor.getDistance())
                        .build();
    }
}
