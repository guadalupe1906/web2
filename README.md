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