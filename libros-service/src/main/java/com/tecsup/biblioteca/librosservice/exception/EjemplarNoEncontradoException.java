package com.tecsup.biblioteca.librosservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EjemplarNoEncontradoException extends RuntimeException {
    public EjemplarNoEncontradoException(String codigoEjemplar) {
        super("Ejemplar no encontrado con código: " + codigoEjemplar);
    }
}
