package com.tecsup.biblioteca.prestamosservice.service;

import com.tecsup.biblioteca.prestamosservice.domain.Prestamo;
import com.tecsup.biblioteca.prestamosservice.dto.*;
import com.tecsup.biblioteca.prestamosservice.pattern.builder.PrestamoResponseBuilder;
import com.tecsup.biblioteca.prestamosservice.pattern.factory.MensajeNotificacionFactory;
import com.tecsup.biblioteca.prestamosservice.repository.PrestamoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final RestClient restClient;
    private final MensajeNotificacionFactory mensajeNotificacionFactory;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           RestClient.Builder restClientBuilder,
                           MensajeNotificacionFactory mensajeNotificacionFactory) {
        this.prestamoRepository = prestamoRepository;
        this.restClient = restClientBuilder.build();
        this.mensajeNotificacionFactory = mensajeNotificacionFactory;
    }

    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        String codigoSocio = request.getCodigoSocio();
        String codigoEjemplar = request.getCodigoEjemplar();

        // 1. Validar Socio vía libros-service
        SocioDTO socio = null;
        try {
            socio = restClient.get()
                    .uri("http://libros-service/api/v1/socios/{codigoSocio}", codigoSocio)
                    .retrieve()
                    .body(SocioDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Socio no existe");
        } catch (Exception e) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Error de comunicación con libros-service");
        }

        if (socio == null) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Socio no existe");
        }

        if (!socio.isActivo()) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Socio inactivo");
        }

        // 2. Validar Ejemplar vía libros-service
        EjemplarDTO ejemplar = null;
        try {
            ejemplar = restClient.get()
                    .uri("http://libros-service/api/v1/libros/{codigoEjemplar}", codigoEjemplar)
                    .retrieve()
                    .body(EjemplarDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Ejemplar no existe");
        } catch (Exception e) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Error de comunicación con libros-service");
        }

        if (ejemplar == null) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "Ejemplar no existe");
        }

        if (!ejemplar.isDisponible()) {
            return guardarYConstruirRechazo(codigoEjemplar, codigoSocio, "No disponible");
        }

        // 3. Camino Feliz: Marcar libro como no disponible
        try {
            restClient.patch()
                    .uri("http://libros-service/api/v1/libros/{codigoEjemplar}/disponibilidad", codigoEjemplar)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("disponible", false))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Advertencia al actualizar disponibilidad del ejemplar: " + e.getMessage());
        }

        // 4. Guardar Préstamo como REGISTRADA
        int dias = (request.getDias() != null && request.getDias() > 0) ? request.getDias() : 7;
        Prestamo prestamo = new Prestamo();
        prestamo.setCodigoEjemplar(codigoEjemplar);
        prestamo.setCodigoSocio(codigoSocio);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(dias));
        prestamo.setEstado("REGISTRADA");
        prestamo.setObservaciones(request.getObservaciones());

        Prestamo guardado = prestamoRepository.save(prestamo);

        // 5. Crear notificación con Factory Method y enviarla a notificaciones-service
        String mensajeNotif = mensajeNotificacionFactory.crearMensaje("REGISTRADA", codigoEjemplar, codigoSocio, null);
        enviarNotificacionSilenciosa(socio.getEmail() != null ? socio.getEmail() : "socio@biblioteca.com", mensajeNotif);

        // 6. Construir respuesta usando Builder a mano
        // [Patrón: Builder]
        return PrestamoResponseBuilder.builder()
                .id(guardado.getId())
                .codigoEjemplar(guardado.getCodigoEjemplar())
                .codigoSocio(guardado.getCodigoSocio())
                .fechaPrestamo(guardado.getFechaPrestamo())
                .fechaDevolucionEsperada(guardado.getFechaDevolucionEsperada())
                .estado(guardado.getEstado())
                .observaciones(guardado.getObservaciones())
                .mensajeNotificacion(mensajeNotif)
                .build();
    }

    public PrestamoResponse devolverPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con id: " + id));

        if ("DEVUELTO".equalsIgnoreCase(prestamo.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El préstamo ya fue devuelto anteriormente.");
        }

        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(LocalDateTime.now());
        Prestamo guardado = prestamoRepository.save(prestamo);

        // Cambiar disponibilidad a true en libros-service
        try {
            restClient.patch()
                    .uri("http://libros-service/api/v1/libros/{codigoEjemplar}/disponibilidad", prestamo.getCodigoEjemplar())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("disponible", true))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Advertencia al reactivar disponibilidad del ejemplar: " + e.getMessage());
        }

        String mensajeNotif = mensajeNotificacionFactory.crearMensaje("DEVUELTO", prestamo.getCodigoEjemplar(), prestamo.getCodigoSocio(), null);
        enviarNotificacionSilenciosa("socio@biblioteca.com", mensajeNotif);

        return PrestamoResponseBuilder.builder()
                .id(guardado.getId())
                .codigoEjemplar(guardado.getCodigoEjemplar())
                .codigoSocio(guardado.getCodigoSocio())
                .fechaPrestamo(guardado.getFechaPrestamo())
                .fechaDevolucionEsperada(guardado.getFechaDevolucionEsperada())
                .fechaDevolucionReal(guardado.getFechaDevolucionReal())
                .estado(guardado.getEstado())
                .observaciones(guardado.getObservaciones())
                .mensajeNotificacion(mensajeNotif)
                .build();
    }

    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAll();
    }

    public Prestamo obtenerPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con id: " + id));
    }

    private PrestamoResponse guardarYConstruirRechazo(String codigoEjemplar, String codigoSocio, String motivoRechazo) {
        Prestamo prestamoRechazado = new Prestamo();
        prestamoRechazado.setCodigoEjemplar(codigoEjemplar);
        prestamoRechazado.setCodigoSocio(codigoSocio);
        prestamoRechazado.setFechaPrestamo(LocalDateTime.now());
        prestamoRechazado.setEstado("RECHAZADA");
        prestamoRechazado.setMotivoRechazo(motivoRechazo);

        Prestamo guardado = prestamoRepository.save(prestamoRechazado);

        return PrestamoResponseBuilder.builder()
                .id(guardado.getId())
                .codigoEjemplar(codigoEjemplar)
                .codigoSocio(codigoSocio)
                .fechaPrestamo(guardado.getFechaPrestamo())
                .estado("RECHAZADA")
                .motivoRechazo(motivoRechazo)
                .build();
    }

    private void enviarNotificacionSilenciosa(String destino, String mensaje) {
        try {
            restClient.post()
                    .uri("http://notificaciones-service/api/v1/notificaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("destino", destino, "mensaje", mensaje, "canal", "EMAIL"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Advertencia al enviar notificación: " + e.getMessage());
        }
    }
}
