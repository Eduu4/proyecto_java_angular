package finanzas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import finanzas.domain.enumeration.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Movimiento.
 */
@Entity
@Table(name = "movimiento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Movimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMovimiento tipo;

    @NotNull
    @Column(name = "monto", precision = 21, scale = 2, nullable = false)
    private BigDecimal monto;

    @NotNull
    @Column(name = "fecha_movimiento", nullable = false)
    private ZonedDateTime fechaMovimiento;

    @NotNull
    @Column(name = "fecha_registro", nullable = false)
    private ZonedDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "usuario" }, allowSetters = true)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "usuario" }, allowSetters = true)
    private Cuenta cuenta;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Movimiento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMovimiento getTipo() {
        return this.tipo;
    }

    public Movimiento tipo(TipoMovimiento tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return this.monto;
    }

    public Movimiento monto(BigDecimal monto) {
        this.setMonto(monto);
        return this;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public ZonedDateTime getFechaMovimiento() {
        return this.fechaMovimiento;
    }

    public Movimiento fechaMovimiento(ZonedDateTime fechaMovimiento) {
        this.setFechaMovimiento(fechaMovimiento);
        return this;
    }

    public void setFechaMovimiento(ZonedDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public ZonedDateTime getFechaRegistro() {
        return this.fechaRegistro;
    }

    public Movimiento fechaRegistro(ZonedDateTime fechaRegistro) {
        this.setFechaRegistro(fechaRegistro);
        return this;
    }

    public void setFechaRegistro(ZonedDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public ZonedDateTime getFechaActualizacion() {
        return this.fechaActualizacion;
    }

    public Movimiento fechaActualizacion(ZonedDateTime fechaActualizacion) {
        this.setFechaActualizacion(fechaActualizacion);
        return this;
    }

    public void setFechaActualizacion(ZonedDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Movimiento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public User getUsuario() {
        return this.usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }

    public Movimiento usuario(User user) {
        this.setUsuario(user);
        return this;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Movimiento categoria(Categoria categoria) {
        this.setCategoria(categoria);
        return this;
    }

    public Cuenta getCuenta() {
        return this.cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Movimiento cuenta(Cuenta cuenta) {
        this.setCuenta(cuenta);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Movimiento)) {
            return false;
        }
        return getId() != null && getId().equals(((Movimiento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Movimiento{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", monto=" + getMonto() +
            ", fechaMovimiento='" + getFechaMovimiento() + "'" +
            ", fechaRegistro='" + getFechaRegistro() + "'" +
            ", fechaActualizacion='" + getFechaActualizacion() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            "}";
    }
}
