// ==================== Patient.java ====================
package com.hospital.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "patients")
public class Patient {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String primaryCarePhysician;
    private String lastVisitedAt;        // Stored as ISO string
    private String lastVisitedDoctor;    // doctorId

    // Constructors
    public Patient() {
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

    public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }

    public String getLastVisitedAt() {
        return lastVisitedAt;
    }

    // Accept String
    public void setLastVisitedAt(String lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt;
    }
    
    // Overloaded to accept LocalDateTime (for PatientsController compatibility)
    public void setLastVisitedAt(LocalDateTime lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt != null ? lastVisitedAt.toString() : null;
    }

    public String getLastVisitedDoctor() {
        return lastVisitedDoctor;
    }

    public void setLastVisitedDoctor(String lastVisitedDoctor) {
        this.lastVisitedDoctor = lastVisitedDoctor;
    }
}
