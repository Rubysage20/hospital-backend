package com.hospital.service;

import com.hospital.model.AppointmentDoc;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {

    private final AppointmentRepository apptRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    public ScheduledTasks(AppointmentRepository apptRepo,
                         DoctorRepository doctorRepo,
                         PatientRepository patientRepo) {
        this.apptRepo = apptRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    // Run every 5 minutes (300000 milliseconds)
    @Scheduled(fixedRate = 300000)
    public void completePastAppointments() {
        long currentTime = System.currentTimeMillis();
        List<AppointmentDoc> scheduledAppointments = apptRepo.findAll().stream()
            .filter(a -> a.getStatus() == AppointmentDoc.Status.SCHEDULED)
            .filter(a -> a.getEndTime() != null && a.getEndTime() < currentTime)
            .toList();

        for (AppointmentDoc appointment : scheduledAppointments) {
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

        if (!scheduledAppointments.isEmpty()) {
            System.out.println("✓ Completed " + scheduledAppointments.size() + " past appointments");
        }
    }
}