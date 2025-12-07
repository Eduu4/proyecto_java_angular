package finanzas.web.rest;

import static finanzas.domain.MovimientoAsserts.*;
import static finanzas.web.rest.TestUtil.createUpdateProxyForBean;
import static finanzas.web.rest.TestUtil.sameInstant;
import static finanzas.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import finanzas.IntegrationTest;
import finanzas.domain.Movimiento;
import finanzas.domain.enumeration.TipoMovimiento;
import finanzas.repository.MovimientoRepository;
import finanzas.repository.UserRepository;
import finanzas.service.MovimientoService;
import finanzas.service.dto.MovimientoDTO;
import finanzas.service.mapper.MovimientoMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MovimientoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MovimientoResourceIT {

    private static final TipoMovimiento DEFAULT_TIPO = TipoMovimiento.GASTO;
    private static final TipoMovimiento UPDATED_TIPO = TipoMovimiento.INGRESO;

    private static final BigDecimal DEFAULT_MONTO = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTO = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_FECHA_MOVIMIENTO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FECHA_MOVIMIENTO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_FECHA_REGISTRO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FECHA_REGISTRO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_FECHA_ACTUALIZACION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FECHA_ACTUALIZACION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/movimientos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private MovimientoRepository movimientoRepositoryMock;

    @Autowired
    private MovimientoMapper movimientoMapper;

    @Mock
    private MovimientoService movimientoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMovimientoMockMvc;

    private Movimiento movimiento;

    private Movimiento insertedMovimiento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movimiento createEntity() {
        return new Movimiento()
            .tipo(DEFAULT_TIPO)
            .monto(DEFAULT_MONTO)
            .fechaMovimiento(DEFAULT_FECHA_MOVIMIENTO)
            .fechaRegistro(DEFAULT_FECHA_REGISTRO)
            .fechaActualizacion(DEFAULT_FECHA_ACTUALIZACION)
            .descripcion(DEFAULT_DESCRIPCION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movimiento createUpdatedEntity() {
        return new Movimiento()
            .tipo(UPDATED_TIPO)
            .monto(UPDATED_MONTO)
            .fechaMovimiento(UPDATED_FECHA_MOVIMIENTO)
            .fechaRegistro(UPDATED_FECHA_REGISTRO)
            .fechaActualizacion(UPDATED_FECHA_ACTUALIZACION)
            .descripcion(UPDATED_DESCRIPCION);
    }

    @BeforeEach
    void initTest() {
        movimiento = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMovimiento != null) {
            movimientoRepository.delete(insertedMovimiento);
            insertedMovimiento = null;
        }
    }

    @Test
    @Transactional
    void createMovimiento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);
        var returnedMovimientoDTO = om.readValue(
            restMovimientoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MovimientoDTO.class
        );

        // Validate the Movimiento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMovimiento = movimientoMapper.toEntity(returnedMovimientoDTO);
        assertMovimientoUpdatableFieldsEquals(returnedMovimiento, getPersistedMovimiento(returnedMovimiento));

        insertedMovimiento = returnedMovimiento;
    }

    @Test
    @Transactional
    void createMovimientoWithExistingId() throws Exception {
        // Create the Movimiento with an existing ID
        movimiento.setId(1L);
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMovimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        movimiento.setTipo(null);

        // Create the Movimiento, which fails.
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        restMovimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        movimiento.setMonto(null);

        // Create the Movimiento, which fails.
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        restMovimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaMovimientoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        movimiento.setFechaMovimiento(null);

        // Create the Movimiento, which fails.
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        restMovimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaRegistroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        movimiento.setFechaRegistro(null);

        // Create the Movimiento, which fails.
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        restMovimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMovimientos() throws Exception {
        // Initialize the database
        insertedMovimiento = movimientoRepository.saveAndFlush(movimiento);

        // Get all the movimientoList
        restMovimientoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movimiento.getId().intValue())))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].monto").value(hasItem(sameNumber(DEFAULT_MONTO))))
            .andExpect(jsonPath("$.[*].fechaMovimiento").value(hasItem(sameInstant(DEFAULT_FECHA_MOVIMIENTO))))
            .andExpect(jsonPath("$.[*].fechaRegistro").value(hasItem(sameInstant(DEFAULT_FECHA_REGISTRO))))
            .andExpect(jsonPath("$.[*].fechaActualizacion").value(hasItem(sameInstant(DEFAULT_FECHA_ACTUALIZACION))))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMovimientosWithEagerRelationshipsIsEnabled() throws Exception {
        when(movimientoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMovimientoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(movimientoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMovimientosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(movimientoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMovimientoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(movimientoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMovimiento() throws Exception {
        // Initialize the database
        insertedMovimiento = movimientoRepository.saveAndFlush(movimiento);

        // Get the movimiento
        restMovimientoMockMvc
            .perform(get(ENTITY_API_URL_ID, movimiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(movimiento.getId().intValue()))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.monto").value(sameNumber(DEFAULT_MONTO)))
            .andExpect(jsonPath("$.fechaMovimiento").value(sameInstant(DEFAULT_FECHA_MOVIMIENTO)))
            .andExpect(jsonPath("$.fechaRegistro").value(sameInstant(DEFAULT_FECHA_REGISTRO)))
            .andExpect(jsonPath("$.fechaActualizacion").value(sameInstant(DEFAULT_FECHA_ACTUALIZACION)))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    void getNonExistingMovimiento() throws Exception {
        // Get the movimiento
        restMovimientoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMovimiento() throws Exception {
        // Initialize the database
        insertedMovimiento = movimientoRepository.saveAndFlush(movimiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the movimiento
        Movimiento updatedMovimiento = movimientoRepository.findById(movimiento.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMovimiento are not directly saved in db
        em.detach(updatedMovimiento);
        updatedMovimiento
            .tipo(UPDATED_TIPO)
            .monto(UPDATED_MONTO)
            .fechaMovimiento(UPDATED_FECHA_MOVIMIENTO)
            .fechaRegistro(UPDATED_FECHA_REGISTRO)
            .fechaActualizacion(UPDATED_FECHA_ACTUALIZACION)
            .descripcion(UPDATED_DESCRIPCION);
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(updatedMovimiento);

        restMovimientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, movimientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(movimientoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMovimientoToMatchAllProperties(updatedMovimiento);
    }

    @Test
    @Transactional
    void putNonExistingMovimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movimiento.setId(longCount.incrementAndGet());

        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMovimientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, movimientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(movimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMovimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movimiento.setId(longCount.incrementAndGet());

        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMovimientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(movimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMovimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movimiento.setId(longCount.incrementAndGet());

        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMovimientoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMovimientoWithPatch() throws Exception {
        // Initialize the database
        insertedMovimiento = movimientoRepository.saveAndFlush(movimiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the movimiento using partial update
        Movimiento partialUpdatedMovimiento = new Movimiento();
        partialUpdatedMovimiento.setId(movimiento.getId());

        partialUpdatedMovimiento.monto(UPDATED_MONTO).fechaRegistro(UPDATED_FECHA_REGISTRO).descripcion(UPDATED_DESCRIPCION);

        restMovimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMovimiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMovimiento))
            )
            .andExpect(status().isOk());

        // Validate the Movimiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMovimientoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMovimiento, movimiento),
            getPersistedMovimiento(movimiento)
        );
    }

    @Test
    @Transactional
    void fullUpdateMovimientoWithPatch() throws Exception {
        // Initialize the database
        insertedMovimiento = movimientoRepository.saveAndFlush(movimiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the movimiento using partial update
        Movimiento partialUpdatedMovimiento = new Movimiento();
        partialUpdatedMovimiento.setId(movimiento.getId());

        partialUpdatedMovimiento
            .tipo(UPDATED_TIPO)
            .monto(UPDATED_MONTO)
            .fechaMovimiento(UPDATED_FECHA_MOVIMIENTO)
            .fechaRegistro(UPDATED_FECHA_REGISTRO)
            .fechaActualizacion(UPDATED_FECHA_ACTUALIZACION)
            .descripcion(UPDATED_DESCRIPCION);

        restMovimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMovimiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMovimiento))
            )
            .andExpect(status().isOk());

        // Validate the Movimiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMovimientoUpdatableFieldsEquals(partialUpdatedMovimiento, getPersistedMovimiento(partialUpdatedMovimiento));
    }

    @Test
    @Transactional
    void patchNonExistingMovimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movimiento.setId(longCount.incrementAndGet());

        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMovimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, movimientoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(movimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMovimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movimiento.setId(longCount.incrementAndGet());

        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMovimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(movimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMovimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        movimiento.setId(longCount.incrementAndGet());

        // Create the Movimiento
        MovimientoDTO movimientoDTO = movimientoMapper.toDto(movimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMovimientoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(movimientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Movimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMovimiento() throws Exception {
        // Initialize the database
        insertedMovimiento = movimientoRepository.saveAndFlush(movimiento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the movimiento
        restMovimientoMockMvc
            .perform(delete(ENTITY_API_URL_ID, movimiento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return movimientoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Movimiento getPersistedMovimiento(Movimiento movimiento) {
        return movimientoRepository.findById(movimiento.getId()).orElseThrow();
    }

    protected void assertPersistedMovimientoToMatchAllProperties(Movimiento expectedMovimiento) {
        assertMovimientoAllPropertiesEquals(expectedMovimiento, getPersistedMovimiento(expectedMovimiento));
    }

    protected void assertPersistedMovimientoToMatchUpdatableProperties(Movimiento expectedMovimiento) {
        assertMovimientoAllUpdatablePropertiesEquals(expectedMovimiento, getPersistedMovimiento(expectedMovimiento));
    }
}
