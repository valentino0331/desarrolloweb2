# 📐 Diagramas Oficiales EcoTechTrack (APF1 / APF2 - UTP)
### Sistema de Trazabilidad y Fiscalización de RAEE (D.S. N° 009-2019-MINAM)

Este documento contiene los códigos exactos en **Mermaid** que coinciden al 100% con las entidades, atributos y relaciones de tu **Informe de Word oficial**. Puedes previsualizarlos en Markdown o copiarlos directamente a tu informe.

---

## 1. 📌 Diagrama Conceptual del Modelo de Dominio (Para Sección 1.4 del Word)

Representa la cardinalidad de la arquitectura base:
- Una **Empresa Gestora** atiende múltiples residuos `(1:N)`.
- Un **Almacén** custodia múltiples residuos `(1:N)`.
- Cada **Residuo Electrónico** posee una colección cronológica de eventos de trazabilidad `(1:N)`.

```mermaid
flowchart LR
    EG["<b>EMPRESA_GESTORA</b><br/><i>(Operador RAEE / EO-RS)</i>"]
    RE["<b>RESIDUO_ELECTRONICO</b><br/><i>(Activo Dado de Baja)</i>"]
    AL["<b>ALMACEN</b><br/><i>(Custodia Temporal)</i>"]
    HM["<b>HISTORIAL_MOVIMIENTO</b><br/><i>(Trazabilidad Inmutable)</i>"]

    EG ---|"(1) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (N)"| RE
    RE ---|"(N) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (1)"| AL
    RE ==>|"(1)<br/><br/>(N)"| HM

    style EG fill:#1e293b,stroke:#f59e0b,stroke-width:2px,color:#fff
    style RE fill:#1e293b,stroke:#3b82f6,stroke-width:2px,color:#fff
    style AL fill:#1e293b,stroke:#10b981,stroke-width:2px,color:#fff
    style HM fill:#1e293b,stroke:#8b5cf6,stroke-width:2px,color:#fff
```

---

## 2. 🏛️ Diagrama de Clases UML Formal (Para Sección 3.1 del Word)

Cajas con **3 compartimentos estándar** (Nombre, Atributos tipados con visibilidad `-`, y Métodos públicos `+`), junto con los `Enums`:

```mermaid
classDiagram
    direction TB

    class EstadoResiduo {
        <<enumeration>>
        DECLARADO_BAJA
        ALMACENADO_TEMPORAL
        SOLICITUD_RECOJO
        EN_TRANSITO
        DISPOSICION_FINAL
    }

    class CategoriaResiduo {
        <<enumeration>>
        EQUIPOS_INFORMATICA_Y_TELECOMUNICACIONES
        GRANDES_ELECTRODOMESTICOS
        PEQUENOS_ELECTRODOMESTICOS
        APARATOS_ALUMBRADO
    }

    class EmpresaGestora {
        <<Entity>>
        -Long id
        -String ruc
        -String razonSocial
        -String registroAutorizacion
        -String email
        -String telefono
        -String direccion
        -List~ResiduoElectronico~ residuos
        +getId() Long
        +getRuc() String
        +getRazonSocial() String
        +getRegistroAutorizacion() String
        +getEmail() String
        +getTelefono() String
        +getDireccion() String
        +getResiduos() List~ResiduoElectronico~
    }

    class Almacen {
        <<Entity>>
        -Long id
        -String codigo
        -String nombre
        -String sede
        -String direccion
        -Double capacidadKg
        -String responsable
        -List~ResiduoElectronico~ residuos
        +getId() Long
        +getCodigo() String
        +getNombre() String
        +getSede() String
        +getDireccion() String
        +getCapacidadKg() Double
        +getResponsable() String
        +getResiduos() List~ResiduoElectronico~
    }

    class ResiduoElectronico {
        <<Entity>>
        -Long id
        -String codigoIdentificacion
        -String descripcion
        -CategoriaResiduo categoria
        -String marca
        -String modelo
        -String numeroSerie
        -Double pesoKg
        -EstadoResiduo estado
        -String observaciones
        -LocalDateTime fechaRegistro
        -LocalDateTime fechaActualizacion
        -Almacen almacen
        -EmpresaGestora empresaGestora
        -List~HistorialMovimiento~ historialMovimientos
        +getId() Long
        +getCodigoIdentificacion() String
        +getDescripcion() String
        +getCategoria() CategoriaResiduo
        +getMarca() String
        +getModelo() String
        +getNumeroSerie() String
        +getPesoKg() Double
        +getEstado() EstadoResiduo
        +getAlmacen() Almacen
        +getEmpresaGestora() EmpresaGestora
        +getHistorialMovimientos() List~HistorialMovimiento~
    }

    class HistorialMovimiento {
        <<Entity>>
        -Long id
        -EstadoResiduo estadoAnterior
        -EstadoResiduo estadoNuevo
        -LocalDateTime fechaMovimiento
        -String responsable
        -String observaciones
        -ResiduoElectronico residuo
        +getId() Long
        +getEstadoAnterior() EstadoResiduo
        +getEstadoNuevo() EstadoResiduo
        +getFechaMovimiento() LocalDateTime
        +getResponsable() String
        +getObservaciones() String
        +getResiduo() ResiduoElectronico
    }

    %% Relaciones UML oficiales
    EmpresaGestora "1" <-- "0..*" ResiduoElectronico : atiende / recoge
    Almacen "1" <-- "0..*" ResiduoElectronico : custodia en
    ResiduoElectronico "1" *-- "1..*" HistorialMovimiento : auditoría (Cascade.ALL)
    ResiduoElectronico ..> EstadoResiduo : estado
    ResiduoElectronico ..> CategoriaResiduo : clasificado en
    HistorialMovimiento ..> EstadoResiduo : transiciona
```

