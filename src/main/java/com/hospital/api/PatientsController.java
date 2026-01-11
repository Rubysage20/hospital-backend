package com.hospital.api;

import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PatientsController {

  private final PatientRepository repo;

  public PatientsController(PatientRepository repo) {
    this.repo = repo;
  }

  // ---- READ ----
  @GetMapping
  public ResponseEntity<List<Patient>> all() {
    try {
      List<Patient> patients = repo.findAll();
      return ResponseEntity.ok(patients);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Patient> one(@PathVariable String id) {
    try {
      return repo.findById(id)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // ---- CREATE ----
  @PostMapping
  public ResponseEntity<Patient> create(@RequestBody Patient p) {
    try {
      // Basic validation
      if (p.getFirstName() == null || p.getFirstName().trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
      }
      if (p.getLastName() == null || p.getLastName().trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
      }
      
      Patient saved = repo.save(p);
      return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // ---- UPDATE ----
  @PutMapping("/{id}")
  public ResponseEntity<Patient> update(@PathVariable String id, @RequestBody Patient p) {
    try {
      return repo.findById(id).map(x -> {
        if (p.getFirstName() != null && !p.getFirstName().trim().isEmpty()) {
          x.setFirstName(p.getFirstName());
        }
        if (p.getLastName() != null && !p.getLastName().trim().isEmpty()) {
          x.setLastName(p.getLastName());
        }
        if (p.getAddress() != null) {
          x.setAddress(p.getAddress());
        }
        if (p.getPrimaryCarePhysician() != null) {
          x.setPrimaryCarePhysician(p.getPrimaryCarePhysician());
        }
        if (p.getLastVisitedAt() != null) {
          x.setLastVisitedAt(p.getLastVisitedAt());
        }
        if (p.getLastVisitedDoctor() != null) {
          x.setLastVisitedDoctor(p.getLastVisitedDoctor());
        }
        return ResponseEntity.ok(repo.save(x));
      }).orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // ---- DELETE ----
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    try {
      if (!repo.existsById(id)) {
        return ResponseEntity.notFound().build();
      }
      repo.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}