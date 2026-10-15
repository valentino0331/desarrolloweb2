# EcoTechTrack REST API — Avance de Portafolio Final 2 (APF2)

**Estudiante:** Valentino Martin Marca Quedena  
**Curso:** Desarrollo Web II  
**Proyecto:** **EcoTechTrack** — Trazabilidad y Fiscalización del Ciclo de Vida de RAEE (D.S. N° 009-2019-MINAM)  
**Tecnologías:** Spring Boot 3.3.4, Java 21, Spring Data JPA / Hibernate, Spring Security 6, JWT (JJWT 0.12.6), H2 Database / MySQL.

---

## 🌿 ¿Qué es EcoTechTrack?
**EcoTechTrack** es una solución web empresarial orientada a la trazabilidad y fiscalización del ciclo de vida de los **Residuos de Aparatos Eléctricos y Electrónicos (RAEE)**. En el Perú, conforme al **D.S. N° 009-2019-MINAM**, las entidades generadoras tienen la obligación legal y ambiental de garantizar que sus activos informáticos dados de baja sean entregados únicamente a **Empresas Operadoras de Residuos Sólidos (EO-RS)** autorizadas, evitando la contaminación por metales pesados (plomo, mercurio y cadmio) y asegurando su correcta valorización.

---

## 📌 Contenido del Repositorio

1. **`salvia-finanzas-api/`**: Proyecto backend completo en Spring Boot con arquitectura en capas (Controller, Service, Repository, Model, DTO, Security/JWT).
2. **`INFORME TÉCNICO Y GUÍA DE ENTREGA.docx`**: Documento oficial del avance con evidencias, capturas de prueba, diseño de base de datos y matriz de seguridad.
3. **[`EcoTechTrack_APF2_Postman.json`](./EcoTechTrack_APF2_Postman.json)**: Colección oficial de Postman lista para importar con los 12 casos de prueba de la rúbrica aplicados a RAEE.
4. **[`DIAGRAMAS.md`](./DIAGRAMAS.md)**: Diagramas técnicos de Entidad-Relación y Clases UML en formato nativo Mermaid con llaves primarias, foráneas y multiplicidad.

---

## 🚀 Requisitos y Ejecución

### Requisitos Previos
- **Java JDK 21** o superior instalado y configurado en el `PATH` / `JAVA_HOME`.
- **Maven 3.9+** (o usar el wrapper incluido `./mvnw`).

### Pasos para Ejecutar
1. Entrar a la carpeta del proyecto:
   ```bash
   cd salvia-finanzas-api
   ```
2. Compilar y empaquetar:
   ```bash
   mvn clean package -DskipTests
   ```
3. Iniciar la aplicación:
   ```bash
   mvn spring-boot:run
   ```
   *O ejecutar directamente el archivo script en Windows:*
   ```cmd
   INICIAR_SALVIA_API.bat
   ```
4. El servidor estará escuchando en: `http://localhost:8080`.
5. Consola H2 disponible en: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:salviadb`, Usuario: `sa`, Contraseña en blanco).

---

## 🔐 Credenciales Precargadas (Semilla / DataInitializer)

| Rol | Usuario | Contraseña | Perfil en EcoTechTrack |
|---|---|---|---|
| **ADMIN** | `admin` | `Admin123!*` | Fiscalizador del MINAM / OEFA con acceso a métricas globales de entidades. |
| **GENERADOR (USER)** | `valentino` | `User123!*` | Entidad generadora que registra bajas de activos RAEE y emite manifiestos a EO-RS. |
| **OPERADOR EO-RS (GUEST)**| `invitado` | `Guest123!*` | Operador autorizado de residuos con permisos de solo lectura. |

---

## 📋 Endpoints Principales y Rúbrica APF2 (EcoTechTrack RAEE)

### 1. Autenticación y Autorización (JWT)
- `POST /api/auth/register` — Registro de nueva entidad generadora (201 Created).
- `POST /api/auth/login` — Autenticación con generación de token Bearer JWT.

### 2. Trazabilidad de Activos RAEE Dados de Baja (`/api/expenses` -> `/api/raee-assets`)
- `GET /api/expenses` — Listar todos los activos RAEE dados de baja (Requiere Bearer Token).
- `POST /api/expenses` — Registrar baja de activo informático con metales pesados (201 Created).
- `GET /api/expenses/filter/category?category={cat}` — Consulta personalizada JPQL por categoría RAEE (ej: `TIC`).
- `GET /api/expenses/filter/high-value?minAmount={peso}` — Consulta JPQL filtrando activos de gran volumen/peso crítico (>= 15 kg).

### 3. Manifiestos de Entrega y Trazabilidad a EO-RS (`/api/missions` -> `/api/manifests`)
- `GET /api/missions` — Listar manifiestos de entrega formal a EO-RS (Requiere Bearer Token).
- `GET /api/missions/filter/near-deadline?days={dias}` — Consulta personalizada JPQL para manifiestos próximos al plazo límite legal del MINAM.

### 4. Seguridad y Control de Roles (`/api/users`)
- `GET /api/users` con Token de **ADMIN** (Fiscalizador) → `200 OK` (Permitido por `@PreAuthorize("hasRole('ADMIN')")`).
- `GET /api/users` con Token de **USER** (Generador) → `403 Forbidden` (Acceso denegado por jerarquía de roles).
- Solicitud sin cabecera `Authorization` → `401 Unauthorized`.
