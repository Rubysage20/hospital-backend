package com.hospital.api;

import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.model.Employee;
import com.hospital.repository.EmployeeRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PayrollController {

  private final DoctorRepository doctorRepo;
  private final EmployeeRepository employeeRepo;

  public PayrollController(DoctorRepository doctorRepo, EmployeeRepository employeeRepo) {
    this.doctorRepo = doctorRepo;
    this.employeeRepo = employeeRepo;
  }

  @GetMapping("/summary")
  public Map<String, Object> summary() {
    List<Doctor> doctors = doctorRepo.findAll();
    List<Employee> employees = employeeRepo.findAll();

    double doctorPayroll = doctors.stream()
        .mapToDouble(d -> safeInt(d.getNumVisitedPatients()) * safeDouble(d.getOfficeVisitFee()))
        .sum();

    double employeePayroll = employees.stream()
        .mapToDouble(e -> safeInt(e.getMonthlyWorkingHours()) * safeDouble(e.getHourlyRate()))
        .sum();

    Map<String, Object> out = new LinkedHashMap<>();
    out.put("doctorPayroll", doctorPayroll);
    out.put("employeePayroll", employeePayroll);
    out.put("total", doctorPayroll + employeePayroll);
    out.put("doctors", doctors);
    out.put("employees", employees);
    return out;
  }

  private static int safeInt(Integer v) { return v == null ? 0 : v; }
  private static double safeDouble(Double v) { return v == null ? 0.0 : v; }
}

