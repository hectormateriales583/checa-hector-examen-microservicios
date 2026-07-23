package com.tecsup.biblioteca.prestamosservice.controller;

import com.tecsup.biblioteca.prestamosservice.domain.Prestamo;
import com.tecsup.biblioteca.prestamosservice.dto.PrestamoRequest;
import com.tecsup.biblioteca.prestamosservice.dto.PrestamoResponse;
import com.tecsup.biblioteca.prestamosservice.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public PrestamoResponse registrarPrestamo(@RequestBody PrestamoRequest request) {
        return prestamoService.registrarPrestamo(request);
    }

    @GetMapping
    public List<Prestamo> listarTodos() {
        return prestamoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Prestamo obtenerPorId(@PathVariable Long id) {
        return prestamoService.obtenerPorId(id);
    }

    @PostMapping("/{id}/devolucion")
    public PrestamoResponse devolverPrestamo(@PathVariable Long id) {
        return prestamoService.devolverPrestamo(id);
    }
}
