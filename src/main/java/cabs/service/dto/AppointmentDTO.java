package cabs.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
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
}
