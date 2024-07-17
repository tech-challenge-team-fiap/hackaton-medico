package com.fiap.hackathon.medico.domain.entity;

import com.fiap.hackathon.medico.controller.dto.schedule.ScheduleFormDto;
import com.fiap.hackathon.medico.domain.enums.Status;
import com.fiap.hackathon.medico.domain.type.StringRepresentationUUIDType;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

@Entity
@Table(name = "SCHEDULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@ToString
public class ScheduleDB implements Serializable {

    @Id
    @Builder.Default
    @Type(StringRepresentationUUIDType.class)
    @Column(name = "ID")
    @NotNull
    @EqualsAndHashCode.Include
    private UUID id = nextId();

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = DoctorDB.class)
    @JoinColumn(name = "DOCTOR_ID")
    private DoctorDB doctor;

    @Column(name = "SCHEDULE_TIME")
    private LocalDateTime scheduleTime;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Status status = Status.CREATED;

    @Column(name = "OBSERVATION")
    private String observation;

    @Column(name = "RATING")
    private Integer rating;

    @Transient
    private String patientName;

    @Transient
    private String patientEmail;

    @NotNull
    private static UUID nextId() {
        return UlidCreator.getMonotonicUlid().toUuid();
    }

    public static ScheduleDB schedule(ScheduleFormDto scheduleFormDto, DoctorDB doctor) {
        return ScheduleDB.builder()
                         .doctor(doctor)
                         .scheduleTime(scheduleTimeFormat(scheduleFormDto.getScheduleTime()))
                         .amount(scheduleFormDto.getAmount())
                         .build();
    }

    public boolean isFinished() {
        return status.equals(Status.FINISHED);
    }

    public boolean isSchedule() {
        return status.equals(Status.SCHEDULE);
    }

    public static LocalDateTime scheduleTimeFormat(Instant scheduleTime) {
        return LocalDateTime.ofInstant(scheduleTime, ZoneId.of("America/Sao_Paulo"));
    }
}
