package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloProductoMapper;
import ar.utn.ccaffa.model.dto.FiltroRolloDto;
import ar.utn.ccaffa.model.dto.FiltroRolloProductoDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.dto.RolloProductoDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.entity.RolloProducto;
import ar.utn.ccaffa.repository.interfaces.RolloProductoRepository;
import ar.utn.ccaffa.repository.interfaces.RolloRepository;
import ar.utn.ccaffa.services.interfaces.RolloProductoService;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class RolloProductoServiceImpl implements RolloProductoService {

    private final RolloProductoRepository rolloRepository;
    private final RolloProductoMapper rolloMapper;

    public RolloProductoServiceImpl(RolloProductoRepository rolloRepository, RolloProductoMapper rolloMapper) {
        this.rolloRepository = rolloRepository;
        this.rolloMapper = rolloMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolloProductoDto> findAll() {
        log.info("Buscando todos los rollos");
        return this.rolloMapper.toDtoList(rolloRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public RolloProductoDto findById(Long id) {
        log.info("Buscando rollo por ID: {}", id);
        return this.rolloMapper.toDto(rolloRepository.findById(id).orElse(RolloProducto.builder().build()));
    }



    @Override
    public RolloProductoDto save(RolloProductoDto rollo) {
        log.info("Guardando nuevo rollo: {}", rollo);
        RolloProducto rolloEntity = this.rolloMapper.toEntity(rollo);

        if (rollo.getRolloPadreId() != null) {
            Rollo padre = new Rollo();
            padre.setId(rollo.getRolloPadreId());
            rolloEntity.setRolloPadre(padre);
        } else {
            rolloEntity.setRolloPadre(null);
        }

        if (rollo.getOrdenDeTrabajoId() != null) {
            OrdenDeTrabajo orden = new OrdenDeTrabajo();
            orden.setId(rollo.getOrdenDeTrabajoId());
            rolloEntity.setOrdenDeTrabajo(orden);
        } else {
            rolloEntity.setOrdenDeTrabajo(null);
        }

        RolloProducto guardado = rolloRepository.save(rolloEntity);
        return this.rolloMapper.toDto(guardado);
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
    public List<RolloProductoDto> findByRolloPadreId(Long rolloId){
        return this.rolloMapper.toDtoList(rolloRepository.findByRolloPadreId(rolloId));
    };

    @Override
    public RolloProductoDto findByOrdenDeTrabajoId(Long ordenId){
        return this.rolloMapper.toDto(rolloRepository.findByOrdenDeTrabajoId(ordenId));
    };

    @Override
    public Optional<RolloProductoDto> findLastByOrdenDeVentaId(Long ordenId){
        Optional<RolloProducto> rolloProducto = rolloRepository.findLastByOrdenDeVentaId(ordenId);
        return rolloProducto.map(this.rolloMapper::toDto);

    }

    @Override
    public List<RolloProductoDto> filtrarRollosProducto(FiltroRolloProductoDto filtros) {

        Specification<RolloProducto> spec = Specification.where(null);

        if (filtros.getPesoMin() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("pesoKG"), filtros.getPesoMin()));
        }

        if (filtros.getPesoMax() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("pesoKG"), filtros.getPesoMax()));
        }

        if (filtros.getAnchoMin() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("anchoMM"), filtros.getAnchoMin()));
        }

        if (filtros.getAnchoMax() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("anchoMM"), filtros.getAnchoMax()));
        }

        if (filtros.getEspesorMin() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("espesorMM"), filtros.getEspesorMin()));
        }

        if (filtros.getEspesorMax() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("espesorMM"), filtros.getEspesorMax()));
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

        if (filtros.getOrdenDeTrabajoId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ordenDeTrabajo").get("id"), filtros.getOrdenDeTrabajoId()));
        }


        return this.rolloMapper.toDtoList(rolloRepository.findAll(spec));
    }

}