package com.tecsup.biblioteca.prestamosservice.pattern.factory;

import org.springframework.stereotype.Component;

// [Patrón: Factory Method]
// Esta fábrica encapsula la lógica de construcción de mensajes de notificación
// según el estado del préstamo (REGISTRADA, RECHAZADA, DEVUELTO), evitando concatenaciones manuales
// desordenadas a lo largo del código de negocio.
@Component
public class MensajeNotificacionFactory {

    public String crearMensaje(String estado, String codigoEjemplar, String codigoSocio, String motivoRechazo) {
        if (estado == null) {
            return "Estado de préstamo desconocido.";
        }
        return switch (estado.toUpperCase()) {
            case "REGISTRADA" -> String.format(
                    "¡Hola! Tu préstamo del ejemplar %s para el socio %s ha sido registrado exitosamente.",
                    codigoEjemplar, codigoSocio);
            case "RECHAZADA" -> String.format(
                    "Estimado socio %s, tu solicitud de préstamo para el ejemplar %s fue RECHAZADA. Motivo: %s",
                    codigoSocio, codigoEjemplar, motivoRechazo != null ? motivoRechazo : "No especificado");
            case "DEVUELTO" -> String.format(
                    "Confirmación de devolución: El ejemplar %s ha sido devuelto correctamente por el socio %s.",
                    codigoEjemplar, codigoSocio);
            default -> String.format("Notificación sobre préstamo %s - Socio %s", codigoEjemplar, codigoSocio);
        };
    }
}
