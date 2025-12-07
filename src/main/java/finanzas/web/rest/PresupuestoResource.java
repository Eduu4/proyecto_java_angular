package finanzas.web.rest;

import finanzas.repository.PresupuestoRepository;
import finanzas.service.PresupuestoService;
import finanzas.service.dto.PresupuestoDTO;
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
 * REST controller for managing {@link finanzas.domain.Presupuesto}.
 */
@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PresupuestoResource.class);

    private static final String ENTITY_NAME = "presupuesto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PresupuestoService presupuestoService;

    private final PresupuestoRepository presupuestoRepository;

    public PresupuestoResource(PresupuestoService presupuestoService, PresupuestoRepository presupuestoRepository) {
        this.presupuestoService = presupuestoService;
        this.presupuestoRepository = presupuestoRepository;
    }

    /**
     * {@code POST  /presupuestos} : Create a new presupuesto.
     *
     * @param presupuestoDTO the presupuestoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new presupuestoDTO, or with status {@code 400 (Bad Request)} if the presupuesto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PresupuestoDTO> createPresupuesto(@Valid @RequestBody PresupuestoDTO presupuestoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Presupuesto : {}", presupuestoDTO);
        if (presupuestoDTO.getId() != null) {
            throw new BadRequestAlertException("A new presupuesto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        presupuestoDTO = presupuestoService.save(presupuestoDTO);
        return ResponseEntity.created(new URI("/api/presupuestos/" + presupuestoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, presupuestoDTO.getId().toString()))
            .body(presupuestoDTO);
    }

    /**
     * {@code PUT  /presupuestos/:id} : Updates an existing presupuesto.
     *
     * @param id the id of the presupuestoDTO to save.
     * @param presupuestoDTO the presupuestoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated presupuestoDTO,
     * or with status {@code 400 (Bad Request)} if the presupuestoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the presupuestoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PresupuestoDTO> updatePresupuesto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PresupuestoDTO presupuestoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Presupuesto : {}, {}", id, presupuestoDTO);
        if (presupuestoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, presupuestoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!presupuestoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        presupuestoDTO = presupuestoService.update(presupuestoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, presupuestoDTO.getId().toString()))
            .body(presupuestoDTO);
    }

    /**
     * {@code PATCH  /presupuestos/:id} : Partial updates given fields of an existing presupuesto, field will ignore if it is null
     *
     * @param id the id of the presupuestoDTO to save.
     * @param presupuestoDTO the presupuestoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated presupuestoDTO,
     * or with status {@code 400 (Bad Request)} if the presupuestoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the presupuestoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the presupuestoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PresupuestoDTO> partialUpdatePresupuesto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PresupuestoDTO presupuestoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Presupuesto partially : {}, {}", id, presupuestoDTO);
        if (presupuestoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, presupuestoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!presupuestoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PresupuestoDTO> result = presupuestoService.partialUpdate(presupuestoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, presupuestoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /presupuestos} : get all the presupuestos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of presupuestos in body.
     */
    @GetMapping("")
    public List<PresupuestoDTO> getAllPresupuestos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Presupuestos");
        return presupuestoService.findAll();
    }

    /**
     * {@code GET  /presupuestos/:id} : get the "id" presupuesto.
     *
     * @param id the id of the presupuestoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the presupuestoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDTO> getPresupuesto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Presupuesto : {}", id);
        Optional<PresupuestoDTO> presupuestoDTO = presupuestoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(presupuestoDTO);
    }

    /**
     * {@code DELETE  /presupuestos/:id} : delete the "id" presupuesto.
     *
     * @param id the id of the presupuestoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePresupuesto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Presupuesto : {}", id);
        presupuestoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
