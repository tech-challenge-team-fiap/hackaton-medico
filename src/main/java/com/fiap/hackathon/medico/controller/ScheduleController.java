package com.fiap.hackathon.medico.controller;

import com.fiap.hackathon.medico.controller.dto.schedule.ScheduleDto;
import com.fiap.hackathon.medico.controller.dto.schedule.ScheduleFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingCancelFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingFinishFormDto;
import com.fiap.hackathon.medico.controller.dto.schedule.SchedulingFormDto;
import com.fiap.hackathon.medico.domain.exception.InvalidProcessException;
import com.fiap.hackathon.medico.service.ScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import static com.fiap.hackathon.medico.domain.utils.ProblemAware.problemOf;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService service;

    @PostMapping
    @Transactional
    public ResponseEntity<?> register(@RequestBody ScheduleFormDto scheduleFormDto) {
        try {
            log.debug("Preparing to create a new schedule for doctor {}", scheduleFormDto.getDoctorId());
            ScheduleDto processed = service.register(scheduleFormDto);
            log.debug("Processed new schedule for doctor {}", scheduleFormDto.getDoctorId());
            return ResponseEntity.ok(processed);
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") UUID id, @RequestBody ScheduleFormDto scheduleFormDto) {
        try {
            log.debug("Preparing to update schedule {}", id);
            ScheduleDto processed = service.edit(id, scheduleFormDto);
            log.debug("Updated schedule {}", id);
            return ResponseEntity.ok(processed);
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        try {
            log.debug("Preparing to delete the schedule {}", id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @GetMapping("/doctor/{id}/schedule")
    public ResponseEntity<?> findDoctorSchedule(@PathVariable("id") UUID id) {
        try {
            List<ScheduleDto> schedules = service.findDoctorSchedule(id);
            return ResponseEntity.ok(schedules);
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @PostMapping("/scheduling/{id}")
    public ResponseEntity<?> scheduling(@PathVariable("id") UUID id, @RequestBody SchedulingFormDto schedulingFormDto) {
        try {
            service.scheduling(id, schedulingFormDto);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/scheduling/{id}/cancel")
    public ResponseEntity<?> cancelScheduling(@PathVariable("id") UUID id, @RequestBody SchedulingCancelFormDto schedulingCancelFormDto) {
        try {
            service.cancelScheduling(id, schedulingCancelFormDto);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }

    @PostMapping("/scheduling/{id}/finish")
    public ResponseEntity<?> finishSchedule(@PathVariable("id") UUID id, @RequestBody SchedulingFinishFormDto schedulingFinishFormDto) {
        try {
            service.finishSchedule(id, schedulingFinishFormDto);
            return ResponseEntity.noContent().build();
        } catch (InvalidProcessException ex) {
            return ResponseEntity.badRequest().body(problemOf(ex));
        }
    }
}
