package com.fiap.hackathon.medico.controller.dto.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Valid
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleFormDto {
    @NotNull
    private UUID doctorId;
    @NotNull
    private Instant scheduleTime;
    @NotNull
    private BigDecimal amount;
}
