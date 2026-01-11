package com.hospital.api;

import com.hospital.model.Employee;
import com.hospital.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EmployeesController {

    private final EmployeeRepository repo;

    public EmployeesController(EmployeeRepository repo) {
        this.repo = repo;
    }

    // ---- READ ----
    @GetMapping
    public ResponseEntity<List<Employee>> all() {
        try {
            List<Employee> employees = repo.findAll();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> one(@PathVariable String id) {
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
public ResponseEntity<Employee> create(@RequestBody Employee e) {
    try {
        System.out.println("Creating new employee...");
        System.out.println("Name: " + e.getName());
        System.out.println("Role: " + e.getRole());
        
        // Basic validation
        if (e.getName() == null || e.getName().trim().isEmpty()) {
            System.err.println("Validation failed: name is required");
            return ResponseEntity.badRequest().build();
        }
        
        // Initialize default values for null fields
        if (e.getMonthlyWorkingHours() == null) {
            e.setMonthlyWorkingHours(0);
        }
        if (e.getHourlyRate() == null) {
            e.setHourlyRate(0.0);
        }
        if (e.getEmail() == null) {
            e.setEmail("");
        }
        if (e.getPhone() == null) {
            e.setPhone("");
        }
        
        // Don't set ID manually - let MongoDB generate it
        Employee saved = repo.save(e);
        System.out.println("Employee created successfully with ID: " + saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (Exception ex) {
        System.err.println("Error creating employee: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    // ---- UPDATE ----
    @PutMapping("/{id}")
public ResponseEntity<Employee> update(@PathVariable String id, @RequestBody Employee e) {
    try {
        System.out.println("Updating employee with ID: " + id);
        System.out.println("Request body: " + e);
        
        return repo.findById(id).map(x -> {
            System.out.println("Found employee: " + x.getName());
            
            if (e.getName() != null && !e.getName().trim().isEmpty()) {
                x.setName(e.getName());
            }
            if (e.getRole() != null) {
                x.setRole(e.getRole());
            }
            if (e.getEmail() != null) {
                x.setEmail(e.getEmail());
            }
            if (e.getPhone() != null) {
                x.setPhone(e.getPhone());
            }
            if (e.getHourlyRate() != null) {
                x.setHourlyRate(e.getHourlyRate());
            }
            if (e.getMonthlyWorkingHours() != null) {
                x.setMonthlyWorkingHours(e.getMonthlyWorkingHours());
            }
            
            Employee saved = repo.save(x);
            System.out.println("Employee updated successfully: " + saved.getId());
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> {
            System.err.println("Employee not found with ID: " + id);
            return ResponseEntity.notFound().build();
        });
    } catch (Exception ex) {
        System.err.println("Error updating employee: " + ex.getMessage());
        ex.printStackTrace();
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