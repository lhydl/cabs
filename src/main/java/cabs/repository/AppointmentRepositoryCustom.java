package cabs.repository;

import cabs.domain.Appointment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Appointment> getUserAppt(Integer userId, String predicate, String sort) {
        String sql = "SELECT * FROM cabs.appointment A WHERE A.patient_id = :userId ORDER BY " + predicate + " " + sort;

        Query query = entityManager.createNativeQuery(sql, Appointment.class);
        query.setParameter("userId", userId);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Appointment> getTodaysAppointments(String today) {
        String sql =
            " SELECT * " +
            " FROM " +
            "     cabs.appointment " +
            " WHERE " +
            "     DATE(appt_datetime) = :today " +
            " ORDER BY " +
            "     appt_datetime ASC ";

        Query query = entityManager.createNativeQuery(sql, Appointment.class);
        query.setParameter("today", today);

        return query.getResultList();
    }
}
