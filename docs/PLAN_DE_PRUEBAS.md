# PLAN DE PRUEBAS – CCAFFA (Control de Calidad y Gestión de Producción)

| EMPRESA | PLAN DE PRUEBAS – “CCAFFA” | VERSIÓN | FECHA |
|---|---|---|---|
| UTN | Backend API REST | 1.0 | 2025-10-06 |

> Este documento define el plan para verificar y validar la API REST del proyecto `ccaffa` desarrollado en Spring Boot 3.5.0 (Java 21).

---

## 1. Objetivos

- Verificar que los endpoints implementados en `src/main/java/ar/utn/ccaffa/web/` cumplen los requerimientos funcionales y no funcionales.
- Asegurar la integridad de los datos persistidos en MySQL y el correcto flujo de negocio (órdenes de venta, órdenes de trabajo, rollos, control de calidad, planificación, etc.).
- Validar seguridad (JWT/roles) y manejo de errores (códigos y payloads `ErrorResponse`).
- Preparar una base para pruebas de regresión automatizables.

---

## 2. Alcance Funcional del Testing

### 2.1 Funcionalidades que serán probadas

| Componente Funcional | Caso de Uso | Descripción | Prioridad | Cant. Casos |
|---|---|---|---|---|
| Autenticación (`AuthController`) | Login | Obtener token JWT | Alta | 3 |
| Usuarios (`UsuarioController`) | CRUD + Roles | Gestión de usuarios con cifrado de password | Alta | 5 |
| Roles (`RolController`) | Consulta/CRUD | Acceso restringido ADMIN | Alta | 3 |
| Clientes (`ClienteController`) | CRUD | Gestión de clientes | Alta | 5 |
| Proveedores (`ProveedorController`) | CRUD | Gestión de proveedores | Media | 4 |
| Máquinas (`MaquinaController`) | CRUD + Disponibles | Gestión y consulta de máquinas | Alta | 5 |
| Rollos (`RolloController`) | CRUD + Árbol | Gestión, anulación y consultas | Alta | 8 |
| Órdenes de Venta (`OrdenVentaController`) | CRUD + Estados | Validaciones y cambios de estado | Alta | 7 |
| Órdenes de Trabajo (`OrdenDeTrabajoController`) | Crear/Modificar/Cancelar/Consultar | Flujo productivo principal | Crítica | 7 |
| ODT-Máquina (`OrdenDeTrabajoMaquinaController`) | Iniciar/Consultar | Gestión de procesos por máquina | Alta | 3 |
| Control de Calidad (`ControlDeCalidadController`) | Crear/Medidas/Estados | Validaciones de inicio y finalización | Crítica | 7 |
| Cámaras (`CamaraController`) | Upload imagen | Envío a análisis | Alta | 3 |
| Imágenes (`ImageController`) | Descarga | Visualización inline | Media | 2 |
| Defectos (`DefectoController`) | Aceptar/Rechazar | Actualización por imagen | Alta | 3 |
| Planificador (`PlannerController`) | Simular/Crear | GA Planner con validaciones | Alta | 5 |

### 2.2 Funcionalidades fuera de alcance (por esta iteración)

| Componente | Caso de Uso | Descripción | Prioridad | Motivo |
|---|---|---|---|---|
| WebSockets | Canal en vivo | Validación de eventos en tiempo real | Baja | Requiere cliente UI dedicado |
| gRPC externo | Clasificador de imágenes | Pruebas de precisión del modelo | Baja | Se simulará con disponibilidad básica |
| PDFs (`CertificadoController` PDF) | Formato visual | Validación visual de layout | Baja | Se valida existencia/descarga básica |

---

## 3. Estrategia de Testing

- **Estrategia Global**: Enfoque basado en riesgo. Cobertura de endpoints críticos primero. Uso de Swagger (`/swagger-ui.html`) y scripts curl. Preparado para automatización futura (RestAssured/JUnit) y Postman/Newman.
- **Pruebas Funcionales**: Casos felices + validaciones + negativos (datos inválidos, estados inconsistentes).
- **Pruebas de Integración**: Persistencia MySQL, interacción entre módulos (OT ↔ OV ↔ Rollo, CC ↔ OTM). gRPC probado como dependencia disponible (mock/stub si no está).
- **Pruebas de Regresión**: Conjunto base ejecutado en cada release (autenticación, CRUDs, flujos principales). 
- **Pruebas de Volumen**: Listados paginados y filtrados (rollos, órdenes) con dataset > 10k filas (ambiente QA). Medición de tiempos de respuesta (< 1s p50, < 3s p95).
- **Pruebas de Concurrencia**: 
  - Inicio simultáneo de OTM en la misma máquina (esperado bloqueo por `hayOTMEnCursoParaMaquina`).
  - Alta simultánea de rollos con mismo código+proveedor (esperado `REPEATED_PROVIDER_CODE_AND_PROVIDER`).
  - Cancelación de OT vs. creación de CC.
