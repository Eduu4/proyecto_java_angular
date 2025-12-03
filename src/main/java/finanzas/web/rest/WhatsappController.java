package finanzas.web.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finanzas.service.WhatsappBotService;
import finanzas.service.dto.MovimientoDTO;

@RestController
@RequestMapping("/api/webhook")
public class WhatsappController {

    private final Logger log = LoggerFactory.getLogger(WhatsappController.class);

    private final WhatsappBotService whatsappBotService;

    public WhatsappController(WhatsappBotService whatsappBotService) {
        this.whatsappBotService = whatsappBotService;
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> receive(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> body) {
        // This endpoint is intentionally generic: adapt to the provider (Twilio/Meta) payload format.
        log.debug("Received whatsapp webhook headers={} body={}", headers, body);

        String from = (String) body.getOrDefault("from", "unknown");
        String text = (String) body.getOrDefault("text", body.getOrDefault("message", ""));

        MovimientoDTO created = whatsappBotService.handleIncomingMessage(from, text != null ? text : "");

        if (created == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Could not parse message"));
        }
        return ResponseEntity.ok(Map.of("status", "ok", "id", created.getId()));
    }
}
