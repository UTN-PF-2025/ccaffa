# API Documentation - CCAFFA

Documentación detallada de los endpoints con ejemplos curl.

## 📋 Índice

- [Autenticación](#autenticación)
- [Usuarios](#usuarios)
- [Clientes](#clientes)
- [Proveedores](#proveedores)
- [Máquinas](#máquinas)
- [Rollos](#rollos)
- [Órdenes de Venta](#órdenes-de-venta)
- [Órdenes de Trabajo](#órdenes-de-trabajo)
- [Control de Calidad](#control-de-calidad)
- [Certificados](#certificados)

---

## Configuración Base

**URL Base:** `http://localhost:8080`

**Headers comunes:**
```
Content-Type: application/json
Authorization: Bearer {token}
```

---

## Autenticación

### Login

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

---

## Usuarios

### Listar usuarios
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener usuario por ID
```bash
curl -X GET http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear usuario (ADMIN)
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevooperador",
    "password": "password123",
    "nombre": "María González",
    "email": "mgonzalez@ccaffa.com",
    "roles": [{"id": 2}]
  }'
```

### Actualizar usuario (ADMIN)
```bash
curl -X PUT http://localhost:8080/api/usuarios/3 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "operador",
    "nombre": "María González Actualizado",
    "email": "mgonzalez@ccaffa.com",
    "roles": [{"id": 2}]
  }'
```

### Eliminar usuario (ADMIN)
```bash
curl -X DELETE http://localhost:8080/api/usuarios/3 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Clientes

### Listar clientes
```bash
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nueva Empresa S.R.L.",
    "cuit": "30-98765432-1",
    "direccion": "Calle Secundaria 567",
    "telefono": "+54 11 5555-6666",
    "email": "info@nuevaempresa.com"
  }'
```

### Actualizar cliente
```bash
curl -X PUT http://localhost:8080/api/clientes/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Empresa Actualizada",
    "cuit": "30-98765432-1",
    "direccion": "Nueva Dirección 789",
    "telefono": "+54 11 5555-6666",
    "email": "info@empresa.com"
  }'
```

### Eliminar cliente
```bash
curl -X DELETE http://localhost:8080/api/clientes/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Proveedores

### Listar proveedores
```bash
curl -X GET http://localhost:8080/api/proveedores \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear proveedor
```bash
curl -X POST http://localhost:8080/api/proveedores \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Proveedor S.A.",
    "cuit": "30-22222222-2",
    "direccion": "Parque Industrial 123",
    "telefono": "+54 11 7777-8888",
    "email": "contacto@proveedor.com"
  }'
```

---

## Máquinas

### Listar máquinas
```bash
curl -X GET http://localhost:8080/api/maquinas \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Listar máquinas disponibles
```bash
curl -X GET http://localhost:8080/api/maquinas/disponibles \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear máquina
```bash
curl -X POST http://localhost:8080/api/maquinas \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Cortadora Industrial 3",
    "tipo": "CORTADORA",
    "estado": "DISPONIBLE",
    "velocidad": 60.0,
    "capacidad": 1200.0
  }'
```

---

## Rollos

### Listar rollos
```bash
curl -X GET http://localhost:8080/api/rollos \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Listar rollos con filtros
```bash
curl -X GET "http://localhost:8080/api/rollos?estado=DISPONIBLE&proveedorId=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener rollo por ID
```bash
curl -X GET http://localhost:8080/api/rollos/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener rollo con padres
```bash
curl -X GET http://localhost:8080/api/rollos/5/conRollosPadres \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener árbol de rollos hijos
```bash
curl -X GET http://localhost:8080/api/rollos/1/arbolDeRollosHijos \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Listar tipos de material
```bash
curl -X GET http://localhost:8080/api/rollos/tipoMateriales \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear rollo
```bash
curl -X POST http://localhost:8080/api/rollos \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoProveedor": "ROLLO-NEW-001",
    "proveedorId": 1,
    "tipoMaterial": "POLIETILENO",
    "ancho": 200.0,
    "espesor": 0.08,
    "peso": 750.0,
    "largo": 1500.0
  }'
```

### Actualizar rollo
```bash
curl -X PUT http://localhost:8080/api/rollos/10 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoProveedor": "ROLLO-UPD",
    "proveedorId": 1,
    "tipoMaterial": "POLIETILENO",
    "ancho": 210.0,
    "espesor": 0.08,
    "peso": 800.0,
    "largo": 1600.0
  }'
```

### Anular rollo
```bash
curl -X POST http://localhost:8080/api/rollos/10/anular \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Simular anulación
```bash
curl -X GET http://localhost:8080/api/rollos/10/anular \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Eliminar rollo
```bash
curl -X DELETE http://localhost:8080/api/rollos/10 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Órdenes de Venta

### Listar órdenes de venta
```bash
curl -X GET http://localhost:8080/api/ordenes-venta \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Listar con filtros
```bash
curl -X GET "http://localhost:8080/api/ordenes-venta?estado=PENDIENTE&clienteId=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener orden por ID
```bash
curl -X GET http://localhost:8080/api/ordenes-venta/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear orden de venta
```bash
curl -X POST http://localhost:8080/api/ordenes-venta \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {"id": 1},
    "fechaEntregaEstimada": "2025-02-15T10:00:00",
    "especificacion": {
      "ancho": 150.0,
      "espesor": 0.06,
      "cantidad": 1500.0,
      "tipoMaterial": "POLIPROPILENO",
      "observaciones": "Material de alta calidad"
    },
    "observaciones": "Entregar en sucursal principal"
  }'
```

### Actualizar orden de venta
```bash
curl -X PUT http://localhost:8080/api/ordenes-venta/5 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 5,
    "cliente": {"id": 1},
    "fechaEntregaEstimada": "2025-02-20T10:00:00",
    "especificacion": {
      "ancho": 150.0,
      "espesor": 0.06,
      "cantidad": 2000.0,
      "tipoMaterial": "POLIPROPILENO"
    }
  }'
```

### Anular orden de venta
```bash
curl -X POST http://localhost:8080/api/ordenes-venta/anular/5 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Finalizar orden de venta
```bash
curl -X POST http://localhost:8080/api/ordenes-venta/finalizar/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Eliminar orden de venta
```bash
curl -X DELETE http://localhost:8080/api/ordenes-venta/5 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Órdenes de Trabajo

### Listar órdenes de trabajo
```bash
curl -X GET http://localhost:8080/api/ordenes-trabajo \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener orden por ID
```bash
curl -X GET http://localhost:8080/api/ordenes-trabajo/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear orden de trabajo
```bash
curl -X POST http://localhost:8080/api/ordenes-trabajo \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ordenDeVentaId": 1,
    "rolloId": 1,
    "fechaInicio": "2025-01-16T08:00:00",
    "fechaFin": "2025-01-20T17:00:00",
    "observaciones": "Proceso normal",
    "maquinas": [
      {
        "id": 1,
        "fechaInicio": "2025-01-16T08:00:00",
        "fechaFin": "2025-01-17T17:00:00",
        "estado": "PROGRAMADA",
        "observaciones": "Primera etapa"
      },
      {
        "id": 2,
        "fechaInicio": "2025-01-18T08:00:00",
        "fechaFin": "2025-01-20T17:00:00",
        "estado": "PROGRAMADA",
        "observaciones": "Segunda etapa"
      }
    ]
  }'
```

### Modificar orden de trabajo
```bash
curl -X PUT http://localhost:8080/api/ordenes-trabajo/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2025-01-17T08:00:00",
    "fechaFin": "2025-01-22T17:00:00",
    "estado": "PROGRAMADA"
  }'
```

### Cancelar orden de trabajo
```bash
curl -X POST http://localhost:8080/api/ordenes-trabajo/1/cancelar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Simular cancelación
```bash
curl -X GET http://localhost:8080/api/ordenes-trabajo/1/simular-cancelacion \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Órdenes con rollo específico
```bash
curl -X GET http://localhost:8080/api/ordenes-trabajo/obtenerOrdenesConRollo/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Órdenes con máquina específica
```bash
curl -X GET http://localhost:8080/api/ordenes-trabajo/obtenerOrdenesConMaquina/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Programaciones de máquina (paginado)
```bash
curl -X GET "http://localhost:8080/api/ordenes-trabajo/programaciones-maquinas/maquina/1?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Control de Calidad

### Listar controles de calidad
```bash
curl -X GET http://localhost:8080/api/controles-calidad \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Obtener control por ID
```bash
curl -X GET http://localhost:8080/api/controles-calidad/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Crear control de calidad
```bash
curl -X POST http://localhost:8080/api/controles-calidad \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ordenDeTrabajoMaquinaId": 1,
    "observaciones": "Control inicial"
  }'
```

### Agregar medida
```bash
curl -X POST http://localhost:8080/api/controles-calidad/1/medidas \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "ANCHO",
    "valor": 150.5,
    "unidad": "mm",
    "observaciones": "Medida dentro del rango"
  }'