- **Otras**: 
  - Seguridad (headers `Authorization: Bearer`), rutas públicas vs protegidas según `SecurityConfig`.
  - Idempotencia/consistencia en endpoints `PUT/POST` sensibles.

---

## 4. Definición de Criterios

### 4.1 Severidad de Defectos

| Categoría | Descripción |
|---|---|
| Crítica | Caída del sistema, pérdida/corrupción de datos, bloqueo de proceso principal (no hay workaround). |
| Alta | Funcionalidad clave inutilizable, errores de negocio graves, respuestas HTTP incorrectas en flujos principales. |
| Media | Funcionalidad secundaria afectada, existe workaround. |
| Baja | Problemas menores/UX, mensajes poco claros, detalles cosméticos. |

### 4.2 Prioridad de Pruebas

- Alta: Validar en cada build (smoke) y antes de liberar.
- Media: Antes de liberar; puede diferirse frente a incidentes de mayor prioridad.
- Baja: Post-liberación si no impacta objetivos.

### 4.3 Criterios de Aceptación

- ≥ 95% de casos PASSED en módulos críticos (Auth, OV, OT, CC, Rollo).
- 0 defectos de severidad Crítica/Alta abiertos al momento de liberar.
- Tiempos de respuesta bajo carga dentro de umbrales definidos.

### 4.4 Criterios de Detección/Reporte

- Todo defecto debe incluir: pasos de reproducción, evidencia (JSON, logs, screenshots Swagger), request-id si aplica, y contexto de datos.

---

## 5. Ambiente de Testing

### 5.1 Servidor de Base de Datos

| Ítem | Valor (QA sugerido) |
|---|---|
| Motor | MySQL 8 |
| Hostname | localhost (QA) |
| Puerto | 3306 |
| Base | ccaffa_qa |
| Usuario/Password | Variables de entorno (no versionar secretos) |

Notas: En `application.properties` del repo hay una URL remota. Para QA usar variables y/o `application-qa.properties`.

### 5.2 Servidor de Aplicación

| Ítem | Valor |
|---|---|
| Runtime | Java 21, Spring Boot 3.5.0 |
| Puerto | 8080 |
| Swagger | `/swagger-ui.html` |
| OpenAPI | `/v3/api-docs` |
| Max upload | `10MB` |
| gRPC | `localhost:50051` (plaintext) |
| Storage | `./storage/images` |

### 5.3 Cliente de Pruebas

| Puesto | Configuración |
|---|---|
| QA | macOS/Windows/Linux, curl 7+, Postman v10+, JDK 21, Docker opcional, Newman (opcional) |

---

## 6. Datos de Prueba

- Usuarios: ADMIN y USER preconfigurados (o creados vía API). 
- Catálogos: Proveedores, Máquinas. 
- Entidades de flujo: Rollos disponibles, Órdenes de Venta en distintos estados, relaciones OT↔OV↔Rollo.
- Imágenes de prueba (PNG/JPG) < 10MB para `/api/camaras/{id}/upload`.

---

## 7. Riesgos de Testing

| ID | Descripción | Responsable | Mitigación | Contingencia |
|---|---|---|---|---|
| R1 | Indisponibilidad de DB/credenciales | DevOps | Variables de entorno y backups | Conmutar a H2 para pruebas de humo |
| R2 | gRPC clasificadora no disponible | Backend | Mock/stub local | Deshabilitar análisis y probar upload básico |
| R3 | Datos inconsistentes entre módulos | QA/Backend | Scripts de limpieza/seed | Restaurar snapshot QA |
| R4 | Endpoints públicos por configuración | QA/Backend | Revisar `SecurityConfig` antes de release | Endurecer reglas y re-ejecutar smoke |
| R5 | Concurrencia en OTM | Backend | Locks/validaciones (`hayOTMEnCurso...`) | Reintentos y colas |

---

## 8. Planilla de Casos de Prueba

La matriz inicial se adjunta en `docs/TEST-CASES.csv`. Se actualizará iterativamente.

---

## 9. Trazabilidad (alto nivel)

- Requisitos de negocio ↔ Módulos: Auth, Usuarios/Roles, Clientes/Proveedores, Máquinas, Rollos, OV, OT, CC, Planner, Imágenes/Defectos.
- Cada caso en CSV referencia `Módulo` y `Endpoint` exacto.

---

## 10. Ejecución y Reporte

- Ejecución manual guiada desde Swagger UI + curl (ver `API-DOCS.md`).
- Registro de resultados: `Estado` en CSV y reporte de defectos en herramienta acordada (Jira/GitHub Issues).
- Frecuencia: smoke por commit principal, regresión por release.

---

## 11. Aprobación

Al cumplir criterios de aceptación y con 0 defectos Críticos/Altos, se considera el sistema apto para liberar a ambientes superiores.
