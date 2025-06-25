package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoResponseMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.repository.interfaces.*;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.model.dto.Bloque;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@RequiredArgsConstructor
public class OrdenDeTrabajoController {

    private final OrdenDeTrabajoService ordenDeTrabajoService;
    private final MaquinaRepository maquinaRepository;
    private final RolloRepository rolloRepository;
    private final OrdenVentaRepository ordenDeVentaRepository;
    private final OrdenDeTrabajoResponseMapper ordenDeTrabajoResponseMapper;

    @PostMapping
    public ResponseEntity<OrdenDeTrabajoResponseDto> crearOrdenDeTrabajo(@RequestBody OrdenDeTrabajoDto request) {
        try {
            OrdenDeTrabajo orden = crearOrdenBasica(request);
            
            OrdenVenta ordenVenta = procesarOrdenVenta(request, orden);
            
            procesarMaquinas(request, orden);
            
            procesarRollo(request, orden, ordenVenta);
            
            configurarOrdenFinal(orden, request);
            
            OrdenDeTrabajo guardada = guardarOrdenCompleta(orden, ordenVenta);
            return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(guardada));
            
        } catch (IllegalArgumentException e) {
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
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerTodasLasOrdenes() {
        List<OrdenDeTrabajo> ordenes = ordenDeTrabajoService.findAll();
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

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdenDeTrabajoResponseDto> cancelarOrdenDeTrabajo(@PathVariable Long id){
        //buscar rollos hijos y ordenes de trabajo para cancelarlas. Dejo el rollo padre y mando a replanificar la orden de venta y las que cancele
        return ordenDeTrabajoService.findById(id)
                .map(existingOrden -> {
                    validarCancelacion(existingOrden);
                    cancelarOrden(existingOrden);
                    OrdenDeTrabajo cancelada = ordenDeTrabajoService.save(existingOrden);
                    return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(cancelada));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Métodos privados para crear orden de trabajo
    private OrdenDeTrabajo crearOrdenBasica(OrdenDeTrabajoDto request) {
        OrdenDeTrabajo orden = new OrdenDeTrabajo();
        orden.setObservaciones(request.getObservaciones());
        return orden;
    }

    private OrdenVenta procesarOrdenVenta(OrdenDeTrabajoDto request, OrdenDeTrabajo orden) {
        if (request.getOrdenDeVentaId() == null) {
            return null;
        }
        
        Optional<OrdenVenta> ordenVentaOpt = ordenDeVentaRepository.findById(request.getOrdenDeVentaId());
        if (ordenVentaOpt.isEmpty()) {
            throw new IllegalArgumentException("Orden de venta no encontrada");
        }
        
        OrdenVenta ordenVenta = ordenVentaOpt.get();
        orden.setOrdenDeVenta(ordenVenta);
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
                .estado(mreq.getEstado())
                .observaciones(mreq.getObservaciones())
                .build();
    }

    private void procesarRollo(OrdenDeTrabajoDto request, OrdenDeTrabajo orden, OrdenVenta ordenVenta) {
        if (request.getRolloId() == null) {
            return;
        }
        
        Optional<Rollo> rolloOpt = rolloRepository.findById(request.getRolloId());
        if (rolloOpt.isEmpty()) {
            throw new IllegalArgumentException("Rollo no encontrado");
        }
        
        Rollo rollo = rolloOpt.get();
        orden.setRollo(rollo);
        rollo.setEstado(EstadoRollo.DIVIDO);

        if (ordenVenta != null && ordenVenta.getEspecificacion() != null) {
            List<Rollo> rolloHijos = crearRollosHijos(rollo, ordenVenta.getEspecificacion());
            asignarFechasRollosHijos(rolloHijos, orden.getOrdenDeTrabajoMaquinas());
            guardarRollosHijos(rolloHijos);
        }
    }

    private void configurarOrdenFinal(OrdenDeTrabajo orden, OrdenDeTrabajoDto request) {
        orden.setFechaEstimadaDeInicio(request.getFechaInicio());
        orden.setFechaEstimadaDeFin(request.getFechaFin());
        orden.setObservaciones(request.getObservaciones());
        orden.setEstado("En Proceso");
    }

    private OrdenDeTrabajo guardarOrdenCompleta(OrdenDeTrabajo orden, OrdenVenta ordenVenta) {
        OrdenDeTrabajo guardada = ordenDeTrabajoService.save(orden);
        
        if (ordenVenta != null) {
            ordenVenta.setOrdenDeTrabajo(guardada);
            ordenDeVentaRepository.save(ordenVenta);
        }
        
        return guardada;
    }

    // Métodos privados para validaciones
    private void validarModificacion(OrdenDeTrabajo orden) {
        if ("En Ejecucion".equals(orden.getEstado()) || "Ejecutando".equals(orden.getEstado())) {
            throw new IllegalStateException("No se puede modificar una orden de trabajo que está en ejecución");
        }
    }

    private void validarCancelacion(OrdenDeTrabajo orden) {
        if ("Cancelada".equals(orden.getEstado()) || "Completada".equals(orden.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una orden que ya está " + orden.getEstado());
        }
    }

    private void actualizarOrden(OrdenDeTrabajo existingOrden, OrdenDeTrabajo orden) {
        existingOrden.setNombre(orden.getNombre());
        existingOrden.setObservaciones(orden.getObservaciones());
        existingOrden.setFechaEstimadaDeInicio(orden.getFechaEstimadaDeInicio());
        existingOrden.setFechaEstimadaDeFin(orden.getFechaEstimadaDeFin());
        existingOrden.setEstado(orden.getEstado());
    }

    private void cancelarOrden(OrdenDeTrabajo orden) {
        orden.setEstado("Cancelada");
        orden.setActiva(false);
        liberarRecursos(orden);
    }

    // Métodos privados para gestión de rollos
    private List<Rollo> crearRollosHijos(Rollo rollo, Especificacion especificacion) {
        List<Rollo> rolloHijos = new ArrayList<>();
        List<Bloque> bloques = cortarBloque(
            new Bloque(0f, 0f, rollo.getAnchoMM(), rollo.getPesoKG()), 
            especificacion.getAncho(), 
            especificacion.getPesoMaximoPorRollo()
        );
        
        for (Bloque bloque : bloques) {
            Rollo rolloHijo = crearRolloHijo(rollo, bloque);
            rolloHijos.add(rolloHijo);
        }
        
        return rolloHijos;
    }

    private Rollo crearRolloHijo(Rollo rolloPadre, Bloque bloque) {
        return Rollo.builder()
                .proveedorId(rolloPadre.getProveedorId())
                .codigoProveedor(rolloPadre.getCodigoProveedor() + "_HIJO")
                .anchoMM(bloque.getAncho())
                .pesoKG(bloque.getLargo())
                .espesorMM(rolloPadre.getEspesorMM())
                .tipoMaterial(rolloPadre.getTipoMaterial())
                .estado(EstadoRollo.DISPONIBLE)
                .fechaIngreso(LocalDateTime.now())
                .rolloPadre(rolloPadre)
                .build();
    }

    private void asignarFechasRollosHijos(List<Rollo> rolloHijos, List<OrdenDeTrabajoMaquina> maquinas) {
        if (maquinas == null || maquinas.isEmpty()) {
            return;
        }
        
        LocalDateTime fechaIngreso = calcularFechaIngreso(maquinas);
        rolloHijos.forEach(rollo -> rollo.setFechaIngreso(fechaIngreso));
    }

    private LocalDateTime calcularFechaIngreso(List<OrdenDeTrabajoMaquina> maquinas) {
        if (maquinas == null || maquinas.isEmpty()) {
            throw new IllegalArgumentException("La lista de máquinas no puede estar vacía");
        }
        
        return maquinas.stream()
                .filter(m -> m.getMaquina() != null && MaquinaTipoEnum.CORTADORA.equals(m.getMaquina().getTipo()))
                .findFirst()
                .map(OrdenDeTrabajoMaquina::getFechaFin)
                .orElseGet(() -> maquinas.get(0).getFechaFin());
    }

    private void guardarRollosHijos(List<Rollo> rolloHijos) {
        rolloHijos.forEach(rolloRepository::save);
    }

    // Métodos privados para liberación de recursos
    private void liberarRecursos(OrdenDeTrabajo orden) {
        liberarMaquinas(orden);
        liberarRollo(orden);
        replanificarOrdenVenta(orden);
    }

    private void liberarMaquinas(OrdenDeTrabajo orden) {
        if (orden.getOrdenDeTrabajoMaquinas() != null) {
            orden.getOrdenDeTrabajoMaquinas().forEach(this::cancelarMaquina);
        }
    }

    private void cancelarMaquina(OrdenDeTrabajoMaquina otm) {
        otm.setEstado("Cancelada");
        otm.setFechaFin(LocalDateTime.now());
        otm.setObservaciones("Cancelada - " + otm.getObservaciones());
    }

    private void liberarRollo(OrdenDeTrabajo orden) {
        if (orden.getRollo() == null) {
            return;
        }
        
        Rollo rollo = orden.getRollo();
        if (rollo.getEstado() == EstadoRollo.AGOTADO) {
            rollo.setEstado(EstadoRollo.DISPONIBLE);
            rolloRepository.save(rollo);
        } else if (rollo.getEstado() == EstadoRollo.DIVIDO) {
            // TODO: Implementar lógica para rollos divididos
            procesarRolloDividido(rollo);
        }
    }

    private void procesarRolloDividido(Rollo rollo) {
        // TODO: Implementar lógica completa para rollos divididos
        // - Buscar rollos hijos
        // - Cancelar órdenes asociadas
        // - Replanificar órdenes de venta
    }

    private void replanificarOrdenVenta(OrdenDeTrabajo orden) {
        if (orden.getOrdenDeVenta() != null) {
            OrdenVenta ordenVenta = orden.getOrdenDeVenta();
            ordenVenta.setEstado("Replanificar");
            ordenDeVentaRepository.save(ordenVenta);
        }
    }

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
