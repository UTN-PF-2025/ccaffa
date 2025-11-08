# CCAFFA - Sistema de Control de Calidad y Gestión de Producción

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-UTN-blue.svg)](https://www.utn.edu.ar)

## 📋 Descripción

Sistema integral para la gestión de producción y control de calidad en la industria manufacturera. Permite gestionar órdenes de venta, órdenes de trabajo, rollos de material, máquinas, clientes, proveedores, certificados y control de calidad con análisis de defectos mediante inteligencia artificial.

### Características Principales

- ✅ **Gestión de Órdenes de Venta y Trabajo**: Control completo del ciclo productivo
- 📦 **Gestión de Rollos**: Trazabilidad de materiales con árbol de dependencias
- 🏭 **Gestión de Máquinas**: Programación y seguimiento de máquinas productivas
- 🔍 **Control de Calidad con IA**: Análisis automático de defectos mediante gRPC
- 👥 **Gestión de Usuarios**: Autenticación JWT con roles y permisos
- 📊 **Planificación Inteligente**: Algoritmo genético para optimización de producción
- 📜 **Certificados PDF**: Generación automática de certificados de calidad
- 📃 **Paginación**: Endpoints con paginación para optimizar el rendimiento
- 🔐 **Seguridad**: Autenticación JWT y autorización basada en roles
- 📡 **WebSockets**: Comunicación en tiempo real
- 🌐 **CORS**: Configurado para múltiples orígenes

---

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 21+
- Maven 3.8+
- MySQL 8+
- (Opcional) Servidor gRPC para clasificación de imágenes en puerto 50051

### Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/ccaffa.git
   cd ccaffa
   ```

2. **Configurar la base de datos**
   
   Editar `src/main/resources/application.properties` con tus credenciales:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ccaffa_db
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password
   ```

3. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

4. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

5. **Verificar que funciona**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

---

## 📚 Documentación de la API

### Acceso a Swagger UI

Una vez que la aplicación esté corriendo, puedes acceder a la documentación interactiva de Swagger:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

Para obtener la especificación OpenAPI en formato JSON:

```
http://localhost:8080/v3/api-docs
```

### Documentación Detallada con Ejemplos CURL

Para ver ejemplos detallados de uso con curl, consulta:
- **[API-DOCS.md](./API-DOCS.md)** - Documentación completa con ejemplos de curl para cada endpoint

---

## 🔑 Autenticación

La API utiliza **JWT (JSON Web Tokens)** para la autenticación.

### 1. Obtener Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"],
  "nombre": "Administrador",
  "id": "1"
}
```

### 2. Usar el Token

Incluye el token en el header `Authorization` de tus peticiones:

```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 🗂️ Estructura de Endpoints

La API está organizada en los siguientes módulos:

### **Autenticación**
- `POST /api/auth/login` - Iniciar sesión

### **Usuarios**
- `GET /api/usuarios` - Listar usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `POST /api/usuarios` - Crear usuario (ADMIN)
- `PUT /api/usuarios/{id}` - Actualizar usuario (ADMIN)
- `DELETE /api/usuarios/{id}` - Eliminar usuario (ADMIN)

### **Clientes**
- `GET /api/clientes` - Listar clientes
- `GET /api/clientes/{id}` - Obtener cliente
- `POST /api/clientes` - Crear cliente
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Eliminar cliente

### **Proveedores**
- `GET /api/proveedores` - Listar proveedores
- `GET /api/proveedores/{id}` - Obtener proveedor
- `POST /api/proveedores` - Crear proveedor
- `PUT /api/proveedores/{id}` - Actualizar proveedor
- `DELETE /api/proveedores/{id}` - Eliminar proveedor

### **Máquinas**
- `GET /api/maquinas` - Listar máquinas
- `GET /api/maquinas/disponibles` - Listar máquinas disponibles
- `GET /api/maquinas/{id}` - Obtener máquina
- `POST /api/maquinas` - Crear máquina
- `PUT /api/maquinas/{id}` - Actualizar máquina
- `DELETE /api/maquinas/{id}` - Eliminar máquina

