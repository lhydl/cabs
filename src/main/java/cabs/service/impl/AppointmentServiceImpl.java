package cabs.service.impl;

import cabs.domain.Appointment;
import cabs.repository.AppointmentRepository;
import cabs.repository.AppointmentRepository.PatientDetailsProjection;
import cabs.repository.AppointmentRepository.PatientMappingsProjection;
import cabs.service.AppointmentService;
import cabs.service.dto.PatientDetailsDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link cabs.domain.Appointment}.
 */
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        log.debug("Request to save Appointment : {}", appointment);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(Appointment appointment) {
        log.debug("Request to update Appointment : {}", appointment);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> partialUpdate(Appointment appointment) {
        log.debug("Request to partially update Appointment : {}", appointment);

        return appointmentRepository
            .findById(appointment.getId())
            .map(existingAppointment -> {
                if (appointment.getApptType() != null) {
                    existingAppointment.setApptType(appointment.getApptType());
                }
                if (appointment.getApptDatetime() != null) {
                    existingAppointment.setApptDatetime(appointment.getApptDatetime());
                }
                if (appointment.getRemarks() != null) {
                    existingAppointment.setRemarks(appointment.getRemarks());
                }
                if (appointment.getPatientId() != null) {
                    existingAppointment.setPatientId(appointment.getPatientId());
                }
                // if (appointment.getDoctorId() != null) {
                //     existingAppointment.setDoctorId(appointment.getDoctorId());
                // }

                return existingAppointment;
            })
            .map(appointmentRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> findAll(Pageable pageable) {
        log.debug("Request to get all Appointments");
        return appointmentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> findOne(Long id) {
        log.debug("Request to get Appointment : {}", id);
        return appointmentRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Appointment : {}", id);
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointment> getUserAppt(String userId) {
        Integer id = Integer.parseInt(userId);
        return appointmentRepository.getUserAppt(id);
    }

    @Override
    public List<String> getExistingTimeSlots(String selectedDate) {
        // Quick fix of time zone issue for prod only
        List<String> dateTimeStrings = appointmentRepository.getExistingTimeSlots(selectedDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        // Convert, add hours, and collect the results
        List<String> updatedDateTimeStrings = dateTimeStrings
            .stream()
            .map(dateTimeStr -> LocalDateTime.parse(dateTimeStr, formatter).plusHours(8))
            .map(updatedDateTime -> updatedDateTime.format(formatter))
            .collect(Collectors.toList());
        return updatedDateTimeStrings;
    }

    @Override
    public void deleteUserAppointments(Integer userId) {
        appointmentRepository.deleteUserAppointments(userId);
    }

    @Override
    public PatientDetailsProjection getPatientDetails(Long userId) {
        return appointmentRepository.getPatientDetails(userId);
    }

    @Override
    public List<PatientMappingsProjection> getPatientMappings() {
        return appointmentRepository.getPatientMappings();
    }
}
