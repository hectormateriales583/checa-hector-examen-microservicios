package com.tecsup.biblioteca.prestamosservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioDTO {
    private String codigoSocio;
    private String nombre;
    private String email;
    private String telefono;
    private LocalDate fechaInscripcion;
    private boolean activo;
}
