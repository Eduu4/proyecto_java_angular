package finanzas.service;

import finanzas.domain.*;
import finanzas.domain.enumeration.EstadoProcesamiento;
import finanzas.domain.enumeration.TipoMovimiento;
import finanzas.repository.CategoriaRepository;
import finanzas.repository.CuentaRepository;
import finanzas.repository.WhatsappMessageRepository;
import finanzas.service.dto.MovimientoDTO;
import finanzas.service.dto.CategoriaDTO;
import finanzas.service.dto.CuentaDTO;
import finanzas.service.dto.UserDTO;
import finanzas.service.mapper.CategoriaMapper;
import finanzas.service.mapper.CuentaMapper;
import finanzas.service.mapper.UserMapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WhatsappMessageProcessorService {

    private final Logger log = LoggerFactory.getLogger(WhatsappMessageProcessorService.class);

    // Regex para parsear: "gasto|ingreso <monto> <categoria> [en <cuenta>] [descripcion...]"
    private static final Pattern MOVIMIENTO_PATTERN = Pattern.compile(
        "^(gasto|ingreso)\\s+(\\d+(\\.\\d{1,2})?)\\s+([\\w\\s]+?)(?:\\s+en\\s+([\\w\\s]+?))?(?:\\s+(.+))?$",
        Pattern.CASE_INSENSITIVE
    );

    private final WhatsappMessageRepository whatsappMessageRepository;
    private final MovimientoService movimientoService;
    private final CategoriaRepository categoriaRepository;
    private final CuentaRepository cuentaRepository;
    private final UserMapper userMapper;
    private final CategoriaMapper categoriaMapper;
    private final CuentaMapper cuentaMapper;

    public WhatsappMessageProcessorService(
        WhatsappMessageRepository whatsappMessageRepository,
        MovimientoService movimientoService,
        CategoriaRepository categoriaRepository,
        CuentaRepository cuentaRepository,
        UserMapper userMapper,
        CategoriaMapper categoriaMapper,
        CuentaMapper cuentaMapper
    ) {
        this.whatsappMessageRepository = whatsappMessageRepository;
        this.movimientoService = movimientoService;
        this.categoriaRepository = categoriaRepository;
        this.cuentaRepository = cuentaRepository;
        this.userMapper = userMapper;
        this.categoriaMapper = categoriaMapper;
        this.cuentaMapper = cuentaMapper;
    }

    @Scheduled(fixedRate = 60000)
    public void processPendingMessages() {
        log.debug("Checking for pending WhatsApp messages to process...");
        List<WhatsappMessage> pendingMessages = whatsappMessageRepository.findByEstado(EstadoProcesamiento.RECIBIDO);
        if (pendingMessages.isEmpty()) {
            return;
        }
        log.info("Found {} pending WhatsApp messages to process.", pendingMessages.size());
        for (WhatsappMessage message : pendingMessages) {
            processSingleMessage(message);
        }
    }

    public void processSingleMessage(WhatsappMessage message) {
        message.setEstado(EstadoProcesamiento.PROCESANDO);
        whatsappMessageRepository.save(message);
        log.info("Processing message ID: {} for user {}", message.getId(), message.getUsuario().getLogin());

        try {
            Matcher matcher = MOVIMIENTO_PATTERN.matcher(message.getMensajeOriginal().trim());

            if (!matcher.matches()) {
                throw new IllegalArgumentException("El formato del mensaje no es válido. Usa: 'gasto/ingreso <monto> <categoría> [en <cuenta>] [descripción]'");
            }

            TipoMovimiento tipo = matcher.group(1).equalsIgnoreCase("gasto") ? TipoMovimiento.GASTO : TipoMovimiento.INGRESO;
            BigDecimal monto = new BigDecimal(matcher.group(2));
            String categoriaNombre = matcher.group(4).trim();
            String cuentaNombre = matcher.group(5) != null ? matcher.group(5).trim() : null;
            String descripcion = matcher.group(6) != null ? matcher.group(6).trim() : "Registrado desde WhatsApp";

            User user = message.getUsuario();

            // Buscar Categoria
            Optional<Categoria> categoriaOpt = categoriaRepository.findByUsuarioAndNombreIgnoreCase(user, categoriaNombre);
            if (categoriaOpt.isEmpty()) {
                throw new IllegalArgumentException("La categoría '" + categoriaNombre + "' no fue encontrada.");
            }
            CategoriaDTO categoriaDTO = categoriaMapper.toDto(categoriaOpt.get());

            // Buscar Cuenta
            Optional<Cuenta> cuentaOpt;
            if (cuentaNombre != null) {
                cuentaOpt = cuentaRepository.findByUsuarioAndNombreIgnoreCase(user, cuentaNombre);
                if (cuentaOpt.isEmpty()) {
                    throw new IllegalArgumentException("La cuenta '" + cuentaNombre + "' no fue encontrada.");
                }
            } else {
                // Si no se especifica, tomar la primera cuenta del usuario como default
                List<Cuenta> cuentas = cuentaRepository.findByUsuario(user);
                if (cuentas.isEmpty()) {
                    throw new IllegalArgumentException("No tienes cuentas configuradas. Por favor, crea una primero.");
                }
                cuentaOpt = Optional.of(cuentas.get(0));
            }
            CuentaDTO cuentaDTO = cuentaMapper.toDto(cuentaOpt.get());

            // Crear y guardar el Movimiento
            MovimientoDTO movimientoDTO = new MovimientoDTO();
            movimientoDTO.setTipo(tipo);
            movimientoDTO.setMonto(monto);
            movimientoDTO.setFechaMovimiento(message.getFechaRecepcion());
            movimientoDTO.setDescripcion(descripcion);
            movimientoDTO.setCategoria(categoriaDTO);
            movimientoDTO.setCuenta(cuentaDTO);
            movimientoDTO.setUsuario(userMapper.toDtoId(user));

            MovimientoDTO resultado = movimientoService.save(movimientoDTO);

            message.setEstado(EstadoProcesamiento.COMPLETADO);
            message.setRespuestaBot("Movimiento de " + monto + " en categoría " + categoriaDTO.getNombre() + " registrado exitosamente.");
            log.info("Successfully processed message ID: {}", message.getId());

        } catch (Exception e) {
            log.error("Error processing message ID: {}", message.getId(), e);

            String errorMessage = e.getMessage();
            // Evitar mensajes de error demasiado técnicos
            if (errorMessage.length() > 255) {
                errorMessage = "Error interno al procesar el mensaje.";
            }

            message.setEstado(EstadoProcesamiento.ERROR);
            message.setErrorMensaje(errorMessage);
            message.setRespuestaBot("Hubo un error: " + e.getMessage());
        }

        whatsappMessageRepository.save(message);
    }
}