### **Rollos**
- `GET /api/rollos` - Listar rollos con paginación (parámetros: page, size, sort)
- `GET /api/rollos/{id}` - Obtener rollo
- `GET /api/rollos/{id}/conRollosPadres` - Obtener rollo con padres
- `GET /api/rollos/{id}/arbolDeRollosHijos` - Obtener árbol de rollos hijos
- `GET /api/rollos/{id}/rollosDisponibles` - Rollos disponibles para orden
- `GET /api/rollos/tipoMateriales` - Listar tipos de material
- `POST /api/rollos` - Crear rollo
- `PUT /api/rollos/{id}` - Actualizar rollo
- `DELETE /api/rollos/{id}` - Eliminar rollo
- `POST /api/rollos/{id}/anular` - Anular rollo
- `GET /api/rollos/{id}/anular` - Simular anulación de rollo

### **Órdenes de Venta**
- `GET /api/ordenes-venta` - Listar órdenes con paginación (parámetros: page, size, sort)
- `GET /api/ordenes-venta/{id}` - Obtener orden
- `POST /api/ordenes-venta` - Crear orden
- `PUT /api/ordenes-venta/{id}` - Actualizar orden
- `DELETE /api/ordenes-venta/{id}` - Eliminar orden
- `POST /api/ordenes-venta/anular/{id}` - Anular orden
- `POST /api/ordenes-venta/finalizar/{id}` - Finalizar orden

### **Órdenes de Trabajo**
- `GET /api/ordenes-trabajo` - Listar órdenes con paginación (parámetros: page, size, sort)
- `GET /api/ordenes-trabajo/{id}` - Obtener orden
- `POST /api/ordenes-trabajo` - Crear orden
- `PUT /api/ordenes-trabajo/{id}` - Modificar orden
- `POST /api/ordenes-trabajo/{id}/cancelar` - Cancelar orden
- `GET /api/ordenes-trabajo/{id}/simular-cancelacion` - Simular cancelación
- `GET /api/ordenes-trabajo/obtenerOrdenesConRollo/{id}` - Órdenes con rollo específico
- `GET /api/ordenes-trabajo/obtenerOrdenesConMaquina/{id}` - Órdenes con máquina específica
- `GET /api/ordenes-trabajo/programaciones-maquinas/maquina/{id}` - Programaciones de máquina (paginado)

### **Control de Calidad**
- `GET /api/controles-calidad` - Listar controles con paginación (parámetros: page, size, sort)
- `GET /api/controles-calidad/{id}` - Obtener control
- `POST /api/controles-calidad` - Crear control
- `PUT /api/controles-calidad/{id}` - Actualizar control
- `DELETE /api/controles-calidad/{id}` - Eliminar control

### **Defectos**
- `GET /api/defectos` - Listar defectos
- `POST /api/defectos/{camaraId}/{imageId}/aceptar` - Aceptar defecto
- `POST /api/defectos/{camaraId}/{imageId}/rechazar` - Rechazar defecto

### **Cámaras**
- `GET /api/camaras` - Listar cámaras
- `GET /api/camaras/{id}` - Obtener cámara
- `POST /api/camaras` - Crear cámara
- `POST /api/camaras/{id}/upload` - Subir imagen
- `PUT /api/camaras/{id}` - Actualizar cámara
- `DELETE /api/camaras/{id}` - Eliminar cámara

### **Imágenes**
- `GET /api/images/{id}/{filename}` - Obtener imagen

### **Certificados**
- Endpoints para generación de certificados PDF

### **Planificación**
- `GET /api/planner` - Endpoints de planificación con algoritmo genético

### **Roles**
- `GET /api/roles` - Listar roles (ADMIN)

---

## 🏗️ Arquitectura

### Stack Tecnológico

- **Backend Framework**: Spring Boot 3.5.0
- **Lenguaje**: Java 21
- **Base de Datos**: MySQL 8
- **ORM**: Spring Data JPA / Hibernate
- **Seguridad**: Spring Security + JWT
- **Documentación**: SpringDoc OpenAPI 3 (Swagger)
- **Comunicación**: gRPC, WebSockets
- **PDFs**: iText
- **IA**: Algoritmos genéticos (Jenetics)

### Estructura del Proyecto

```
ccaffa/
├── src/
│   ├── main/
│   │   ├── java/ar/utn/ccaffa/
│   │   │   ├── config/          # Configuraciones (Security, OpenAPI, etc.)
│   │   │   ├── enums/           # Enumeraciones
│   │   │   ├── exceptions/      # Manejo de excepciones
│   │   │   ├── mapper/          # Mappers DTO-Entity
│   │   │   ├── model/
│   │   │   │   ├── dto/         # Data Transfer Objects
│   │   │   │   └── entity/      # Entidades JPA
│   │   │   ├── repository/      # Repositorios JPA
│   │   │   ├── services/        # Lógica de negocio
│   │   │   └── web/             # Controllers REST
│   │   ├── proto/               # Definiciones Protocol Buffers
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── storage/                     # Almacenamiento de imágenes
├── pom.xml
├── README.md
└── API-DOCS.md
```

