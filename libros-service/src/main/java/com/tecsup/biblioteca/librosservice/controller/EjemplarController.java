package com.tecsup.biblioteca.librosservice.controller;

import com.tecsup.biblioteca.librosservice.domain.Ejemplar;
import com.tecsup.biblioteca.librosservice.service.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/libros")
public class EjemplarController {

    private final LibroService libroService;

    public EjemplarController(LibroService libroService) {
        this.libroService = libroService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ejemplar crear(@RequestBody Ejemplar ejemplar) {
        return libroService.crear(ejemplar);
    }

    @GetMapping
    public List<Ejemplar> listarTodos() {
        return libroService.listarTodos();
    }

    @GetMapping("/{codigoEjemplar}")
    public Ejemplar obtenerPorCodigo(@PathVariable String codigoEjemplar) {
        return libroService.obtenerPorCodigo(codigoEjemplar);
    }

    @PutMapping("/{codigoEjemplar}")
    public Ejemplar actualizar(@PathVariable String codigoEjemplar, @RequestBody Ejemplar datos) {
        return libroService.actualizar(codigoEjemplar, datos);
    }

    @DeleteMapping("/{codigoEjemplar}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigoEjemplar) {
        libroService.eliminar(codigoEjemplar);
    }

    @PatchMapping("/{codigoEjemplar}/disponibilidad")
    public Ejemplar cambiarDisponibilidad(
            @PathVariable String codigoEjemplar,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(required = false) Boolean disponible) {
        
        boolean nuevoEstado = false;
        if (body != null && body.containsKey("disponible")) {
            nuevoEstado = Boolean.TRUE.equals(body.get("disponible"));
        } else if (disponible != null) {
            nuevoEstado = disponible;
        }
        return libroService.cambiarDisponibilidad(codigoEjemplar, nuevoEstado);
    }
}
