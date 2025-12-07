package finanzas.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import finanzas.domain.enumeration.EstadoProcesamiento;
import finanzas.domain.enumeration.TipoMovimiento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidad para almacenar mensajes recibidos a través de WhatsApp.
 */
@Entity
@Table(name = "whatsapp_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WhatsappMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1, max = 500)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensajeOriginal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "monto", nullable = false, precision = 21, scale = 2)
    private java.math.BigDecimal monto;

    @Size(max = 255)
    @Column(name = "categoria")
    private String categoria;

    @Size(max = 255)
    @Column(name = "cuenta")
    private String cuenta;

    @Size(max = 500)
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoProcesamiento estado;

    @Size(max = 255)
    @Column(name = "numero_telefonico", nullable = false)
    private String numeroTelefonico;

    @NotNull
    @Column(name = "fecha_recepcion", nullable = false)
    private ZonedDateTime fechaRecepcion;

    @Column(name = "fecha_procesamiento")
    private ZonedDateTime fechaProcesamiento;

    @Size(max = 1000)
    @Column(name = "respuesta_bot", columnDefinition = "TEXT")
    private String respuestaBot;

    @Size(max = 500)
    @Column(name = "error_mensaje")
    private String errorMensaje;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private Movimiento movimientoAsociado;

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

    public java.math.BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(java.math.BigDecimal monto) {
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

    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public void setNumeroTelefonico(String numeroTelefonico) {
        this.numeroTelefonico = numeroTelefonico;
    }

    public ZonedDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(ZonedDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public ZonedDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(ZonedDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public String getRespuestaBot() {
        return respuestaBot;
    }

    public void setRespuestaBot(String respuestaBot) {
        this.respuestaBot = respuestaBot;
    }

    public String getErrorMensaje() {
        return errorMensaje;
    }

    public void setErrorMensaje(String errorMensaje) {
        this.errorMensaje = errorMensaje;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Movimiento getMovimientoAsociado() {
        return movimientoAsociado;
    }

    public void setMovimientoAsociado(Movimiento movimientoAsociado) {
        this.movimientoAsociado = movimientoAsociado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WhatsappMessage)) {
            return false;
        }
        return id != null && id.equals(((WhatsappMessage) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "WhatsappMessage{" +
            "id=" + id +
            ", mensajeOriginal='" + mensajeOriginal + '\'' +
            ", tipoMovimiento=" + tipoMovimiento +
            ", monto=" + monto +
            ", estado=" + estado +
            ", numeroTelefonico='" + numeroTelefonico + '\'' +
            ", fechaRecepcion=" + fechaRecepcion +
            '}';
    }
}