---

## 3. 🗄️ Diagrama Entidad-Relación de Base de Datos (Relacional con PK / FK / UK)

Estructura SQL física generada por Hibernate:

```mermaid
erDiagram
    EMPRESAS_GESTORAS ||--o{ RESIDUOS_ELECTRONICOS : "atiende (empresa_gestora_id)"
    ALMACENES ||--o{ RESIDUOS_ELECTRONICOS : "custodia (almacen_id)"
    RESIDUOS_ELECTRONICOS ||--|{ HISTORIAL_MOVIMIENTOS : "registra (residuo_id)"

    EMPRESAS_GESTORAS {
        bigint id PK "Clave primaria autoincremental"
        varchar ruc UK "RUC 11 dígitos único"
        varchar razon_social "Razón Social EO-RS"
        varchar registro_autorizacion "N° Registro MINAM / DIGESA"
        varchar email "Correo de contacto"
        varchar telefono "Teléfono institucional"
        varchar direccion "Planta o sede legal"
    }

    ALMACENES {
        bigint id PK "Clave primaria autoincremental"
        varchar codigo UK "Código único (ej: ALM-TI-01)"
        varchar nombre "Nombre del Almacén"
        varchar sede "Sede Central - Sótano 1"
        varchar direccion "Dirección física"
        double_precision capacidad_kg "Capacidad máxima en kilogramos"
        varchar responsable "Custodio de depósito"
    }

    RESIDUOS_ELECTRONICOS {
        bigint id PK "Clave primaria autoincremental"
        varchar codigo_identificacion UK "Código patrimonial único (RAEE-2026-001)"
        varchar descripcion "Descripción técnica del equipo"
        varchar categoria "EQUIPOS_INFORMATICA_Y_TELECOMUNICACIONES"
        varchar marca "Marca del fabricante"
        varchar modelo "Modelo técnico"
        varchar numero_serie "Número de serie de fábrica"
        double_precision peso_kg "Peso neto en kg"
        varchar estado "DECLARADO_BAJA, EN_TRANSITO, etc."
        varchar observaciones "Detalles de estado"
        timestamp fecha_registro "Fecha de alta en el sistema"
        timestamp fecha_actualizacion "Último cambio"
        bigint almacen_id FK "Referencia a ALMACENES(id)"
        bigint empresa_gestora_id FK "Referencia a EMPRESAS_GESTORAS(id)"
    }

    HISTORIAL_MOVIMIENTOS {
        bigint id PK "Clave primaria autoincremental"
        varchar estado_anterior "Estado previo (nullable)"
        varchar estado_nuevo "Estado nuevo asumido"
        timestamp fecha_movimiento "Momento exacto del cambio"
        varchar responsable "Persona que autorizó el movimiento"
        varchar observaciones "Motivo o guía de remisión"
        bigint residuo_id FK "Referencia a RESIDUOS_ELECTRONICOS(id)"
    }
```

