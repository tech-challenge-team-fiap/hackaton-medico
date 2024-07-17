package com.fiap.hackathon.medico.service;

import com.fiap.hackathon.medico.controller.dto.doctor.DoctorDto;
import com.fiap.hackathon.medico.controller.dto.doctor.DoctorFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingDto;
import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import com.fiap.hackathon.medico.domain.entity.ScheduleDB;
import com.fiap.hackathon.medico.domain.enums.Expertise;
import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;
import com.fiap.hackathon.medico.domain.exception.doctor.DoctorNotFoundException;
import com.fiap.hackathon.medico.domain.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorRepository repository;

    public DoctorDto register(DoctorFormDto doctorFromDto) throws InvalidProcessException {
        try {
            log.debug("Register the doctor information [Doctor: {}]", doctorFromDto);
            DoctorDB doctor = DoctorDB.doctorOf(doctorFromDto);
            DoctorDB saved = repository.save(doctor);
            log.debug("Saved doctor {} information [Doctor: {}]", saved.getId(), saved);
            return DoctorDto.doctorOf(saved);
        } catch (Exception ex) {
            log.error("Error registering new doctor", ex);
            throw new RuntimeException(ex);
        }
    }

    public DoctorDto edit(UUID id, DoctorFormDto doctorFromDto) throws InvalidProcessException {
        try {
            DoctorDB doctor = findDoctorById(id);
            log.debug("Doctor {} was saved, so edit it", id);

            doctor.setName(doctorFromDto.getName());
            doctor.setCrm(doctorFromDto.getCrm());
            doctor.setExpertise(doctorFromDto.getExpertise());
            doctor.setDistance(doctorFromDto.getDistance());

            DoctorDB saved = repository.save(doctor);

            log.debug("Updated doctor {} information [Doctor: {}]", saved.getId(), saved);

            return DoctorDto.doctorOf(saved);
        } catch (Exception ex) {
            log.error("Error updating doctor", ex);
            throw new RuntimeException(ex);
        }
    }

    public void delete(UUID id) throws InvalidProcessException {
        try {
            DoctorDB doctor = findDoctorById(id);
            repository.delete(doctor);
            log.debug("Doctor {} was deleted", id);
        } catch (Exception ex) {
            log.error("Error deleting doctor", ex);
            throw new RuntimeException(ex);
        }
    }

    public List<DoctorDto> findDoctorBy(Optional<Expertise> expertise, Optional<Long> distance, Optional<Integer> rating) throws InvalidProcessException {
        log.debug("Preparing to find doctors by [Expertise: {}, Range: {}, Rating: {}]", expertise, distance, rating);
        List<DoctorDB> doctors = repository.findDoctorBy(expertise, distance);

        return doctors.stream()
                      .filter(ratingFilter(rating))
                      .map(DoctorDto::doctorOf)
                      .collect(Collectors.toList());
    }

    private Predicate<DoctorDB> ratingFilter(Optional<Integer> rating) {
        return rating.<Predicate<DoctorDB>>map(integer -> x -> x.getRating().equals(integer)).orElseGet(() -> x -> true);
    }

    public DoctorDB findDoctorById(UUID id) throws DoctorNotFoundException {
        Optional<DoctorDB> optionalDoctor = repository.findById(id);

        if (optionalDoctor.isPresent()) {
            return optionalDoctor.get();
        } else {
            log.error("Doctor with id {} does not found", id);
            throw new DoctorNotFoundException(id);
        }
    }

    public List<SchedulingDto> findDoctorSchedulings(UUID id) throws InvalidProcessException {
        log.debug("Preparing to getting the doctor {} schedulings", id);
        DoctorDB doctor = findDoctorById(id);
        List<SchedulingDto> schedulings = doctor.getSchedules()
                                                .stream()
                                                .filter(ScheduleDB::isSchedule)
                                                .map(SchedulingDto::scheduling)
                                                .collect(Collectors.toList());
        log.debug("Returning the doctor {} scheduling [Schedulings: {}]", id, schedulings);
        return schedulings;
    }
}
