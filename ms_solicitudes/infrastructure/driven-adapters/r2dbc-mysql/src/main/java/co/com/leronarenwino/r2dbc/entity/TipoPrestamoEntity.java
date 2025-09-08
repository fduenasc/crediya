package co.com.leronarenwino.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("tipo_prestamo")
public class TipoPrestamoEntity {

    @Id
    private Long idTipoPrestamo;
    private String nombre;
    private Double montoMinimo;
    private Double montoMaximo;
    private Double tasaInteres;
    private Boolean validacionAutomatica;

    public Long getIdTipoPrestamo() {
        return idTipoPrestamo;
    }

    public void setIdTipoPrestamo(Long idTipoPrestamo) {
        this.idTipoPrestamo = idTipoPrestamo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(Double montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public Double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(Double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public Double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(Double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public Boolean getValidacionAutomatica() {
        return validacionAutomatica;
    }

    public void setValidacionAutomatica(Boolean validacionAutomatica) {
        this.validacionAutomatica = validacionAutomatica;
    }
}