package finanzas.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CuentaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Cuenta getCuentaSample1() {
        return new Cuenta().id(1L).nombre("nombre1").descripcion("descripcion1");
    }

    public static Cuenta getCuentaSample2() {
        return new Cuenta().id(2L).nombre("nombre2").descripcion("descripcion2");
    }

    public static Cuenta getCuentaRandomSampleGenerator() {
        return new Cuenta().id(longCount.incrementAndGet()).nombre(UUID.randomUUID().toString()).descripcion(UUID.randomUUID().toString());
    }
}
