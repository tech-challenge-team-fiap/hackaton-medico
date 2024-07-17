package com.fiap.hackathon.medico.controller;

import com.fiap.hackathon.medico.controller.dto.doctor.DoctorDto;
import com.fiap.hackathon.medico.controller.dto.doctor.DoctorFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingDto;
import com.fiap.hackathon.medico.domain.enums.Expertise;
import com.fiap.hackathon.medico.domain.enums.Status;
import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;
import com.fiap.hackathon.medico.service.DoctorService;
import com.fiap.hackathon.medico.service.ScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.hackathon.medico.domain.utils.ProblemAware.problemOf;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;
    private final ScheduleService scheduleService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> register(@RequestBody DoctorFormDto doctorFromDto) {
        try {
            log.debug("Registering new doctor [Doctor: {}]", doctorFromDto);
            DoctorDto processed = doctorService.register(doctorFromDto);
            log.debug("Registered new doctor [Doctor: {}]", processed);
            return ResponseEntity.ok(processed);
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<?> edit(@PathVariable("id") UUID id, @RequestBody DoctorFormDto doctorFromDto) {
        try {
            log.debug("Editing the doctor {}", id);
            DoctorDto processed = doctorService.edit(id, doctorFromDto);
            log.debug("Doctor {} was updatded [Doctor: {}]", id, processed);
            return ResponseEntity.ok(processed);
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        try {
            log.debug("Deleting the doctor {}", id);
            doctorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @GetMapping
    @Transactional
    public ResponseEntity<?> findDoctorsBy(@RequestParam(value = "expertise", required = false) Optional<Expertise> expertise,
                                           @RequestParam(value = "distance", required = false) Optional<Long> distance,
                                           @RequestParam(value = "rating", required = false) Optional<Integer> rating) {
        try {
            List<DoctorDto> doctorList = doctorService.findDoctorBy(expertise, distance, rating);
            if (doctorList.isEmpty()) {
               log.debug("No doctor found with arguments");
               return ResponseEntity.noContent().build();
            } else {
                log.debug("Founded {} doctor with arguments", doctorList.size());
                return ResponseEntity.ok(doctorList);
            }
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @GetMapping("/{id}/schedulings")
    @Transactional
    public ResponseEntity<?> findDoctorSchedulings(@PathVariable("id") UUID id) {
        try {
            List<SchedulingDto> scheduligs = doctorService.findDoctorSchedulings(id);
            return ResponseEntity.ok(scheduligs);
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @PostMapping("/schedule/{id}/accept")
    @Transactional
    public ResponseEntity<?> confirmSchedule(@PathVariable("id") UUID id) {
        try {
            scheduleService.updateScheduleStatus(id, Status.ACCEPTED);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @PostMapping("/schedule/{id}/decline")
    public ResponseEntity<?> declineSchedule(@PathVariable("id") UUID id) {
        try {
            scheduleService.updateScheduleStatus(id, Status.DECLINE);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }
}
