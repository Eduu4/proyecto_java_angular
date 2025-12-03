package finanzas.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import finanzas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CuentaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CuentaDTO.class);
        CuentaDTO cuentaDTO1 = new CuentaDTO();
        cuentaDTO1.setId(1L);
        CuentaDTO cuentaDTO2 = new CuentaDTO();
        assertThat(cuentaDTO1).isNotEqualTo(cuentaDTO2);
        cuentaDTO2.setId(cuentaDTO1.getId());
        assertThat(cuentaDTO1).isEqualTo(cuentaDTO2);
        cuentaDTO2.setId(2L);
        assertThat(cuentaDTO1).isNotEqualTo(cuentaDTO2);
        cuentaDTO1.setId(null);
        assertThat(cuentaDTO1).isNotEqualTo(cuentaDTO2);
    }
}
