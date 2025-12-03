package finanzas.service.dto;

import finanzas.domain.enumeration.TipoMovimiento;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link finanzas.domain.Movimiento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MovimientoDTO implements Serializable {

    private Long id;

    @NotNull
    private TipoMovimiento tipo;

    @NotNull
    private BigDecimal monto;

    @NotNull
    private ZonedDateTime fechaMovimiento;

    @NotNull
    private ZonedDateTime fechaRegistro;

    private ZonedDateTime fechaActualizacion;

    private String descripcion;

    private UserDTO usuario;

    private CategoriaDTO categoria;

    private CuentaDTO cuenta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public ZonedDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(ZonedDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public ZonedDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(ZonedDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public ZonedDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(ZonedDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UserDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UserDTO usuario) {
        this.usuario = usuario;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public CuentaDTO getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaDTO cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MovimientoDTO)) {
            return false;
        }

        MovimientoDTO movimientoDTO = (MovimientoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, movimientoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MovimientoDTO{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", monto=" + getMonto() +
            ", fechaMovimiento='" + getFechaMovimiento() + "'" +
            ", fechaRegistro='" + getFechaRegistro() + "'" +
            ", fechaActualizacion='" + getFechaActualizacion() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", usuario=" + getUsuario() +
            ", categoria=" + getCategoria() +
            ", cuenta=" + getCuenta() +
            "}";
    }
}
