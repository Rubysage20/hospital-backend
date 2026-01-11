// ==================== Doctor.java ====================
package com.hospital.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String specialty;
    private Double officeVisitFee;
    private Integer numVisitedPatients;
    private Integer monthlyWorkingHours;
    private Double paycheck;

    // Constructors
    public Doctor() {
        this.numVisitedPatients = 0;
        this.monthlyWorkingHours = 0;
        this.paycheck = 0.0;
        this.officeVisitFee = 0.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Double getOfficeVisitFee() {
        return officeVisitFee;
    }

    public void setOfficeVisitFee(Double officeVisitFee) {
        this.officeVisitFee = officeVisitFee;
    }

    public Integer getNumVisitedPatients() {
        return numVisitedPatients;
    }

    public void setNumVisitedPatients(Integer numVisitedPatients) {
        this.numVisitedPatients = numVisitedPatients;
    }

    public Integer getMonthlyWorkingHours() {
        return monthlyWorkingHours;
    }

    public void setMonthlyWorkingHours(Integer monthlyWorkingHours) {
        this.monthlyWorkingHours = monthlyWorkingHours;
    }

    public Double getPaycheck() {
        return paycheck;
    }

    public void setPaycheck(Double paycheck) {
        this.paycheck = paycheck;
    }
}