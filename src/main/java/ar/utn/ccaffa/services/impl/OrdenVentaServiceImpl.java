package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.exceptions.UnprocessableContentException;
import ar.utn.ccaffa.mapper.interfaces.OrdenVentaMapper;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import ar.utn.ccaffa.repository.interfaces.DefectoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrdenVentaServiceImpl implements OrdenVentaService {
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenVentaMapper ordenVentaMapper;
    private final DefectoRepository defectoRepository;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final OrdenDeTrabajoService ordenDeTrabajoService;

    public OrdenVentaServiceImpl(OrdenVentaRepository ordenVentaRepository, OrdenVentaMapper ordenVentaMapper, DefectoRepository defectoRepository, OrdenDeTrabajoRepository ordenDeTrabajoRepository, OrdenDeTrabajoService ordenDeTrabajoService) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.ordenVentaMapper = ordenVentaMapper;
        this.defectoRepository = defectoRepository;
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
    public List<OrdenVentaDto> searchByDate(LocalDate fecha) {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByFechaCreacion(fecha));
    }

    @Override
    public List<OrdenVentaDto> searchByDateRange(LocalDate fechaInicio, LocalDate fechaFin) {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByFechaCreacionBetween(fechaInicio, fechaFin));
    }

    @Override
    public List<OrdenVentaDto> searchByEstado(String estado) {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByEstado(estado));
    }

    @Override
    public List<OrdenVentaDto> searchByEstados(List<String> estados) {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByEstadoIn(estados));
    }

    @Override
    public List<OrdenVentaDto> searchByCliente(Long clienteId) {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByClienteId(clienteId));
    }

    @Override
    public List<OrdenVentaDto> searchByClienteAndEstado(Long clienteId, String estado) {
        return null;
        //corregir
        //this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByClienteAndEstado(this.clienteRepository.findById(clienteId).orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId)), estado));
    }

    @Override
    public List<OrdenVentaDto> searchByObservaciones(String observaciones) {
        return this.ordenVentaMapper.toDtoList(this.ordenVentaRepository.findByObservacionesContainingIgnoreCase(observaciones));
    }

    @Override
    public OrdenVentaDto searchByOrderId(Long orderId) {
        return this.ordenVentaMapper.toDto(this.ordenVentaRepository.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundException("Orden de venta", "orderId", orderId)));
    }

    @Override
    public OrdenVenta save(OrdenVentaDto ordenVenta) {
        return this.ordenVentaRepository.save(this.ordenVentaMapper.toEntity(ordenVenta));
    }

    @Override
    public void deleteById(Long id) {
        this.ordenVentaRepository.deleteById(id);
    }

    @Override
    public Defecto obtenerDefectoPorId(Long id) {
        return this.defectoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Defecto", "id", id));
    }

    @Override
    public void crearDefecto(Defecto defecto) {
        this.defectoRepository.save(defecto);
    }

    @Override
    public void anular(Long ordenVentaId) {
        OrdenVentaDto ordenVentaAAnular = this.findById(ordenVentaId);
        List<OrdenDeTrabajo> ordenesDeTrabajo = this.ordenDeTrabajoRepository.findByOrdenDeVenta_Id(ordenVentaAAnular.getId());
        if(ordenesDeTrabajo.isEmpty()){
            ordenVentaAAnular.setEstado("Cancelada");
            this.save(ordenVentaAAnular);
        } else {
            OrdenDeTrabajo ordenDeTrabajo1 = ordenesDeTrabajo.getFirst();
            if(ordenDeTrabajo1.yaComenzo()){
                throw new UnprocessableContentException("Orden de Venta - Anulación");
            } else {
                this.ordenDeTrabajoService.cancelar(ordenDeTrabajo1.getId());
                ordenVentaAAnular.setEstado("Cancelada");
                this.save(ordenVentaAAnular);
            }
        }
    }

}
