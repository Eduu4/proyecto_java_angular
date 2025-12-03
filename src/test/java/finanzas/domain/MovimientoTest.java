package finanzas.domain;

import static finanzas.domain.CategoriaTestSamples.*;
import static finanzas.domain.CuentaTestSamples.*;
import static finanzas.domain.MovimientoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import finanzas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MovimientoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Movimiento.class);
        Movimiento movimiento1 = getMovimientoSample1();
        Movimiento movimiento2 = new Movimiento();
        assertThat(movimiento1).isNotEqualTo(movimiento2);

        movimiento2.setId(movimiento1.getId());
        assertThat(movimiento1).isEqualTo(movimiento2);

        movimiento2 = getMovimientoSample2();
        assertThat(movimiento1).isNotEqualTo(movimiento2);
    }

    @Test
    void categoriaTest() {
        Movimiento movimiento = getMovimientoRandomSampleGenerator();
        Categoria categoriaBack = getCategoriaRandomSampleGenerator();

        movimiento.setCategoria(categoriaBack);
        assertThat(movimiento.getCategoria()).isEqualTo(categoriaBack);

        movimiento.categoria(null);
        assertThat(movimiento.getCategoria()).isNull();
    }

    @Test
    void cuentaTest() {
        Movimiento movimiento = getMovimientoRandomSampleGenerator();
        Cuenta cuentaBack = getCuentaRandomSampleGenerator();

        movimiento.setCuenta(cuentaBack);
        assertThat(movimiento.getCuenta()).isEqualTo(cuentaBack);

        movimiento.cuenta(null);
        assertThat(movimiento.getCuenta()).isNull();
    }
}
