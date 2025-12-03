package finanzas.domain;

import static finanzas.domain.CuentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import finanzas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CuentaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cuenta.class);
        Cuenta cuenta1 = getCuentaSample1();
        Cuenta cuenta2 = new Cuenta();
        assertThat(cuenta1).isNotEqualTo(cuenta2);

        cuenta2.setId(cuenta1.getId());
        assertThat(cuenta1).isEqualTo(cuenta2);

        cuenta2 = getCuentaSample2();
        assertThat(cuenta1).isNotEqualTo(cuenta2);
    }
}
