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
    private String apptType;

    @NotNull
    @Column(name = "appt_datetime", nullable = false)
    private ZonedDateTime apptDatetime;

    @Size(max = 200)
    @Column(name = "remarks", length = 200)
    private String remarks;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "doctor_id", nullable = true)
    private Integer doctorId;

    @Column(name = "status", nullable = true)
    private Integer status = 0;

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

    public String getApptType() {
        return this.apptType;
    }

    public Appointment apptType(String apptType) {
        this.setApptType(apptType);
        return this;
    }

    public void setApptType(String apptType) {
        this.apptType = apptType;
    }

    public ZonedDateTime getApptDatetime() {
        return this.apptDatetime;
    }

    public Appointment apptDatetime(ZonedDateTime apptDatetime) {
        this.setApptDatetime(apptDatetime);
        return this;
    }

    public void setApptDatetime(ZonedDateTime apptDatetime) {
        this.apptDatetime = apptDatetime;
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

    public Integer getPatientId() {
        return this.patientId;
    }

    public Appointment patientId(Integer patientId) {
        this.setPatientId(patientId);
        return this;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return this.doctorId;
    }

    public Appointment doctorId(Integer doctorId) {
        this.setDoctorId(doctorId);
        return this;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Appointment status(Integer status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
            ", apptType='" + getApptType() + "'" +
            ", apptDatetime='" + getApptDatetime() + "'" +
            ", remarks='" + getRemarks() + "'" +
            ", patientId=" + getPatientId() +
            ", doctorId=" + getDoctorId() +
            ", status=" + getStatus() +
            "}";
    }
}
