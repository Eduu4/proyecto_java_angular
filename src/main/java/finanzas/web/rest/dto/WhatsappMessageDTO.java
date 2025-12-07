package finanzas.web.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import finanzas.domain.enumeration.EstadoProcesamiento;
import finanzas.domain.enumeration.TipoMovimiento;

/**
 * DTO para WhatsappMessage.
 */
public class WhatsappMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String mensajeOriginal;

    private TipoMovimiento tipoMovimiento;

    private BigDecimal monto;

    private String categoria;

    private String cuenta;

    private String descripcion;

    private EstadoProcesamiento estado;

    private String respuestaBot;

    private ZonedDateTime fechaRecepcion;

    public WhatsappMessageDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensajeOriginal() {
        return mensajeOriginal;
    }

    public void setMensajeOriginal(String mensajeOriginal) {
        this.mensajeOriginal = mensajeOriginal;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoProcesamiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoProcesamiento estado) {
        this.estado = estado;
    }

    public String getRespuestaBot() {
        return respuestaBot;
    }

    public void setRespuestaBot(String respuestaBot) {
        this.respuestaBot = respuestaBot;
    }

    public ZonedDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(ZonedDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    @Override
    public String toString() {
        return "WhatsappMessageDTO{" +
            "id=" + id +
            ", mensajeOriginal='" + mensajeOriginal + '\'' +
            ", tipoMovimiento=" + tipoMovimiento +
            ", monto=" + monto +
            ", estado=" + estado +
            '}';
    }
}
