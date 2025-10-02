package ar.utn.ccaffa.web;

import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.EspecificacionDto;
import ar.utn.ccaffa.model.dto.FiltroOrdenVentaDTO;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public ResponseEntity<?> crearOrden(@Valid @RequestBody OrdenVentaDto ordenVentaDto) {
        ordenVentaDto.setOrderId(null);

        if (ordenVentaDto.getEspecificacion().getAncho() == null ||  ordenVentaDto.getEspecificacion().getAncho() <= 0){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ANCHO_INVALIDO")
                    .message("El ancho debe ser mayor a 0")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if (ordenVentaDto.getEspecificacion().getEspesor() == null || ordenVentaDto.getEspecificacion().getEspesor() <= 0){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ESPESOR_INVALIDO")
                    .message("El espesor debe ser mayor a 0")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if (ordenVentaDto.getEspecificacion().getCantidad() == null || ordenVentaDto.getEspecificacion().getCantidad() <= 0){
            ErrorResponse error = ErrorResponse.builder()
                    .status("PESO_INVALIDO")
                    .message("El peso debe ser mayor a 0")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if (ordenVentaDto.getCliente() == null){
            ErrorResponse error = ErrorResponse.builder()
                    .status("CLIENTE_INVALIDO")
                    .message("Se debe otorgar un cliente")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if (ordenVentaDto.getFechaEntregaEstimada() == null){
            ErrorResponse error = ErrorResponse.builder()
                    .status("FECHA_INVALIDA")
                    .message("Se debe otorgar un fecha de estimación")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }




        OrdenVentaDto nuevaOrden = ordenVentaService.save(ordenVentaDto);

        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenVentaDto> actualizarOrden(
            @PathVariable Long id,
            @Valid @RequestBody OrdenVentaDto ordenVentaDto) {
        if (!id.equals(ordenVentaDto.getOrderId())) {
            return ResponseEntity.badRequest().build();
        }
        
        OrdenVentaDto ordenActualizada = ordenVentaService.save(ordenVentaDto);
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