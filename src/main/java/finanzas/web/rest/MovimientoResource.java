package finanzas.web.rest;

import finanzas.repository.MovimientoRepository;
import finanzas.service.MovimientoService;
import finanzas.service.dto.MovimientoDTO;
import finanzas.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link finanzas.domain.Movimiento}.
 */
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoResource {

    private static final Logger LOG = LoggerFactory.getLogger(MovimientoResource.class);

    private static final String ENTITY_NAME = "movimiento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MovimientoService movimientoService;

    private final MovimientoRepository movimientoRepository;

    public MovimientoResource(MovimientoService movimientoService, MovimientoRepository movimientoRepository) {
        this.movimientoService = movimientoService;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * {@code POST  /movimientos} : Create a new movimiento.
     *
     * @param movimientoDTO the movimientoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new movimientoDTO, or with status {@code 400 (Bad Request)} if the movimiento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MovimientoDTO> createMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Movimiento : {}", movimientoDTO);
        if (movimientoDTO.getId() != null) {
            throw new BadRequestAlertException("A new movimiento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        movimientoDTO = movimientoService.save(movimientoDTO);
        return ResponseEntity.created(new URI("/api/movimientos/" + movimientoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, movimientoDTO.getId().toString()))
            .body(movimientoDTO);
    }

    /**
     * {@code PUT  /movimientos/:id} : Updates an existing movimiento.
     *
     * @param id the id of the movimientoDTO to save.
     * @param movimientoDTO the movimientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated movimientoDTO,
     * or with status {@code 400 (Bad Request)} if the movimientoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the movimientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> updateMovimiento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MovimientoDTO movimientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Movimiento : {}, {}", id, movimientoDTO);
        if (movimientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, movimientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!movimientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        movimientoDTO = movimientoService.update(movimientoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, movimientoDTO.getId().toString()))
            .body(movimientoDTO);
    }

    /**
     * {@code PATCH  /movimientos/:id} : Partial updates given fields of an existing movimiento, field will ignore if it is null
     *
     * @param id the id of the movimientoDTO to save.
     * @param movimientoDTO the movimientoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated movimientoDTO,
     * or with status {@code 400 (Bad Request)} if the movimientoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the movimientoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the movimientoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MovimientoDTO> partialUpdateMovimiento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MovimientoDTO movimientoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Movimiento partially : {}, {}", id, movimientoDTO);
        if (movimientoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, movimientoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!movimientoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MovimientoDTO> result = movimientoService.partialUpdate(movimientoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, movimientoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /movimientos} : get all the movimientos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of movimientos in body.
     */
    @GetMapping("")
    public List<MovimientoDTO> getAllMovimientos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Movimientos");
        return movimientoService.findAll();
    }

    /**
     * {@code GET  /movimientos/:id} : get the "id" movimiento.
     *
     * @param id the id of the movimientoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the movimientoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> getMovimiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Movimiento : {}", id);
        Optional<MovimientoDTO> movimientoDTO = movimientoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(movimientoDTO);
    }

    /**
     * {@code DELETE  /movimientos/:id} : delete the "id" movimiento.
     *
     * @param id the id of the movimientoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimiento(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Movimiento : {}", id);
        movimientoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
