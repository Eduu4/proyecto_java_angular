package finanzas.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MovimientoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Movimiento getMovimientoSample1() {
        return new Movimiento().id(1L).descripcion("descripcion1");
    }

    public static Movimiento getMovimientoSample2() {
        return new Movimiento().id(2L).descripcion("descripcion2");
    }

    public static Movimiento getMovimientoRandomSampleGenerator() {
        return new Movimiento().id(longCount.incrementAndGet()).descripcion(UUID.randomUUID().toString());
    }
}