```

### Obtener control de proceso
```bash
curl -X GET http://localhost:8080/api/controles-calidad/1/proceso \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Iniciar control
```bash
curl -X PUT http://localhost:8080/api/controles-calidad/1/iniciar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Finalizar control
```bash
curl -X PUT http://localhost:8080/api/controles-calidad/1/finalizar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Marcar como a corregir
```bash
curl -X PUT http://localhost:8080/api/controles-calidad/1/a-corregir \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Certificados

### Generar certificado
```bash
curl -X POST http://localhost:8080/api/certificados \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ordenVentaId": 1,
    "observaciones": "Certificado de calidad completo"
  }'
```

### Obtener certificado
```bash
curl -X GET http://localhost:8080/api/certificados/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Descargar PDF
```bash
curl -X GET http://localhost:8080/api/certificados/pdf/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output certificado.pdf
```

---

## Defectos

### Aceptar defecto
```bash
curl -X POST http://localhost:8080/api/defectos/1/IMG001/aceptar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Rechazar defecto
```bash
curl -X POST http://localhost:8080/api/defectos/1/IMG001/rechazar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Códigos de Estado HTTP

- **200 OK**: Operación exitosa
- **201 CREATED**: Recurso creado exitosamente
- **204 NO CONTENT**: Operación exitosa sin contenido de respuesta
- **400 BAD REQUEST**: Error en la petición (datos inválidos)
- **401 UNAUTHORIZED**: No autenticado o token inválido
- **403 FORBIDDEN**: No tiene permisos para esta operación
- **404 NOT FOUND**: Recurso no encontrado
- **500 INTERNAL SERVER ERROR**: Error interno del servidor

