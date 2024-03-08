package cabs.service.impl;

import cabs.domain.Appointment;
import cabs.repository.AppointmentRepository;
import cabs.service.AppointmentService;
import java.util.Optional;
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
                if (appointment.getAppt_type() != null) {
                    existingAppointment.setAppt_type(appointment.getAppt_type());
                }
                if (appointment.getAppt_datetime() != null) {
                    existingAppointment.setAppt_datetime(appointment.getAppt_datetime());
                }
                if (appointment.getRemarks() != null) {
                    existingAppointment.setRemarks(appointment.getRemarks());
                }
                if (appointment.getPatient_id() != null) {
                    existingAppointment.setPatient_id(appointment.getPatient_id());
                }
                // if (appointment.getDoctor_id() != null) {
                //     existingAppointment.setDoctor_id(appointment.getDoctor_id());
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
}