### Patrones de Diseño

- **Controller-Service-Repository**: Arquitectura en capas
- **DTO Pattern**: Separación entre entidades y objetos de transferencia
- **Dependency Injection**: Inyección de dependencias con Spring
- **Builder Pattern**: Construcción de objetos complejos
- **Strategy Pattern**: Para diferentes tipos de procesamiento

---

## 🔒 Seguridad

### Roles de Usuario

- **ADMIN**: Acceso total al sistema
- **USER**: Acceso a operaciones básicas
- **OPERADOR**: Acceso a operaciones de cámara y control

### Configuración JWT

- **Secret**: Configurable en `application.properties`
- **Expiración**: 5 horas (18000 segundos)
- **Header**: `Authorization: Bearer {token}`

### CORS

Orígenes permitidos por defecto:
- `http://localhost:5173`
- `http://localhost:5174`
- `http://localhost:8080`
- Producción configurada

---

## 🗄️ Base de Datos

### Conexión

```properties
spring.datasource.url=jdbc:mysql://tu-servidor:3306/tu-database
spring.datasource.username=tu-usuario
spring.datasource.password=tu-password
```

### Schema

El schema se crea/actualiza automáticamente con:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Principales Entidades

- **Usuario**: Usuarios del sistema
- **Cliente**: Clientes de la empresa
- **Proveedor**: Proveedores de materiales
- **Rollo**: Rollos de material (con árbol de dependencias)
- **Maquina**: Máquinas productivas
- **OrdenVenta**: Órdenes de venta de clientes
- **OrdenDeTrabajo**: Órdenes de trabajo de producción
- **OrdenDeTrabajoMaquina**: Programación de máquinas
- **ControlDeCalidad**: Controles de calidad
- **Defecto**: Defectos detectados
- **Certificado**: Certificados de calidad
- **Camara**: Cámaras de inspección

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

---

## 📦 Deployment

### Generar JAR

```bash
mvn clean package -DskipTests
```

El JAR se generará en `target/ccaffa-0.0.1-SNAPSHOT.jar`

### Ejecutar JAR

```bash
java -jar target/ccaffa-0.0.1-SNAPSHOT.jar
```

### Docker (Ejemplo)

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/ccaffa-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 📄 Paginación

Todos los endpoints principales de listados ahora soportan paginación con los siguientes parámetros:

- `page`: Número de página (0-based)
- `size`: Cantidad de elementos por página
- `sort`: Propiedad y dirección de ordenamiento (formato: `propiedad,(asc|desc)`)

**Ejemplo de uso:**

```bash
# Obtener la primera página de rollos, con 10 elementos por página, ordenados por id descendente
curl -X GET "http://localhost:8080/api/rollos?page=0&size=10&sort=id,desc" \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta:**

```json
{
  "content": [
    { /* datos del rollo */ },
    /* ... más rollos ... */
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "orders": [{ "direction": "DESC", "property": "id" }] }
  },
  "totalElements": 150,
  "totalPages": 15,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10
}
```

## 🔧 Configuración Avanzada

### gRPC Cliente (Clasificación de Imágenes)

```properties
grpc.client.image-classifier.address=static://localhost:50051
grpc.client.image-classifier.negotiation-type=plaintext
```

### Almacenamiento de Archivos

```properties
file.storage.path=./storage/images
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### WebSockets

Endpoint: `ws://localhost:8080/api/ws/**`

---

## 📖 Recursos Adicionales

- **[API-DOCS.md](./API-DOCS.md)** - Documentación detallada con ejemplos curl
- **[Swagger UI](http://localhost:8080/swagger-ui.html)** - Documentación interactiva
- **[OpenAPI Spec](http://localhost:8080/v3/api-docs)** - Especificación OpenAPI
- **[Health Check](http://localhost:8080/actuator/health)** - Estado de la aplicación

---

## 👥 Contribución

Este es un proyecto de la Universidad Tecnológica Nacional (UTN).

---

## 📄 Licencia

Proyecto Final - UTN

---

## 📧 Contacto

Para más información, contacta a: contacto@ccaffa.utn.ar

---

**¡Gracias por usar CCAFFA!** 🚀
