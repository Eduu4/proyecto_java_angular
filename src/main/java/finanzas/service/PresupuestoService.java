package finanzas.service;

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

import finanzas.domain.Presupuesto;
import finanzas.repository.PresupuestoRepository;
import finanzas.service.dto.PresupuestoDTO;
import finanzas.service.mapper.PresupuestoMapper;
import finanzas.web.rest.errors.BadRequestAlertException;

/**
 * Service Implementation for managing {@link finanzas.domain.Presupuesto}.
 */
@Service
@Transactional
public class PresupuestoService {

    private static final Logger LOG = LoggerFactory.getLogger(PresupuestoService.class);

    private final PresupuestoRepository presupuestoRepository;

    private final PresupuestoMapper presupuestoMapper;

    public PresupuestoService(PresupuestoRepository presupuestoRepository, PresupuestoMapper presupuestoMapper) {
        this.presupuestoRepository = presupuestoRepository;
        this.presupuestoMapper = presupuestoMapper;
    }

    /**
     * Save a presupuesto.
     *
     * @param presupuestoDTO the entity to save.
     * @return the persisted entity.
     */
    public PresupuestoDTO save(PresupuestoDTO presupuestoDTO) {
        LOG.debug("Request to save Presupuesto : {}", presupuestoDTO);
        Presupuesto presupuesto = presupuestoMapper.toEntity(presupuestoDTO);
        validatePresupuesto(presupuesto);
        presupuesto = presupuestoRepository.save(presupuesto);
        return presupuestoMapper.toDto(presupuesto);
    }

    /**
     * Update a presupuesto.
     *
     * @param presupuestoDTO the entity to save.
     * @return the persisted entity.
     */
    public PresupuestoDTO update(PresupuestoDTO presupuestoDTO) {
        LOG.debug("Request to update Presupuesto : {}", presupuestoDTO);
        Presupuesto presupuesto = presupuestoMapper.toEntity(presupuestoDTO);
        validatePresupuesto(presupuesto);
        presupuesto = presupuestoRepository.save(presupuesto);
        return presupuestoMapper.toDto(presupuesto);
    }

    /**
     * Partially update a presupuesto.
     *
     * @param presupuestoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PresupuestoDTO> partialUpdate(PresupuestoDTO presupuestoDTO) {
        LOG.debug("Request to partially update Presupuesto : {}", presupuestoDTO);
        // For partial update we still want to ensure no overlap if dates/category changed.
        return presupuestoRepository
            .findById(presupuestoDTO.getId())
            .map(existingPresupuesto -> {
                presupuestoMapper.partialUpdate(existingPresupuesto, presupuestoDTO);
                validatePresupuesto(existingPresupuesto);

                return existingPresupuesto;
            })
            .map(presupuestoRepository::save)
            .map(presupuestoMapper::toDto);
    }

    private void validatePresupuesto(Presupuesto presupuesto) {
        if (presupuesto.getFechaInicio() == null || presupuesto.getFechaFin() == null) {
            throw new BadRequestAlertException("Fechas de presupuesto incompletas", "presupuesto", "fecha.incomplete");
        }
        if (presupuesto.getFechaInicio().isAfter(presupuesto.getFechaFin())) {
            throw new BadRequestAlertException("Fecha inicio posterior a fecha fin", "presupuesto", "fecha.invalid");
        }

        Long categoriaId = presupuesto.getCategoria() != null ? presupuesto.getCategoria().getId() : null;
        Long id = presupuesto.getId();
        List<Presupuesto> overlapping = presupuestoRepository.findOverlappingForCurrentUser(categoriaId, presupuesto.getFechaInicio(), presupuesto.getFechaFin(), id);
        if (overlapping != null && !overlapping.isEmpty()) {
            throw new BadRequestAlertException("Presupuesto se solapa con otro existente", "presupuesto", "presupuesto.overlap");
        }
    }

    /**
     * Get all the presupuestos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> findAll() {
        LOG.debug("Request to get all Presupuestos");
        return presupuestoRepository.findAll().stream().map(presupuestoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the presupuestos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PresupuestoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return presupuestoRepository.findAllWithEagerRelationships(pageable).map(presupuestoMapper::toDto);
    }

    /**
     * Get one presupuesto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PresupuestoDTO> findOne(Long id) {
        LOG.debug("Request to get Presupuesto : {}", id);
        return presupuestoRepository.findOneWithEagerRelationships(id).map(presupuestoMapper::toDto);
    }

    /**
     * Delete the presupuesto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Presupuesto : {}", id);
        presupuestoRepository.deleteById(id);
    }
}
