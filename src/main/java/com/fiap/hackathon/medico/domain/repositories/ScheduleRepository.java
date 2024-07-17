package com.fiap.hackathon.medico.domain.repositories;

import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import com.fiap.hackathon.medico.domain.entity.ScheduleDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleDB, UUID> {

    @Query("select s from ScheduleDB s left join fetch s.doctor d where s.doctor = :doctor and s.status in ('CREATED', 'DECLINE', 'CANCELED')")
    List<ScheduleDB> findDoctorSchedule(@Param("doctor") DoctorDB doctor);

    @Query("select s from ScheduleDB s where s.id = :id and s.status in ('SCHEDULE', 'ACCEPTED')")
    Optional<ScheduleDB> findCancelSchedule(@Param("id") UUID id);
}
