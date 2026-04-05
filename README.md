# AI Article Extractor & Summarizer con Spring Boot

**Servicio de IA con Rapid API**, Article Extractor and Summarizer extrae el contenido de artículos a partir de una URL y genera un resumen usando GPT. El proceso es síncrono: se envía la URL del artículo y se recibe directamente el resumen generado. Los resultados se persisten en **PostgreSQL (Neon)**.

---

## Tecnologías y Versiones

### 1. Cognitive Services

- **RapidAPI** — [Article Extractor and Summarizer](https://rapidapi.com/restyler/api/article-extractor-and-summarizer)
- Extracción del cuerpo del artículo desde una URL
- Resumen automático del contenido usando **GPT**
- Motor de scraping: **ScrapeNinja** con proxies rotativos
- Tipo de proceso: **Síncrono** — URL → resumen generado

### 2. Spring Boot

| Herramienta        | Versión                                               |
|--------------------|-------------------------------------------------------|
| Java (JDK)         | **17**                                                |
| Spring Boot        | **3.5.13**                                            |
| Apache Maven       | **3.9+**                                              |
| IDE recomendado    | IntelliJ IDEA / Visual Studio Code / GitHub Codespace |

### 3. Base de Datos — PostgreSQL (Neon)

| Herramienta              | Detalle                                          |
|--------------------------|--------------------------------------------------|
| Base de datos            | **PostgreSQL**                                   |
| Proveedor cloud          | **Neon** (serverless PostgreSQL)                 |
| Driver reactivo          | **R2DBC** (Reactive Relational Database Connectivity) |
| URL de conexión          | `${NEON_R2DBC_URL}` (desde `.env`)               |

### 4. Dependencias Maven

| Dependencia                                  | Versión            | Propósito                              |
|----------------------------------------------|--------------------|----------------------------------------|
| `spring-boot-starter-webflux`                | 3.5.13 (BOM)       | Servidor reactivo no bloqueante        |
| `spring-boot-starter-data-r2dbc`             | 3.5.13 (BOM)       | Persistencia reactiva con R2DBC        |
| `r2dbc-postgresql`                           | **1.0.5.RELEASE**  | Driver R2DBC para PostgreSQL           |
| `postgresql`                                 | 42.7+ (BOM)        | Driver JDBC (para migraciones)         |
| `springdoc-openapi-starter-webflux-ui`       | **2.8.8**          | Documentación Swagger UI               |
| `spring-dotenv`                              | **4.0.0**          | Variables de entorno desde `.env`      |
| `lombok`                                     | 1.18+ (BOM)        | Reducción de boilerplate               |
| `reactor-test`                               | 3.6+ (BOM)         | Testing reactivo                       |

---

## Dependencias `pom.xml`

### Spring WebFlux + R2DBC (PostgreSQL Reactivo)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
    <version>1.0.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
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
    name: article-summarizer
  r2dbc:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

rapidapi:
  key: ${RAPIDAPI_KEY}
  host: article-extractor-and-summarizer.p.rapidapi.com
  base-url: https://article-extractor-and-summarizer.p.rapidapi.com

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
  api-docs:
    path: /api-docs

server:
  port: 8081
```

Las variables sensibles se leen desde un archivo `.env` en la raíz del proyecto.

### Ejemplo `.env`

```env
NEON_R2DBC_URL=r2dbc:postgresql://ep-xxx.us-east-2.aws.neon.tech/neondb?sslmode=require
NEON_USERNAME=tu_usuario_neon
NEON_PASSWORD=tu_password_neon

RAPIDAPI_KEY=tu_rapidapi_key_aqui
RAPIDAPI_HOST=article-extractor-and-summarizer.p.rapidapi.com
RAPIDAPI_BASE_URL=https://article-extractor-and-summarizer.p.rapidapi.com
```

---

## Flujo de la Aplicación

```
Cliente
  │
  ├─► POST /api/article/summarize        (URL del artículo + length)
  │       │
  │       └─► RapidAPI: extrae y resume el artículo con GPT
  │               │
  │               ├─► Devuelve el resumen generado
  │               └─► Guarda en PostgreSQL (Neon) con status = "completed"
  │
  ├─► POST /api/article/summarize-text   (texto plano)
  │       │
  │       └─► RapidAPI: resume texto directo con GPT
  │               └─► Guarda en PostgreSQL (Neon)
  │
  └─► GET /api/article/all
      GET /api/article/{id}
      GET /api/article/status/{status}
```

---

## Endpoints REST

| Método | Endpoint                         | Descripción                                         |
|--------|----------------------------------|-----------------------------------------------------|
| `POST` | `/api/article/summarize`         | Extrae y resume artículo a partir de una URL        |
| `POST` | `/api/article/summarize-text`    | Resume texto libre enviado en el body               |
| `GET`  | `/api/article/all`               | Lista todos los registros en PostgreSQL             |
| `GET`  | `/api/article/{id}`              | Busca registro por ID                               |
| `GET`  | `/api/article/status/{status}`   | Filtra por estado: `pending`, `completed`, `failed` |

### Ejemplo: Resumir por URL

```
POST /api/article/summarize
Content-Type: application/json

{
  "url": "https://time.com/6266679/musk-ai-open-letter/",
  "length": 3
}
```

### Ejemplo: Resumir texto directo

```
POST /api/article/summarize-text
Content-Type: application/json

{
  "text": "Artificial intelligence is transforming industries worldwide..."
}
```

### Ejemplo: Respuesta exitosa

```json
{
  "id": 1,
  "sourceUrl": "https://time.com/6266679/musk-ai-open-letter/",
  "summary": "Elon Musk and other AI leaders signed an open letter calling for a pause in AI development...",
  "summaryLength": 3,
  "status": "completed",
  "createdAt": "2025-01-15T10:30:00"
}
```

---

## Modelo — Tabla `article_summary` (PostgreSQL)

| Campo            | Tipo          | Descripción                                       |
|------------------|---------------|---------------------------------------------------|
| `id`             | BIGSERIAL PK  | ID autoincremental                                |
| `source_url`     | TEXT          | URL del artículo fuente                           |
| `input_text`     | TEXT          | Texto de entrada (si se usa summarize-text)       |
| `summary`        | TEXT          | Resumen generado por GPT                          |
| `summary_length` | INTEGER       | Cantidad de oraciones del resumen                 |
| `error_msg`      | VARCHAR(500)  | Mensaje de error si aplica                        |
| `status`         | VARCHAR(20)   | `pending`, `completed`, `failed`                  |
| `created_at`     | TIMESTAMP     | Fecha y hora de creación                          |

### Script SQL de creación

```sql
CREATE TABLE IF NOT EXISTS article_summary (
    id             BIGSERIAL PRIMARY KEY,
    source_url     TEXT,
    input_text     TEXT,
    summary        TEXT,
    summary_length INTEGER,
    error_msg      VARCHAR(500),
    status         VARCHAR(20) NOT NULL DEFAULT 'pending',
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);
```

---

## Estructura del Proyecto

```
src/
└── main/
    ├── java/com/ai/articlesummarizer/
    │   ├── config/
    │   │   ├── WebClientConfig.java         # Configuración del WebClient reactivo
    │   │   ├── R2dbcConfig.java             # Configuración de conexión R2DBC / Neon
    │   │   └── OpenApiConfig.java           # Configuración de Swagger / OpenAPI
    │   ├── controller/
    │   │   └── ArticleController.java       # Endpoints REST
    │   ├── model/
    │   │   └── ArticleSummary.java          # Entidad R2DBC (@Table)
    │   ├── repository/
    │   │   └── ArticleRepository.java       # ReactiveCrudRepository
    │   ├── service/
    │   │   └── ArticleService.java          # Lógica de negocio + llamada a RapidAPI
    │   └── dto/
    │       ├── SummarizeUrlRequestDto.java  # DTO entrada por URL
    │       ├── SummarizeTextRequestDto.java # DTO entrada por texto
    │       └── SummaryResponseDto.java      # DTO respuesta de RapidAPI
    └── resources/
        ├── application.yaml
        └── schema.sql                       # Script DDL ejecutado al iniciar
```

---

## Cómo Ejecutar

### Requisitos previos

- Java 17 instalado
- Maven 3.9+
- Cuenta en [Neon](https://neon.tech) con base de datos PostgreSQL creada
- Cuenta en [RapidAPI](https://rapidapi.com) con suscripción a la API (plan Free: 50 requests/mes)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/ai-article-summarizer.git
cd ai-article-summarizer

# 2. Crear el archivo .env en la raíz con las variables requeridas

# 3. Compilar el proyecto
mvn clean install

# 4. Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081`

---

## Documentación Swagger

Una vez levantado el servicio, accede a la UI interactiva en:

```
http://localhost:8081/webjars/swagger-ui/index.html
```

---

## RapidAPI — Article Extractor and Summarizer

| Propiedad          | Valor                                                                                  |
|--------------------|----------------------------------------------------------------------------------------|
| Proveedor          | restyler                                                                               |
| Motor de scraping  | ScrapeNinja con proxies rotativos                                                      |
| Motor de resumen   | GPT                                                                                    |
| Tipo de proceso    | Síncrono                                                                               |
| Plan gratuito      | 50 requests / mes                                                                      |
| Host               | `article-extractor-and-summarizer.p.rapidapi.com`                                     |
| Endpoint por URL   | `GET /summarize?url={url}&length={n}`                                                  |
| Endpoint por texto | `POST /summarize-text` — body JSON `{"text": "..."}`                                  |
| Documentación      | [Ver en RapidAPI](https://rapidapi.com/restyler/api/article-extractor-and-summarizer) |