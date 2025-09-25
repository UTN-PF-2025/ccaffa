package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.exceptions.UnprocessableContentException;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.model.dto.FiltroOrdenVentaDTO;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenVentaServiceImpl implements OrdenVentaService {
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenVentaMapper ordenVentaMapper;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final OrdenDeTrabajoService ordenDeTrabajoService;

    public OrdenVentaServiceImpl(OrdenVentaRepository ordenVentaRepository, OrdenVentaMapper ordenVentaMapper, OrdenDeTrabajoRepository ordenDeTrabajoRepository, OrdenDeTrabajoService ordenDeTrabajoService) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenVentaMapper = ordenVentaMapper;
        this.ordenDeTrabajoRepository = ordenDeTrabajoRepository;
        this.ordenDeTrabajoService = ordenDeTrabajoService;
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
    public void deleteById(Long id) {
        this.ordenVentaRepository.deleteById(id);
    }

    @Override
    public void anular(Long ordenVentaId) {
        OrdenVentaDto ordenVentaAAnular = this.findById(ordenVentaId);
        List<OrdenDeTrabajo> ordenesDeTrabajo = this.ordenDeTrabajoRepository.findByOrdenDeVenta_Id(ordenVentaAAnular.getOrderId());
        if(ordenesDeTrabajo.isEmpty()){
            ordenVentaAAnular.setEstado("Cancelada");
            this.save(ordenVentaAAnular);
        } else {
            OrdenDeTrabajo ordenDeTrabajo1 = ordenesDeTrabajo.getFirst();
            if(ordenDeTrabajo1.yaComenzo()){
                throw new UnprocessableContentException("Orden de Venta - Anulación");
            } else {
                this.ordenDeTrabajoService.desactivar(ordenDeTrabajo1.getId());
                ordenVentaAAnular.setEstado("Cancelada");
                this.save(ordenVentaAAnular);
            }
        }
    }

    @Override
    public List<OrdenVenta> findByIdIn(List<Long> ids){
        return this.ordenVentaRepository.findByIdIn(ids);
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
    public void finalizar(Long ordenVentaId) {
        OrdenVenta ordenVenta = this.ordenVentaRepository.findById(ordenVentaId).orElseThrow(() -> new ResourceNotFoundException("Orden de venta", "id", ordenVentaId));
        if(ordenVenta.getEstado().equals("Finalizada")){
            throw new UnprocessableContentException("Orden de Venta - Finalizar");
        }
        List<OrdenDeTrabajo> ordenesDeTrabajo = this.ordenDeTrabajoRepository.findByOrdenDeVenta_Id(ordenVentaId);
        
        if(ordenesDeTrabajo.getFirst().getEstado().equals("Finalizada")){
            ordenVenta.setEstado("Finalizada");
            this.ordenVentaRepository.save(ordenVenta);
        } else {
            throw new UnprocessableContentException("Orden de Venta - Finalizar");
        }
    }

}
