package finanzas.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import finanzas.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Security tests for Movimiento endpoints to ensure unauthenticated access is rejected.
 */
@IntegrationTest
@AutoConfigureMockMvc
class MovimientoResourceAuthIT {

    private static final String ENTITY_API_URL = "/api/movimientos";

    @Autowired
    private MockMvc restMockMvc;

    @Test
    void getAllMovimientos_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        restMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc")).andExpect(status().isUnauthorized());
    }

    @Test
    void getMovimiento_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        restMockMvc.perform(get(ENTITY_API_URL + "/99999")).andExpect(status().isUnauthorized());
    }
}
