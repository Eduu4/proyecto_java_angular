package finanzas.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import finanzas.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PresupuestoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PresupuestoDTO.class);
        PresupuestoDTO presupuestoDTO1 = new PresupuestoDTO();
        presupuestoDTO1.setId(1L);
        PresupuestoDTO presupuestoDTO2 = new PresupuestoDTO();
        assertThat(presupuestoDTO1).isNotEqualTo(presupuestoDTO2);
        presupuestoDTO2.setId(presupuestoDTO1.getId());
        assertThat(presupuestoDTO1).isEqualTo(presupuestoDTO2);
        presupuestoDTO2.setId(2L);
        assertThat(presupuestoDTO1).isNotEqualTo(presupuestoDTO2);
        presupuestoDTO1.setId(null);
        assertThat(presupuestoDTO1).isNotEqualTo(presupuestoDTO2);
    }
}
