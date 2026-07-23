package com.tecsup.biblioteca.librosservice.repository;

import com.tecsup.biblioteca.librosservice.domain.Ejemplar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EjemplarRepository extends JpaRepository<Ejemplar, String> {
}
