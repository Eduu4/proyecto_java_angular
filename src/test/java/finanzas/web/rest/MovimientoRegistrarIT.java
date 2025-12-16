package finanzas.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import finanzas.IntegrationTest;
import finanzas.repository.MovimientoRepository;
import finanzas.service.dto.MovimientoRequestDTO;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MovimientoRegistrarIT {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private ObjectMapper om;

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
    void registrarMovimiento_validaCampos() throws Exception {
        MovimientoRequestDTO req = new MovimientoRequestDTO();
        // missing tipo -> validation error
        req.setMonto(100.0);
        req.setDescripcion("test");
        req.setFecha(LocalDate.now());

        restMockMvc
            .perform(post("/api/movimientos/registrar").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void registrarMovimiento_ok() throws Exception {
        MovimientoRequestDTO req = new MovimientoRequestDTO();
        req.setTipo(finanzas.domain.enumeration.TipoMovimiento.GASTO);
        req.setMonto(150.0);
        req.setDescripcion("Supermercado");
        req.setFecha(LocalDate.now());

        restMockMvc
            .perform(post("/api/movimientos/registrar").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.monto").value(150.0));
    }
}
