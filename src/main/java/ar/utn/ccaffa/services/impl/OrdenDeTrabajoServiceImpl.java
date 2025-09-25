package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.dto.FiltroOrdenDeTrabajoDto;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoResponseMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.CancelacionSimulacionDto;
import ar.utn.ccaffa.model.dto.OrdenVentaSimpleDto;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.repository.interfaces.RolloRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class OrdenDeTrabajoServiceImpl implements OrdenDeTrabajoService {
    private final OrdenDeTrabajoRepository repository;
    private final OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository;
    private final RolloRepository rolloRepository;
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenDeTrabajoResponseMapper ordenDeTrabajoResponseMapper;
    private final RolloMapper rolloMapper;

    public OrdenDeTrabajoServiceImpl(OrdenDeTrabajoRepository repository,
                                     OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository,
                                     RolloRepository rolloRepository,
                                     OrdenVentaRepository ordenVentaRepository,
                                     OrdenDeTrabajoResponseMapper ordenDeTrabajoResponseMapper, RolloMapper rolloMapper) {
        this.repository = repository;
        this.ordenDeTrabajoMaquinaRepository = ordenDeTrabajoMaquinaRepository;
        this.rolloRepository = rolloRepository;
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenDeTrabajoResponseMapper = ordenDeTrabajoResponseMapper;
        this.rolloMapper = rolloMapper;
    }
    @Override
    public OrdenDeTrabajo save(OrdenDeTrabajo orden) {
        return repository.save(orden);
    }

    @Override
    public List<OrdenDeTrabajo> saveAllDtos(List<OrdenDeTrabajoResponseDto> ordenes) {
        List<OrdenDeTrabajo> ordenDeTrabajos = this.ordenDeTrabajoResponseMapper.toEntityList(ordenes);
        return this.repository.saveAll(ordenDeTrabajos);

    }

    @Override
    public List<OrdenDeTrabajo> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<OrdenDeTrabajo> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<OrdenDeTrabajo> update(Long id, OrdenDeTrabajo orden) {
        return repository.findById(id).map(existing -> {
            orden.setId(id);
            return repository.save(orden);
        });
    }

    @Override
    public Optional<OrdenDeTrabajo> desactivar(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setActiva(false);
            return repository.save(existing);
        });
    }

    @Override
    public OrdenDeTrabajo cancelarOrdenDeTrabajo(Long id) {
        OrdenDeTrabajo ordenACancelar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de trabajo no encontrada"));

        validarCancelacion(ordenACancelar);

        CancelacionAfectados afectados = recolectarAfectados(ordenACancelar);

        // 1. Cancelar todas las órdenes de trabajo
        afectados.getOrdenesTrabajoACancelar().forEach(orden -> {
            if (!"Cancelada".equals(orden.getEstado())) {
                cancelarOrden(orden);
                repository.save(orden);
            }
        });

        // 2. Actualizar el estado de los rollos
        afectados.getRollosACancelar().forEach(rollo -> {
            rollo.setEstado(EstadoRollo.CANCELADO);
            rolloRepository.save(rollo);
        });

        // 3. Marcar el rollo padre como disponible si existe
        if (afectados.getRolloPadre() != null) {
            afectados.getRolloPadre().setEstado(EstadoRollo.DISPONIBLE);
            rolloRepository.save(afectados.getRolloPadre());
        }

        // 4. Replanificar todas las órdenes de venta afectadas
        afectados.getOrdenesVentaAReplanificar().forEach(ordenVenta -> {
            ordenVenta.setEstado("Replanificar");
            this.ordenVentaRepository.save(ordenVenta);
        });

        return ordenACancelar;
    }

    @Override
    public CancelacionSimulacionDto simularCancelacion(Long id) {
        OrdenDeTrabajo ordenACancelar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de trabajo no encontrada"));

        validarCancelacion(ordenACancelar);

        CancelacionAfectados afectados = recolectarAfectados(ordenACancelar);

        // Mapear entidades a DTOs
        List<OrdenVentaSimpleDto> ordenesVentaDto = afectados.getOrdenesVentaAReplanificar().stream()
                .map(this::mapToOrdenVentaSimpleDto) // Método de mapeo manual
                .toList();

        return CancelacionSimulacionDto.builder()
                .ordenesVentaAReplanificar(ordenesVentaDto)
                .ordenesTrabajoACancelar(ordenDeTrabajoResponseMapper.toDtoList(afectados.getOrdenesTrabajoACancelar().stream().toList()))
                .rollosACancelar(rolloMapper.toDtoList(afectados.getRollosACancelar().stream().toList()))
                .build();
    }

    private CancelacionAfectados recolectarAfectados(OrdenDeTrabajo ordenACancelar) {
        Set<OrdenVenta> ordenesVentaAReplanificar = new HashSet<>();
        Set<OrdenDeTrabajo> ordenesTrabajoACancelar = new HashSet<>();
        Set<Rollo> rollosACancelar = new HashSet<>();

        ordenesTrabajoACancelar.add(ordenACancelar);
        if (ordenACancelar.getOrdenDeVenta() != null) {
            ordenesVentaAReplanificar.add(ordenACancelar.getOrdenDeVenta());
        }

        Rollo rolloActual = ordenACancelar.getRollo();
        Rollo rolloPadre = null;

        if (rolloActual != null) {
            rollosACancelar.add(rolloActual);
            rolloPadre = rolloActual.getRolloPadre();

            if (rolloPadre != null) {
                procesarAncestros(rolloPadre, ordenesVentaAReplanificar, ordenesTrabajoACancelar, rollosACancelar);

                List<Rollo> rollosHermanos = rolloRepository.findByRolloPadreId(rolloPadre.getId());
                for (Rollo rolloHermano : rollosHermanos) {
                    rollosACancelar.add(rolloHermano);
                    List<OrdenDeTrabajo> ordenesHermano = findByRolloId(rolloHermano.getId());
                    for (OrdenDeTrabajo ordenHermano : ordenesHermano) {
                        if (ordenHermano.getOrdenDeVenta() != null) {
                            ordenesVentaAReplanificar.add(ordenHermano.getOrdenDeVenta());
                        }
                        ordenesTrabajoACancelar.add(ordenHermano);
                    }
                    procesarDescendientes(rolloHermano, ordenesVentaAReplanificar, ordenesTrabajoACancelar, rollosACancelar);
                }
            } else {
                procesarDescendientes(rolloActual, ordenesVentaAReplanificar, ordenesTrabajoACancelar, rollosACancelar);
            }
        }

        return new CancelacionAfectados(ordenesVentaAReplanificar, ordenesTrabajoACancelar, rollosACancelar, rolloPadre);
    }

    private void procesarAncestros(Rollo rollo, Set<OrdenVenta> ordenesVenta, Set<OrdenDeTrabajo> ordenesTrabajo, Set<Rollo> rollosACancelar) {
        if (rollo == null) {
            return;
        }
        rollosACancelar.add(rollo);
        List<OrdenDeTrabajo> ordenesAncestro = findByRolloId(rollo.getId());
        for (OrdenDeTrabajo orden : ordenesAncestro) {
            if (orden.getOrdenDeVenta() != null) {
                ordenesVenta.add(orden.getOrdenDeVenta());
            }
            ordenesTrabajo.add(orden);
        }
        if (rollo.getRolloPadre() != null) {
            procesarAncestros(rollo.getRolloPadre(), ordenesVenta, ordenesTrabajo, rollosACancelar);
        }
    }

    private void procesarDescendientes(Rollo rollo, Set<OrdenVenta> ordenesVenta, Set<OrdenDeTrabajo> ordenesTrabajo, Set<Rollo> rollosACancelar) {
        List<Rollo> hijos = rolloRepository.findByRolloPadreId(rollo.getId());
        for (Rollo hijo : hijos) {
            rollosACancelar.add(hijo);
            List<OrdenDeTrabajo> ordenesHijo = findByRolloId(hijo.getId());
            for (OrdenDeTrabajo orden : ordenesHijo) {
                if (orden.getOrdenDeVenta() != null) {
                    ordenesVenta.add(orden.getOrdenDeVenta());
                }
                ordenesTrabajo.add(orden);
            }
            procesarDescendientes(hijo, ordenesVenta, ordenesTrabajo, rollosACancelar);
        }
    }

    private void validarCancelacion(OrdenDeTrabajo orden) {
        if ("Cancelada".equals(orden.getEstado()) || "Completada".equals(orden.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una orden que ya está " + orden.getEstado());
        }
    }

    private void cancelarOrden(OrdenDeTrabajo orden) {
        orden.setEstado("Cancelada");
        orden.setActiva(false);
    }

    // Método de mapeo manual para OrdenVenta a OrdenVentaSimpleDto
    private OrdenVentaSimpleDto mapToOrdenVentaSimpleDto(OrdenVenta ordenVenta) {
        if (ordenVenta == null) {
            return null;
        }
        // Aquí necesitaríamos los mappers para Cliente y Especificacion, si no los tenemos, los dejamos como null
        // ClienteDto clienteDto = (ordenVenta.getCliente() != null && clienteMapper != null) ? clienteMapper.toDto(ordenVenta.getCliente()) : null;
        // EspecificacionDto especificacionDto = (ordenVenta.getEspecificacion() != null && especificacionMapper != null) ? especificacionMapper.toDto(ordenVenta.getEspecificacion()) : null;

        return OrdenVentaSimpleDto.builder()
                .orderId(ordenVenta.getId())
                .fechaCreacion(ordenVenta.getFechaCreacion().toString())
                .fechaEntregaEstimada(ordenVenta.getFechaEntregaEstimada().toString())
                .estado(ordenVenta.getEstado())
                .observaciones(ordenVenta.getObservaciones())
                // .cliente(clienteDto)
                // .especificacion(especificacionDto)
                .build();
    }

    // Clase contenedora privada para los resultados de la recolección
    private static class CancelacionAfectados {
        private final Set<OrdenVenta> ordenesVentaAReplanificar;
        private final Set<OrdenDeTrabajo> ordenesTrabajoACancelar;
        private final Set<Rollo> rollosACancelar;
        private final Rollo rolloPadre;

        public CancelacionAfectados(Set<OrdenVenta> ordenesVentaAReplanificar, Set<OrdenDeTrabajo> ordenesTrabajoACancelar, Set<Rollo> rollosACancelar, Rollo rolloPadre) {
            this.ordenesVentaAReplanificar = ordenesVentaAReplanificar;
            this.ordenesTrabajoACancelar = ordenesTrabajoACancelar;
            this.rollosACancelar = rollosACancelar;
            this.rolloPadre = rolloPadre;
        }

        public Set<OrdenVenta> getOrdenesVentaAReplanificar() {
            return ordenesVentaAReplanificar;
        }

        public Set<OrdenDeTrabajo> getOrdenesTrabajoACancelar() {
            return ordenesTrabajoACancelar;
        }

        public Set<Rollo> getRollosACancelar() {
            return rollosACancelar;
        }

        public Rollo getRolloPadre() {
            return rolloPadre;
        }
    }

    @Override
    public List<OrdenDeTrabajo> findByRolloId(Long rolloId) {
        return repository.findByRolloId(rolloId);
    }

    @Override
    public OrdenDeTrabajo findByProcesoId(Long procesoId) {
      return repository.findByOrdenDeTrabajoMaquinas_Id(procesoId);
    }

    @Override
    public List<OrdenDeTrabajo> filtrarOrdenes(FiltroOrdenDeTrabajoDto filtros) {
        Specification<OrdenDeTrabajo> spec = Specification.where(null);

        if (filtros.getRolloId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rolloId"), filtros.getRolloId()));
        }

        if (filtros.getMaquinaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("maquinaId"), filtros.getMaquinaId()));
        }

        if (filtros.getOrdenDeVentaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ordenDeVentaId"), filtros.getOrdenDeVentaId()));
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

        if (filtros.getFechaFinalizacionDesde() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaFinalizacion"), filtros.getFechaFinalizacionDesde()));
        }

        if (filtros.getFechaFinalizacionHasta() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaFinalizacion"), filtros.getFechaFinalizacionHasta()));
        }

        if (filtros.getMaquinaTipo() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("maquinaTipo"), filtros.getMaquinaTipo()));
        }

        return repository.findAll(spec);
    }

    @Override
    public List<OrdenDeTrabajoMaquina> findOrdenDeTrabajoMaquinaByEstadoAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn(String estado, LocalDateTime fechaFinDesde, LocalDateTime fecaFinHasta, List<Maquina> maquinas){
        return this.ordenDeTrabajoMaquinaRepository.findOrdenDeTrabajoMaquinaByEstadoAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn(estado, fechaFinDesde, fecaFinHasta, maquinas);
    }
}