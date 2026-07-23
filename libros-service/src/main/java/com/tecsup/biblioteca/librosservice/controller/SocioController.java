package com.tecsup.biblioteca.librosservice.controller;

import com.tecsup.biblioteca.librosservice.domain.Socio;
import com.tecsup.biblioteca.librosservice.service.SocioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/socios")
public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Socio crear(@RequestBody Socio socio) {
        return socioService.crear(socio);
    }

    @GetMapping
    public List<Socio> listarTodos() {
        return socioService.listarTodos();
    }

    @GetMapping("/{codigoSocio}")
    public Socio obtenerPorCodigo(@PathVariable String codigoSocio) {
        return socioService.obtenerPorCodigo(codigoSocio);
    }

    @PutMapping("/{codigoSocio}")
    public Socio actualizar(@PathVariable String codigoSocio, @RequestBody Socio datos) {
        return socioService.actualizar(codigoSocio, datos);
    }

    @DeleteMapping("/{codigoSocio}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigoSocio) {
        socioService.eliminar(codigoSocio);
    }
}
