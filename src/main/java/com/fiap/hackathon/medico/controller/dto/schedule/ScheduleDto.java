package com.fiap.hackathon.medico.controller.dto.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.hackathon.medico.controller.dto.doctor.DoctorDto;
import com.fiap.hackathon.medico.domain.entity.ScheduleDB;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ScheduleDto {
    private UUID id;
    private DoctorDto doctor;
    private LocalDateTime scheduleTime;
    private BigDecimal amount;

    public static ScheduleDto Schedule(ScheduleDB schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .doctor(DoctorDto.doctorOf(schedule.getDoctor()))
                .scheduleTime(schedule.getScheduleTime())
                .amount(schedule.getAmount())
                .build();
    }
}
