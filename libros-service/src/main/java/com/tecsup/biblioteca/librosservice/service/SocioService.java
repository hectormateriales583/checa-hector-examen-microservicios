package com.tecsup.biblioteca.librosservice.service;

import com.tecsup.biblioteca.librosservice.domain.Socio;
import com.tecsup.biblioteca.librosservice.exception.SocioNoEncontradoException;
import com.tecsup.biblioteca.librosservice.repository.SocioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocioService {

    private final SocioRepository socioRepository;

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    public Socio crear(Socio socio) {
        return socioRepository.save(socio);
    }

    public List<Socio> listarTodos() {
        return socioRepository.findAll();
    }

    public Socio obtenerPorCodigo(String codigoSocio) {
        return socioRepository.findById(codigoSocio)
                .orElseThrow(() -> new SocioNoEncontradoException(codigoSocio));
    }

    public Socio actualizar(String codigoSocio, Socio datos) {
        Socio existente = obtenerPorCodigo(codigoSocio);
        if (datos.getNombre() != null) existente.setNombre(datos.getNombre());
        if (datos.getEmail() != null) existente.setEmail(datos.getEmail());
        if (datos.getTelefono() != null) existente.setTelefono(datos.getTelefono());
        existente.setActivo(datos.isActivo());
        return socioRepository.save(existente);
    }

    public void eliminar(String codigoSocio) {
        Socio existente = obtenerPorCodigo(codigoSocio);
        socioRepository.delete(existente);
    }
}
