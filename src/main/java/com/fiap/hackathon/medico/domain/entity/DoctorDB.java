package com.fiap.hackathon.medico.domain.entity;

import com.fiap.hackathon.medico.controller.dto.doctor.DoctorFormDto;
import com.fiap.hackathon.medico.domain.enums.Expertise;
import com.fiap.hackathon.medico.domain.type.StringRepresentationUUIDType;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "DOCTOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@ToString
public class DoctorDB implements Serializable {

    @Id
    @Builder.Default
    @Type(StringRepresentationUUIDType.class)
    @Column(name = "ID")
    @NotNull
    @EqualsAndHashCode.Include
    private UUID id = nextId();

    @Column(name = "NAME")
    private String name;

    @Column(name = "CRM")
    private String crm;

    @Column(name = "EXPERTISE")
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Exclude
    private Expertise expertise;

    /*
    todo - requisito não funciona
    alterar para que tenha o endereço do medico
    e no momento da busca de medicos use alguma API do google maps
    para que consiga gerar uma distancia entre o medico e o paciente
     */
    @Column(name = "DISTANCE")
    private Long distance;

//    @Column(name = "EMAIL")
//    private String email;

    //apenas para apresentação esta como EAGER
    @OneToMany(mappedBy = "doctor", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<ScheduleDB> schedules = new ArrayList<>();

    @NotNull
    private static UUID nextId() {
        return UlidCreator.getMonotonicUlid().toUuid();
    }

    public static DoctorDB doctorOf(DoctorFormDto doctorFromDto) {
        return DoctorDB.builder()
                       .name(doctorFromDto.getName())
                       .crm(doctorFromDto.getCrm())
                       .expertise(doctorFromDto.getExpertise())
                       .distance(doctorFromDto.getDistance())
                       .build();
    }

    public Integer getRating() {
        Integer quantity = ratingQuantity();
        Integer sum = ratingSum();

        if(quantity.equals(0)){
            return 0;
        } else {
            return (sum / quantity);
        }
    }

    private Integer ratingSum() {
        return schedules.stream()
                        .filter(ScheduleDB::isFinished)
                        .map(ScheduleDB::getRating)
                        .reduce(0, Integer::sum);
    }

    private Integer ratingQuantity() {
        return schedules.stream()
                .filter(ScheduleDB::isFinished)
                .collect(Collectors.toSet())
                .size();
    }

}
