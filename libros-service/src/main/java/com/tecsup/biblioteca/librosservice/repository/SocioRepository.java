package com.tecsup.biblioteca.librosservice.repository;

import com.tecsup.biblioteca.librosservice.domain.Socio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocioRepository extends JpaRepository<Socio, String> {
}
