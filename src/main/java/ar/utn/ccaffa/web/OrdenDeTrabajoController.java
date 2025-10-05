package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.*;
import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoMaquinaMapper;
import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoResponseMapper;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.repository.interfaces.*;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.model.dto.Bloque;
import ar.utn.ccaffa.model.dto.FiltroOrdenDeTrabajoDto;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/ordenes-trabajo")
@RequiredArgsConstructor
public class OrdenDeTrabajoController {

    private final OrdenDeTrabajoService ordenDeTrabajoService;
    private final MaquinaRepository maquinaRepository;
    private final RolloRepository rolloRepository;
    private final OrdenVentaRepository ordenDeVentaRepository;
    private final OrdenDeTrabajoResponseMapper ordenDeTrabajoResponseMapper;
    private final OrdenDeTrabajoMaquinaService ordenDeTrabajoMaquinaService;
    private final OrdenDeTrabajoMaquinaMapper ordenDeTrabajoMaquinaMapper;

    @PostMapping
    public ResponseEntity<?> crearOrdenDeTrabajo(@RequestBody OrdenDeTrabajoDto request) {
        try {
            // 1. Procesar orden de venta
            OrdenVenta ordenVenta = procesarOrdenVenta(request);
            if (ordenVenta == null){
                ErrorResponse error = ErrorResponse.builder()
                        .status("ORDEN_DE_VENTA_NOT_FOUND")
                        .message("No se encontró la orden de venta")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // 2. Crear y guardar la orden de trabajo básica para obtener un ID
            OrdenDeTrabajo orden = crearOrdenBasica(request);
            orden.setOrdenDeVenta(ordenVenta);

            // 3. Procesar y asociar máquinas
            procesarMaquinas(request, orden);

            // 4. Procesar y asociar el rollo y sus hijos
            List<Rollo> rolloHijos = procesarRollo(request, orden, ordenVenta);
            if (rolloHijos == null){
                ErrorResponse error = ErrorResponse.builder()
                        .status("ROLLO_ID_NOT_FOUND")
                        .message("No se encontró el rollo")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }


            // 5. Configurar los datos finales de la orden
            configurarOrdenFinal(orden, request);

            // 6. Guardar la orden de trabajo completa con todas sus asociaciones
            rolloRepository.saveAll(rolloHijos);
            ordenDeVentaRepository.save(ordenVenta);
            OrdenDeTrabajo guardada = ordenDeTrabajoService.save(orden);
            return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(guardada));

        } catch (IllegalArgumentException e) {
            log.error("Error al crear orden de trabajo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDeTrabajoResponseDto> obtenerOrdenDeTrabajo(@PathVariable Long id) {
        return ordenDeTrabajoService.findById(id)
                .map(ordenDeTrabajoResponseMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerTodasLasOrdenes(FiltroOrdenDeTrabajoDto filtros) {
        List<OrdenDeTrabajo> ordenes = ordenDeTrabajoService.filtrarOrdenes(filtros);
        List<OrdenDeTrabajoResponseDto> ordenesDto = ordenDeTrabajoResponseMapper.toDtoList(ordenes);
        return ResponseEntity.ok(ordenesDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenDeTrabajoResponseDto> modificarOrdenDeTrabajo(@PathVariable Long id, @RequestBody OrdenDeTrabajo orden) {
        return ordenDeTrabajoService.findById(id)
                .map(existingOrden -> {
                    validarModificacion(existingOrden);
                    actualizarOrden(existingOrden, orden);
                    OrdenDeTrabajo updated = ordenDeTrabajoService.save(existingOrden);
                    return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenDeTrabajoResponseDto> cancelarOrdenDeTrabajo(@PathVariable Long id) {
        try {
            OrdenDeTrabajo ordenCancelada = ordenDeTrabajoService.cancelarOrdenDeTrabajo(id);
            return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(ordenCancelada));
        } catch (Exception e) {
            log.error("Error al cancelar la orden de trabajo: {}", e.getMessage());
            // Considera devolver un ResponseEntity con un estado de error más específico
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/simular-cancelacion")
    public ResponseEntity<ar.utn.ccaffa.model.dto.CancelacionSimulacionDto> simularCancelacion(@PathVariable Long id) {
        try {
            ar.utn.ccaffa.model.dto.CancelacionSimulacionDto simulacion = ordenDeTrabajoService.simularCancelacion(id);
            return ResponseEntity.ok(simulacion);
        } catch (Exception e) {
            log.error("Error al simular la cancelación de la orden de trabajo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/obtenerOrdenesConRollo/{id}")
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerOrdenesDeTrabajoConRolloID(@PathVariable Long id) {
        List<OrdenDeTrabajo> ordenes = ordenDeTrabajoService.findByRolloId(id);
        List<OrdenDeTrabajoResponseDto> ordenesDto = ordenDeTrabajoResponseMapper.toDtoList(ordenes);
        return ResponseEntity.ok(ordenesDto);

    }


    @GetMapping("/obtenerOrdenesConMaquina/{id}")
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerOrdenesDeTrabajoConMaquina(@PathVariable Long id) {

      List<OrdenDeTrabajoMaquina> ordenesDeTrabajoMaquinas = ordenDeTrabajoMaquinaService.findByMaquinaId(id);
      List<OrdenDeTrabajo> ordenes = ordenesDeTrabajoMaquinas.stream().map(otm -> ordenDeTrabajoService.findByProcesoId(otm.getId())).toList();
      List<OrdenDeTrabajoResponseDto> ordenesDto = ordenDeTrabajoResponseMapper.toDtoList(ordenes);
      return ResponseEntity.ok(ordenesDto);
    }

    @GetMapping("/programaciones-maquinas/maquina/{id}")
    public ResponseEntity<Page<ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaDto>> obtenerProximaOrdenPendienteConMaquina(
            @PathVariable Long id,
            Pageable pageable) {

      Page<OrdenDeTrabajoMaquina> ordenesDeTrabajoMaquina = ordenDeTrabajoMaquinaService.findByMaquinaIdPaginated(id, pageable);
      
      if (ordenesDeTrabajoMaquina.isEmpty()) {
        return ResponseEntity.noContent().build();
      }
      
      Page<ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaDto> ordenesDto = ordenesDeTrabajoMaquina.map(ordenDeTrabajoMaquinaMapper::toDto);
      return ResponseEntity.ok(ordenesDto);
    }


    // Métodos privados para crear orden de trabajo
    private OrdenDeTrabajo crearOrdenBasica(OrdenDeTrabajoDto request) {
        OrdenDeTrabajo orden = new OrdenDeTrabajo();
        orden.setObservaciones(request.getObservaciones());
        return orden;
    }

    private OrdenVenta procesarOrdenVenta(OrdenDeTrabajoDto request) {
        if (request.getOrdenDeVentaId() == null) {
            return null;
        }

        Optional<OrdenVenta> ordenVentaOpt = ordenDeVentaRepository.findById(request.getOrdenDeVentaId());
        if (ordenVentaOpt.isEmpty()) {
            return null;
        }

        OrdenVenta ordenVenta = ordenVentaOpt.get();
        ordenVenta.setEstado(EstadoOrdenVentaEnum.PROGRAMADA);
        return ordenVenta;
    }

    private void procesarMaquinas(OrdenDeTrabajoDto request, OrdenDeTrabajo orden) {
        if (request.getMaquinas() == null) {
            return;
        }

        List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas = new ArrayList<>();

        for (OrdenDeTrabajoDto.MaquinaDto mreq : request.getMaquinas()) {
            Optional<Maquina> maquinaOpt = maquinaRepository.findById(mreq.getId());
            if (maquinaOpt.isEmpty()) {
                throw new IllegalArgumentException("Máquina no encontrada: " + mreq.getId());
            }

            OrdenDeTrabajoMaquina otm = crearOrdenDeTrabajoMaquina(orden, maquinaOpt.get(), mreq);
            ordenDeTrabajoMaquinas.add(otm);
        }

        orden.setOrdenDeTrabajoMaquinas(ordenDeTrabajoMaquinas);
    }

    private OrdenDeTrabajoMaquina crearOrdenDeTrabajoMaquina(OrdenDeTrabajo orden, Maquina maquina, OrdenDeTrabajoDto.MaquinaDto mreq) {
        return OrdenDeTrabajoMaquina.builder()
                .ordenDeTrabajo(orden)
                .maquina(maquina)
                .fechaInicio(mreq.getFechaInicio())
                .fechaFin(mreq.getFechaFin())
                .estado(EstadoOrdenTrabajoMaquinaEnum.valueOf(mreq.getEstado()))
                .observaciones(mreq.getObservaciones())
                .build();
    }

    private List<Rollo> procesarRollo(OrdenDeTrabajoDto request, OrdenDeTrabajo orden, OrdenVenta ordenVenta) {
        if (request.getRolloId() == null) {
            return null;
        }

        Optional<Rollo> rolloOpt = rolloRepository.findById(request.getRolloId());
        if (rolloOpt.isEmpty()) {
            throw new IllegalArgumentException("Rollo no encontrado");
        }

        Rollo rollo = rolloOpt.get();
        orden.setRollo(rollo);

        List<Rollo> rolloHijos = new ArrayList<>();

        if (ordenVenta != null && ordenVenta.getEspecificacion() != null) {
            rolloHijos.addAll(orden.procesarRollo());
        }
        return rolloHijos;


    }

    private void configurarOrdenFinal(OrdenDeTrabajo orden, OrdenDeTrabajoDto request) {
        orden.setFechaEstimadaDeInicio(request.getFechaInicio());
        orden.setFechaEstimadaDeFin(request.getFechaFin());
        orden.setObservaciones(request.getObservaciones());
        orden.setEstado(EstadoOrdenTrabajoEnum.PROGRAMADA);
    }


    // Métodos privados para validaciones
    private void actualizarOrden(OrdenDeTrabajo existingOrden, OrdenDeTrabajo nuevaOrden) {
        // Actualizar los campos necesarios de la orden existente con los valores de la nueva orden
        // Asegúrate de solo actualizar los campos que deberían ser modificables
        if (nuevaOrden.getFechaInicio() != null) {
            existingOrden.setFechaInicio(nuevaOrden.getFechaInicio());
        }
        if (nuevaOrden.getFechaFin() != null) {
            existingOrden.setFechaFin(nuevaOrden.getFechaFin());
        }
        if (nuevaOrden.getEstado() != null) {
            existingOrden.setEstado(nuevaOrden.getEstado());
        }
        // Agrega aquí cualquier otro campo que necesites actualizar
    }

    private void validarModificacion(OrdenDeTrabajo orden) {
        if (EstadoOrdenTrabajoEnum.is(orden.getEstado(), EstadoOrdenTrabajoEnum.EN_CURSO)) {
            throw new IllegalStateException("No se puede modificar una orden de trabajo que está en ejecución");
        }
    }





    // Métodos privados para liberación de recursos

    // Métodos estáticos para corte de bloques
    public static List<Bloque> cortarBloque(Bloque original, Float reqAncho, Float reqAlto) {
        validarDimensiones(original, reqAncho, reqAlto);

        List<Bloque> resultado = new ArrayList<>();

        // Sobrante vertical
        agregarSobranteVertical(resultado, original, reqAncho, reqAlto);

        // Sobrante horizontal
        agregarSobranteHorizontal(resultado, original, reqAncho, reqAlto);

        return resultado;
    }

    private static void validarDimensiones(Bloque original, Float reqAncho, Float reqLargo) {
        if (reqAncho > original.getAncho() || reqLargo > original.getLargo()) {
            throw new IllegalArgumentException("El bloque requerido es más grande que el original.");
        }
    }

    private static void agregarSobranteVertical(List<Bloque> resultado, Bloque original, Float reqAncho, Float reqLargo) {
        Float sobranteLargo = original.getLargo() - reqLargo;
        if (sobranteLargo > 0) {
            resultado.add(new Bloque(original.getX(), original.getY() + reqLargo, reqAncho, sobranteLargo));
        }
    }

    private static void agregarSobranteHorizontal(List<Bloque> resultado, Bloque original, Float reqAncho, Float reqLargo) {
        Float sobranteAncho = original.getAncho() - reqAncho;
        if (sobranteAncho > 0) {
            resultado.add(new Bloque(original.getX() + reqAncho, original.getY(), sobranteAncho, reqLargo));
        }
    }

}
