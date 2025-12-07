package finanzas.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import finanzas.service.WhatsappMessageService;
import finanzas.web.rest.dto.WhatsappMessageDTO;

/**
 * REST controller para recibir webhooks de WhatsApp.
 * Este endpoint es consumido por proveedores de WhatsApp Business API como Twilio o Meta.
 */
@RestController
@RequestMapping("/api/webhook/whatsapp")
public class WhatsappController {

    private static final Logger log = LoggerFactory.getLogger(WhatsappController.class);

    private final WhatsappMessageService whatsappMessageService;

    public WhatsappController(WhatsappMessageService whatsappMessageService) {
        this.whatsappMessageService = whatsappMessageService;
    }

    /**
     * Endpoint POST para recibir mensajes de WhatsApp desde el proveedor.
     * Formato esperado:
     * {
     *   "from": "+34912345678",
     *   "text": "GASTO 25.50 Alimentación \"Cuenta Principal\"",
     *   "timestamp": "1638885593",
     *   "message_id": "wamid.xxx"
     * }
     */
    @PostMapping
    public ResponseEntity<WhatsappMessageDTO> recibirMensajeWhatsApp(@RequestBody WhatsappWebhookRequest request) {
        log.info("Webhook recibido de WhatsApp desde: {}", request.getFrom());

        if (request.getFrom() == null || request.getText() == null) {
            log.warn("Webhook con parámetros inválidos");
            return ResponseEntity.badRequest().build();
        }

        // Procesar el mensaje
        WhatsappMessageDTO resultado = whatsappMessageService.procesarMensajeWhatsApp(request.getFrom(), request.getText());

        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint GET para verificación de webhook (Challenge token).
     * Los proveedores de WhatsApp requieren verificación inicial.
     */
    @GetMapping
    public ResponseEntity<String> verificarWebhook(
        @RequestParam(value = "hub.mode", required = false) String mode,
        @RequestParam(value = "hub.challenge", required = false) String challenge,
        @RequestParam(value = "hub.verify_token", required = false) String verifyToken
    ) {
        log.info("Verificación de webhook recibida");

        // En producción, verificar el token contra una clave configurada
        String expectedToken = System.getenv().getOrDefault("WHATSAPP_VERIFY_TOKEN", "finanzas_webhook_token");

        if ("subscribe".equals(mode) && expectedToken.equals(verifyToken)) {
            log.info("Webhook verificado exitosamente");
            return ResponseEntity.ok(challenge);
        }

        log.warn("Intento de verificación de webhook fallido");
        return ResponseEntity.status(403).build();
    }

    /**
     * Clase interna para deserializar el payload del webhook.
     */
    public static class WhatsappWebhookRequest {

        private String from;

        private String text;

        private String timestamp;

        private String message_id;

        public WhatsappWebhookRequest() {}

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }
    }
}

