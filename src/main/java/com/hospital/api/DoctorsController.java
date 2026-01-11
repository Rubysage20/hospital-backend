package com.hospital.api;

import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class DoctorsController {

  private final DoctorRepository repo;

  public DoctorsController(DoctorRepository repo) {
    this.repo = repo;
  }

  // ---- READ ----
  @GetMapping
  public List<Doctor> all() {
    return repo.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Doctor> one(@PathVariable String id) {
    return repo.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // ---- CREATE ----
  @PostMapping
  public Doctor create(@RequestBody Doctor d) {
    if (d.getNumVisitedPatients() == null) d.setNumVisitedPatients(0);
    if (d.getMonthlyWorkingHours() == null) d.setMonthlyWorkingHours(0);
    if (d.getPaycheck() == null) d.setPaycheck(0.0);
    if (d.getOfficeVisitFee() == null) d.setOfficeVisitFee(0.0);
    return repo.save(d);
  }

  // ---- UPDATE ----
  @PutMapping("/{id}")
  public ResponseEntity<Doctor> update(@PathVariable String id, @RequestBody Doctor d) {
    return repo.findById(id).map(x -> {
      if (d.getFirstName() != null) x.setFirstName(d.getFirstName());
      if (d.getLastName() != null)  x.setLastName(d.getLastName());
      if (d.getAddress() != null)   x.setAddress(d.getAddress());
      if (d.getSpecialty() != null) x.setSpecialty(d.getSpecialty());
      if (d.getOfficeVisitFee() != null) x.setOfficeVisitFee(d.getOfficeVisitFee());
      if (d.getNumVisitedPatients() != null) x.setNumVisitedPatients(d.getNumVisitedPatients());
      if (d.getMonthlyWorkingHours() != null) x.setMonthlyWorkingHours(d.getMonthlyWorkingHours());
      if (d.getPaycheck() != null) x.setPaycheck(d.getPaycheck());
      return ResponseEntity.ok(repo.save(x));
    }).orElse(ResponseEntity.notFound().build());
  }

  // ---- DELETE ----
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    if (!repo.existsById(id)) return ResponseEntity.notFound().build();
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
