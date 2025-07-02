package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes-venta")
@RequiredArgsConstructor
public class OrdenVentaController {

    private final OrdenVentaService ordenVentaService;

    @GetMapping
    public ResponseEntity<List<OrdenVentaDto>> obtenerTodasLasOrdenes() {
        List<OrdenVentaDto> ordenes = ordenVentaService.findAll();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenVentaDto> obtenerOrdenPorId(@PathVariable Long id) {
        OrdenVentaDto orden = ordenVentaService.findById(id);
        return orden != null ?
                ResponseEntity.ok(orden) :
                ResponseEntity.notFound().build();
    }

    @GetMapping("/consulta")
    public ResponseEntity<List<OrdenVentaDto>> buscarConFiltros(
        @RequestParam(required = false) LocalDate fecha,
        @RequestParam(required = false) LocalDate fechaInicio,
        @RequestParam(required = false) LocalDate fechaFin,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) List<String> estados,
        @RequestParam(required = false) Long clienteId,
        @RequestParam(required = false) String observaciones,
        @RequestParam(required = false) Long orderId) {
    
    if (fecha != null) {
        return ResponseEntity.ok(ordenVentaService.searchByDate(fecha));
    }
    if (fechaInicio != null && fechaFin != null) {
        return ResponseEntity.ok(ordenVentaService.searchByDateRange(fechaInicio, fechaFin));
    }
    if (estado != null && clienteId != null) {
        return ResponseEntity.ok(ordenVentaService.searchByClienteAndEstado(clienteId, estado));
    }
    if (estado != null) {
        return ResponseEntity.ok(ordenVentaService.searchByEstado(estado));
    }
    if (estados != null && !estados.isEmpty()) {
        return ResponseEntity.ok(ordenVentaService.searchByEstados(estados));
    }
    if (clienteId != null) {
        return ResponseEntity.ok(ordenVentaService.searchByCliente(clienteId));
    }
    if (observaciones != null) {
        return ResponseEntity.ok(ordenVentaService.searchByObservaciones(observaciones));
    }
    if (orderId != null) {
        return ResponseEntity.ok(List.of(ordenVentaService.searchByOrderId(orderId)));
    }
    
    return ResponseEntity.ok(ordenVentaService.findAll());
}

    
    @GetMapping("/{id}/defecto")
    public ResponseEntity<Defecto> obtenerDefectoPorId(@PathVariable Long id) {
        Defecto defecto = ordenVentaService.obtenerDefectoPorId(id);
        return defecto != null ?
                ResponseEntity.ok(defecto) :
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OrdenVenta> crearOrden(@Valid @RequestBody OrdenVentaDto ordenVentaDto) {
        OrdenVenta nuevaOrden = ordenVentaService.save(ordenVentaDto);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }

    @PostMapping("/defecto")
    public ResponseEntity<String> crearDefecto(@Valid @RequestBody Defecto defecto) {
        ordenVentaService.crearDefecto(defecto);
        return ResponseEntity.ok("qweqw");
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenVenta> actualizarOrden(
            @PathVariable Long id,
            @Valid @RequestBody OrdenVentaDto ordenVentaDto) {
        if (!id.equals(ordenVentaDto.getId())) {
            return ResponseEntity.badRequest().build();
        }
        OrdenVenta ordenActualizada = ordenVentaService.save(ordenVentaDto);
        return ResponseEntity.ok(ordenActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenVentaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/anular/{id}")
    public ResponseEntity<Object> anular(@PathVariable Long id) throws BadRequestException {
        this.ordenVentaService.anular(id);
        return ResponseEntity.ok("Orden de Venta anulada correctamente");
    }
}