package cabs.repository;

import cabs.domain.Appointment;
import cabs.domain.User;
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
}