---

## 4. 🔄 Ciclo de Vida y Máquina de Estados del Residuo (Para Sección 3.3 del Word)

Representa el flujo inmutable de auditoría:

```mermaid
stateDiagram-v2
    [*] --> DECLARADO_BAJA : 1. Declara la baja técnica (Jefe TI / Resp. Ambiental)
    DECLARADO_BAJA --> ALMACENADO_TEMPORAL : 2. Ingreso físico al depósito y pesaje (Encargado Almacén)
    ALMACENADO_TEMPORAL --> SOLICITUD_RECOJO : 3. Se asigna Empresa Gestora EO-RS autorizada
    SOLICITUD_RECOJO --> EN_TRANSITO : 4. Entrega con Guía de Remisión al transportista
    EN_TRANSITO --> DISPOSICION_FINAL : 5. Acta y Certificado de Valorización / Disposición
    DISPOSICION_FINAL --> [*] : Ciclo completado y certificado legalmente
```

---

## 5. 🏗️ Arquitectura en Capas Spring Boot (Para Sección 2.2 del Word)

Inyección de dependencias por constructor sin `@Autowired`:

```mermaid
classDiagram
    direction TB

    %% Capa Controladores
    class ResiduoController {
        -ResiduoService residuoService
        +ResiduoController(ResiduoService)
        +getResiduo(Long id) ResponseEntity
        +createResiduo(ResiduoRequest) ResponseEntity
        +getHistorial(Long id) ResponseEntity
    }

    class AlmacenController {
        -AlmacenService almacenService
        +AlmacenController(AlmacenService)
        +getAllAlmacenes() ResponseEntity
    }

    class EmpresaGestoraController {
        -EmpresaGestoraService gestoraService
        +EmpresaGestoraController(EmpresaGestoraService)
        +getAllGestoras() ResponseEntity
    }

    %% Capa Servicios
    class ResiduoService {
        <<interface>>
        +findById(Long id) ResiduoResponse
        +create(ResiduoRequest req) ResiduoResponse
        +getHistorial(Long id) List~HistorialResponse~
    }

    class AlmacenService {
        <<interface>>
        +findAll() List~AlmacenResponse~
    }

    class EmpresaGestoraService {
        <<interface>>
        +findAll() List~EmpresaGestoraResponse~
    }

    %% Capa Repositorios
    class ResiduoRepository {
        <<interface>>
        +findByCodigoIdentificacion(String cod) Optional
    }

    class AlmacenRepository {
        <<interface>>
        +findByCodigo(String cod) Optional
    }

    class EmpresaGestoraRepository {
        <<interface>>
        +findByRuc(String ruc) Optional
    }

    class HistorialMovimientoRepository {
        <<interface>>
        +findByResiduoIdOrderByFechaMovimientoDesc(Long id) List
    }

    %% Inyecciones por constructor
    ResiduoController --> ResiduoService : inyecta
    AlmacenController --> AlmacenService : inyecta
    EmpresaGestoraController --> EmpresaGestoraService : inyecta

    ResiduoService --> ResiduoRepository : persiste
    ResiduoService --> HistorialMovimientoRepository : registra auditoría
    AlmacenService --> AlmacenRepository : persiste
    EmpresaGestoraService --> EmpresaGestoraRepository : persiste
```

---

## 6. 🌐 Diagrama de Arquitectura Global y Capas de Seguridad

