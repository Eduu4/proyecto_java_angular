package finanzas.service;

import finanzas.domain.Cuenta;
import finanzas.repository.CuentaRepository;
import finanzas.service.dto.CuentaDTO;
import finanzas.service.mapper.CuentaMapper;
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
 * Service Implementation for managing {@link finanzas.domain.Cuenta}.
 */
@Service
@Transactional
public class CuentaService {

    private static final Logger LOG = LoggerFactory.getLogger(CuentaService.class);

    private final CuentaRepository cuentaRepository;

    private final CuentaMapper cuentaMapper;

    public CuentaService(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
    }

    /**
     * Save a cuenta.
     *
     * @param cuentaDTO the entity to save.
     * @return the persisted entity.
     */
    public CuentaDTO save(CuentaDTO cuentaDTO) {
        LOG.debug("Request to save Cuenta : {}", cuentaDTO);
        Cuenta cuenta = cuentaMapper.toEntity(cuentaDTO);
        cuenta = cuentaRepository.save(cuenta);
        return cuentaMapper.toDto(cuenta);
    }

    /**
     * Update a cuenta.
     *
     * @param cuentaDTO the entity to save.
     * @return the persisted entity.
     */
    public CuentaDTO update(CuentaDTO cuentaDTO) {
        LOG.debug("Request to update Cuenta : {}", cuentaDTO);
        Cuenta cuenta = cuentaMapper.toEntity(cuentaDTO);
        cuenta = cuentaRepository.save(cuenta);
        return cuentaMapper.toDto(cuenta);
    }

    /**
     * Partially update a cuenta.
     *
     * @param cuentaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CuentaDTO> partialUpdate(CuentaDTO cuentaDTO) {
        LOG.debug("Request to partially update Cuenta : {}", cuentaDTO);

        return cuentaRepository
            .findById(cuentaDTO.getId())
            .map(existingCuenta -> {
                cuentaMapper.partialUpdate(existingCuenta, cuentaDTO);

                return existingCuenta;
            })
            .map(cuentaRepository::save)
            .map(cuentaMapper::toDto);
    }

    /**
     * Get all the cuentas.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CuentaDTO> findAll() {
        LOG.debug("Request to get all Cuentas");
        return cuentaRepository.findAll().stream().map(cuentaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the cuentas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CuentaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cuentaRepository.findAllWithEagerRelationships(pageable).map(cuentaMapper::toDto);
    }

    /**
     * Get one cuenta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CuentaDTO> findOne(Long id) {
        LOG.debug("Request to get Cuenta : {}", id);
        return cuentaRepository.findOneWithEagerRelationships(id).map(cuentaMapper::toDto);
    }

    /**
     * Delete the cuenta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Cuenta : {}", id);
        cuentaRepository.deleteById(id);
    }
}
