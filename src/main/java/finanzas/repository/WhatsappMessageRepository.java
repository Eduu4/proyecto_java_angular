package finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import finanzas.domain.WhatsappMessage;
import finanzas.domain.enumeration.EstadoProcesamiento;

/**
 * Spring Data JPA repository for the WhatsappMessage entity.
 */
@Repository
public interface WhatsappMessageRepository extends JpaRepository<WhatsappMessage, Long> {
    List<WhatsappMessage> findByEstado(EstadoProcesamiento estado);
    
    List<WhatsappMessage> findByUsuarioIdOrderByFechaRecepcionDesc(Long usuarioId);
}