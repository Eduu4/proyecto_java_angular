package finanzas.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finanzas.domain.Cuenta;
import finanzas.domain.Movimiento;
import finanzas.domain.User;
import finanzas.domain.WhatsappMessage;
import finanzas.domain.enumeration.EstadoProcesamiento;
import finanzas.domain.enumeration.TipoMovimiento;
import finanzas.repository.CuentaRepository;
import finanzas.repository.MovimientoRepository;
import finanzas.repository.UserRepository;
import finanzas.repository.WhatsappMessageRepository;
import finanzas.web.rest.dto.WhatsappMessageDTO;

/**
 * Servicio para procesar y guardar mensajes de WhatsApp.
 */
@Service
@Transactional
public class WhatsappMessageService {

    private static final Logger log = LoggerFactory.getLogger(WhatsappMessageService.class);

    private final WhatsappMessageRepository whatsappMessageRepository;
    private final UserRepository userRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoService movimientoService;

    public WhatsappMessageService(
        WhatsappMessageRepository whatsappMessageRepository,
        UserRepository userRepository,
        CuentaRepository cuentaRepository,
        MovimientoRepository movimientoRepository,
        MovimientoService movimientoService
    ) {
        this.whatsappMessageRepository = whatsappMessageRepository;
        this.userRepository = userRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.movimientoService = movimientoService;
    }

    /**
     * Procesa un mensaje recibido por WhatsApp.
     *
     * @param numeroTelefonico Número de teléfono del remitente
     * @param mensajeText Texto del mensaje
     * @return DTO con el resultado del procesamiento
     */
    public WhatsappMessageDTO procesarMensajeWhatsApp(String numeroTelefonico, String mensajeText) {
        log.info("Procesando mensaje de WhatsApp desde: {}", numeroTelefonico);

        // Buscar usuario por número de teléfono
        Optional<User> userOptional = userRepository.findByPhoneNumber(numeroTelefonico);
        if (userOptional.isEmpty()) {
            log.warn("No se encontró usuario para el número: {}", numeroTelefonico);
            return crearRespuestaError(
                numeroTelefonico,
                mensajeText,
                "⚠️ Número de teléfono no registrado. Por favor, configura tu número en la aplicación."
            );
        }

        User usuario = userOptional.orElseThrow();
        WhatsappMessage whatsappMessage = new WhatsappMessage();
        whatsappMessage.setMensajeOriginal(mensajeText);
        whatsappMessage.setNumeroTelefonico(numeroTelefonico);
        whatsappMessage.setUsuario(usuario);
        whatsappMessage.setFechaRecepcion(ZonedDateTime.now());
        whatsappMessage.setEstado(EstadoProcesamiento.RECIBIDO);

        try {
            // Parsear el mensaje
            ParseResultado resultado = parsearMensaje(mensajeText, usuario);

            if (!resultado.valido) {
                whatsappMessage.setEstado(EstadoProcesamiento.ERROR);
                whatsappMessage.setErrorMensaje(resultado.error);
                whatsappMessage.setRespuestaBot(resultado.error);
                whatsappMessageRepository.save(whatsappMessage);
                return convertToDTO(whatsappMessage);
            }

            // Asignar datos parseados
            whatsappMessage.setTipoMovimiento(resultado.tipoMovimiento);
            whatsappMessage.setMonto(resultado.monto);
            whatsappMessage.setCategoria(resultado.categoria);
            whatsappMessage.setCuenta(resultado.cuenta);
            whatsappMessage.setDescripcion(resultado.descripcion);

            // Crear o actualizar movimiento
            try {
                Movimiento movimiento = crearMovimiento(resultado, usuario);
                whatsappMessage.setMovimientoAsociado(movimiento);
                whatsappMessage.setEstado(EstadoProcesamiento.COMPLETADO);
                whatsappMessage.setFechaProcesamiento(ZonedDateTime.now());
                whatsappMessage.setRespuestaBot("✅ Movimiento registrado exitosamente: " + resultado.tipoMovimiento + " $" + resultado.monto);
            } catch (Exception e) {
                log.error("Error al crear movimiento", e);
                whatsappMessage.setEstado(EstadoProcesamiento.ERROR);
                whatsappMessage.setErrorMensaje(e.getMessage());
                whatsappMessage.setRespuestaBot("❌ Error al registrar el movimiento: " + e.getMessage());
            }

            whatsappMessageRepository.save(whatsappMessage);
            return convertToDTO(whatsappMessage);

        } catch (Exception e) {
            log.error("Error inesperado procesando mensaje de WhatsApp", e);
            whatsappMessage.setEstado(EstadoProcesamiento.ERROR);
            whatsappMessage.setErrorMensaje(e.getMessage());
            whatsappMessage.setRespuestaBot("❌ Error inesperado: " + e.getMessage());
            whatsappMessageRepository.save(whatsappMessage);
            return convertToDTO(whatsappMessage);
        }
    }

