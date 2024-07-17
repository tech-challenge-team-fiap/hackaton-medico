package com.fiap.hackathon.medico.controller.dto.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Valid
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchedulingCancelFormDto {
    @NotNull
    private String observation;

    public SchedulingCancelFormDto(String observation) {
        this.observation = observation;
    }

    public SchedulingCancelFormDto() {

    }
}
