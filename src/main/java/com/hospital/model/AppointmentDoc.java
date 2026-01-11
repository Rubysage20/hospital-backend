package com.hospital.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointments")
public class AppointmentDoc {

  public enum Status { SCHEDULED, COMPLETED, CANCELED }

  @Id
  private String id;

  private String doctorId;
  private String patientId;

  // ISO strings: "2025-10-01T10:00"
  private String start;
  private String end;

  // epoch millis UTC
  private Long startTime;
  private Long endTime;

  private Status status = Status.SCHEDULED;

  // getters/setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getDoctorId() { return doctorId; }
  public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

  public String getPatientId() { return patientId; }
  public void setPatientId(String patientId) { this.patientId = patientId; }

  public String getStart() { return start; }
  public void setStart(String start) { this.start = start; }

  public String getEnd() { return end; }
  public void setEnd(String end) { this.end = end; }

  public Long getStartTime() { return startTime; }
  public void setStartTime(Long startTime) { this.startTime = startTime; }

  public Long getEndTime() { return endTime; }
  public void setEndTime(Long endTime) { this.endTime = endTime; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
}