    /**
     * Parsea un mensaje de WhatsApp extrayendo los componentes.
     * Formatos soportados:
     * - GASTO [monto] [categoria] [cuenta] [descripción opcional]
     * - INGRESO [monto] [categoria] [cuenta] [descripción opcional]
     */
    private ParseResultado parsearMensaje(String mensaje, User usuario) {
        ParseResultado resultado = new ParseResultado();

        // Limpiar el mensaje
        String mensajeLimpio = mensaje.trim().toUpperCase();

        // Separar por espacios respetando comillas
        String[] tokens = dividirTokens(mensajeLimpio);

        if (tokens.length < 4) {
            resultado.valido = false;
            resultado.error = "❌ Formato incorrecto. Usa: GASTO [monto] [categoria] [cuenta]";
            return resultado;
        }

        try {
            // Determinar tipo
            String tipoStr = tokens[0];
            if (!tipoStr.equals("GASTO") && !tipoStr.equals("INGRESO")) {
                resultado.valido = false;
                resultado.error = "❌ Tipo inválido. Usa GASTO o INGRESO";
                return resultado;
            }
            resultado.tipoMovimiento = TipoMovimiento.valueOf(tipoStr);

            // Monto
            resultado.monto = new BigDecimal(tokens[1]);
            if (resultado.monto.compareTo(BigDecimal.ZERO) <= 0) {
                resultado.valido = false;
                resultado.error = "❌ El monto debe ser mayor a 0";
                return resultado;
            }

            // Categoría
            resultado.categoria = tokens[2].replaceAll("\"", "");

            // Cuenta
            resultado.cuenta = tokens[3].replaceAll("\"", "");

            // Descripción (opcional)
            if (tokens.length > 4) {
                StringBuilder desc = new StringBuilder();
                for (int i = 4; i < tokens.length; i++) {
                    desc.append(tokens[i]).append(" ");
                }
                resultado.descripcion = desc.toString().trim().replaceAll("\"", "");
            }

            resultado.valido = true;
            return resultado;

        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.error = "❌ El monto debe ser un número válido";
            return resultado;
        } catch (IllegalArgumentException e) {
            resultado.valido = false;
            resultado.error = "❌ Tipo de movimiento inválido";
            return resultado;
        }
    }

    /**
     * Divide un string en tokens respetando comillas.
     */
    private String[] dividirTokens(String texto) {
        java.util.List<String> tokens = new java.util.ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean enComillas = false;

        for (char c : texto.toCharArray()) {
            if (c == '"') {
                enComillas = !enComillas;
                token.append(c);
            } else if (c == ' ' && !enComillas) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
            } else {
                token.append(c);
            }
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens.toArray(new String[0]);
    }

    /**
     * Crea un movimiento a partir de los datos parseados.
     */
    private Movimiento crearMovimiento(ParseResultado resultado, User usuario) {
        // Buscar cuenta del usuario
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByNombreAndUsuarioLogin(resultado.cuenta, usuario.getLogin());

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(resultado.tipoMovimiento);
        movimiento.setMonto(resultado.monto);
        movimiento.setDescripcion(resultado.descripcion != null ? resultado.descripcion : "Registrado por WhatsApp");
        movimiento.setFechaMovimiento(ZonedDateTime.now());
        movimiento.setFechaRegistro(ZonedDateTime.now());
        movimiento.setUsuario(usuario);

        cuentaOpt.ifPresent(movimiento::setCuenta);

        // Nota: La categoría se asignaría si se busca en la BD
        // Por ahora se guarda como descripción adicional

        movimientoRepository.save(movimiento);
        return movimiento;
    }

    /**
     * Obtiene el historial de mensajes de un usuario.
     */
    public java.util.List<WhatsappMessageDTO> obtenerHistorialUsuario(Long usuarioId) {
        return whatsappMessageRepository
            .findByUsuarioIdOrderByFechaRecepcionDesc(usuarioId)
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    /**
     * Convierte una entidad a DTO.
     */
    public WhatsappMessageDTO convertToDTO(WhatsappMessage entity) {
        WhatsappMessageDTO dto = new WhatsappMessageDTO();
        dto.setId(entity.getId());
        dto.setMensajeOriginal(entity.getMensajeOriginal());
        dto.setTipoMovimiento(entity.getTipoMovimiento());
        dto.setMonto(entity.getMonto());
        dto.setCategoria(entity.getCategoria());
        dto.setCuenta(entity.getCuenta());
        dto.setEstado(entity.getEstado());
        dto.setRespuestaBot(entity.getRespuestaBot());
        dto.setFechaRecepcion(entity.getFechaRecepcion());
        return dto;
    }

    /**
     * Crea una respuesta de error.
     */
    private WhatsappMessageDTO crearRespuestaError(String numeroTelefonico, String mensaje, String error) {
        WhatsappMessageDTO dto = new WhatsappMessageDTO();
        dto.setMensajeOriginal(mensaje);
        dto.setEstado(EstadoProcesamiento.ERROR);
        dto.setRespuestaBot(error);
        dto.setFechaRecepcion(ZonedDateTime.now());
        return dto;
    }

    /**
     * Clase interna para resultado de parseado.
     */
    private static class ParseResultado {

        boolean valido = false;
        String error = "";
        TipoMovimiento tipoMovimiento;
        BigDecimal monto;
        String categoria;
        String cuenta;
        String descripcion;
    }
}
