package finanzas.repository;

import finanzas.domain.Movimiento;
import finanzas.domain.enumeration.TipoMovimiento;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Movimiento entity.
 */
@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    @Query("select movimiento from Movimiento movimiento where movimiento.usuario.login = ?#{authentication.name}")
    List<Movimiento> findByUsuarioIsCurrentUser();

    default Optional<Movimiento> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Movimiento> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Movimiento> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select movimiento from Movimiento movimiento left join fetch movimiento.usuario",
        countQuery = "select count(movimiento) from Movimiento movimiento"
    )
    Page<Movimiento> findAllWithToOneRelationships(Pageable pageable);

    @Query("select movimiento from Movimiento movimiento left join fetch movimiento.usuario")
    List<Movimiento> findAllWithToOneRelationships();

    @Query("select movimiento from Movimiento movimiento left join fetch movimiento.usuario where movimiento.id =:id")
    Optional<Movimiento> findOneWithToOneRelationships(@Param("id") Long id);

    List<Movimiento> findByCuentaId(Long cuentaId);

    List<Movimiento> findByCategoriaId(Long categoriaId);

    List<Movimiento> findByCuentaIdAndFechaMovimientoBetween(Long cuentaId, ZonedDateTime start, ZonedDateTime end);

    List<Movimiento> findByCategoriaIdAndFechaMovimientoBetween(Long categoriaId, ZonedDateTime start, ZonedDateTime end);

    @Query("select sum(m.monto) from Movimiento m where m.tipo = :tipo")
    Optional<BigDecimal> sumByTipo(@Param("tipo") TipoMovimiento tipo);
}
