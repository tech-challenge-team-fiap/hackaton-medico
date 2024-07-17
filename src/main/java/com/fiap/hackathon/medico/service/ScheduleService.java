package com.fiap.hackathon.medico.service;

import com.fiap.hackathon.medico.controller.dto.schedule.ScheduleDto;
import com.fiap.hackathon.medico.controller.dto.schedule.ScheduleFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingCancelFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingFinishFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingFormDto;
import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import com.fiap.hackathon.medico.domain.entity.ScheduleDB;
import com.fiap.hackathon.medico.domain.enums.Status;
import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;
import com.fiap.hackathon.medico.domain.exception.schedule.CancelScheduleNotFoundException;
import com.fiap.hackathon.medico.domain.exception.schedule.FinishScheduleNotFoundException;
import com.fiap.hackathon.medico.domain.exception.schedule.ScheduleNotFoundException;
import com.fiap.hackathon.medico.domain.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository repository;
    private final DoctorService doctorService;
    private final GoogleCalendarService calendarService;

    public ScheduleDto register(ScheduleFormDto scheduleFormDto) throws InvalidProcessException {
        try {
            log.debug("Preparing to create a new schedule for doctor {}", scheduleFormDto.getDoctorId());
            DoctorDB doctor = doctorService.findDoctorById(scheduleFormDto.getDoctorId());
            ScheduleDB schedule = ScheduleDB.schedule(scheduleFormDto, doctor);
            ScheduleDB processed = repository.save(schedule);
            log.debug("Schedule {} was saved for doctor {}", processed.getId(), scheduleFormDto.getDoctorId());

            return ScheduleDto.Schedule(processed);
        } catch (Exception ex) {
            log.error("Error registering new schedule for doctor {}", scheduleFormDto.getDoctorId(), ex);
            throw new RuntimeException(ex);
        }
    }

    public ScheduleDto edit(UUID id, ScheduleFormDto scheduleFormDto) throws InvalidProcessException {
        try {
            log.debug("Preparing to update the schedule {}", id);
            ScheduleDB schedule = findScheduleById(id);

            DoctorDB doctor = doctorService.findDoctorById(scheduleFormDto.getDoctorId());

            schedule.setDoctor(doctor);
            schedule.setScheduleTime(ScheduleDB.scheduleTimeFormat(scheduleFormDto.getScheduleTime()));
            schedule.setAmount(scheduleFormDto.getAmount());

            ScheduleDB processed = repository.save(schedule);

            log.debug("Schedule {} was updated success", processed.getId());

            return ScheduleDto.Schedule(processed);
        } catch (Exception ex) {
            log.error("Error updating the schedule {}", id, ex);
            throw new RuntimeException(ex);
        }
    }

    public void delete(UUID id) throws InvalidProcessException {
        try {
            log.debug("Preparing to delete the schedule {}", id);
            ScheduleDB schedule = findScheduleById(id);

            repository.deleteById(schedule.getId());
            log.debug("Schedule {} deleted success", id);
        } catch (Exception ex) {
            log.error("Error deleting the schedule {}", id, ex);
            throw new RuntimeException(ex);
        }
    }

    public List<ScheduleDto> findDoctorSchedule(UUID doctorId) throws InvalidProcessException {
        log.debug("Preparing getting the doctor {} schedule", doctorId);
        DoctorDB doctorDB = doctorService.findDoctorById(doctorId);

        List<ScheduleDB> schedules = repository.findDoctorSchedule(doctorDB);

        log.debug("Returning the doctor schedule [Schedule: {}]", schedules);
        return schedules.stream()
                        .map(ScheduleDto::Schedule)
                        .collect(Collectors.toList());
    }

    public void scheduling(UUID scheduleId, SchedulingFormDto schedulingFormDto) throws InvalidProcessException, GeneralSecurityException, IOException {
        ScheduleDB processed = updateScheduleStatus(scheduleId, Status.SCHEDULE);

        processed.setPatientEmail(schedulingFormDto.getPatientEmail());
        processed.setPatientName(schedulingFormDto.getPatientName());

        calendarService.creatingNewEvent(processed);
    }

    public ScheduleDB updateScheduleStatus(UUID scheduleId, Status status) throws InvalidProcessException {
        try {
            log.debug("Preparing to scheduling {}", scheduleId);
            ScheduleDB schedule = findScheduleById(scheduleId);

            schedule.setStatus(status);

            ScheduleDB processed = repository.save(schedule);
            log.debug("Success scheduled");
            return processed;
        } catch (Exception ex) {
            log.error("Error update schedule {} status {}", scheduleId, status, ex);
            throw new RuntimeException(ex);
        }
    }

    public void cancelScheduling(UUID id, SchedulingCancelFormDto schedulingCancelFormDto) throws InvalidProcessException {
        try {
            log.debug("Preparing canceling the schedule {} with {} justification", id, schedulingCancelFormDto.getObservation());
            Optional<ScheduleDB> optionalSchedule = repository.findCancelSchedule(id);

            if (optionalSchedule.isPresent()) {
                ScheduleDB schedule = optionalSchedule.get();

                schedule.setObservation(schedulingCancelFormDto.getObservation());
                schedule.setStatus(Status.CANCELED);

                log.debug("Schedule {} was canceled", id);
                repository.save(schedule);
            } else {
                log.error("The schedule {} was not found to cancel", id);
                throw new CancelScheduleNotFoundException(id);
            }
        } catch (Exception ex) {
            log.error("Error cancel schedule {}", id, ex);
            throw new RuntimeException(ex);
        }
    }

    private ScheduleDB findScheduleById(UUID id) throws ScheduleNotFoundException {
        Optional<ScheduleDB> optionalSchedule = repository.findById(id);

        if (optionalSchedule.isPresent()) {
            return optionalSchedule.get();
        } else {
            log.error("Schedule with {} does not exists", id);
            throw new ScheduleNotFoundException(id);
        }
    }

    public void finishSchedule(UUID id, SchedulingFinishFormDto schedulingFinishFormDto) throws InvalidProcessException {
        try {
            log.debug("Preparing finishing the schedule {} with {} rating", id, schedulingFinishFormDto.getRating());
            Optional<ScheduleDB> optionalSchedule = repository.findCancelSchedule(id);

            if (optionalSchedule.isPresent()) {
                ScheduleDB schedule = optionalSchedule.get();

                schedule.setRating(schedulingFinishFormDto.getRating());
                schedule.setStatus(Status.FINISHED);

                log.debug("Schedule {} was finished", id);
                repository.save(schedule);
            } else {
                log.error("The schedule {} was not found to finish", id);
                throw new FinishScheduleNotFoundException(id);
            }
        } catch (Exception ex) {
            log.error("Error cancel schedule {}", id, ex);
            throw new RuntimeException(ex);
        }
    }
}
