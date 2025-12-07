package finanzas.web.rest.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import finanzas.repository.WhatsappMessageRepository;
import finanzas.security.AuthoritiesConstants;
import finanzas.service.WhatsappMessageService;
import finanzas.web.rest.dto.WhatsappMessageDTO;

/**
 * REST controller para administración de mensajes WhatsApp.
 * Solo accesible por usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/whatsapp")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class WhatsappAdminResource {

    private final Logger log = LoggerFactory.getLogger(WhatsappAdminResource.class);

    private final WhatsappMessageRepository whatsappMessageRepository;
    private final WhatsappMessageService whatsappMessageService;

    public WhatsappAdminResource(
        WhatsappMessageRepository whatsappMessageRepository,
        WhatsappMessageService whatsappMessageService
    ) {
        this.whatsappMessageRepository = whatsappMessageRepository;
        this.whatsappMessageService = whatsappMessageService;
    }

    /**
     * GET /admin/whatsapp/messages : obtener historial de mensajes WhatsApp
     *
     * @return lista de mensajes WhatsApp
     */
    @GetMapping("/messages")
    public ResponseEntity<List<WhatsappMessageDTO>> getAllMessages() {
        log.debug("REST request to get all WhatsApp messages");
        List<WhatsappMessageDTO> messages = whatsappMessageRepository
            .findAll()
            .stream()
            .map(whatsappMessageService::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    /**
     * GET /admin/whatsapp/messages/user/:userId : obtener historial de un usuario
     *
     * @param userId ID del usuario
     * @return lista de mensajes del usuario
     */
    @GetMapping("/messages/user/{userId}")
    public ResponseEntity<List<WhatsappMessageDTO>> getUserMessages(@PathVariable Long userId) {
        log.debug("REST request to get WhatsApp messages for user: {}", userId);
        List<WhatsappMessageDTO> messages = whatsappMessageService.obtenerHistorialUsuario(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * POST /admin/whatsapp/test : enviar mensaje de prueba
     * Endpoint para probar el procesamiento de mensajes WhatsApp
     *
     * @param mensajeText texto del mensaje de prueba
     * @param numeroTelefonico número telefónico del remitente
     * @return resultado del procesamiento
     */
    @PostMapping("/test")
    public ResponseEntity<WhatsappMessageDTO> testWhatsappMessage(
        @RequestParam String mensajeText,
        @RequestParam String numeroTelefonico
    ) {
        log.debug("REST request to test WhatsApp message processing");
        try {
            WhatsappMessageDTO resultado = whatsappMessageService.procesarMensajeWhatsApp(mensajeText, numeroTelefonico);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error processing test message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
