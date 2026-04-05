# AI Text-to-Image Generator con Spring Boot

**Servicio de IA con Rapid API**, AI Text-to-Image Generator convierte texto (prompts) en imágenes usando inteligencia artificial mediante el modelo **Flux**. El proceso es síncrono: se envía un prompt y se recibe directamente la URL de la imagen generada. Los resultados se persisten en MongoDB.

---

## Tecnologías y Versiones

### 1. Cognitive Services

- **RapidAPI** — [AI Text to Image Generator Flux Free API](https://rapidapi.com/poorav925/api/ai-text-to-image-generator-flux-free-api)
- Generación de imágenes a partir de texto (prompt) con IA
- Modelo: **Flux** (generación de imágenes de alta calidad)
- Procesamiento síncrono: prompt → URL de imagen generada

### 2. Spring Boot

| Herramienta | Versión |
|-------------|---------|
| Java (JDK) | **17** |
| Spring Boot | **3.5.13** |
| Apache Maven | **3.9+** |
| IDE recomendado | IntelliJ IDEA / Visual Studio Code / GitHub Codespace |

### 3. Dependencias Maven

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `spring-boot-starter-webflux` | 3.5.13 (BOM) | Servidor reactivo no bloqueante |
| `spring-boot-starter-data-mongodb-reactive` | 3.5.13 (BOM) | Persistencia reactiva en MongoDB |
| `springdoc-openapi-starter-webflux-ui` | **2.8.8** | Documentación Swagger UI |
| `spring-dotenv` | **4.0.0** | Variables de entorno desde `.env` |
| `lombok` | 1.18+ (BOM) | Reducción de boilerplate |
| `reactor-test` | 3.6+ (BOM) | Testing reactivo |

---

## Dependencias `pom.xml`

### Spring WebFlux + MongoDB Reactivo

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Swagger para Spring WebFlux

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.8.8</version>
</dependency>
```

### Variables de entorno (spring-dotenv)

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Lombok

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## Configuración — `application.yaml`

```yaml
spring:
  application:
    name: imagen-generator
  data:
    mongodb:
      uri: ${MONGO_URI}
      database: ${MONGO_DATABASE}

rapidapi:
  key: ${RAPIDAPI_KEY}
  host: ai-text-to-image-generator-flux-free-api.p.rapidapi.com
  base-url: https://ai-text-to-image-generator-flux-free-api.p.rapidapi.com
  endpoint-stable-diffusion: /aaaaaaaaaaaaaaaaaiimagegenerator/quick.php

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
  api-docs:
    path: /api-docs

server:
  port: 8080
```

Las variables sensibles se leen desde un archivo `.env` en la raíz del proyecto.

---

## Flujo de la Aplicación

```
Cliente
  │
  └─► POST /api/image/generate   (prompt + parámetros opcionales)
          │
          └─► RapidAPI Flux: genera imagen con IA
                  │
                  ├─► Devuelve URL de imagen generada
                  │
                  └─► Guarda en MongoDB con status = "completed"
                              │
                              └─► GET /api/image/all
                                  GET /api/image/{id}
                                  GET /api/image/status/{status}
```

---

## Endpoints REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/image/generate` | Envía un prompt y genera una imagen con IA |
| `GET` | `/api/image/all` | Lista todos los documentos en MongoDB |
| `GET` | `/api/image/{id}` | Busca documento por ID de MongoDB |
| `GET` | `/api/image/status/{status}` | Filtra por estado: `pending`, `completed`, `failed` |

### Ejemplo: Generar imagen

```
POST /api/image/generate
Content-Type: application/json

{
  "prompt": "A futuristic city at sunset with flying cars and neon lights",
  "width": 1024,
  "height": 1024
}
```

### Ejemplo: Respuesta exitosa

```json
{
  "id": "64f3a1b2c3d4e5f6a7b8c9d0",
  "prompt": "A futuristic city at sunset with flying cars and neon lights",
  "imageUrl": "https://cdn.example.com/generated/abc123.png",
  "status": "completed",
  "createdAt": "2025-01-15T10:30:00"
}
```

---

## Modelo MongoDB — `ImageResult`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | String | ID del documento (MongoDB) |
| `prompt` | String | Texto de entrada para generar la imagen |
| `width` | Integer | Ancho de la imagen generada |
| `height` | Integer | Alto de la imagen generada |
| `imageUrl` | String | URL de la imagen generada por RapidAPI |
| `errorCode` | Integer | Código de error (0 = sin error) |
| `errorMsg` | String | Mensaje de error si aplica |
| `status` | String | `pending`, `completed`, `failed` |
| `createdAt` | LocalDateTime | Fecha y hora de creación |

---

## Estructura del Proyecto

```
src/
└── main/
    └── java/com/ai/imagegenerator/
        ├── config/
        │   ├── WebClientConfig.java       # Configuración del WebClient reactivo
        │   └── OpenApiConfig.java         # Configuración de Swagger/OpenAPI
        ├── controller/
        │   └── ImageController.java       # Endpoints REST
        ├── model/
        │   └── ImageResult.java           # Documento MongoDB (@Document)
        ├── repository/
        │   └── ImageRepository.java       # ReactiveCrudRepository
        ├── service/
        │   └── ImageService.java          # Lógica de negocio + llamada a RapidAPI
        └── dto/
            ├── ImageRequestDto.java       # DTO de entrada (prompt + parámetros)
            └── ImageResponseDto.java      # DTO de respuesta de RapidAPI
```

---

## Cómo Ejecutar

### Requisitos previos

- Java 17 instalado
- Maven 3.9+
- MongoDB Atlas o local
- Cuenta en [RapidAPI](https://rapidapi.com) con suscripción a la API

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/ai-image-generator.git
cd ai-image-generator

# 2. Crear el archivo .env en la raíz con las variables requeridas

# 3. Compilar el proyecto
mvn clean install

# 4. Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

---

## Documentación Swagger

Una vez levantado el servicio, accede a la UI interactiva en:

```
http://localhost:8080/swagger-ui/index.html
```

---

## RapidAPI — AI Text to Image Generator Flux

| Propiedad | Valor |
|-----------|-------|
| Proveedor | poorav925 |
| API | AI Text to Image Generator Flux Free API |
| Modelo IA | Flux |
| Tipo de proceso | Síncrono |
| Formato de entrada | JSON con campo `prompt` |
| Formato de salida | URL de imagen generada |
| Host | `ai-text-to-image-generator-flux-free-api.p.rapidapi.com` |
| Documentación | [Ver en RapidAPI](https://rapidapi.com/poorav925/api/ai-text-to-image-generator-flux-free-api) |  