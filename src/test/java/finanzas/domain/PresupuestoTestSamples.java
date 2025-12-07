package finanzas.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PresupuestoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Presupuesto getPresupuestoSample1() {
        return new Presupuesto().id(1L);
    }

    public static Presupuesto getPresupuestoSample2() {
        return new Presupuesto().id(2L);
    }

    public static Presupuesto getPresupuestoRandomSampleGenerator() {
        return new Presupuesto().id(longCount.incrementAndGet());
    }
}
