// ==================== Employee.java ====================
package com.hospital.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    
    // Support both formats: name (new) and firstName/lastName (old)
    private String name;
    private String firstName;
    private String lastName;
    
    private String role;
    private String email;
    private String phone;
    private Double hourlyRate;
    private Double baseSalary;  // Old format uses this
    private Integer monthlyWorkingHours;

    // Constructors
    public Employee() {
        this.monthlyWorkingHours = 0;
        this.hourlyRate = 0.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        // If name is null, build from firstName/lastName for old records
        if (name == null && firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Return hourlyRate, or baseSalary if hourlyRate is null (for old records)
    public Double getHourlyRate() {
        return hourlyRate != null ? hourlyRate : baseSalary;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public Double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Integer getMonthlyWorkingHours() {
        return monthlyWorkingHours;
    }

    public void setMonthlyWorkingHours(Integer monthlyWorkingHours) {
        this.monthlyWorkingHours = monthlyWorkingHours;
    }
}