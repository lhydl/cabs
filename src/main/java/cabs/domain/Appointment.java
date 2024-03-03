package cabs.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "appt_type", length = 100, nullable = false)
    private String appt_type;

    @NotNull
    @Column(name = "appt_datetime", nullable = false)
    private ZonedDateTime appt_datetime;

    @Size(max = 200)
    @Column(name = "remarks", length = 200)
    private String remarks;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private Integer patient_id;

    @NotNull
    @Column(name = "doctor_id", nullable = false)
    private Integer doctor_id;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Appointment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppt_type() {
        return this.appt_type;
    }

    public Appointment appt_type(String appt_type) {
        this.setAppt_type(appt_type);
        return this;
    }

    public void setAppt_type(String appt_type) {
        this.appt_type = appt_type;
    }

    public ZonedDateTime getAppt_datetime() {
        return this.appt_datetime;
    }

    public Appointment appt_datetime(ZonedDateTime appt_datetime) {
        this.setAppt_datetime(appt_datetime);
        return this;
    }

    public void setAppt_datetime(ZonedDateTime appt_datetime) {
        this.appt_datetime = appt_datetime;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Appointment remarks(String remarks) {
        this.setRemarks(remarks);
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getPatient_id() {
        return this.patient_id;
    }

    public Appointment patient_id(Integer patient_id) {
        this.setPatient_id(patient_id);
        return this;
    }

    public void setPatient_id(Integer patient_id) {
        this.patient_id = patient_id;
    }

    public Integer getDoctor_id() {
        return this.doctor_id;
    }

    public Appointment doctor_id(Integer doctor_id) {
        this.setDoctor_id(doctor_id);
        return this;
    }

    public void setDoctor_id(Integer doctor_id) {
        this.doctor_id = doctor_id;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        return getId() != null && getId().equals(((Appointment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", appt_type='" + getAppt_type() + "'" +
            ", appt_datetime='" + getAppt_datetime() + "'" +
            ", remarks='" + getRemarks() + "'" +
            ", patient_id=" + getPatient_id() +
            ", doctor_id=" + getDoctor_id() +
            "}";
    }
}
