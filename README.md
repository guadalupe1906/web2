# TP1 · API REST con Spring Boot - Catálogo de Productos y Favoritos

API REST desarrollada con Spring Boot que expone dos grupos de endpoints:
- Productos: catálogo de solo lectura que consume la API externa [DummyJSON](https://dummyjson.com/products).

- Favoritos: CRUD completo, almacenado en memoria (sin base de datos).


## Cómo levantar el proyecto

1. Clonar el repositorio:
```bash
   git clone <URL_DEL_REPO>
   cd <NOMBRE_DEL_PROYECTO>
```

2. Levantar la aplicación:
```bash
   ./mvnw spring-boot:run
```
   (En Windows: `mvnw.cmd spring-boot:run`)

3. La aplicación queda disponible en `http://localhost:8080`.

## Documentación de la API (Swagger)

Con la app levantada, entrar a:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Especificación OpenAPI: http://localhost:8080/v3/api-docs

Swagger muestra los dos grupos de endpoints: `productos` y `favoritos`.

## Endpoints

### Salud

`GET /api/health` verifica que la aplicación esté funcionando.

### Productos

- `GET /api/productos`: obtiene el catálogo desde DummyJSON y lo transforma al DTO propio.
- `GET /api/productos/{id}`: obtiene un producto por su identificador.

### Favoritos

- `POST /api/favoritos`: crea un favorito y responde `201 Created`.
- `GET /api/favoritos`: lista todos los favoritos y responde `200 OK`.
- `GET /api/favoritos/{id}`: obtiene un favorito y responde `200 OK`.
- `PUT /api/favoritos/{id}`: actualiza un favorito y responde `200 OK`.
- `DELETE /api/favoritos/{id}`: elimina un favorito y responde `204 No Content`.

El cuerpo para crear o actualizar un favorito es:

```json
{
   "productoId": 1,
   "nota": "Producto recomendado"
}
```

## Validación y errores

La API valida `productoId` y `nota`. Por ejemplo, un request inválido responde `400 Bad Request` con el campo y el motivo del error. Si el favorito no existe, responde `404 Not Found`. Si DummyJSON no está disponible, responde `502 Bad Gateway`.

Los errores usan siempre el siguiente formato:

```json
{
   "timestamp": "2026-09-17T20:00:00",
   "status": 400,
   "mensaje": "Error de validación",
   "detalles": ["nota: La nota no puede estar vacía"]
}
```

## Pruebas rápidas

El archivo [requests.http](requests.http) contiene casos de éxito y error para ambos recursos. Puede ejecutarse desde VS Code con la extensión REST Client, o copiarse a Postman/Insomnia.

Ejemplos mínimos:

```bash
curl http://localhost:8080/api/productos

curl -X POST http://localhost:8080/api/favoritos \
   -H "Content-Type: application/json" \
   -d '{"productoId":1,"nota":"Producto recomendado"}'

curl -X POST http://localhost:8080/api/favoritos \
   -H "Content-Type: application/json" \
   -d '{"productoId":1,"nota":""}'
```

## Tests

Para compilar y ejecutar las pruebas:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

## Puertos y adaptadores: qué cambió en el TP2 (punto 4)

### Clases nuevas
- `FavoritoEntity`: entidad JPA que representa la tabla `favoritos`.
- `FavoritoJpaRepository`: interfaz de Spring Data que se encarga de las consultas a PostgreSQL.
- `FavoritoRepositoryAdapter`: implementa el puerto `FavoritoRepository`, convierte entre
  `Favorito` (dominio) y `FavoritoEntity`, y delega en `FavoritoJpaRepository`.

### Clase eliminada
- `InMemoryFavoritoRepository`: la versión del TP1 que guardaba los favoritos en un `Map`.

### Clases que quedaron exactamente iguales
- `Favorito`
- `FavoritoRepository` 
- `FavoritoService`
- `FavoritoController`
- Los DTOs de favoritos

### Por qué fue posible
`FavoritoRepository` es un puerto: define qué operaciones existen (guardar, buscar,
borrar) pero no cómo se implementan. El service solo depende de ese contrato, no de una
implementación concreta. Tanto `InMemoryFavoritoRepository` como
`FavoritoRepositoryAdapter` son adapters: implementaciones concretas de
infraestructura que se pueden intercambiar detrás de la misma interfaz. Por eso pasar de
memoria a PostgreSQL no obligó a modificar el dominio, el service ni el controller.

## Evolución del esquema (punto 6)

## Evolución del esquema

Para hacer obligatorio `lista_id` usé una migración nueva (`V4`) y no modifiqué V1, V2 ni V3, porque Flyway guarda un checksum de cada migración ya aplicada y falla si el archivo cambia.Esas migraciones ya corrieron en otras bases, así que editarlas dejaría los esquemas distintos entre sí.

### Justificación de Transacciones (Punto 7.3)

Si elimináramos la anotación `@Transactional` del método `moverFavoritosYEliminarLista` y ocurriera un fallo a mitad de la ejecución (por ejemplo, después de reasignar los favoritos pero justo antes de borrar la lista origen):

* **Inconsistencia en la base de datos:** Los favoritos habrían migrado a la nueva lista destino, pero la lista origen seguiría existiendo activa y vacía en la base de datos.
* **Violación de la Atomicidad (Propiedades ACID):** La propiedad de Atomicidad establece que una unidad de trabajo compuesta por múltiples operaciones debe ejecutarse de forma "todo o nada". Sin `@Transactional`, las operaciones parciales quedan confirmadas (*committed*) individualmente. Con `@Transactional`, si cualquier paso falla, Spring/JPA ejecuta un *rollback* completo regresando la base de datos a su estado inicial.

### Instrucciones para levantar PostgreSQL y ejecutar la aplicación

### Requisitos previos
- Docker Desktop instalado y en funcionamiento (o una instalación local de PostgreSQL 16).
- Java 26 (o compatible) y Maven.

### Pasos de ejecución:

#### Opción A: Con Docker (Recomendado)
1. Iniciar la base de datos PostgreSQL ejecutando en la raíz del proyecto:
   ```bash
   docker compose up -d

Ejecutar la aplicación Spring Boot:

En Windows (PowerShell):

PowerShell
.\mvnw.cmd spring-boot:run
En Linux / macOS / Git Bash:

Bash
./mvnw spring-boot:run


### Opción B: Instalación local de PostgreSQL
Asegurarse de tener el servicio de PostgreSQL corriendo en el puerto 5432.

Crear una base de datos llamada app_db.

Verificar que las credenciales coincidan con las configuradas en src/main/resources/application.properties:

Properties
spring.datasource.url=jdbc:postgresql://localhost:5432/app_db
spring.datasource.username=postgres
spring.datasource.password=postgres
Iniciar la aplicación ejecutando .\mvnw.cmd spring-boot:run o ./mvnw spring-boot:run.

### Confirmación de las Migraciones de Flyway
Al iniciar la aplicación, Flyway aplicará automáticamente todos los scripts ubicados en src/main/resources/db/migration/.

A. Confirmación por logs de consola
En la consola de ejecución de Spring Boot deben figurar líneas similares a:

Plaintext
INFO --- org.flywaydb.core.FlywayExecutor          : Database: jdbc:postgresql://localhost:5432/app_db (PostgreSQL 16)
INFO --- o.f.core.internal.command.DbMigrate       : Migrating schema "public" to version "1 - create favoritos"
INFO --- o.f.core.internal.command.DbMigrate       : Migrating schema "public" to version "2 - create listas"
INFO --- o.f.core.internal.command.DbMigrate       : Migrating schema "public" to version "3 - add lista id a favoritos"
INFO --- o.f.core.internal.command.DbMigrate       : Migrating schema "public" to version "4 - lista id obligatorio"
INFO --- o.f.core.internal.command.DbMigrate       : Successfully applied 4 migrations to schema "public", now at version v4
B. Consulta en la base de datos (flyway_schema_history)
Para verificar la tabla de historial generada por Flyway, ejecutar en la terminal:

Con Docker:

Bash
docker exec -it tp2-postgres psql -U postgres -d app_db -c "SELECT installed_rank, version, description, success FROM flyway_schema_history;"
Con PostgreSQL local:

Bash
psql -U postgres -d app_db -c "SELECT installed_rank, version, description, success FROM flyway_schema_history;"
Todas las migraciones (V1 a V4) deben figurar registradas con success = true (o t).