```mermaid
flowchart TD
    CLIENT["<b>CLIENTES</b><br/><i>(Postman / Web / Mobile)</i>"]
    
    SEC["<b>SECURITY LAYER</b> <i>(Spring Security 6 + JJWT)</i><br/>• AuthTokenFilter (OncePerRequestFilter)<br/>• JwtUtils (Validación & Extracción de Claims)<br/>• UserDetailsServiceImpl (Carga Principal desde BD)"]
    
    CTRL["<b>CONTROLLERS LAYER</b> <i>(@RestController)</i><br/>• AuthController | ResiduoController<br/>• AlmacenController | EmpresaGestoraController"]
    
    SERV["<b>SERVICE LAYER</b> <i>(@Service + @Transactional)</i><br/>• Lógica de Negocio, Validaciones y Reglas de Negocio<br/>• Cambio de Estado Atómico y Trazabilidad Inmutable"]
    
    REPO["<b>PERSISTENCE LAYER</b> <i>(Spring Data JPA)</i><br/>• Repositorios con Métodos Derivados (@Repository)<br/>• Consultas JPQL Complejas (@Query)"]
    
    DB["<b>DATABASE LAYER</b> <i>(Motor SQL Relacional)</i><br/>• H2 Database (Memoria para desarrollo)<br/>• MySQL (Producción)"]

    CLIENT -->|"HTTP / JSON + Authorization: Bearer &lt;JWT&gt;"| SEC
    SEC -->|"Petición Autenticada (SecurityContextHolder)"| CTRL
    CTRL -->|"DTOs / Requests validados (@Valid)"| SERV
    SERV -->|"Entidades de Dominio (@Entity)"| REPO
    REPO -->|"SQL / JDBC (Hibernate ORM)"| DB

    %% Estilos de las capas
    classDef clientCard fill:#1e293b,stroke:#f59e0b,stroke-width:2px,color:#f8fafc;
    classDef layerCard fill:#0f172a,stroke:#3b82f6,stroke-width:2px,color:#f8fafc;
    classDef dbCard fill:#1e293b,stroke:#10b981,stroke-width:2px,color:#f8fafc;

    class CLIENT clientCard;
    class SEC,CTRL,SERV,REPO layerCard;
    class DB dbCard;
```

---

## 7. 🔐 Diagrama de Secuencia: Flujo de Autenticación con JWT (JSON Web Tokens)

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente (Postman)
    participant AuthCtrl as AuthController<br/>(/api/auth/login)
    participant AuthMgr as AuthenticationManager
    participant UserDetails as UserDetailsServiceImpl
    participant Jwt as JwtUtils
    participant Filter as AuthTokenFilter
    participant ResiduoCtrl as ResiduoController<br/>(/api/residuos)

    rect rgba(245, 158, 11, 0.15)
        Note over Cliente,ResiduoCtrl: FASE 1: Autenticación y Generación de Token
        Cliente->>AuthCtrl: POST /api/auth/login (username, password)
        activate AuthCtrl
        AuthCtrl->>AuthMgr: authenticate(UsernamePasswordAuthenticationToken)
        activate AuthMgr
        AuthMgr->>UserDetails: loadUserByUsername("valentino")
        activate UserDetails
        UserDetails-->>AuthMgr: UserDetails (Credenciales válidas)
        deactivate UserDetails
        AuthMgr-->>AuthCtrl: Authentication Exitosa
        deactivate AuthMgr
        AuthCtrl->>Jwt: generateJwtToken(authentication)
        activate Jwt
        Jwt-->>AuthCtrl: Token JWT (Header.Payload.Signature)
        deactivate Jwt
        AuthCtrl-->>Cliente: 200 OK (token, type: "Bearer", username, roles)
        deactivate AuthCtrl
    end

    rect rgba(59, 130, 246, 0.15)
        Note over Cliente,ResiduoCtrl: FASE 2: Consumo de Endpoints Protegidos
        Cliente->>Filter: GET /api/residuos (Header: Authorization: Bearer <token>)
        activate Filter
        Filter->>Jwt: validateJwtToken(token)
        activate Jwt
        Jwt-->>Filter: Token Válido (Firma y expiración OK)
        deactivate Jwt
        Filter->>Jwt: getUsernameFromJwtToken(token)
        activate Jwt
        Jwt-->>Filter: "valentino"
        deactivate Jwt
        Filter->>UserDetails: loadUserByUsername("valentino")
        activate UserDetails
        UserDetails-->>Filter: UserDetails con ROLES
        deactivate UserDetails
        Filter->>Filter: SecurityContextHolder.setAuthentication(...)
        Filter->>ResiduoCtrl: Permite paso al controlador
        activate ResiduoCtrl
        ResiduoCtrl-->>Cliente: 200 OK (Lista de Residuos RAEE)
        deactivate ResiduoCtrl
        deactivate Filter
    end
```

