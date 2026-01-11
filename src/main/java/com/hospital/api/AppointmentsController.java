package com.hospital.api;

import com.hospital.model.AppointmentDoc;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AppointmentsController {

  private final AppointmentRepository apptRepo;
  private final DoctorRepository doctorRepo;
  private final PatientRepository patientRepo;

  public AppointmentsController(AppointmentRepository apptRepo,
                                DoctorRepository doctorRepo,
                                PatientRepository patientRepo) {
    this.apptRepo = apptRepo;
    this.doctorRepo = doctorRepo;
    this.patientRepo = patientRepo;
  }

  // ---- READ ---- (UPDATED METHODS)
  @GetMapping
  public List<AppointmentDoc> all() {
    List<AppointmentDoc> appointments = apptRepo.findAll();
    // Check and update each appointment
    appointments.forEach(this::checkAndCompleteIfPast);
    return appointments;
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppointmentDoc> byId(@PathVariable String id) {
    return apptRepo.findById(id)
        .map(a -> {
            checkAndCompleteIfPast(a);
            return ResponseEntity.ok(a);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/byDoctor/{doctorId}")
  public List<AppointmentDoc> byDoctor(@PathVariable String doctorId) {
    List<AppointmentDoc> appointments = apptRepo.findByDoctorId(doctorId);
    appointments.forEach(this::checkAndCompleteIfPast);
    return appointments;
  }

  @GetMapping("/byPatient/{patientId}")
  public List<AppointmentDoc> byPatient(@PathVariable String patientId) {
    List<AppointmentDoc> appointments = apptRepo.findByPatientId(patientId);
    appointments.forEach(this::checkAndCompleteIfPast);
    return appointments;
  }

  // ---- CREATE ----
  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateOrUpdate req) {
    try {
      require(doctorRepo.existsById(req.doctorId), "Doctor not found: " + req.doctorId);
      require(patientRepo.existsById(req.patientId), "Patient not found: " + req.patientId);

      LocalDateTime start = parseStart(req);
      LocalDateTime end   = (req.end != null) ? req.end : start.plusHours(1);

      AppointmentDoc a = new AppointmentDoc();
      a.setId(UUID.randomUUID().toString());
      a.setDoctorId(req.doctorId);
      a.setPatientId(req.patientId);
      a.setStart(start.toString()); // ISO like "2025-10-01T10:00"
      a.setEnd(end.toString());
      a.setStartTime(toEpochMillis(start)); // epoch millis to match your Mongo docs
      a.setEndTime(toEpochMillis(end));
      a.setStatus(req.status == null ? AppointmentDoc.Status.SCHEDULED
                                     : AppointmentDoc.Status.valueOf(req.status));

      return ResponseEntity.ok(apptRepo.save(a));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(msg(ex.getMessage()));
    }
  }

  // ---- UPDATE ----
  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable String id, @RequestBody CreateOrUpdate req) {
    return apptRepo.findById(id).map(a -> {
      if (req.doctorId != null) a.setDoctorId(req.doctorId);
      if (req.patientId != null) a.setPatientId(req.patientId);
      if (req.status != null)    a.setStatus(AppointmentDoc.Status.valueOf(req.status));

      if (req.start != null || req.date != null || req.end != null) {
        LocalDateTime start = (req.start != null) ? req.start : parseStart(req);
        LocalDateTime end   = (req.end != null)   ? req.end   : start.plusHours(1);
        a.setStart(start.toString());
        a.setEnd(end.toString());
        a.setStartTime(toEpochMillis(start));
        a.setEndTime(toEpochMillis(end));
      }
      return ResponseEntity.ok(apptRepo.save(a));
    }).orElse(ResponseEntity.notFound().build());
  }

  // ---- DELETE ----
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    if (!apptRepo.existsById(id)) return ResponseEntity.notFound().build();
    apptRepo.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // ---- mark COMPLETE (increments doctor visits & stamps patient) ----
  @PostMapping("/{id}/complete")
  public ResponseEntity<?> complete(@PathVariable String id) {
    return apptRepo.findById(id).map(a -> {
      a.setStatus(AppointmentDoc.Status.COMPLETED);
      apptRepo.save(a);

      doctorRepo.findById(a.getDoctorId()).ifPresent(d -> {
        Integer visits = d.getNumVisitedPatients() == null ? 0 : d.getNumVisitedPatients();
        d.setNumVisitedPatients(visits + 1);
        doctorRepo.save(d);
      });

      patientRepo.findById(a.getPatientId()).ifPresent(p -> {
        p.setLastVisitedAt(a.getStart());        // ISO string
        p.setLastVisitedDoctor(a.getDoctorId()); // store doctorId there
        patientRepo.save(p);
      });

      return ResponseEntity.ok(a);
    }).orElse(ResponseEntity.notFound().build());
  }

  // ---- NEW: Helper method to check and complete past appointments ----
  private void checkAndCompleteIfPast(AppointmentDoc appointment) {
    // Only process SCHEDULED appointments
    if (appointment.getStatus() != AppointmentDoc.Status.SCHEDULED) {
      return;
    }
    
    // Check if end time has passed
    long currentTime = System.currentTimeMillis();
    if (appointment.getEndTime() != null && appointment.getEndTime() < currentTime) {
      // Mark as completed
      appointment.setStatus(AppointmentDoc.Status.COMPLETED);
      apptRepo.save(appointment);
      
      // Increment doctor visits
      doctorRepo.findById(appointment.getDoctorId()).ifPresent(d -> {
        Integer visits = d.getNumVisitedPatients() == null ? 0 : d.getNumVisitedPatients();
        d.setNumVisitedPatients(visits + 1);
        doctorRepo.save(d);
      });
      
      // Update patient last visit
      patientRepo.findById(appointment.getPatientId()).ifPresent(p -> {
        p.setLastVisitedAt(appointment.getStart());
        p.setLastVisitedDoctor(appointment.getDoctorId());
        patientRepo.save(p);
      });
    }
  }

  // ---- helpers ----
  public static class CreateOrUpdate {
    public String doctorId;
    public String patientId;
    public LocalDateTime start;   
    public LocalDateTime end;     
    public String date;           // ISO "yyyy-MM-dd'T'HH:mm"
    public String status;         // SCHEDULED/COMPLETED/CANCELED
  }

  private static void require(boolean ok, String msg) {
    if (!ok) throw new IllegalArgumentException(msg);
  }

  private static LocalDateTime parseStart(CreateOrUpdate req) {
    if (req.start != null) return req.start;
    if (req.date != null) {
      try {
        return LocalDateTime.parse(req.date);
      } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Invalid date. Use 2025-10-01T10:00");
      }
    }
    throw new IllegalArgumentException("Missing start/date");
  }

  private static long toEpochMillis(LocalDateTime ldt) {
    // store as UTC epoch millis to match your existing startTime/endTime
    return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
  }

  private static Map<String, Object> msg(String m) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("message", m);
    return map;
  }
}