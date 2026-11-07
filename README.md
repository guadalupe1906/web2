# TP · API REST con Spring Boot - Catálogo de Productos y Favoritos

API REST desarrollada con **Java 26** y **Spring Boot**, que expone un catálogo de productos de consumo externo y gestión de favoritos con persistencia relacional, control de migraciones y despliegue contenerizado.

---

## 🚀 Tecnologías Utilizadas
* **Java 26**
* **Spring Boot** (Spring Data JPA, Web, Validation)
* **Maven** (Gestión de dependencias y empaquetado)
* **PostgreSQL & Flyway** (Base de datos y control de versiones de esquemas)
* **Docker & Docker Compose** (Contenerización)
* **GitHub Actions** (CI/CD Automatizado)
* **Render** (Despliegue en la nube)
* **Springdoc OpenAPI / Swagger** (Documentación interactiva)

---

## 📋 Cómo Levantar el Proyecto

### Opción A: Con Docker Compose (Recomendado para Producción / Local completo)
1. Clonar el repositorio y ubicarse en la raíz.
2. Iniciar la base de datos y servicios:
   ```bash
   docker compose up -d
Ejecutar la aplicación Spring Boot:

Windows (PowerShell): .\mvnw.cmd spring-boot:run

Linux / macOS / Git Bash: ./mvnw spring-boot:run

Opción B: Ejecución Local
Asegurarse de tener corriendo una instancia de PostgreSQL en el puerto 5432 con una base de datos llamada app_db.

Verificar las credenciales en src/main/resources/application.properties.

Levantar la aplicación:

Bash
./mvnw spring-boot:run
La aplicación quedará disponible en http://localhost:8080.

🧪 Ejecución de Tests
Para compilar el proyecto y ejecutar la suite completa de pruebas unitarias e integración de forma local:

En Linux / macOS / Git Bash:

Bash
./mvnw test
En Windows (PowerShell):

PowerShell
.\mvnw.cmd test
Los resultados de la ejecución se mostrarán en la consola detallando los tests pasados, fallidos o ignorados.

🔄 Integración Continua (CI con GitHub Actions)
El repositorio cuenta con un pipeline automatizado de GitHub Actions configurado para compilar el proyecto y correr las pruebas ante cada cambio o push en la rama de trabajo.

Cómo leer el resultado de CI:
Dirigite a tu repositorio en GitHub y hacé clic en la pestaña Actions.

Seleccioná el último workflow ejecutado asociado al commit reciente.

Indicadores de estado:

🟢 Check verde (Success): El código compila correctamente en Java 26, las dependencias bajaron bien y todas las pruebas pasaron de forma exitosa.

🔴 Cruz roja (Failure): Hubo un error de compilación, una falla en las pruebas unitarias o problemas de configuración. Al hacer clic sobre el job fallido, podrás expandir los logs paso a paso para identificar la línea exacta del error.

☁️ Pasos de Despliegue en Render (Producción)
Dado que Render no cuenta con un runtime nativo para JVM/Java, la aplicación se despliega empaquetada dentro de un contenedor Docker. Los pasos seguidos para el despliegue fueron:

Creación del Dockerfile: Se configuró un build multi-etapa en la raíz del repositorio utilizando la imagen oficial de Eclipse Temurin (eclipse-temurin:26-jdk para compilar con Maven y eclipse-temurin:26-jre para la ejecución ligera del .jar).

Creación del Servicio Web en Render:

Se conectó el repositorio de GitHub y se seleccionó la rama correspondiente (tp3-starter).

En el tipo de entorno (Environment/Runtime), se seleccionó explícitamente Docker.

Configuración de Variables de Entorno: En el panel de Render se configuraron las variables críticas de producción:

SPRING_DATASOURCE_URL (con el prefijo obligatorio jdbc:postgresql://...).

SPRING_DATASOURCE_USERNAME y SPRING_DATASOURCE_PASSWORD.

SPRING_FLYWAY_ENABLED=true para garantizar la ejecución automática de las migraciones en la base de datos remota.

JWT_SECRET (clave generada por criptografía segura).

Despliegue y Compilación: Mediante un Manual Deploy (o despliegue automático por push), Render lee el Dockerfile, construye la imagen en su infraestructura y levanta el servicio web expuesto públicamente.

📚 Documentación de la API (Swagger UI)
Con la app levantada, podés explorar y probar todos los endpoints de forma interactiva:

Swagger UI: http://localhost:8080/swagger-ui/index.html (o tu URL de Render /swagger-ui/index.html)

Especificación OpenAPI: http://localhost:8080/v3/api-docs

Swagger muestra agrupados los endpoints de productos y favoritos.

🔌 Endpoints Principales
Salud
GET /api/health: Verifica que la aplicación esté funcionando.

Productos
GET /api/productos: Obtiene el catálogo desde DummyJSON y lo transforma al DTO propio.

GET /api/productos/{id}: Obtiene un producto por su identificador.

Favoritos
POST /api/favoritos: Crea un favorito y responde 201 Created.

GET /api/favoritos: Lista todos los favoritos (200 OK).

GET /api/favoritos/{id}: Obtiene un favorito específico (200 OK).

PUT /api/favoritos/{id}: Actualiza un favorito (200 OK).

DELETE /api/favoritos/{id}: Elimina un favorito (204 No Content).

Cuerpo esperado para crear o actualizar un favorito:

JSON
{
   "productoId": 1,
   "nota": "Producto recomendado"
}
⚠️ Validación y Manejo de Errores
La API valida productoId y nota. Ante solicitudes inválidas responde 400 Bad Request. Si el recurso no existe, responde 404 Not Found.

Formato estándar de errores:

JSON
{
   "timestamp": "2026-11-07T20:00:00",
   "status": 400,
   "mensaje": "Error de validación",
   "detalles": ["nota: La nota no puede estar vacía"]
}
🏛️ Decisiones de Arquitectura (Puertos y Adaptadores)
Clases de Infraestructura nuevas: FavoritoEntity, FavoritoJpaRepository, y FavoritoRepositoryAdapter (implementa el puerto de persistencia conectándose a PostgreSQL).

Independencia del Dominio: FavoritoRepository actúa como puerto (define operaciones sin acoplarlas a una tecnología). Gracias a esto, la transición desde almacenamiento en memoria (TP1) hacia una base de datos relacional (TP2/TP3) no requirió modificar las reglas de negocio, los servicios ni los controladores.

🔒 Dónde vive la lógica de autorización (Consigna 4.5)
La lógica de autorización y control de acceso (verificar permisos sobre los recursos) reside en la capa de servicios de aplicación (Service), desacoplada de los adaptadores web o de infraestructura. Esto garantiza que las reglas de negocio y de seguridad sean agnósticas al protocolo HTTP, facilitando su mantenimiento y testeo unitario.

🔄 Evolución del Esquema y Flyway
El versionado de la base de datos se maneja mediante Flyway (V1 a V4).

Para hacer obligatorio lista_id se implementó una migración nueva (V4) en lugar de alterar scripts anteriores, garantizando la integridad de los entornos ya migrados.

Justificación de Transaccionalidad (@Transactional)
El uso de @Transactional en operaciones complejas garantiza la Atomicidad (propiedades ACID). Si ocurriera un fallo a mitad de una reasignación masiva o eliminación en cascada, Spring ejecuta un rollback completo, evitando estados inconsistentes o registros huérfanos en la base de datos.
