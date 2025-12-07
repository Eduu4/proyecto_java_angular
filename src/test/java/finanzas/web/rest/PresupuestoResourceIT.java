package finanzas.web.rest;

import static finanzas.domain.PresupuestoAsserts.*;
import static finanzas.web.rest.TestUtil.createUpdateProxyForBean;
import static finanzas.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import finanzas.IntegrationTest;
import finanzas.domain.Presupuesto;
import finanzas.domain.enumeration.PeriodoPresupuesto;
import finanzas.repository.PresupuestoRepository;
import finanzas.repository.UserRepository;
import finanzas.service.PresupuestoService;
import finanzas.service.dto.PresupuestoDTO;
import finanzas.service.mapper.PresupuestoMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PresupuestoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PresupuestoResourceIT {

    private static final BigDecimal DEFAULT_MONTO = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTO = new BigDecimal(2);

    private static final PeriodoPresupuesto DEFAULT_PERIODO = PeriodoPresupuesto.SEMANAL;
    private static final PeriodoPresupuesto UPDATED_PERIODO = PeriodoPresupuesto.MENSUAL;

    private static final LocalDate DEFAULT_FECHA_INICIO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_INICIO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FECHA_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_FIN = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/presupuestos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PresupuestoRepository presupuestoRepositoryMock;

    @Autowired
    private PresupuestoMapper presupuestoMapper;

    @Mock
    private PresupuestoService presupuestoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPresupuestoMockMvc;

    private Presupuesto presupuesto;

    private Presupuesto insertedPresupuesto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Presupuesto createEntity() {
        return new Presupuesto()
            .monto(DEFAULT_MONTO)
            .periodo(DEFAULT_PERIODO)
            .fechaInicio(DEFAULT_FECHA_INICIO)
            .fechaFin(DEFAULT_FECHA_FIN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Presupuesto createUpdatedEntity() {
        return new Presupuesto()
            .monto(UPDATED_MONTO)
            .periodo(UPDATED_PERIODO)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFin(UPDATED_FECHA_FIN);
    }

    @BeforeEach
    void initTest() {
        presupuesto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPresupuesto != null) {
            presupuestoRepository.delete(insertedPresupuesto);
            insertedPresupuesto = null;
        }
    }

    @Test
    @Transactional
    void createPresupuesto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);
        var returnedPresupuestoDTO = om.readValue(
            restPresupuestoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PresupuestoDTO.class
        );

        // Validate the Presupuesto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPresupuesto = presupuestoMapper.toEntity(returnedPresupuestoDTO);
        assertPresupuestoUpdatableFieldsEquals(returnedPresupuesto, getPersistedPresupuesto(returnedPresupuesto));

        insertedPresupuesto = returnedPresupuesto;
    }

    @Test
    @Transactional
    void createPresupuestoWithExistingId() throws Exception {
        // Create the Presupuesto with an existing ID
        presupuesto.setId(1L);
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMontoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        presupuesto.setMonto(null);

        // Create the Presupuesto, which fails.
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        restPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        presupuesto.setPeriodo(null);

        // Create the Presupuesto, which fails.
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        restPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaInicioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        presupuesto.setFechaInicio(null);

        // Create the Presupuesto, which fails.
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        restPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaFinIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        presupuesto.setFechaFin(null);

        // Create the Presupuesto, which fails.
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        restPresupuestoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPresupuestos() throws Exception {
        // Initialize the database
        insertedPresupuesto = presupuestoRepository.saveAndFlush(presupuesto);

        // Get all the presupuestoList
        restPresupuestoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(presupuesto.getId().intValue())))
            .andExpect(jsonPath("$.[*].monto").value(hasItem(sameNumber(DEFAULT_MONTO))))
            .andExpect(jsonPath("$.[*].periodo").value(hasItem(DEFAULT_PERIODO.toString())))
            .andExpect(jsonPath("$.[*].fechaInicio").value(hasItem(DEFAULT_FECHA_INICIO.toString())))
            .andExpect(jsonPath("$.[*].fechaFin").value(hasItem(DEFAULT_FECHA_FIN.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPresupuestosWithEagerRelationshipsIsEnabled() throws Exception {
        when(presupuestoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPresupuestoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(presupuestoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPresupuestosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(presupuestoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPresupuestoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(presupuestoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPresupuesto() throws Exception {
        // Initialize the database
        insertedPresupuesto = presupuestoRepository.saveAndFlush(presupuesto);

        // Get the presupuesto
        restPresupuestoMockMvc
            .perform(get(ENTITY_API_URL_ID, presupuesto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(presupuesto.getId().intValue()))
            .andExpect(jsonPath("$.monto").value(sameNumber(DEFAULT_MONTO)))
            .andExpect(jsonPath("$.periodo").value(DEFAULT_PERIODO.toString()))
            .andExpect(jsonPath("$.fechaInicio").value(DEFAULT_FECHA_INICIO.toString()))
            .andExpect(jsonPath("$.fechaFin").value(DEFAULT_FECHA_FIN.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPresupuesto() throws Exception {
        // Get the presupuesto
        restPresupuestoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPresupuesto() throws Exception {
        // Initialize the database
        insertedPresupuesto = presupuestoRepository.saveAndFlush(presupuesto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the presupuesto
        Presupuesto updatedPresupuesto = presupuestoRepository.findById(presupuesto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPresupuesto are not directly saved in db
        em.detach(updatedPresupuesto);
        updatedPresupuesto.monto(UPDATED_MONTO).periodo(UPDATED_PERIODO).fechaInicio(UPDATED_FECHA_INICIO).fechaFin(UPDATED_FECHA_FIN);
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(updatedPresupuesto);

        restPresupuestoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, presupuestoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(presupuestoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPresupuestoToMatchAllProperties(updatedPresupuesto);
    }

    @Test
    @Transactional
    void putNonExistingPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presupuesto.setId(longCount.incrementAndGet());

        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPresupuestoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, presupuestoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(presupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presupuesto.setId(longCount.incrementAndGet());

        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresupuestoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(presupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presupuesto.setId(longCount.incrementAndGet());

        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresupuestoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePresupuestoWithPatch() throws Exception {
        // Initialize the database
        insertedPresupuesto = presupuestoRepository.saveAndFlush(presupuesto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the presupuesto using partial update
        Presupuesto partialUpdatedPresupuesto = new Presupuesto();
        partialUpdatedPresupuesto.setId(presupuesto.getId());

        partialUpdatedPresupuesto.periodo(UPDATED_PERIODO).fechaInicio(UPDATED_FECHA_INICIO);

        restPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPresupuesto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPresupuesto))
            )
            .andExpect(status().isOk());

        // Validate the Presupuesto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPresupuestoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPresupuesto, presupuesto),
            getPersistedPresupuesto(presupuesto)
        );
    }

    @Test
    @Transactional
    void fullUpdatePresupuestoWithPatch() throws Exception {
        // Initialize the database
        insertedPresupuesto = presupuestoRepository.saveAndFlush(presupuesto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the presupuesto using partial update
        Presupuesto partialUpdatedPresupuesto = new Presupuesto();
        partialUpdatedPresupuesto.setId(presupuesto.getId());

        partialUpdatedPresupuesto
            .monto(UPDATED_MONTO)
            .periodo(UPDATED_PERIODO)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFin(UPDATED_FECHA_FIN);

        restPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPresupuesto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPresupuesto))
            )
            .andExpect(status().isOk());

        // Validate the Presupuesto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPresupuestoUpdatableFieldsEquals(partialUpdatedPresupuesto, getPersistedPresupuesto(partialUpdatedPresupuesto));
    }

    @Test
    @Transactional
    void patchNonExistingPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presupuesto.setId(longCount.incrementAndGet());

        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, presupuestoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(presupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presupuesto.setId(longCount.incrementAndGet());

        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresupuestoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(presupuestoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPresupuesto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presupuesto.setId(longCount.incrementAndGet());

        // Create the Presupuesto
        PresupuestoDTO presupuestoDTO = presupuestoMapper.toDto(presupuesto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresupuestoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(presupuestoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Presupuesto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePresupuesto() throws Exception {
        // Initialize the database
        insertedPresupuesto = presupuestoRepository.saveAndFlush(presupuesto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the presupuesto
        restPresupuestoMockMvc
            .perform(delete(ENTITY_API_URL_ID, presupuesto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return presupuestoRepository.count();
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

    protected Presupuesto getPersistedPresupuesto(Presupuesto presupuesto) {
        return presupuestoRepository.findById(presupuesto.getId()).orElseThrow();
    }

    protected void assertPersistedPresupuestoToMatchAllProperties(Presupuesto expectedPresupuesto) {
        assertPresupuestoAllPropertiesEquals(expectedPresupuesto, getPersistedPresupuesto(expectedPresupuesto));
    }

    protected void assertPersistedPresupuestoToMatchUpdatableProperties(Presupuesto expectedPresupuesto) {
        assertPresupuestoAllUpdatablePropertiesEquals(expectedPresupuesto, getPersistedPresupuesto(expectedPresupuesto));
    }
}
