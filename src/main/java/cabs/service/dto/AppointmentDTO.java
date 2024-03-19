package cabs.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class AppointmentDTO implements Serializable {

    private Long id;
    private String apptType;
    private ZonedDateTime apptDatetime;
    private String remarks;
    private Integer patientId;
    private Integer doctorId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    public AppointmentDTO() {
        // No-args constructor
    }

    // Getters and setters for each field

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApptType() {
        return apptType;
    }

    public void setApptType(String apptType) {
        this.apptType = apptType;
    }

    public ZonedDateTime getApptDatetime() {
        return apptDatetime;
    }

    public void setApptDatetime(ZonedDateTime apptDatetime) {
        this.apptDatetime = apptDatetime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // Optionally, override equals(), hashCode(), and toString() methods if needed.
}
