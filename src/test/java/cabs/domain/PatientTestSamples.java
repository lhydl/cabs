package cabs.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PatientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Patient getPatientSample1() {
        return new Patient().id(1L).name("name1").email("email1").phone_number("phone_number1");
    }

    public static Patient getPatientSample2() {
        return new Patient().id(2L).name("name2").email("email2").phone_number("phone_number2");
    }

    public static Patient getPatientRandomSampleGenerator() {
        return new Patient()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone_number(UUID.randomUUID().toString());
    }
}
