package cabs.repository;

import cabs.domain.Appointment;
import cabs.domain.User;
import cabs.service.dto.PatientDetailsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Appointment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query(
        value = " SELECT " + "     * " + " FROM " + "     cabs.appointment A" + " WHERE " + "     A.patient_id = :userId ",
        nativeQuery = true
    )
    List<Appointment> getUserAppt(@Param("userId") Integer userId);

    @Query(
        value = " SELECT " +
        "     appt_datetime " +
        " FROM " +
        "     cabs.appointment " +
        " where " +
        "     DATE(appt_datetime) = :selectedDate ",
        nativeQuery = true
    )
    List<String> getExistingTimeSlots(@Param("selectedDate") String selectedDate);

    @Modifying
    @Query(value = " DELETE FROM " + "     cabs.appointment A " + " where " + "     A.patient_id = :userId ", nativeQuery = true)
    void deleteUserAppointments(@Param("userId") Integer userId);

    @Query(
        "SELECT new cabs.service.dto.PatientDetailsDTO(u.firstName, u.lastName, u.email, u.phoneNumber) FROM User u WHERE u.id = :userId"
    )
    PatientDetailsDTO getPatientDetails(@Param("userId") Long userId);
}
