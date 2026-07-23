package com.tecsup.biblioteca.librosservice.service;

import com.tecsup.biblioteca.librosservice.domain.Ejemplar;
import com.tecsup.biblioteca.librosservice.exception.EjemplarNoEncontradoException;
import com.tecsup.biblioteca.librosservice.repository.EjemplarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private EjemplarRepository ejemplarRepository;

    @InjectMocks
    private LibroService libroService;

    private Ejemplar ejemplarEjemplo;

    @BeforeEach
    void setUp() {
        ejemplarEjemplo = new Ejemplar("BIB-0001", "Cien Años de Soledad", "Gabo", "978-0307474728", 1967, true, null, null);
    }

    @Test
    void cuandoObtenerPorCodigoExiste_retornaEjemplar() {
        when(ejemplarRepository.findById("BIB-0001")).thenReturn(Optional.of(ejemplarEjemplo));

        Ejemplar resultado = libroService.obtenerPorCodigo("BIB-0001");

        assertNotNull(resultado);
        assertEquals("Cien Años de Soledad", resultado.getTitulo());
        verify(ejemplarRepository, times(1)).findById("BIB-0001");
    }

    @Test
    void cuandoObtenerPorCodigoNoExiste_lanzaExcepcionTipada() {
        when(ejemplarRepository.findById("BIB-9999")).thenReturn(Optional.empty());

        assertThrows(EjemplarNoEncontradoException.class, () -> {
            libroService.obtenerPorCodigo("BIB-9999");
        });

        verify(ejemplarRepository, times(1)).findById("BIB-9999");
    }
}
