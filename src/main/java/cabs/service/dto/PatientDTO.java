package cabs.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link cabs.domain.Patient} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PatientDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    @NotNull
    @Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}")
    private String email;

    @NotNull
    @Pattern(regexp = "^[0-9]{1,8}$")
    private String phone_number;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatientDTO)) {
            return false;
        }

        PatientDTO patientDTO = (PatientDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, patientDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PatientDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone_number='" + getPhone_number() + "'" +
            "}";
    }
}
