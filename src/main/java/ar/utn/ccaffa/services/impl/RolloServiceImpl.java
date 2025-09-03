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

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class RolloServiceImpl implements RolloService {

    private final RolloRepository rolloRepository;
    private final RolloMapper rolloMapper;
    private final OrdenVentaRepository ordenVentaRepository;

    public RolloServiceImpl(RolloRepository rolloRepository, RolloMapper rolloMapper, OrdenVentaRepository ordenVentaRepository) {
        this.rolloRepository = rolloRepository;
        this.rolloMapper = rolloMapper;
        this.ordenVentaRepository = ordenVentaRepository;
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
    public RolloDto obtenerArbolCompletoDeHijos(Long rolloId) {

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
    public List<Rollo> findEntitiesByEstado(EstadoRollo estadoRollo){
        return rolloRepository.findByEstado(estadoRollo);
    }
    @Override
    public List<Rollo> findEntitiesByIdIn(List<Long> ids){
        return rolloRepository.findByIdIn(ids);
    }

}