# ProyectoMultimedia

Gestor de archivos multimedia basado en microservicios. Los usuarios suben imágenes, vídeos, audios y documentos; un servicio en segundo plano genera miniaturas y extrae metadatos, y la web permite organizarlos en carpetas, etiquetarlos, buscarlos y previsualizarlos.

## Arquitectura

```
                    ┌──────────────────── nginx (:80) ────────────────────┐
                    │  /auth/*  → auth-service   /api/*  → app-service    │
                    │  /*       → web                                     │
                    └─────────────────────────────────────────────────────┘
                          │                  │                  │
                   ┌──────▼──────┐    ┌──────▼──────┐    ┌──────▼──────┐
                   │ auth-service│    │ app-service │    │     web     │
                   │    :8081    │◄───│    :8080    │    │    :3000    │
                   └──────┬──────┘JWKS└──┬───────┬──┘    └─────────────┘
                          │              │       │ media.uploaded ▲
                          ▼              ▼       ▼                │ media.processed
                     ┌──────────────────────┐ ┌──────────┐ ┌──────┴──────────┐
                     │      PostgreSQL      │ │ RabbitMQ │◄┤optimizer-service│
                     └──────────────────────┘ └──────────┘ │      :8082      │
                                                           └─────────────────┘
                     app-service y optimizer-service comparten ./storage (/data)
```

| Servicio | Stack | Responsabilidad |
|---|---|---|
| `auth-service` | Spring Boot 3, Java 21, JPA, Flyway | Registro y login (bcrypt). Emite JWT firmados con RSA y publica la clave pública en `/.well-known/jwks.json`. |
| `app-service` | Spring Boot 3, Java 21, JPA, Flyway, Tika | API principal: subida, carpetas, etiquetas, búsqueda avanzada, descarga de originales y derivados, borrado. Valida los JWT contra el JWKS de `auth-service`. |
| `optimizer-service` | Spring Boot 3, Java 21, ffmpeg, Thumbnailator | Consume `media.uploaded`, genera miniaturas y extrae metadatos (EXIF, duración, códecs, artista/álbum, nº de páginas…) y publica `media.processed`. Los fallos van a una DLQ. |
| `web` | React 18, Vite, Tailwind, React Query, Zustand | Explorador de archivos con visores de imagen, vídeo, audio (wavesurfer.js) y PDF. |
| `nginx` | nginx:alpine (compose aparte) | Proxy inverso por dominio y por ruta. |

Los tres servicios Java siguen **arquitectura hexagonal**:

```
domain/          modelo, excepciones y puertos (port/in = casos de uso, port/out = dependencias)
application/     implementación de los casos de uso
infrastructure/  adaptadores: in/web, in/messaging, out/persistence, out/storage, config
```

### Flujo de una subida

1. La web envía `POST /api/files` (multipart) con el JWT.
2. `app-service` detecta el MIME con Tika, guarda el original en `storage/{ownerId}/{fileId}/original.{ext}`, crea el registro con estado `PROCESSING` y publica `media.uploaded`.
3. `optimizer-service` procesa el archivo, escribe los derivados en `storage/{ownerId}/{fileId}/thumbnails/` y publica `media.processed`.
4. `app-service` guarda metadatos y miniaturas y marca el archivo como `READY` (o `FAILED`).

## Requisitos

- Docker y Docker Compose v2
- (Opcional, para desarrollo local sin Docker) Java 21, Maven 3.9, Node 20

## Puesta en marcha

```bash
# 1. Variables de entorno
cp .env.example .env        # y edita las contraseñas

# 2. Stack principal (postgres, rabbitmq, servicios y web)
docker compose up --build -d

# 3. Proxy nginx (proyecto separado; necesita la red proxy creada en el paso 2)
cd nginx
docker compose up -d
```

> La red externa se llama `proyectomultimedia_proxy` porque Compose usa el nombre de la carpeta raíz como prefijo. Si clonas el repo en una carpeta con otro nombre, ajusta `name:` en `nginx/docker-compose.yml`.

### Dominios locales

nginx enruta por `server_name`. Añade estas líneas al archivo hosts (`C:\Windows\System32\drivers\etc\hosts` en Windows, `/etc/hosts` en Linux/macOS):

```
127.0.0.1  mi-domain.com www.mi-domain.com auth.mi-domain.com back.mi-domain.com
```

