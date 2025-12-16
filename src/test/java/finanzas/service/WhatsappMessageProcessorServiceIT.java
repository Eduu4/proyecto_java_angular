package finanzas.service;

import static org.assertj.core.api.Assertions.assertThat;

import finanzas.IntegrationTest;
import finanzas.domain.Categoria;
import finanzas.domain.Cuenta;
import finanzas.domain.Movimiento;
import finanzas.domain.User;
import finanzas.domain.WhatsappMessage;
import finanzas.domain.enumeration.EstadoProcesamiento;
import finanzas.domain.enumeration.TipoCategoria;
import finanzas.domain.enumeration.TipoMovimiento;
import finanzas.repository.CategoriaRepository;
import finanzas.repository.CuentaRepository;
import finanzas.repository.MovimientoRepository;
import finanzas.repository.UserRepository;
import finanzas.repository.WhatsappMessageRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** Integration test for {@link WhatsappMessageProcessorService} */
@IntegrationTest
@Transactional
@org.springframework.test.context.TestPropertySource(
    properties = {
        "spring.cache.type=jcache",
        "spring.cache.jcache.provider=org.ehcache.jsr107.EhcacheCachingProvider",
        "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
        "spring.jpa.properties.hibernate.cache.use_query_cache=false",
    }
)
@org.springframework.context.annotation.Import(WhatsappMessageProcessorServiceIT.TestConfig.class)
class WhatsappMessageProcessorServiceIT {

    @Autowired
    private WhatsappMessageRepository whatsappMessageRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WhatsappMessageProcessorService whatsappMessageProcessorService;

    private User user;
    private Categoria categoria;
    private Cuenta cuenta;
    private WhatsappMessage message;

    @BeforeEach
    void init() {
        // create a test user
        user = new User();
        user.setLogin("wa-test-user");
        // tests persist the user entity directly, so the password must satisfy
        // the entity validation (BCrypt hash length = 60 chars)
        user.setPassword("a".repeat(60));
        user.setActivated(true);
        user.setEmail("wa-test@localhost");
        userRepository.saveAndFlush(user);

        // create category and account for that user
        categoria = new Categoria();
        categoria.setNombre("Cafe");
        categoria.setTipo(TipoCategoria.GASTO);
        categoria.setUsuario(user);
        categoriaRepository.saveAndFlush(categoria);

        cuenta = new Cuenta();
        cuenta.setNombre("Cuenta Principal");
        cuenta.setSaldoInicial(BigDecimal.ZERO);
        cuenta.setUsuario(user);
        cuentaRepository.saveAndFlush(cuenta);
    }

    @AfterEach
    void cleanup() {
        if (message != null && message.getId() != null) {
            whatsappMessageRepository.deleteById(message.getId());
        }
        if (categoria != null && categoria.getId() != null) {
            categoriaRepository.deleteById(categoria.getId());
        }
        if (cuenta != null && cuenta.getId() != null) {
            cuentaRepository.deleteById(cuenta.getId());
        }
        if (user != null && user.getId() != null) {
            userRepository.deleteById(user.getId());
        }
    }

    @Test
    void processSingleMessage_createsMovimientoAndCompletesMessage() {
        // GIVEN: a received whatsapp message referencing existing category/account names
        WhatsappMessage wm = new WhatsappMessage();
        wm.setMensajeOriginal("gasto 12.50 Cafe en Cuenta Principal");
        wm.setTipoMovimiento(TipoMovimiento.GASTO);
        wm.setMonto(new BigDecimal("12.50"));
        wm.setEstado(EstadoProcesamiento.RECIBIDO);
        wm.setNumeroTelefonico("+5491112345678");
        wm.setFechaRecepcion(ZonedDateTime.now());
        wm.setUsuario(user);

        message = whatsappMessageRepository.saveAndFlush(wm);

        // WHEN
        whatsappMessageProcessorService.processSingleMessage(message);

        // THEN: message should be marked COMPLETADO and have movimiento associated
        WhatsappMessage updated = whatsappMessageRepository.findById(message.getId()).orElseThrow();
        assertThat(updated.getEstado()).isEqualTo(EstadoProcesamiento.COMPLETADO);
        assertThat(updated.getMovimientoAsociado()).isNotNull();

        Long movId = updated.getMovimientoAsociado().getId();
        Movimiento mov = movimientoRepository.findById(movId).orElseThrow();
        assertThat(mov.getMonto()).isEqualByComparingTo(new BigDecimal("12.50"));
        assertThat(mov.getDescripcion()).isNotNull();
        assertThat(mov.getFechaRegistro()).isNotNull();
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {

        @org.springframework.context.annotation.Bean
        public javax.cache.CacheManager cacheManager() {
            try {
                // Prefer explicit Ehcache JCache provider when available in test scope
                return javax.cache.Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider").getCacheManager();
            } catch (Exception e) {
                // fallback to default provider
                return javax.cache.Caching.getCachingProvider().getCacheManager();
            }
        }

        @org.springframework.context.annotation.Bean
        public org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
            return hibernateProperties -> {
                // no-op customizer for tests to avoid requiring a javax.cache.CacheManager
            };
        }
    }
}
