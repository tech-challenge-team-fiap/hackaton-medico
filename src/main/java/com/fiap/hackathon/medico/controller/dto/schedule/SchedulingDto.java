package com.fiap.hackathon.medico.controller.dto.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.hackathon.medico.domain.entity.ScheduleDB;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class SchedulingDto {
    private UUID id;
    private LocalDateTime scheduleTime;

    public static SchedulingDto scheduling(ScheduleDB schedule) {
        return SchedulingDto.builder()
                .id(schedule.getId())
                .scheduleTime(schedule.getScheduleTime())
                .build();
    }
}
