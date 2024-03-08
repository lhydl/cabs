package cabs.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AppointmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Appointment getAppointmentSample1() {
        return new Appointment().id(1L).apptType("appt_type1").remarks("remarks1").patientId(1).doctorId(1);
    }

    public static Appointment getAppointmentSample2() {
        return new Appointment().id(2L).apptType("appt_type2").remarks("remarks2").patientId(2).doctorId(2);
    }

    public static Appointment getAppointmentRandomSampleGenerator() {
        return new Appointment()
            .id(longCount.incrementAndGet())
            .apptType(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString())
            .patientId(intCount.incrementAndGet())
            .doctorId(intCount.incrementAndGet());
    }
}
