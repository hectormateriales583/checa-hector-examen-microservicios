package com.tecsup.biblioteca.notificacionesservice.service;

import com.tecsup.biblioteca.notificacionesservice.domain.Notificacion;
import com.tecsup.biblioteca.notificacionesservice.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion enviar(Notificacion notificacion) {
        if (notificacion.getCanal() == null || notificacion.getCanal().isBlank()) {
            notificacion.setCanal("EMAIL");
        }
        notificacion.setEstado("ENVIADO");
        notificacion.setFechaEnvio(LocalDateTime.now());
        // Simulación de envío (log)
        System.out.println("[NOTIFICACION SIMULADA] Enviando a: " + notificacion.getDestino() + " | Mensaje: " + notificacion.getMensaje());
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAll();
    }
}
