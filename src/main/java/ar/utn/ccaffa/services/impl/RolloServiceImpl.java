package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.FiltroRolloDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.model.entity.Especificacion;
import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.repository.interfaces.RolloRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import ar.utn.ccaffa.model.dto.ModificarRolloRequestDto;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;

@Service
@Slf4j
@Transactional
public class RolloServiceImpl implements RolloService {

    private final RolloRepository rolloRepository;
    private final RolloMapper rolloMapper;
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenDeTrabajoService ordenDeTrabajoService;

    public RolloServiceImpl(RolloRepository rolloRepository, RolloMapper rolloMapper, OrdenVentaRepository ordenVentaRepository, OrdenDeTrabajoService ordenDeTrabajoService) {
        this.rolloRepository = rolloRepository;
        this.rolloMapper = rolloMapper;
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenDeTrabajoService = ordenDeTrabajoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolloDto> findAll() {
        log.info("Buscando todos los rollos");
        return this.rolloMapper.toDtoList(rolloRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public RolloDto findById(Long id) {
        log.info("Buscando rollo por ID: {}", id);
        return this.rolloMapper.toDtoOnlyWithRolloPadreID(rolloRepository.findById(id).orElse(Rollo.builder().build()));
    }

    @Override
    @Transactional(readOnly = true)
    public RolloDto findByIdConRollosPadres(Long id) {
        log.info("Buscando rollo por ID: {}", id);
        return this.rolloMapper.toDto(rolloRepository.findById(id).orElse(Rollo.builder().build()));
    }

    @Override
    public RolloDto save(RolloDto rollo) {
        log.info("Guardando nuevo rollo: {}", rollo);
        Rollo rolloEntity = this.rolloMapper.toEntity(rollo);

        if (rollo.getRolloPadreId() != null) {
            Rollo padre = new Rollo();
            padre.setId(rollo.getRolloPadreId());
            rolloEntity.setRolloPadre(padre);
        } else {
            rolloEntity.setRolloPadre(null);
        }

        Rollo guardado = rolloRepository.save(rolloEntity);
        return this.rolloMapper.toDtoOnlyWithRolloPadreID(guardado);
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Eliminando rollo con ID: {}", id);
        if (!rolloRepository.existsById(id)) {
            log.warn("No se encontró el rollo con ID: {}", id);
            return false;
        }
        rolloRepository.deleteById(id);
        return true;
    }

    @Override
    public RolloDto obtenerArbolCompletoDeRollosHijos(Long rolloId) {

        Rollo root = rolloRepository.findById(rolloId).orElse(Rollo.builder().build());

        inicializarRecursivamente(root);

        return rolloMapper.toDtoWithRolloHijos(root);
    }
    private void inicializarRecursivamente(Rollo rollo) {
        Hibernate.initialize(rollo.getHijos());
        if (rollo.getHijos() != null) {
            for (Rollo hijo : rollo.getHijos()) {
                inicializarRecursivamente(hijo);
            }
        }
    }
    @Override
    public List<RolloDto> filtrarRollos(FiltroRolloDto filtros) {

        Specification<Rollo> spec = Specification.where(null);

        if (filtros.getProveedorId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("proveedorId"), filtros.getProveedorId()));
        }

        if (filtros.getCodigoProveedor() != null && !filtros.getCodigoProveedor().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("codigoProveedor")), "%" + filtros.getCodigoProveedor().toLowerCase() + "%"));
        }

        if (filtros.getPesoMin() != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("pesoKG"), filtros.getPesoMin()));
        }

        if (filtros.getPesoMax() != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("pesoKG"), filtros.getPesoMax()));
        }

        if (filtros.getAnchoMin() != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("anchoMM"), filtros.getAnchoMin()));
        }

        if (filtros.getAnchoMax() != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("anchoMM"), filtros.getAnchoMax()));
        }

        if (filtros.getEspesorMin() != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("espesorMM"), filtros.getEspesorMin()));
        }

        if (filtros.getEspesorMax() != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("espesorMM"), filtros.getEspesorMax()));
        }

        if (filtros.getTipoMaterial() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoMaterial"), filtros.getTipoMaterial()));
        }

        if (filtros.getEstado() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), filtros.getEstado()));
        }

        if (filtros.getFechaIngresoDesde() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaIngreso"), filtros.getFechaIngresoDesde()));
        }

        if (filtros.getFechaIngresoHasta() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaIngreso"), filtros.getFechaIngresoHasta()));
        }

        if (filtros.getRolloPadreId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rolloPadre").get("id"), filtros.getRolloPadreId()));
        }

        return this.rolloMapper.toDtoListOnlyWithRolloPadreID(rolloRepository.findAll(spec));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolloDto> obtenerRollosDisponiblesParaOrdenVenta(Long ordenVentaId) {
        log.info("Buscando rollos disponibles para orden de venta ID: {}", ordenVentaId);
        
        OrdenVenta ordenVenta = ordenVentaRepository.findById(ordenVentaId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de venta", "id", ordenVentaId));
        
        if (ordenVenta.getEspecificacion() == null) {
            log.warn("La orden de venta {} no tiene especificaciones", ordenVentaId);
            return List.of();
        }
        
        Especificacion especificacion = ordenVenta.getEspecificacion();
        
        Specification<Rollo> spec = Specification.where(null);
        
        spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), EstadoRollo.DISPONIBLE));
        
        if (especificacion.getTipoMaterial() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoMaterial"), especificacion.getTipoMaterial()));
        }
        
        if (especificacion.getAncho() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("anchoMM"), especificacion.getAncho()));
        }
        
        if (especificacion.getEspesor() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("espesorMM"), especificacion.getEspesor()));
        }
        
        if (especificacion.getPesoMaximoPorRollo() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("pesoKG"), especificacion.getPesoMaximoPorRollo()));
        }
        
        List<Rollo> rollosDisponibles = rolloRepository.findAll(spec);
        log.info("Encontrados {} rollos disponibles para la orden de venta {}", rollosDisponibles.size(), ordenVentaId);
        
        return this.rolloMapper.toDtoListOnlyWithRolloPadreID(rollosDisponibles);
    }

    @Override
    public RolloDto modificarRollo(ModificarRolloRequestDto request) {
        log.info("Modificando rollo con ID: {}", request.getId());
        
        Rollo rollo = rolloRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rollo", "id", request.getId()));
        
        Float pesoOriginal = rollo.getPesoKG();
        Float anchoOriginal = rollo.getAnchoMM();
        
        if (request.getPesoKG() != null) {
            rollo.setPesoKG(request.getPesoKG());
            log.info("Peso del rollo {} actualizado a: {} kg", request.getId(), request.getPesoKG());
        }
        
        if (request.getAnchoMM() != null) {
            rollo.setAnchoMM(request.getAnchoMM());
            log.info("Ancho del rollo {} actualizado a: {} mm", request.getId(), request.getAnchoMM());
        }
        
        // Verificar si se redujo el peso o ancho
        boolean pesoReducido = request.getPesoKG() != null && request.getPesoKG() < pesoOriginal;
        boolean anchoReducido = request.getAnchoMM() != null && request.getAnchoMM() < anchoOriginal;
        
        if (pesoReducido || anchoReducido) {
            log.info("Se detectó reducción en el rollo {}. Peso reducido: {}, Ancho reducido: {}", 
                    request.getId(), pesoReducido, anchoReducido);
            
            procesarCancelacionOrdenesTrabajo(rollo);
        }
        
        Rollo rolloModificado = rolloRepository.save(rollo);
        log.info("Rollo {} modificado exitosamente", request.getId());
        
        return this.rolloMapper.toDtoOnlyWithRolloPadreID(rolloModificado);
    }
    
    /**
     * Procesa la cancelación de órdenes de trabajo y replanificación de órdenes de venta
     * cuando se reduce el peso o ancho de un rollo
     */
    private void procesarCancelacionOrdenesTrabajo(Rollo rollo) {
        log.info("Procesando cancelación de órdenes de trabajo para rollo {}", rollo.getId());
        
        // Buscar todas las órdenes de trabajo asociadas al rollo
        List<OrdenDeTrabajo> ordenesTrabajo = ordenDeTrabajoService.findByRolloId(rollo.getId());
        
        if (ordenesTrabajo.isEmpty()) {
            log.info("No se encontraron órdenes de trabajo para el rollo {}", rollo.getId());
            return;
        }
        
        log.info("Encontradas {} órdenes de trabajo para cancelar", ordenesTrabajo.size());
        
        for (OrdenDeTrabajo ordenTrabajo : ordenesTrabajo) {
            // Solo cancelar si no está ya cancelada
            if (!"Cancelada".equals(ordenTrabajo.getEstado())) {
                log.info("Cancelando orden de trabajo ID: {}", ordenTrabajo.getId());
                
                // Cancelar la orden de trabajo
                ordenTrabajo.setEstado("Cancelada");
                ordenTrabajo.setActiva(false);
                ordenTrabajo.setObservaciones("Cancelada - Rollo modificado (peso/ancho reducido)");
                ordenTrabajo.setFechaFin(LocalDateTime.now());
                
                // Guardar la orden de trabajo cancelada
                ordenDeTrabajoService.save(ordenTrabajo);
                

                log.info("Replanificando orden de venta ID: {}", ordenTrabajo.getOrdenDeVenta().getId());
                ordenTrabajo.getOrdenDeVenta().setEstado("REPLANIFICADO");
                ordenVentaRepository.save(ordenTrabajo.getOrdenDeVenta());
            
            }
        }
        
        log.info("Proceso de cancelación completado para el rollo {}", rollo.getId());
    }
    
    @Override
    public boolean anularRollo(Long id) {
        log.info("Anulando rollo con ID: {}", id);
        
        Rollo rollo = rolloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rollo", "id", id));
        
        procesarCancelacionOrdenesTrabajo(rollo);
        
        rollo.setEstado(EstadoRollo.CANCELADO);
        rolloRepository.save(rollo);
        
        log.info("Rollo {} anulado exitosamente", id);
        return true;
    }
}