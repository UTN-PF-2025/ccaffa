package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.*;
import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoResponseMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.repository.interfaces.*;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.model.dto.Bloque;
import ar.utn.ccaffa.model.dto.FiltroOrdenDeTrabajoDto;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @PostMapping
    public ResponseEntity<OrdenDeTrabajoResponseDto> crearOrdenDeTrabajo(@RequestBody OrdenDeTrabajoDto request) {
        try {
            // 1. Crear y guardar la orden de trabajo básica para obtener un ID
            OrdenDeTrabajo orden = crearOrdenBasica(request);
            ordenDeTrabajoService.save(orden); // Guardado inicial

            // 2. Procesar y asociar la orden de venta
            OrdenVenta ordenVenta = procesarOrdenVenta(request, orden);
            if (ordenVenta != null) {
                ordenDeVentaRepository.save(ordenVenta);
            }

            // 3. Procesar y asociar máquinas
            procesarMaquinas(request, orden);

            // 4. Procesar y asociar el rollo y sus hijos
            procesarRollo(request, orden, ordenVenta);

            // 5. Configurar los datos finales de la orden
            configurarOrdenFinal(orden, request);

            // 6. Guardar la orden de trabajo completa con todas sus asociaciones
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

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdenDeTrabajoResponseDto> cancelarOrdenDeTrabajo(@PathVariable Long id) {
        return ordenDeTrabajoService.findById(id)
                .map(ordenACancelar -> {
                    try {
                        validarCancelacion(ordenACancelar);
                        
                        // 1. Obtener el rollo actual y su padre si existe
                        Rollo rolloActual = ordenACancelar.getRollo();
                        Rollo rolloPadre = rolloActual != null ? rolloActual.getRolloPadre() : null;

                        // 2. Inicializar conjuntos para órdenes de venta y trabajo a procesar
                        Set<OrdenVenta> ordenesVentaAReplanificar = new HashSet<>();
                        Set<OrdenDeTrabajo> ordenesTrabajoACancelar = new HashSet<>();
                        Set<Rollo> rollosACancelar = new HashSet<>(); // Para manejar el estado de los rollos
                        
                        // 3. Agregar la orden actual a las órdenes a cancelar
                        ordenesTrabajoACancelar.add(ordenACancelar);
                        if (rolloActual != null) {
                            rollosACancelar.add(rolloActual);
                        }
                        
                        // 4. Si tiene un rollo, procesar la jerarquía completa
                        if (rolloActual != null) {
                            // 4.1. Si tiene padre, procesar ancestros, hermanos y descendientes
                            if (rolloPadre != null) {
                                // 4.1.1. Procesar ancestros (padre, abuelo, etc.)
                                procesarAncestros(rolloPadre, ordenesVentaAReplanificar, ordenesTrabajoACancelar);
                                
                                // 4.1.2. Obtener todos los rollos del mismo nivel (hermanos)
                                List<Rollo> rollosHermanos = rolloRepository.findByRolloPadreId(rolloPadre.getId());
                                
                                // 4.1.3. Procesar cada rollo hermano
                                for (Rollo rolloHermano : rollosHermanos) {
                                    // Agregar el rollo hermano a la lista de rollos a cancelar
                                    rollosACancelar.add(rolloHermano);
                                    
                                    // Obtener todas las órdenes de trabajo del hermano
                                    List<OrdenDeTrabajo> ordenesHermano = ordenDeTrabajoService.findByRolloId(rolloHermano.getId());
                                    for (OrdenDeTrabajo ordenHermano : ordenesHermano) {
                                        // Agregar la orden de venta a replanificar si existe
                                        if (ordenHermano.getOrdenDeVenta() != null) {
                                            ordenesVentaAReplanificar.add(ordenHermano.getOrdenDeVenta());
                                        }
                                        // Agregar la orden de trabajo a cancelar
                                        ordenesTrabajoACancelar.add(ordenHermano);
                                    }
                                    
                                    // Procesar descendientes del hermano (hijos, nietos, etc.)
                                    procesarDescendientes(rolloHermano, ordenesVentaAReplanificar, ordenesTrabajoACancelar, rollosACancelar);
                                }
                                
                                // 4.1.4. Marcar el padre como disponible
                                rolloPadre.setEstado(EstadoRollo.DISPONIBLE);
                                rolloRepository.save(rolloPadre);
                            } else {
                                // 4.2. Si no tiene padre, procesar solo los descendientes del rollo actual
                                procesarDescendientes(rolloActual, ordenesVentaAReplanificar, ordenesTrabajoACancelar, rollosACancelar);
                            }
                        }
                        
                        // 5. Cancelar todas las órdenes de trabajo
                        for (OrdenDeTrabajo orden : ordenesTrabajoACancelar) {
                            if (!EstadoOrdenTrabajoEnum.is(orden.getEstado(), EstadoOrdenTrabajoEnum.ANULADA)) {
                                cancelarOrden(orden);
                                ordenDeTrabajoService.save(orden);
                                
                                // Agregar la orden de venta a replanificar si existe
                                if (orden.getOrdenDeVenta() != null) {
                                    ordenesVentaAReplanificar.add(orden.getOrdenDeVenta());
                                }
                            }
                        }
                        
                        // 6. Actualizar el estado de los rollos
                        for (Rollo rollo : rollosACancelar) {
                            // Solo actualizar si no es el rollo padre
                            if (rolloPadre == null || !rollo.getId().equals(rolloPadre.getId())) {
                                rollo.setEstado(EstadoRollo.CANCELADO);
                                rolloRepository.save(rollo);
                            }
                        }

                        // 7. Replanificar todas las órdenes de venta afectadas
                        for (OrdenVenta ordenVenta : ordenesVentaAReplanificar) {
                            ordenDeVentaRepository.updateOrdenDeVentaEstado(ordenVenta.getId(), EstadoOrdenVentaEnum.REPLANIFICAR.name());
                        }

                        return ResponseEntity.ok(ordenDeTrabajoResponseMapper.toDto(ordenACancelar));
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cancelar la orden de trabajo: " + e.getMessage(), e);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/obtenerOrdenesConRollo/{id}")
    public ResponseEntity<List<OrdenDeTrabajoResponseDto>> obtenerOrdenesDeTrabajoConRolloID(@PathVariable Long id) {
        List<OrdenDeTrabajo> ordenes = ordenDeTrabajoService.findByRolloId(id);
        List<OrdenDeTrabajoResponseDto> ordenesDto = ordenDeTrabajoResponseMapper.toDtoList(ordenes);
        return ResponseEntity.ok(ordenesDto);

    }


    /**
     * Procesa recursivamente los ancestros de un rollo (padre, abuelo, etc.)
     * para recolectar órdenes de venta y trabajo
     */
    private void procesarAncestros(Rollo rollo, Set<OrdenVenta> ordenesVenta, Set<OrdenDeTrabajo> ordenesTrabajo) {
        if (rollo == null) {
            return;
        }
        
        // Obtener todas las órdenes de trabajo del ancestro actual
        List<OrdenDeTrabajo> ordenesAncestro = ordenDeTrabajoService.findByRolloId(rollo.getId());
        
        for (OrdenDeTrabajo orden : ordenesAncestro) {
            // Agregar la orden de venta si existe
            if (orden.getOrdenDeVenta() != null) {
                ordenesVenta.add(orden.getOrdenDeVenta());
            }
            // Agregar la orden de trabajo a cancelar
            ordenesTrabajo.add(orden);
        }
        
        // Continuar con el siguiente ancestro
        if (rollo.getRolloPadre() != null) {
            procesarAncestros(rollo.getRolloPadre(), ordenesVenta, ordenesTrabajo);
        }
    }

    /**
     * Procesa recursivamente los descendientes de un rollo para recolectar órdenes de venta y trabajo
     * @param rollo Rollo del que se procesarán los descendientes
     * @param ordenesVenta Conjunto donde se agregarán las órdenes de venta encontradas
     * @param ordenesTrabajo Conjunto donde se agregarán las órdenes de trabajo encontradas
     * @param rollosACancelar Conjunto donde se agregarán los rollos que deben ser cancelados
     */
    private void procesarDescendientes(Rollo rollo, Set<OrdenVenta> ordenesVenta, 
                                     Set<OrdenDeTrabajo> ordenesTrabajo, Set<Rollo> rollosACancelar) {
        // Obtener todos los hijos directos
        List<Rollo> hijos = rolloRepository.findByRolloPadreId(rollo.getId());
        
        for (Rollo hijo : hijos) {
            // Agregar el rollo hijo a la lista de rollos a cancelar
            rollosACancelar.add(hijo);
            
            // Obtener todas las órdenes de trabajo del hijo
            List<OrdenDeTrabajo> ordenesHijo = ordenDeTrabajoService.findByRolloId(hijo.getId());
            
            for (OrdenDeTrabajo orden : ordenesHijo) {
                // Agregar la orden de venta si existe
                if (orden.getOrdenDeVenta() != null) {
                    ordenesVenta.add(orden.getOrdenDeVenta());
                }
                // Agregar la orden de trabajo a cancelar
                ordenesTrabajo.add(orden);
            }
            
            // Procesar recursivamente los descendientes
            procesarDescendientes(hijo, ordenesVenta, ordenesTrabajo, rollosACancelar);
        }
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
        ordenVenta.setOrdenDeTrabajo(orden);
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
                .estado(EstadoOrdenTrabajoMaquinaEnum.valueOf(mreq.getEstado()))
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

        if (ordenVenta != null && ordenVenta.getEspecificacion() != null) {
            List<Rollo> rolloHijos = orden.procesarRollo();
            rolloRepository.saveAll(rolloHijos);
        }
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

    private void validarCancelacion(OrdenDeTrabajo orden) {
        if (EstadoOrdenTrabajoEnum.in(orden.getEstado(), EstadoOrdenTrabajoEnum.ANULADA, EstadoOrdenTrabajoEnum.FINALIZADA)) {
            throw new IllegalStateException("No se puede cancelar una orden que ya está " + orden.getEstado());
        }
    }


    private OrdenDeTrabajo cancelarOrden(OrdenDeTrabajo orden) {
        orden.setEstado(EstadoOrdenTrabajoEnum.ANULADA);
        orden.setActiva(false);
        liberarRecursos(orden);
        return orden;
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
        otm.setEstado(EstadoOrdenTrabajoMaquinaEnum.ANULADA);
        otm.setFechaFin(LocalDateTime.now());
        otm.setObservaciones("Cancelada - " + otm.getObservaciones());
    }

    private void liberarRollo(OrdenDeTrabajo orden) {
        if (orden.getRollo() == null) {
            return;
        }

        Rollo rollo = orden.getRollo();
        if (rollo.getEstado().equals(EstadoRollo.AGOTADO)) {
            rollo.setEstado(EstadoRollo.DISPONIBLE);
            rolloRepository.save(rollo);
        } else if (rollo.getEstado().equals(EstadoRollo.DIVIDO)) {
            procesarRolloDividido(rollo);
        }
    }

    private void procesarRolloDividido(Rollo rollo) {
        // Buscar todos los rollos hijos
        List<Rollo> rollosHijos = rolloRepository.findByRolloPadreId(rollo.getId());

        // Para cada rollo hijo, buscar sus órdenes de trabajo y cancelarlas
        for (Rollo rolloHijo : rollosHijos) {
            // Buscar órdenes de trabajo asociadas al rollo hijo
            List<OrdenDeTrabajo> ordenesTrabajoHijo = ordenDeTrabajoService.findByRolloId(rolloHijo.getId());

            // Cancelar cada orden de trabajo asociada
            for (OrdenDeTrabajo ordenHijo : ordenesTrabajoHijo) {
                if (!EstadoOrdenTrabajoEnum.is(ordenHijo.getEstado(), EstadoOrdenTrabajoEnum.ANULADA)) {
                    cancelarOrden(ordenHijo);
                    ordenDeTrabajoService.save(ordenHijo);

                    // Replanificar la orden de venta asociada si existe
                    if (ordenHijo.getOrdenDeVenta() != null) {
                        replanificarOrdenVenta(ordenHijo);
                    }
                }
            }

            // Actualizar el estado del rollo hijo
            rolloHijo.setEstado(EstadoRollo.CANCELADO);
            rolloRepository.save(rolloHijo);
        }

        // Actualizar el estado del rollo padre
        rollo.setEstado(EstadoRollo.DISPONIBLE);
        rolloRepository.save(rollo);
    }

    private void replanificarOrdenVenta(OrdenDeTrabajo orden) {
        if (orden.getOrdenDeVenta() != null) {
            ordenDeVentaRepository.updateOrdenDeVentaEstado(orden.getOrdenDeVenta().getId(), EstadoOrdenVentaEnum.REPLANIFICAR.name());
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

    /**
     * Método auxiliar para cancelar solo las órdenes de trabajo de un rollo
     */
    private void cancelarOrdenesDeTrabajo(Rollo rollo, Set<OrdenVenta> ordenesVentaAReplanificar) {
        List<OrdenDeTrabajo> ordenesDelRollo = ordenDeTrabajoService.findByRolloId(rollo.getId());
        for (OrdenDeTrabajo orden : ordenesDelRollo) {
            if (!EstadoOrdenTrabajoEnum.is(orden.getEstado(), EstadoOrdenTrabajoEnum.ANULADA)) {
                cancelarOrden(orden);
                ordenDeTrabajoService.save(orden);
                
                if (orden.getOrdenDeVenta() != null) {
                    ordenesVentaAReplanificar.add(orden.getOrdenDeVenta());
                }
            }
        }
    }
}
