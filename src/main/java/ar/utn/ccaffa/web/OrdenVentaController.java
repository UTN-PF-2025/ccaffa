package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.FiltroOrdenVentaDTO;
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
    public ResponseEntity<List<OrdenVentaDto>> obtenerTodasLasOrdenes(FiltroOrdenVentaDTO filtroOrdenVentaDTO) {
        List<OrdenVentaDto> ordenes = ordenVentaService.searchByFiltros(filtroOrdenVentaDTO);
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenVentaDto> obtenerOrdenPorId(@PathVariable Long id) {
        OrdenVentaDto orden = ordenVentaService.findById(id);
        return orden != null ?
                ResponseEntity.ok(orden) :
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OrdenVenta> crearOrden(@Valid @RequestBody OrdenVentaDto ordenVentaDto) {
        OrdenVenta nuevaOrden = ordenVentaService.save(ordenVentaDto);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
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

    @PostMapping("/finalizar/{id}")
    public ResponseEntity<Object> finalizar(@PathVariable Long id) throws BadRequestException {
        this.ordenVentaService.finalizar(id);
        return ResponseEntity.ok("Orden de Venta finalizada correctamente");
    }
}