package finanzas.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CategoriaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Categoria getCategoriaSample1() {
        return new Categoria().id(1L).nombre("nombre1").color("color1");
    }

    public static Categoria getCategoriaSample2() {
        return new Categoria().id(2L).nombre("nombre2").color("color2");
    }

    public static Categoria getCategoriaRandomSampleGenerator() {
        return new Categoria().id(longCount.incrementAndGet()).nombre(UUID.randomUUID().toString()).color(UUID.randomUUID().toString());
    }
}
