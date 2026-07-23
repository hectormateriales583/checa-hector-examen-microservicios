package com.tecsup.biblioteca.prestamosservice.pattern.builder;

import com.tecsup.biblioteca.prestamosservice.dto.PrestamoResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

// [Patrón: Builder]
// Implementación manual del patrón Builder sin anotaciones de Lombok.
// Permite construir dinámicamente el objeto PrestamoResponse adaptando sus campos
// según el flujo ejecutado (REGISTRADA, RECHAZADA o DEVUELTO).
public class PrestamoResponseBuilder {

    private final PrestamoResponse response;

    public PrestamoResponseBuilder() {
        this.response = new PrestamoResponse();
    }

    public static PrestamoResponseBuilder builder() {
        return new PrestamoResponseBuilder();
    }

    public PrestamoResponseBuilder id(Long id) {
        this.response.setId(id);
        return this;
    }

    public PrestamoResponseBuilder codigoEjemplar(String codigoEjemplar) {
        this.response.setCodigoEjemplar(codigoEjemplar);
        return this;
    }

    public PrestamoResponseBuilder codigoSocio(String codigoSocio) {
        this.response.setCodigoSocio(codigoSocio);
        return this;
    }

    public PrestamoResponseBuilder fechaPrestamo(LocalDateTime fechaPrestamo) {
        this.response.setFechaPrestamo(fechaPrestamo);
        return this;
    }

    public PrestamoResponseBuilder fechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.response.setFechaDevolucionEsperada(fechaDevolucionEsperada);
        return this;
    }

    public PrestamoResponseBuilder fechaDevolucionReal(LocalDateTime fechaDevolucionReal) {
        this.response.setFechaDevolucionReal(fechaDevolucionReal);
        return this;
    }

    public PrestamoResponseBuilder estado(String estado) {
        this.response.setEstado(estado);
        return this;
    }

    public PrestamoResponseBuilder motivoRechazo(String motivoRechazo) {
        this.response.setMotivoRechazo(motivoRechazo);
        return this;
    }

    public PrestamoResponseBuilder observaciones(String observaciones) {
        this.response.setObservaciones(observaciones);
        return this;
    }

    public PrestamoResponseBuilder mensajeNotificacion(String mensajeNotificacion) {
        this.response.setMensajeNotificacion(mensajeNotificacion);
        return this;
    }

    public PrestamoResponse build() {
        return this.response;
    }
}
