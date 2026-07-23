# Sistema de Biblioteca con Microservicios (Examen Práctico)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=libros-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=libros-service)

Sistema distribuido de gestión de biblioteca desarrollado en **Spring Boot 3**, **Spring Cloud (Eureka Server & Client)**, **Spring Data JPA** y **PostgreSQL**, con orquestación de préstamos, notificaciones y patrones de diseño creacionales.

---

## 🏗️ Estructura del Proyecto

```text
apellido-nombre-examen-microservicios/
├── eureka-server/           # Servidor de Registro y Descubrimiento (Puerto 8761)
├── libros-service/          # Gestión de Ejemplares y Socios (Puerto 8081)
├── prestamos-service/       # Orquestador de Préstamos y Devoluciones (Puerto 8082)
├── notificaciones-service/  # Registro y Simulación de Notificaciones (Puerto 8083)
├── postman/
│   ├── Biblioteca.postman_collection.json      # Colección para los 9 escenarios del sistema
│   └── RENIEC-Gateway.postman_collection.json # Colección de prueba Gateway con JWT del docente
├── docs/
│   ├── eureka-dashboard.png
│   └── sonarcloud-quality-gate.png
└── README.md
```

---

## 🛢️ 1. Cómo levantar las 3 Bases de Datos PostgreSQL (Anexo A)

Puedes utilizar **Docker** ejecutando los siguientes 3 comandos independientes en tu terminal:

```bash
# 1. Base de datos para Libros y Socios (Puerto 5432)
docker run --name libros-db -e POSTGRES_DB=librosdb -e POSTGRES_USER=libros \
  -e POSTGRES_PASSWORD=libros123 -p 5432:5432 -d postgres:16-alpine

# 2. Base de datos para Préstamos (Puerto 5433)
docker run --name prestamos-db -e POSTGRES_DB=prestamosdb -e POSTGRES_USER=prestamos \
  -e POSTGRES_PASSWORD=prestamos123 -p 5433:5432 -d postgres:16-alpine

# 3. Base de datos para Notificaciones (Puerto 5434)
docker run --name notif-db -e POSTGRES_DB=notificacionesdb -e POSTGRES_USER=notif \
  -e POSTGRES_PASSWORD=notif123 -p 5434:5432 -d postgres:16-alpine
```

*(Si usas una instalación de PostgreSQL local existente, crea 3 bases de datos llamadas `librosdb`, `prestamosdb` y `notificacionesdb` con sus respectivos puertos o credenciales actualizadas en `application.yml`).*

---

## 🚀 2. Orden de Arranque de los 4 Servicios

1. **`eureka-server`** (Puerto `8761`)
   - *Esperar 10-15 segundos a que inicie completamente en http://localhost:8761.*
2. **`libros-service`** (Puerto `8081`)
   - *Se conecta a `librosdb` y realiza el sembrado de datos inicial (`CommandLineRunner`).*
3. **`notificaciones-service`** (Puerto `8083`)
   - *Se conecta a `notificacionesdb` y se registra en Eureka.*
4. **`prestamos-service`** (Puerto `8082`)
   - *Microservicio orquestador. Se conecta a `prestamosdb` y utiliza `RestClient` con `@LoadBalanced` para consultar por nombre a `libros-service` y `notificaciones-service`.*

---

## 🎨 3. Justificación de los Patrones de Diseño Creacionales

### 3.1 Patrón **Factory Method** (`MensajeNotificacionFactory.java`)
- **Ubicación**: `prestamos-service/src/main/java/com/tecsup/biblioteca/prestamosservice/pattern/factory/MensajeNotificacionFactory.java`
- **Justificación**: Se aplicó **Factory Method** para desatar la lógica de construcción de mensajes de texto de notificación del flujo del servicio principal. La fábrica analiza el tipo o estado de la transacción (`REGISTRADA`, `RECHAZADA`, `DEVUELTO`) y genera de forma limpia y extensible la plantilla del correo o mensaje, evitando bloques de concatenación repetitivos y desordenados en el código de negocio.

### 3.2 Patrón **Builder** (`PrestamoResponseBuilder.java`)
- **Ubicación**: `prestamos-service/src/main/java/com/tecsup/biblioteca/prestamosservice/pattern/builder/PrestamoResponseBuilder.java`
- **Justificación**: Se implementó el patrón **Builder a mano (sin Lombok)** para construir la DTO de respuesta `PrestamoResponse`. Debido a que un préstamo puede finalizar de múltiples formas (éxito con fecha esperada, rechazo con motivo específico, devolución con fecha real), el patrón Builder permite instanciar de forma fluida, legible y segura solo los atributos relevantes según la naturaleza de cada respuesta.

---

## 🔍 4. Evidencia de Calidad SonarCloud

- **Quality Gate**: `PASSED`
- **Dashboard**: [SonarCloud Project Dashboard](https://sonarcloud.io)
- **Workflow CI**: `.github/workflows/sonarcloud.yml`

*(Las capturas de evidencia se encuentran en la carpeta `docs/sonarcloud-quality-gate.png`).*

---

## 🧪 5. Guía para Reproducir el Flujo de Pruebas (Sección 8)

Puedes importar la colección `postman/Biblioteca.postman_collection.json` en Postman. Los escenarios deben ejecutarse en el siguiente orden:

1. **`GET /api/v1/libros`**: Muestra la lista de ejemplares sembrados (`BIB-0001`, `BIB-0002`, `BIB-0003`).
2. **`GET /api/v1/socios`**: Muestra la lista de socios sembrados (`S001` activo, `S002` inactivo).
3. **`POST /api/v1/prestamos`** (con `BIB-0001` y `S001`): Responde `200 OK` con estado `REGISTRADA`. Marca el libro como `disponible = false` y dispara notificación.
4. **`POST /api/v1/prestamos`** (repetir con `BIB-0001` y otro socio): Responde `200 OK` con estado `RECHAZADA` y motivo `"No disponible"`.
5. **`POST /api/v1/prestamos`** (usando socio `S002` inactivo): Responde `200 OK` con estado `RECHAZADA` y motivo `"Socio inactivo"`.
6. **`POST /api/v1/prestamos`** (usando ejemplar `BIB-9999` inexistente): Responde `200 OK` con estado `RECHAZADA` y motivo `"Ejemplar no existe"`.
7. **`POST /api/v1/prestamos/1/devolucion`**: Devuelve el préstamo del paso 3. Responde estado `DEVUELTO` y reactiva la disponibilidad del ejemplar (`disponible = true`).
8. **`POST /api/v1/prestamos/1/devolucion`** (repetir devolución): Retorna error controlado `409 Conflict` (Préstamo ya devuelto).
9. **`GET /api/v1/notificaciones`**: Muestra el registro de notificaciones generadas en la base de datos de notificaciones.
