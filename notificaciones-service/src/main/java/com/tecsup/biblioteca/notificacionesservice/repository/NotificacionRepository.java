package com.tecsup.biblioteca.notificacionesservice.repository;

import com.tecsup.biblioteca.notificacionesservice.domain.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
