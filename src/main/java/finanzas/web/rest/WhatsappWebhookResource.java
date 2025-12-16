package finanzas.web.rest;

import finanzas.domain.User;
import finanzas.domain.WhatsappMessage;
import finanzas.domain.enumeration.EstadoProcesamiento;
import finanzas.repository.UserRepository;
import finanzas.repository.WhatsappMessageRepository;
import finanzas.web.rest.dto.WhatsappInboundMessageDTO;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WhatsappWebhookResource {

    private final Logger log = LoggerFactory.getLogger(WhatsappWebhookResource.class);

    private final WhatsappMessageRepository whatsappMessageRepository;
    private final UserRepository userRepository;

    public WhatsappWebhookResource(WhatsappMessageRepository whatsappMessageRepository, UserRepository userRepository) {
        this.whatsappMessageRepository = whatsappMessageRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/whatsapp-webhook")
    public ResponseEntity<Void> receiveWhatsappMessage(@RequestBody WhatsappInboundMessageDTO inboundMessage) {
        log.info("Received WhatsApp message: {}", inboundMessage);

        // El número de teléfono puede venir en formatos como "whatsapp:+14155238886"
        // Necesitamos limpiarlo para que solo queden los dígitos.
        String cleanPhoneNumber = inboundMessage.getFrom().replaceAll("[^\\d]", "");

        // Asumimos que el número en la BD también está limpio de prefijos y símbolos.
        Optional<User> userOptional = userRepository.findByPhoneNumber(cleanPhoneNumber);

        if (userOptional.isEmpty()) {
            log.warn("Received message from unknown phone number: {}", cleanPhoneNumber);
            // Devolvemos 200 OK para que el proveedor no siga reintentando.
            // Podríamos también guardar el mensaje en un estado de "NO_ASOCIADO".
            return ResponseEntity.ok().build();
        }

        User user = userOptional.orElseThrow();
        WhatsappMessage whatsappMessage = new WhatsappMessage();
        whatsappMessage.setMensajeOriginal(inboundMessage.getBody());
        whatsappMessage.setNumeroTelefonico(cleanPhoneNumber);
        whatsappMessage.setFechaRecepcion(ZonedDateTime.now());
        whatsappMessage.setUsuario(user);
        whatsappMessage.setEstado(EstadoProcesamiento.RECIBIDO);

        whatsappMessageRepository.save(whatsappMessage);

        log.info("Saved incoming WhatsApp message for user {}", user.getLogin());

        // Aquí se podría encolar un evento para procesar el mensaje de forma asíncrona.
        // Por ahora, lo dejamos guardado en estado RECIBIDO.

        return ResponseEntity.ok().build();
    }
}
