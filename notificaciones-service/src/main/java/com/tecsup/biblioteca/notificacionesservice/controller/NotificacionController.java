package com.tecsup.biblioteca.notificacionesservice.controller;

import com.tecsup.biblioteca.notificacionesservice.domain.Notificacion;
import com.tecsup.biblioteca.notificacionesservice.service.NotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notificacion enviar(@RequestBody Notificacion notificacion) {
        return notificacionService.enviar(notificacion);
    }

    @GetMapping
    public List<Notificacion> listarTodas() {
        return notificacionService.listarTodas();
    }
}
