package finanzas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import finanzas.domain.Presupuesto;

/**
 * Spring Data JPA repository for the Presupuesto entity.
 */
@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    @Query("select presupuesto from Presupuesto presupuesto where presupuesto.usuario.login = ?#{authentication.name}")
    List<Presupuesto> findByUsuarioIsCurrentUser();

    default Optional<Presupuesto> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Presupuesto> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Presupuesto> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select presupuesto from Presupuesto presupuesto left join fetch presupuesto.usuario",
        countQuery = "select count(presupuesto) from Presupuesto presupuesto"
    )
    Page<Presupuesto> findAllWithToOneRelationships(Pageable pageable);

    @Query("select presupuesto from Presupuesto presupuesto left join fetch presupuesto.usuario")
    List<Presupuesto> findAllWithToOneRelationships();

    @Query("select presupuesto from Presupuesto presupuesto left join fetch presupuesto.usuario where presupuesto.id =:id")
    Optional<Presupuesto> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select p from Presupuesto p where p.categoria.id = :categoriaId and p.usuario.login = ?#{authentication.name} and p.fechaInicio <= :fechaFin and p.fechaFin >= :fechaInicio and (:id is null or p.id <> :id)"
    )
    List<Presupuesto> findOverlappingForCurrentUser(
        @Param("categoriaId") Long categoriaId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        @Param("id") Long id
    );
}
