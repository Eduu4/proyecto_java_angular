package finanzas.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import finanzas.service.dto.CategoriaDTO;
import finanzas.service.dto.CuentaDTO;
import finanzas.service.dto.MovimientoDTO;

/**
 * Servicio simple que parsea mensajes entrantes y crea movimientos usando el servicio existente.
 * Este servicio es solo un scaffold: para producción conecta con la API de WhatsApp/Twilio y añade seguridad.
 */
@Service
public class WhatsappBotService {

    private final Logger log = LoggerFactory.getLogger(WhatsappBotService.class);

    private final MovimientoService movimientoService;

    public WhatsappBotService(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /**
     * Ejemplo de parser: texto esperado: "GASTO 12.50 comida cuenta:1 categoria:2 desc:almuerzo"
     * Crea un MovimientoDTO mínimo y lo guarda.
     */
    public MovimientoDTO handleIncomingMessage(String from, String messageText) {
        log.debug("Incoming whatsapp message from {}: {}", from, messageText);

        try {
            String[] parts = messageText.split("\\s+");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Formato inválido");
            }
            String tipoStr = parts[0].toUpperCase();
            String montoStr = parts[1].replaceAll("[^0-9.,-]", "").replace(',', '.');
            BigDecimal monto = new BigDecimal(montoStr);

            MovimientoDTO dto = new MovimientoDTO();
            dto.setMonto(monto);
            dto.setFechaMovimiento(ZonedDateTime.now());
            dto.setFechaRegistro(ZonedDateTime.now());
            dto.setDescripcion(messageText);

            if (tipoStr.startsWith("G")) dto.setTipo(finanzas.domain.enumeration.TipoMovimiento.GASTO);
            else dto.setTipo(finanzas.domain.enumeration.TipoMovimiento.INGRESO);

            // Parse simple tokens and set nested DTOs
            for (String token : parts) {
                if (token.startsWith("cuenta:")) {
                    try {
                        Long cuentaId = Long.valueOf(token.substring(7));
                        CuentaDTO cuentaDto = new CuentaDTO();
                        cuentaDto.setId(cuentaId);
                        dto.setCuenta(cuentaDto);
                    } catch (Exception e) {
                        // ignore parse errors
                    }
                }
                if (token.startsWith("categoria:")) {
                    try {
                        Long categoriaId = Long.valueOf(token.substring(10));
                        CategoriaDTO categoriaDto = new CategoriaDTO();
                        categoriaDto.setId(categoriaId);
                        dto.setCategoria(categoriaDto);
                    } catch (Exception e) {
                        // ignore parse errors
                    }
                }
            }

            return movimientoService.save(dto);
        } catch (Exception e) {
            log.warn("No se pudo parsear mensaje de WhatsApp: {}", e.getMessage());
            return null;
        }
    }
}
