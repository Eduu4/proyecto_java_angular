package finanzas.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finanzas.domain.Cuenta;
import finanzas.domain.Movimiento;
import finanzas.repository.CuentaRepository;
import finanzas.repository.MovimientoRepository;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public ReportService(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    public BigDecimal getBalanceForCuenta(Long cuentaId, ZonedDateTime from, ZonedDateTime to) {
        log.debug("Calculate balance for cuenta {} between {} and {}", cuentaId, from, to);
        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElse(null);
        BigDecimal saldoInicial = cuenta != null && cuenta.getSaldoInicial() != null ? cuenta.getSaldoInicial() : BigDecimal.ZERO;

        List<Movimiento> movimientos;
        if (from != null && to != null) {
            movimientos = movimientoRepository.findByCuentaIdAndFechaMovimientoBetween(cuentaId, from, to);
        } else {
            movimientos = movimientoRepository.findByCuentaId(cuentaId);
        }

        BigDecimal net = movimientos.stream()
            .map(m -> m.getTipo() == null ? BigDecimal.ZERO : (m.getTipo().name().equals("INGRESO") ? m.getMonto() : m.getMonto().negate()))
            .filter(x -> x != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return saldoInicial.add(net);
    }

    public BigDecimal getBalanceForCategoria(Long categoriaId, ZonedDateTime from, ZonedDateTime to) {
        log.debug("Calculate balance for categoria {} between {} and {}", categoriaId, from, to);
        List<Movimiento> movimientos;
        if (from != null && to != null) {
            movimientos = movimientoRepository.findByCategoriaIdAndFechaMovimientoBetween(categoriaId, from, to);
        } else {
            movimientos = movimientoRepository.findByCategoriaId(categoriaId);
        }

        BigDecimal net = movimientos.stream()
            .map(m -> m.getTipo() == null ? BigDecimal.ZERO : (m.getTipo().name().equals("INGRESO") ? m.getMonto() : m.getMonto().negate()))
            .filter(x -> x != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return net;
    }
}
