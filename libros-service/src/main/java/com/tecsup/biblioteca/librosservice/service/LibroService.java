package com.tecsup.biblioteca.librosservice.service;

import com.tecsup.biblioteca.librosservice.domain.Ejemplar;
import com.tecsup.biblioteca.librosservice.exception.EjemplarNoEncontradoException;
import com.tecsup.biblioteca.librosservice.repository.EjemplarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private final EjemplarRepository ejemplarRepository;

    public LibroService(EjemplarRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    public Ejemplar crear(Ejemplar ejemplar) {
        return ejemplarRepository.save(ejemplar);
    }

    public List<Ejemplar> listarTodos() {
        return ejemplarRepository.findAll();
    }

    public Ejemplar obtenerPorCodigo(String codigoEjemplar) {
        return ejemplarRepository.findById(codigoEjemplar)
                .orElseThrow(() -> new EjemplarNoEncontradoException(codigoEjemplar));
    }

    public Ejemplar actualizar(String codigoEjemplar, Ejemplar datos) {
        Ejemplar existente = obtenerPorCodigo(codigoEjemplar);
        if (datos.getTitulo() != null) existente.setTitulo(datos.getTitulo());
        if (datos.getAutor() != null) existente.setAutor(datos.getAutor());
        if (datos.getIsbn() != null) existente.setIsbn(datos.getIsbn());
        if (datos.getAnioPublicacion() != null) existente.setAnioPublicacion(datos.getAnioPublicacion());
        existente.setDisponible(datos.isDisponible());
        return ejemplarRepository.save(existente);
    }

    public void eliminar(String codigoEjemplar) {
        Ejemplar existente = obtenerPorCodigo(codigoEjemplar);
        ejemplarRepository.delete(existente);
    }

    public Ejemplar cambiarDisponibilidad(String codigoEjemplar, boolean disponible) {
        Ejemplar existente = obtenerPorCodigo(codigoEjemplar);
        existente.setDisponible(disponible);
        return ejemplarRepository.save(existente);
    }
}
