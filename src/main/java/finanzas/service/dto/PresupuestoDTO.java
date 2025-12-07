package finanzas.service.dto;

import finanzas.domain.enumeration.PeriodoPresupuesto;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link finanzas.domain.Presupuesto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PresupuestoDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal monto;

    @NotNull
    private PeriodoPresupuesto periodo;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    private UserDTO usuario;

    private CategoriaDTO categoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public PeriodoPresupuesto getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoPresupuesto periodo) {
        this.periodo = periodo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PresupuestoDTO)) {
            return false;
        }

        PresupuestoDTO presupuestoDTO = (PresupuestoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, presupuestoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PresupuestoDTO{" +
            "id=" + getId() +
            ", monto=" + getMonto() +
            ", periodo='" + getPeriodo() + "'" +
            ", fechaInicio='" + getFechaInicio() + "'" +
            ", fechaFin='" + getFechaFin() + "'" +
            ", usuario=" + getUsuario() +
            ", categoria=" + getCategoria() +
            "}";
    }
}
