package com.hospital.repository;

import com.hospital.model.AppointmentDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<AppointmentDoc, String> {
    List<AppointmentDoc> findByDoctorId(String doctorId);
    List<AppointmentDoc> findByPatientId(String patientId);
}
