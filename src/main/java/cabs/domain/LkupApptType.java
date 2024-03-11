package cabs.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "LKUP_APPT_TYPE")
public class LkupApptType {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "label")
    private String label;

    @Column(name = "order_number")
    private Integer order_number;

    @Column(name = "is_active")
    private Boolean is_active;
}
