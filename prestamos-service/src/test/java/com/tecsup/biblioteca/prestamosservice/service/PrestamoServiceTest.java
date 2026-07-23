package com.tecsup.biblioteca.prestamosservice.service;

import com.tecsup.biblioteca.prestamosservice.domain.Prestamo;
import com.tecsup.biblioteca.prestamosservice.dto.PrestamoRequest;
import com.tecsup.biblioteca.prestamosservice.dto.PrestamoResponse;
import com.tecsup.biblioteca.prestamosservice.pattern.factory.MensajeNotificacionFactory;
import com.tecsup.biblioteca.prestamosservice.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient.Builder restClientBuilder;

    @Mock
    private MensajeNotificacionFactory mensajeNotificacionFactory;

    private PrestamoService prestamoService;

    @BeforeEach
    void setUp() {
        prestamoService = new PrestamoService(prestamoRepository, restClientBuilder, mensajeNotificacionFactory);
    }

    @Test
    void cuandoDevolucionEsDuplicada_lanzaExcepcionConflict() {
        Prestamo prestamoDevuelto = new Prestamo();
        prestamoDevuelto.setId(1L);
        prestamoDevuelto.setEstado("DEVUELTO");

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoDevuelto));

        assertThrows(ResponseStatusException.class, () -> {
            prestamoService.devolverPrestamo(1L);
        });

        verify(prestamoRepository, times(1)).findById(1L);
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    void cuandoDevolucionEsExitosa_actualizaEstadoADevuelto() {
        Prestamo prestamoRegistrado = new Prestamo();
        prestamoRegistrado.setId(2L);
        prestamoRegistrado.setCodigoEjemplar("BIB-0001");
        prestamoRegistrado.setCodigoSocio("S001");
        prestamoRegistrado.setFechaPrestamo(LocalDateTime.now());
        prestamoRegistrado.setEstado("REGISTRADA");

        when(prestamoRepository.findById(2L)).thenReturn(Optional.of(prestamoRegistrado));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mensajeNotificacionFactory.crearMensaje(eq("DEVUELTO"), anyString(), anyString(), any())).thenReturn("Devuelto OK");

        PrestamoResponse response = prestamoService.devolverPrestamo(2L);

        assertNotNull(response);
        assertEquals("DEVUELTO", response.getEstado());
        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    @Test
    void cuandoSocioInactivo_guardaYRetornaEstadoRechazada() {
        PrestamoRequest request = new PrestamoRequest("BIB-0001", "S002", 7, "Prueba");

        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> {
            Prestamo p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        // En caso de que falle la llamada REST a libros-service por mock, cae en catch y retorna RECHAZADA
        PrestamoResponse response = prestamoService.registrarPrestamo(request);

        assertNotNull(response);
        assertEquals("RECHAZADA", response.getEstado());
        assertNotNull(response.getMotivoRechazo());
    }
}
