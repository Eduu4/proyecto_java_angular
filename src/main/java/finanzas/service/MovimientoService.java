package finanzas.service;

import finanzas.domain.Movimiento;
import finanzas.repository.MovimientoRepository;
import finanzas.service.dto.MovimientoDTO;
import finanzas.service.mapper.MovimientoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link finanzas.domain.Movimiento}.
 */
@Service
@Transactional
public class MovimientoService {

    private static final Logger LOG = LoggerFactory.getLogger(MovimientoService.class);

    private final MovimientoRepository movimientoRepository;

    private final MovimientoMapper movimientoMapper;
    private final finanzas.repository.CategoriaRepository categoriaRepository;
    private final finanzas.repository.UserRepository userRepository;

    public MovimientoService(
        MovimientoRepository movimientoRepository,
        MovimientoMapper movimientoMapper,
        finanzas.repository.CategoriaRepository categoriaRepository,
        finanzas.repository.UserRepository userRepository
    ) {
        this.movimientoRepository = movimientoRepository;
        this.movimientoMapper = movimientoMapper;
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a movimiento.
     *
     * @param movimientoDTO the entity to save.
     * @return the persisted entity.
     */
    public MovimientoDTO save(MovimientoDTO movimientoDTO) {
        LOG.debug("Request to save Movimiento : {}", movimientoDTO);
        Movimiento movimiento = movimientoMapper.toEntity(movimientoDTO);
        movimiento = movimientoRepository.save(movimiento);
        return movimientoMapper.toDto(movimiento);
    }

    /**
     * Update a movimiento.
     *
     * @param movimientoDTO the entity to save.
     * @return the persisted entity.
     */
    public MovimientoDTO update(MovimientoDTO movimientoDTO) {
        LOG.debug("Request to update Movimiento : {}", movimientoDTO);
        Movimiento movimiento = movimientoMapper.toEntity(movimientoDTO);
        movimiento = movimientoRepository.save(movimiento);
        return movimientoMapper.toDto(movimiento);
    }

    /**
     * Partially update a movimiento.
     *
     * @param movimientoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MovimientoDTO> partialUpdate(MovimientoDTO movimientoDTO) {
        LOG.debug("Request to partially update Movimiento : {}", movimientoDTO);

        return movimientoRepository
            .findById(movimientoDTO.getId())
            .map(existingMovimiento -> {
                movimientoMapper.partialUpdate(existingMovimiento, movimientoDTO);

                return existingMovimiento;
            })
            .map(movimientoRepository::save)
            .map(movimientoMapper::toDto);
    }

    /**
     * Get all the movimientos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MovimientoDTO> findAll() {
        LOG.debug("Request to get all Movimientos");
        return movimientoRepository.findAll().stream().map(movimientoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the movimientos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MovimientoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return movimientoRepository.findAllWithEagerRelationships(pageable).map(movimientoMapper::toDto);
    }

    /**
     * Get one movimiento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MovimientoDTO> findOne(Long id) {
        LOG.debug("Request to get Movimiento : {}", id);
        return movimientoRepository.findOneWithEagerRelationships(id).map(movimientoMapper::toDto);
    }

    /**
     * Delete the movimiento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Movimiento : {}", id);
        movimientoRepository.deleteById(id);
    }

    /**
     * Registrar un movimiento a partir de un request DTO (lógica de negocio centralizada aquí)
     */
    @Transactional
    public finanzas.service.dto.MovimientoResponseDTO registrar(finanzas.service.dto.MovimientoRequestDTO request) {
        // Validaciones de negocio
        if (request.getMonto() <= 0) {
            throw new finanzas.web.rest.errors.BadRequestAlertException("Monto debe ser mayor que 0", "movimiento", "monto.invalid");
        }
        if (request.getFecha().isAfter(java.time.LocalDate.now())) {
            throw new finanzas.web.rest.errors.BadRequestAlertException("Fecha no puede ser futura", "movimiento", "fecha.invalid");
        }

        Movimiento mov = new Movimiento();
        mov.setTipo(request.getTipo());
        mov.setMonto(java.math.BigDecimal.valueOf(request.getMonto()));
        mov.setDescripcion(request.getDescripcion());
        mov.setFechaMovimiento(request.getFecha().atStartOfDay(java.time.ZoneId.systemDefault()));
        mov.setFechaRegistro(java.time.ZonedDateTime.now());
        if (request.getCategoria() != null && !request.getCategoria().isBlank()) {
            // try to find categoria for current user, otherwise create one
            String login = finanzas.security.SecurityUtils.getCurrentUserLogin().orElse(null);
            finanzas.domain.Categoria cat = null;
            if (login != null) {
                // prefer orElse / orElseGet to avoid Optional.get()
                finanzas.domain.User user = userRepository.findOneByLogin(login).orElse(null);
                if (user != null) {
                    cat = categoriaRepository.findByUsuarioAndNombreIgnoreCase(user, request.getCategoria()).orElse(null);
                    if (cat == null) {
                        cat = new finanzas.domain.Categoria();
                        cat.setNombre(request.getCategoria());
                        // set tipo de categoria acorde al movimiento
                        cat.setTipo(
                            request.getTipo() == finanzas.domain.enumeration.TipoMovimiento.INGRESO
                                ? finanzas.domain.enumeration.TipoCategoria.INGRESO
                                : finanzas.domain.enumeration.TipoCategoria.GASTO
                        );
                        cat.setUsuario(user);
                        cat = categoriaRepository.save(cat);
                    }
                }
            }
            mov.setCategoria(cat);
        }

        Movimiento saved = movimientoRepository.save(mov);

        // map to response
        finanzas.service.dto.MovimientoResponseDTO dto = new finanzas.service.dto.MovimientoResponseDTO();
        dto.setId(saved.getId());
        dto.setTipo(saved.getTipo());
        dto.setMonto(saved.getMonto().doubleValue());
        dto.setDescripcion(saved.getDescripcion());
        dto.setCategoria(saved.getCategoria() != null ? saved.getCategoria().getNombre() : null);
        dto.setFecha(saved.getFechaMovimiento().toLocalDate());
        return dto;
    }

    @Transactional(readOnly = true)
    public finanzas.service.dto.ResumenFinancieroDTO obtenerResumen() {
        java.math.BigDecimal ingresos = movimientoRepository
            .sumByTipo(finanzas.domain.enumeration.TipoMovimiento.INGRESO)
            .orElse(java.math.BigDecimal.ZERO);
        java.math.BigDecimal gastos = movimientoRepository
            .sumByTipo(finanzas.domain.enumeration.TipoMovimiento.GASTO)
            .orElse(java.math.BigDecimal.ZERO);

        finanzas.service.dto.ResumenFinancieroDTO dto = new finanzas.service.dto.ResumenFinancieroDTO();
        dto.setTotalIngresos(ingresos.doubleValue());
        dto.setTotalGastos(gastos.doubleValue());
        dto.setBalance(ingresos.subtract(gastos).doubleValue());
        return dto;
    }
}
