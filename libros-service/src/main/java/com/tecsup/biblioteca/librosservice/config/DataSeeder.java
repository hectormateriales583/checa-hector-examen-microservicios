package com.tecsup.biblioteca.librosservice.config;

import com.tecsup.biblioteca.librosservice.domain.Ejemplar;
import com.tecsup.biblioteca.librosservice.domain.Socio;
import com.tecsup.biblioteca.librosservice.repository.EjemplarRepository;
import com.tecsup.biblioteca.librosservice.repository.SocioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final EjemplarRepository ejemplarRepository;
    private final SocioRepository socioRepository;

    public DataSeeder(EjemplarRepository ejemplarRepository, SocioRepository socioRepository) {
        this.ejemplarRepository = ejemplarRepository;
        this.socioRepository = socioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (ejemplarRepository.count() == 0) {
            Ejemplar e1 = new Ejemplar("BIB-0001", "Cien Años de Soledad", "Gabriel García Márquez", "978-0307474728", 1967, true, null, null);
            Ejemplar e2 = new Ejemplar("BIB-0002", "El Principito", "Antoine de Saint-Exupéry", "978-0156012195", 1943, true, null, null);
            Ejemplar e3 = new Ejemplar("BIB-0003", "Don Quijote de la Mancha", "Miguel de Cervantes", "978-8424116032", 1605, false, null, null);

            ejemplarRepository.save(e1);
            ejemplarRepository.save(e2);
            ejemplarRepository.save(e3);
        }

        if (socioRepository.count() == 0) {
            Socio s1 = new Socio("S001", "Juan Pérez", "juan.perez@example.com", "987654321", LocalDate.now().minusMonths(6), true, null, null);
            Socio s2 = new Socio("S002", "Maria Lopez", "maria.lopez@example.com", "912345678", LocalDate.now().minusYears(1), false, null, null);

            socioRepository.save(s1);
            socioRepository.save(s2);
        }
    }
}