| URL | Destino |
|---|---|
| http://mi-domain.com | Aplicación web (`/auth/*` y `/api/*` se enrutan a los backends) |
| http://auth.mi-domain.com | `auth-service` directo |
| http://back.mi-domain.com | `app-service` directo |
| http://localhost:15672 | Consola de RabbitMQ (`RABBITMQ_USER` / `RABBITMQ_PASSWORD`) |
| `localhost:5432` | PostgreSQL (`POSTGRES_USER` / `POSTGRES_PASSWORD`) |

Las secciones de TLS de `nginx/nginx.conf` están comentadas e incluyen los comandos para generar certificados autofirmados o usar certbot.

## Variables de entorno

| Variable | Usada por | Descripción |
|---|---|---|
| `POSTGRES_DB` | postgres, auth, app | Nombre de la base de datos |
| `POSTGRES_USER` | postgres, auth, app | Usuario de PostgreSQL |
| `POSTGRES_PASSWORD` | postgres, auth, app | Contraseña de PostgreSQL |
| `RABBITMQ_USER` | rabbitmq, app, optimizer | Usuario de RabbitMQ |
| `RABBITMQ_PASSWORD` | rabbitmq, app, optimizer | Contraseña de RabbitMQ |

`docker-compose.yml` fija además `JWKS_URI` y `STORAGE_PATH=/data`. Fuera de Docker, los servicios aceptan también `POSTGRES_HOST`, `POSTGRES_PORT`, `RABBITMQ_HOST` y `RABBITMQ_PORT` (por defecto `localhost` y los puertos estándar).

## API

Todas las rutas de `app-service` requieren `Authorization: Bearer <jwt>`. A través de nginx llevan el prefijo `/api` (se elimina antes de llegar al servicio).

**auth-service**

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/register` | Crear cuenta |
| POST | `/auth/login` | Obtener JWT |
| GET | `/.well-known/jwks.json` | Clave pública para validar tokens |

**app-service**

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/files` | Subir archivo (`file`, `folderId` opcional) |
| GET | `/files/{id}` | Detalle con metadatos y derivados |
| GET | `/files/{id}/original` | Descargar original |
| GET | `/files/{id}/derivative/{label}` | Descargar derivado (p. ej. `thumb_512`) |
| DELETE | `/files/{id}` | Borrar archivo y su carpeta en disco |
| GET | `/files/search` | Búsqueda paginada (ver abajo) |
| POST · GET | `/folders` | Crear / listar carpetas |
| PATCH | `/folders/{id}/name` | Renombrar carpeta |
| PATCH | `/folders/{id}/parent` | Mover carpeta |
| DELETE | `/folders/{id}` | Borrar carpeta |
| POST · GET | `/tags` | Crear / listar etiquetas |
| GET · POST | `/files/{fileId}/tags` | Ver / asignar etiquetas de un archivo |
| DELETE | `/files/{fileId}/tags/{tagId}` | Quitar etiqueta |

Filtros de `/files/search`:

- **Genéricos:** `name`, `fileType` (`IMAGE`, `VIDEO`, `AUDIO`, `DOCUMENT`), `fromDate`, `toDate`, `folderId`, `tags`
- **Imagen:** `mobileCamera`, `minWidth`, `minHeight`, `takenFrom`, `takenTo`
- **Vídeo:** `minDuration`, `maxDuration`, `videoCodec`, `minVideoWidth`
- **Audio:** `artist`, `album`
- **Documento:** `minPageCount`, `pdfAuthor`
- **Paginación:** `page` (0), `size` (20), `sortBy` (`createdAt`), `sortDir` (`desc`)

## Desarrollo local

Levanta solo la infraestructura con Docker y ejecuta los servicios desde el IDE o la terminal:

```bash
docker compose up -d postgres rabbitmq
```

> `docker-compose.yml` no publica el puerto 5672 de RabbitMQ. Para conectar servicios que corren fuera de Docker, añade `- "5672:5672"` a `rabbitmq.ports` en local.

```bash
# En terminales separadas
cd auth-service      && mvn spring-boot:run
cd app-service       && STORAGE_PATH=../storage mvn spring-boot:run
cd optimizer-service && STORAGE_PATH=../storage mvn spring-boot:run   # necesita ffmpeg en el PATH

cd web && npm install && npm run dev
```

El esquema de base de datos lo gestiona Flyway (`src/main/resources/db/migration`); Hibernate solo valida (`ddl-auto: validate`).

## Estructura del repositorio

```
auth-service/        autenticación y emisión de JWT
app-service/         API principal
optimizer-service/   procesamiento de medios
web/                 frontend React
nginx/               proxy inverso (compose independiente)
storage/             archivos subidos y derivados (ignorado por git)
docker-compose.yml   stack principal
.env.example         plantilla de variables de entorno
```
