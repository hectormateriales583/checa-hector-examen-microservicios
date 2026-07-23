package com.tecsup.biblioteca.librosservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SocioNoEncontradoException extends RuntimeException {
    public SocioNoEncontradoException(String codigoSocio) {
        super("Socio no encontrado con código: " + codigoSocio);
    }
}
