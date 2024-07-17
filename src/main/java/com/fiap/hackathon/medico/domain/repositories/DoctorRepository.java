package com.fiap.hackathon.medico.domain.repositories;

import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorDB, UUID>, CustomDoctorRepository {
}
