package finanzas.domain;

import static finanzas.domain.CategoriaTestSamples.*;
import static finanzas.domain.PresupuestoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import finanzas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PresupuestoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Presupuesto.class);
        Presupuesto presupuesto1 = getPresupuestoSample1();
        Presupuesto presupuesto2 = new Presupuesto();
        assertThat(presupuesto1).isNotEqualTo(presupuesto2);

        presupuesto2.setId(presupuesto1.getId());
        assertThat(presupuesto1).isEqualTo(presupuesto2);

        presupuesto2 = getPresupuestoSample2();
        assertThat(presupuesto1).isNotEqualTo(presupuesto2);
    }

    @Test
    void categoriaTest() {
        Presupuesto presupuesto = getPresupuestoRandomSampleGenerator();
        Categoria categoriaBack = getCategoriaRandomSampleGenerator();

        presupuesto.setCategoria(categoriaBack);
        assertThat(presupuesto.getCategoria()).isEqualTo(categoriaBack);

        presupuesto.categoria(null);
        assertThat(presupuesto.getCategoria()).isNull();
    }
}
