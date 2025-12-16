package finanzas.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import finanzas.IntegrationTest;
import finanzas.domain.Movimiento;
import finanzas.domain.enumeration.TipoMovimiento;
import finanzas.repository.MovimientoRepository;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MovimientoResumenIT {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private MockMvc restMockMvc;

    @BeforeEach
    void init() {
        movimientoRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        movimientoRepository.deleteAll();
    }

    @Test
    @Transactional
    void resumenCalculadoCorrectamente() throws Exception {
        Movimiento m1 = new Movimiento();
        m1.setTipo(TipoMovimiento.INGRESO);
        m1.setMonto(new java.math.BigDecimal("1000"));
        m1.setFechaMovimiento(ZonedDateTime.now().minusDays(1));
        m1.setFechaRegistro(ZonedDateTime.now());

        Movimiento m2 = new Movimiento();
        m2.setTipo(TipoMovimiento.GASTO);
        m2.setMonto(new java.math.BigDecimal("400"));
        m2.setFechaMovimiento(ZonedDateTime.now().minusDays(1));
        m2.setFechaRegistro(ZonedDateTime.now());

        movimientoRepository.saveAndFlush(m1);
        movimientoRepository.saveAndFlush(m2);

        restMockMvc
            .perform(get("/api/movimientos/resumen"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalIngresos").value(1000.0))
            .andExpect(jsonPath("$.totalGastos").value(400.0))
            .andExpect(jsonPath("$.balance").value(600.0));
    }
}
