package finanzas.service;

import finanzas.domain.Categoria;
import finanzas.repository.CategoriaRepository;
import finanzas.service.dto.CategoriaDTO;
import finanzas.service.mapper.CategoriaMapper;
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
 * Service Implementation for managing {@link finanzas.domain.Categoria}.
 */
@Service
@Transactional
public class CategoriaService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;

    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    /**
     * Save a categoria.
     *
     * @param categoriaDTO the entity to save.
     * @return the persisted entity.
     */
    public CategoriaDTO save(CategoriaDTO categoriaDTO) {
        LOG.debug("Request to save Categoria : {}", categoriaDTO);
        Categoria categoria = categoriaMapper.toEntity(categoriaDTO);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(categoria);
    }

    /**
     * Update a categoria.
     *
     * @param categoriaDTO the entity to save.
     * @return the persisted entity.
     */
    public CategoriaDTO update(CategoriaDTO categoriaDTO) {
        LOG.debug("Request to update Categoria : {}", categoriaDTO);
        Categoria categoria = categoriaMapper.toEntity(categoriaDTO);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(categoria);
    }

    /**
     * Partially update a categoria.
     *
     * @param categoriaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CategoriaDTO> partialUpdate(CategoriaDTO categoriaDTO) {
        LOG.debug("Request to partially update Categoria : {}", categoriaDTO);

        return categoriaRepository
            .findById(categoriaDTO.getId())
            .map(existingCategoria -> {
                categoriaMapper.partialUpdate(existingCategoria, categoriaDTO);

                return existingCategoria;
            })
            .map(categoriaRepository::save)
            .map(categoriaMapper::toDto);
    }

    /**
     * Get all the categorias.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        LOG.debug("Request to get all Categorias");
        return categoriaRepository.findAll().stream().map(categoriaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the categorias with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CategoriaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return categoriaRepository.findAllWithEagerRelationships(pageable).map(categoriaMapper::toDto);
    }

    /**
     * Get one categoria by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CategoriaDTO> findOne(Long id) {
        LOG.debug("Request to get Categoria : {}", id);
        return categoriaRepository.findOneWithEagerRelationships(id).map(categoriaMapper::toDto);
    }

    /**
     * Delete the categoria by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Categoria : {}", id);
        categoriaRepository.deleteById(id);
    }
}
