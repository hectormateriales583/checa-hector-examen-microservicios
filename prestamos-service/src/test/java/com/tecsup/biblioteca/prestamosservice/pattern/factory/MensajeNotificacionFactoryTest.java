package com.tecsup.biblioteca.prestamosservice.pattern.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensajeNotificacionFactoryTest {

    private MensajeNotificacionFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MensajeNotificacionFactory();
    }

    @Test
    void cuandoEstadoEsRegistrada_armaMensajeExitoso() {
        String mensaje = factory.crearMensaje("REGISTRADA", "BIB-0001", "S001", null);

        assertNotNull(mensaje);
        assertTrue(mensaje.contains("BIB-0001"));
        assertTrue(mensaje.contains("S001"));
        assertTrue(mensaje.contains("registrado exitosamente"));
    }

    @Test
    void cuandoEstadoEsRechazada_armaMensajeConMotivo() {
        String mensaje = factory.crearMensaje("RECHAZADA", "BIB-0001", "S001", "Socio inactivo");

        assertNotNull(mensaje);
        assertTrue(mensaje.contains("RECHAZADA"));
        assertTrue(mensaje.contains("Socio inactivo"));
    }

    @Test
    void cuandoEstadoEsDevuelto_armaMensajeDevolucion() {
        String mensaje = factory.crearMensaje("DEVUELTO", "BIB-0001", "S001", null);

        assertNotNull(mensaje);
        assertTrue(mensaje.contains("devuelto correctamente"));
    }
}
