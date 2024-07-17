package com.fiap.hackathon.medico.controller.dto.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Valid
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchedulingFinishFormDto {
    @NotNull
    private Integer rating;

    public SchedulingFinishFormDto(Integer rating) {
        this.rating = rating;
    }

    public SchedulingFinishFormDto() {

    }
}
