package cabs.web.rest;

import static cabs.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cabs.IntegrationTest;
import cabs.domain.Appointment;
import cabs.repository.AppointmentRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppointmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppointmentResourceIT {

    private static final String DEFAULT_APPT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_APPT_TYPE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_APPT_DATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_APPT_DATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final Integer DEFAULT_PATIENT_ID = 1;
    private static final Integer UPDATED_PATIENT_ID = 2;

    private static final Integer DEFAULT_DOCTOR_ID = 1;
    private static final Integer UPDATED_DOCTOR_ID = 2;

    private static final String ENTITY_API_URL = "/api/appointments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppointmentMockMvc;

    private Appointment appointment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .apptType(DEFAULT_APPT_TYPE)
            .apptDatetime(DEFAULT_APPT_DATETIME)
            .remarks(DEFAULT_REMARKS)
            .patientId(DEFAULT_PATIENT_ID)
            .doctorId(DEFAULT_DOCTOR_ID);
        return appointment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createUpdatedEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .apptType(UPDATED_APPT_TYPE)
            .apptDatetime(UPDATED_APPT_DATETIME)
            .remarks(UPDATED_REMARKS)
            .patientId(UPDATED_PATIENT_ID)
            .doctorId(UPDATED_DOCTOR_ID);
        return appointment;
    }

    @BeforeEach
    public void initTest() {
        appointment = createEntity(em);
    }

    @Test
    @Transactional
    void createAppointment() throws Exception {
        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();
        // Create the Appointment
        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isCreated());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate + 1);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getApptType()).isEqualTo(DEFAULT_APPT_TYPE);
        assertThat(testAppointment.getApptDatetime()).isEqualTo(DEFAULT_APPT_DATETIME);
        assertThat(testAppointment.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testAppointment.getPatientId()).isEqualTo(DEFAULT_PATIENT_ID);
        assertThat(testAppointment.getDoctorId()).isEqualTo(DEFAULT_DOCTOR_ID);
    }

    @Test
    @Transactional
    void createAppointmentWithExistingId() throws Exception {
        // Create the Appointment with an existing ID
        appointment.setId(1L);

        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAppt_typeIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setApptType(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAppt_datetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setApptDatetime(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPatient_idIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setPatientId(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDoctor_idIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setDoctorId(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppointments() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].apptType").value(hasItem(DEFAULT_APPT_TYPE)))
            .andExpect(jsonPath("$.[*].apptDatetime").value(hasItem(sameInstant(DEFAULT_APPT_DATETIME))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].patientId").value(hasItem(DEFAULT_PATIENT_ID)))
            .andExpect(jsonPath("$.[*].doctorId").value(hasItem(DEFAULT_DOCTOR_ID)));
    }

    @Test
    @Transactional
    void getAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get the appointment
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL_ID, appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appointment.getId().intValue()))
            .andExpect(jsonPath("$.apptType").value(DEFAULT_APPT_TYPE))
            .andExpect(jsonPath("$.apptDatetime").value(sameInstant(DEFAULT_APPT_DATETIME)))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS))
            .andExpect(jsonPath("$.patientId").value(DEFAULT_PATIENT_ID))
            .andExpect(jsonPath("$.doctorId").value(DEFAULT_DOCTOR_ID));
    }

    @Test
    @Transactional
    void getNonExistingAppointment() throws Exception {
        // Get the appointment
        restAppointmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment
        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment);
        updatedAppointment
            .apptType(UPDATED_APPT_TYPE)
            .apptDatetime(UPDATED_APPT_DATETIME)
            .remarks(UPDATED_REMARKS)
            .patientId(UPDATED_PATIENT_ID)
            .doctorId(UPDATED_DOCTOR_ID);

        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAppointment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getApptType()).isEqualTo(UPDATED_APPT_TYPE);
        assertThat(testAppointment.getApptDatetime()).isEqualTo(UPDATED_APPT_DATETIME);
        assertThat(testAppointment.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testAppointment.getPatientId()).isEqualTo(UPDATED_PATIENT_ID);
        assertThat(testAppointment.getDoctorId()).isEqualTo(UPDATED_DOCTOR_ID);
    }

    @Test
    @Transactional
    void putNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appointment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment.apptType(UPDATED_APPT_TYPE).apptDatetime(UPDATED_APPT_DATETIME).doctorId(UPDATED_DOCTOR_ID);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getApptType()).isEqualTo(UPDATED_APPT_TYPE);
        assertThat(testAppointment.getApptDatetime()).isEqualTo(UPDATED_APPT_DATETIME);
        assertThat(testAppointment.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testAppointment.getPatientId()).isEqualTo(DEFAULT_PATIENT_ID);
        assertThat(testAppointment.getDoctorId()).isEqualTo(UPDATED_DOCTOR_ID);
    }

    @Test
    @Transactional
    void fullUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment
            .apptType(UPDATED_APPT_TYPE)
            .apptDatetime(UPDATED_APPT_DATETIME)
            .remarks(UPDATED_REMARKS)
            .patientId(UPDATED_PATIENT_ID)
            .doctorId(UPDATED_DOCTOR_ID);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getApptType()).isEqualTo(UPDATED_APPT_TYPE);
        assertThat(testAppointment.getApptDatetime()).isEqualTo(UPDATED_APPT_DATETIME);
        assertThat(testAppointment.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testAppointment.getPatientId()).isEqualTo(UPDATED_PATIENT_ID);
        assertThat(testAppointment.getDoctorId()).isEqualTo(UPDATED_DOCTOR_ID);
    }

    @Test
    @Transactional
    void patchNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeDelete = appointmentRepository.findAll().size();

        // Delete the appointment
        restAppointmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, appointment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