---

## Ejemplos de Respuestas de Error

### Error de validación
```json
{
  "status": "ANCHO_INVALIDO",
  "message": "El ancho debe ser mayor a 0"
}
```

### Error de rollo no encontrado
```json
{
  "status": "ROLLO_ID_NOT_FOUND",
  "message": "No se encontró el rollo"
}
```

### Error de orden de venta no encontrada
```json
{
  "status": "ORDEN_DE_VENTA_NOT_FOUND",
  "message": "No se encontró la orden de venta"
}
```

---

## Notas Importantes

1. **Todos los endpoints (excepto `/api/auth/login`) requieren autenticación** mediante token JWT en el header `Authorization: Bearer {token}`.

2. **Los tokens expiran después de 5 horas** (18000 segundos). Debes renovar el token haciendo login nuevamente.

3. **Fechas**: Usar formato ISO 8601: `yyyy-MM-ddTHH:mm:ss` (ejemplo: `2025-01-15T10:30:00`)

4. **Filtros en GET**: Los parámetros se envían como query parameters en la URL.

5. **Estados comunes**:
   - Órdenes de Venta: `PENDIENTE`, `PROGRAMADA`, `TRABAJO_FINALIZADO`, `ENTREGADA`, `ANULADA`
   - Órdenes de Trabajo: `PROGRAMADA`, `EN_CURSO`, `FINALIZADA`, `CANCELADA`
   - Rollos: `DISPONIBLE`, `EN_USO`, `CONSUMIDO`, `ANULADO`
   - Máquinas: `DISPONIBLE`, `EN_USO`, `MANTENIMIENTO`

6. **Paginación**: Algunos endpoints soportan paginación con `?page=0&size=10`

---

**Para más información consulta la documentación interactiva de Swagger:**
http://localhost:8080/swagger-ui.html
