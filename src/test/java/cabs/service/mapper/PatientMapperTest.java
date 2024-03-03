package cabs.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class PatientMapperTest {

    private PatientMapper patientMapper;

    @BeforeEach
    public void setUp() {
        patientMapper = new PatientMapperImpl();
    }
}
