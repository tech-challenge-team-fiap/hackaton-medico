package com.fiap.hackathon.medico.domain.repositories;

import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import com.fiap.hackathon.medico.domain.enums.Expertise;

import java.util.List;
import java.util.Optional;

public interface CustomDoctorRepository {

    List<DoctorDB> findDoctorBy(Optional<Expertise> expertise, Optional<Long> distance);
}
