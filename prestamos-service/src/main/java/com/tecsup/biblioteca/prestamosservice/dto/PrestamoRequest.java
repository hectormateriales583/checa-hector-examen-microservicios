package com.tecsup.biblioteca.prestamosservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoRequest {
    private String codigoEjemplar;
    private String codigoSocio;
    private Integer dias; // Días esperados de préstamo (opcional)
    private String observaciones;
}
