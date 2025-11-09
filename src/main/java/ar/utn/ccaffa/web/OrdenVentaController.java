package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordenes-venta")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Venta", description = "API para gestionar órdenes de venta")
public class OrdenVentaController {

    private final OrdenVentaService ordenVentaService;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final RolloService rolloService;

    @GetMapping
    @Operation(
        summary = "Obtener órdenes de venta con paginación", 
        description = "Retorna una página de órdenes de venta filtradas por los criterios especificados"
    )
    public ResponseEntity<Page<OrdenVentaDto>> obtenerTodasLasOrdenes(
            FiltroOrdenVentaDTO filtroOrdenVentaDTO,
            @Parameter(description = "Parámetros de paginación") Pageable pageable) {
        Page<OrdenVentaDto> ordenesPage = ordenVentaService.searchByFiltros(filtroOrdenVentaDTO, pageable);
        return ResponseEntity.ok(ordenesPage);
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

    @PostMapping("/{id}/anular")
    public ResponseEntity<Object> anular(@PathVariable Long id) throws BadRequestException {
        try {
            this.ordenVentaService.anular(id);
            return ResponseEntity.ok("Orden de Venta anulada correctamente");
        }
        catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status("ERROR_EN_LA_ANULACION")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{id}/simular-anulacion")
    public ResponseEntity<?> simularCancelacion(@PathVariable Long id) {
        try {
            CancelacionSimulacionDto simulacion = ordenVentaService.simularCancelacion(id);
            return ResponseEntity.ok(simulacion);
        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status("ERROR_EN_LA_SIMULACION")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


    @PostMapping("/finalizar/{id}")
    public ResponseEntity<?> finalizar(@PathVariable Long id) throws BadRequestException {
        OrdenVentaDto ordenVenta = this.ordenVentaService.findById(id);
        if (ordenVenta.getEstado() != EstadoOrdenVentaEnum.TRABAJO_FINALIZADO){
            ErrorResponse error = ErrorResponse.builder()
                    .status("TRABAJO_NO_TERMINADO")
                    .message("El trabajo no está terminado")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Optional<OrdenDeTrabajo> ordenesDeTrabajo = this.ordenDeTrabajoRepository.findTopByOrdenDeVenta_IdAndEstadoInOrderByIdDesc(id, List.of(EstadoOrdenTrabajoEnum.FINALIZADA));
        if (ordenesDeTrabajo.isEmpty()){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ORDEN_DE_TRABAJO_NO_EXISTE")
                    .message("No tiene una órden de trabajo asignada")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        this.ordenVentaService.finalizar(id, ordenesDeTrabajo.get());
        return ResponseEntity.ok("Orden de Venta finalizada correctamente");
    }
    @GetMapping("/obtenerUltimoProductoParaOrdenDeVenta/{id}")
    public ResponseEntity<?> obtenerUltimoProductoParaOrdenDeVentaId(@PathVariable Long id) {

        if (!this.ordenVentaService.trabajoFinalizado(id)){
            ErrorResponse error = ErrorResponse.builder()
                    .status("ROLLO_PRODUCTO_NO_DISPONIBLE")
                    .message("El rollo todavía no está producido")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Optional<RolloDto> rollo = rolloService.findLastProductForOrdenDeVentaId(id);
        return ResponseEntity.ok(rollo);

    }

}