package com.tecsup.biblioteca.prestamosservice.repository;

import com.tecsup.biblioteca.prestamosservice.domain.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}
