package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.CancelacionSimulacionDto;
import ar.utn.ccaffa.model.dto.FiltroOrdenVentaDTO;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.repository.interfaces.RolloRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenVentaServiceImpl implements OrdenVentaService {
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenVentaMapper ordenVentaMapper;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final OrdenDeTrabajoService ordenDeTrabajoService;

    private final RolloMapper rolloMapper;

    private final RolloRepository rolloRepository;

    public OrdenVentaServiceImpl(OrdenVentaRepository ordenVentaRepository, OrdenVentaMapper ordenVentaMapper, OrdenDeTrabajoRepository ordenDeTrabajoRepository, OrdenDeTrabajoService ordenDeTrabajoService, RolloRepository rolloRepository, RolloMapper rolloMapper) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenVentaMapper = ordenVentaMapper;
        this.ordenDeTrabajoRepository = ordenDeTrabajoRepository;
        this.ordenDeTrabajoService = ordenDeTrabajoService;
        this.rolloRepository = rolloRepository;
        this.rolloMapper = rolloMapper;
    }

    @Override
    public List<OrdenVentaDto> findAll() {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findAll());
    }

    @Override
    public OrdenVentaDto findById(Long id) {
        return this.ordenVentaMapper.toDto(this.ordenVentaRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Orden de venta", "id", id)));
    }


    @Override
    public OrdenVentaDto save(OrdenVentaDto ordenVenta) {
        return ordenVentaMapper.toDto(this.ordenVentaRepository.save(this.ordenVentaMapper.toEntity(ordenVenta)));
    }

    @Override
    public List<OrdenVentaDto> setToProgamada(List<Long> ids){
        List<OrdenVenta> ordenVentas = this.ordenVentaRepository.findByIdIn(ids);
        ordenVentas.forEach(ov -> ov.setEstado(EstadoOrdenVentaEnum.PROGRAMADA));
        return ordenVentaMapper.toDtoList(this.ordenVentaRepository.saveAll(ordenVentas));
    }
    @Override
    public void deleteById(Long id) {
        this.ordenVentaRepository.deleteById(id);
    }

    @Override
    public void anular(Long ordenVentaId) {
        OrdenVentaDto ordenVentaAAnular = this.findById(ordenVentaId);

        if(!ordenVentaAAnular.esAnulable())
            throw new IllegalStateException("La órden no se encuentra en un estado anulable");

        Optional<OrdenDeTrabajo> ordenesDeTrabajo = this.ordenDeTrabajoRepository.findTopByOrdenDeVenta_IdAndEstadoInOrderByIdDesc(ordenVentaAAnular.getOrderId(), List.of(EstadoOrdenTrabajoEnum.PROGRAMADA, EstadoOrdenTrabajoEnum.EN_CURSO));

        if(ordenesDeTrabajo.isPresent()){
            OrdenDeTrabajo ordenDeTrabajo = ordenesDeTrabajo.get();
            this.ordenDeTrabajoService.cancelarOrdenDeTrabajo(ordenDeTrabajo.getId());
        }

        ordenVentaAAnular.setEstado(EstadoOrdenVentaEnum.ANULADA);
        this.save(ordenVentaAAnular);
    }

    @Override
    public CancelacionSimulacionDto simularCancelacion(Long id) {
        OrdenVentaDto ordenVentaAAnular = this.findById(id);

        if(!ordenVentaAAnular.esAnulable())
            throw new IllegalStateException("La órden no se encuentra en un estado anulable");

        Optional<OrdenDeTrabajo> ordenesDeTrabajo = this.ordenDeTrabajoRepository.findTopByOrdenDeVenta_IdAndEstadoInOrderByIdDesc(ordenVentaAAnular.getOrderId(), List.of(EstadoOrdenTrabajoEnum.PROGRAMADA, EstadoOrdenTrabajoEnum.EN_CURSO, EstadoOrdenTrabajoEnum.FINALIZADA));

        if (ordenesDeTrabajo.isEmpty()){
            return CancelacionSimulacionDto.builder()
                    .ordenesVentaAReplanificar(List.of(ordenVentaAAnular))
                    .rollosACancelar(new ArrayList<>())
                    .ordenesTrabajoACancelar(new ArrayList<>())
                    .build() ;
        }


        OrdenDeTrabajo ordenDeTrabajo = ordenesDeTrabajo.get();

        return this.ordenDeTrabajoService.simularCancelacion(ordenDeTrabajo.getId());

    }

    @Override
    public List<OrdenVenta> findByIdIn(List<Long> ids){
        return this.ordenVentaRepository.findByIdIn(ids);
    }

    @Override
    public Boolean trabajoFinalizado(Long id) {
        OrdenVentaDto ordenVentaDto = this.findById(id);
        if (ordenVentaDto == null) return false;

        return (ordenVentaDto.getEstado() == EstadoOrdenVentaEnum.TRABAJO_FINALIZADO || ordenVentaDto.getEstado() == EstadoOrdenVentaEnum.FINALIZADA);
    }

    @Override
    public List<OrdenVentaDto> searchByFiltros(FiltroOrdenVentaDTO filtros) {
        Specification<OrdenVenta> spec = Specification.where(null);
        if (filtros.getFechaCreacion() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaCreacion"), filtros.getFechaCreacion()));
        }
        if (filtros.getFechaInicio() != null && filtros.getFechaFin() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("fechaCreacion"), filtros.getFechaInicio(), filtros.getFechaFin()));
        }
        if (filtros.getEstado() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), filtros.getEstado()));
        }
        if (filtros.getEstados() != null) {
            spec = spec.and((root, query, cb) -> cb.in(root.get("estado")).value(filtros.getEstados()));
        }
        if (filtros.getClienteId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cliente").get("id"), filtros.getClienteId()));
        }
        if (filtros.getObservaciones() != null) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("observaciones"), "%" + filtros.getObservaciones() + "%"));
        }

        if (filtros.getOrderId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), filtros.getOrderId()));
        }
        if(filtros.getClienteId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cliente").get("id"), filtros.getClienteId()));
        }
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findAll(spec));
    }

    @Override
    public void finalizar(Long ordenVentaId, OrdenDeTrabajo ordenDeTrabajo) {

        Rollo rolloProducto = ordenDeTrabajo.getRolloProducto();
        rolloProducto.setEstado(EstadoRollo.ENTREGADO);
        this.rolloRepository.save(rolloProducto);

        this.ordenVentaRepository.updateOrdenDeVentaEstado(ordenVentaId, EstadoOrdenVentaEnum.FINALIZADA);
    }

}
