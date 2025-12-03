package finanzas.web.rest;

import static finanzas.domain.CuentaAsserts.*;
import static finanzas.web.rest.TestUtil.createUpdateProxyForBean;
import static finanzas.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import finanzas.IntegrationTest;
import finanzas.domain.Cuenta;
import finanzas.repository.CuentaRepository;
import finanzas.repository.UserRepository;
import finanzas.service.CuentaService;
import finanzas.service.dto.CuentaDTO;
import finanzas.service.mapper.CuentaMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CuentaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CuentaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SALDO_INICIAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SALDO_INICIAL = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cuentas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CuentaRepository cuentaRepositoryMock;

    @Autowired
    private CuentaMapper cuentaMapper;

    @Mock
    private CuentaService cuentaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCuentaMockMvc;

    private Cuenta cuenta;

    private Cuenta insertedCuenta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cuenta createEntity() {
        return new Cuenta().nombre(DEFAULT_NOMBRE).saldoInicial(DEFAULT_SALDO_INICIAL).descripcion(DEFAULT_DESCRIPCION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cuenta createUpdatedEntity() {
        return new Cuenta().nombre(UPDATED_NOMBRE).saldoInicial(UPDATED_SALDO_INICIAL).descripcion(UPDATED_DESCRIPCION);
    }

    @BeforeEach
    void initTest() {
        cuenta = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCuenta != null) {
            cuentaRepository.delete(insertedCuenta);
            insertedCuenta = null;
        }
    }

    @Test
    @Transactional
    void createCuenta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);
        var returnedCuentaDTO = om.readValue(
            restCuentaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CuentaDTO.class
        );

        // Validate the Cuenta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCuenta = cuentaMapper.toEntity(returnedCuentaDTO);
        assertCuentaUpdatableFieldsEquals(returnedCuenta, getPersistedCuenta(returnedCuenta));

        insertedCuenta = returnedCuenta;
    }

    @Test
    @Transactional
    void createCuentaWithExistingId() throws Exception {
        // Create the Cuenta with an existing ID
        cuenta.setId(1L);
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cuenta.setNombre(null);

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSaldoInicialIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cuenta.setSaldoInicial(null);

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCuentas() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.saveAndFlush(cuenta);

        // Get all the cuentaList
        restCuentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cuenta.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].saldoInicial").value(hasItem(sameNumber(DEFAULT_SALDO_INICIAL))))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCuentasWithEagerRelationshipsIsEnabled() throws Exception {
        when(cuentaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCuentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(cuentaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCuentasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cuentaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCuentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(cuentaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCuenta() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.saveAndFlush(cuenta);

        // Get the cuenta
        restCuentaMockMvc
            .perform(get(ENTITY_API_URL_ID, cuenta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cuenta.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.saldoInicial").value(sameNumber(DEFAULT_SALDO_INICIAL)))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    void getNonExistingCuenta() throws Exception {
        // Get the cuenta
        restCuentaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCuenta() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.saveAndFlush(cuenta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cuenta
        Cuenta updatedCuenta = cuentaRepository.findById(cuenta.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCuenta are not directly saved in db
        em.detach(updatedCuenta);
        updatedCuenta.nombre(UPDATED_NOMBRE).saldoInicial(UPDATED_SALDO_INICIAL).descripcion(UPDATED_DESCRIPCION);
        CuentaDTO cuentaDTO = cuentaMapper.toDto(updatedCuenta);

        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cuentaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCuentaToMatchAllProperties(updatedCuenta);
    }

    @Test
    @Transactional
    void putNonExistingCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(longCount.incrementAndGet());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cuentaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(longCount.incrementAndGet());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(longCount.incrementAndGet());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCuentaWithPatch() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.saveAndFlush(cuenta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cuenta using partial update
        Cuenta partialUpdatedCuenta = new Cuenta();
        partialUpdatedCuenta.setId(cuenta.getId());

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuenta))
            )
            .andExpect(status().isOk());

        // Validate the Cuenta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCuentaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCuenta, cuenta), getPersistedCuenta(cuenta));
    }

    @Test
    @Transactional
    void fullUpdateCuentaWithPatch() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.saveAndFlush(cuenta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cuenta using partial update
        Cuenta partialUpdatedCuenta = new Cuenta();
        partialUpdatedCuenta.setId(cuenta.getId());

        partialUpdatedCuenta.nombre(UPDATED_NOMBRE).saldoInicial(UPDATED_SALDO_INICIAL).descripcion(UPDATED_DESCRIPCION);

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuenta))
            )
            .andExpect(status().isOk());

        // Validate the Cuenta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCuentaUpdatableFieldsEquals(partialUpdatedCuenta, getPersistedCuenta(partialUpdatedCuenta));
    }

    @Test
    @Transactional
    void patchNonExistingCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(longCount.incrementAndGet());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cuentaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(longCount.incrementAndGet());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(longCount.incrementAndGet());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCuenta() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.saveAndFlush(cuenta);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cuenta
        restCuentaMockMvc
            .perform(delete(ENTITY_API_URL_ID, cuenta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cuentaRepository.count();
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

    protected Cuenta getPersistedCuenta(Cuenta cuenta) {
        return cuentaRepository.findById(cuenta.getId()).orElseThrow();
    }

    protected void assertPersistedCuentaToMatchAllProperties(Cuenta expectedCuenta) {
        assertCuentaAllPropertiesEquals(expectedCuenta, getPersistedCuenta(expectedCuenta));
    }

    protected void assertPersistedCuentaToMatchUpdatableProperties(Cuenta expectedCuenta) {
        assertCuentaAllUpdatablePropertiesEquals(expectedCuenta, getPersistedCuenta(expectedCuenta));
    }
}
