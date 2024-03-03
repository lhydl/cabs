package cabs.service.mapper;

import cabs.domain.Patient;
import cabs.service.dto.PatientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Patient} and its DTO {@link PatientDTO}.
 */
@Mapper(componentModel = "spring")
public interface PatientMapper extends EntityMapper<PatientDTO, Patient> {}
