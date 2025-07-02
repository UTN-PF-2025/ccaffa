package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.apache.coyote.BadRequestException;

import java.time.LocalDate;
import java.util.List;

public interface OrdenVentaService {
    List<OrdenVentaDto> findAll();
    
    OrdenVentaDto findById(Long id);
    List<OrdenVentaDto> searchByDate(LocalDate fecha);
    List<OrdenVentaDto> searchByDateRange(LocalDate fechaInicio, LocalDate fechaFin);
    List<OrdenVentaDto> searchByEstado(String estado);
    List<OrdenVentaDto> searchByEstados(List<String> estados);
    List<OrdenVentaDto> searchByCliente(Long clienteId);
    List<OrdenVentaDto> searchByClienteAndEstado(Long clienteId, String estado);
    List<OrdenVentaDto> searchByObservaciones(String observaciones);
    OrdenVentaDto searchByOrderId(Long orderId);


    OrdenVenta save(OrdenVentaDto ordenVenta);
    
    void deleteById(Long id);
    Defecto obtenerDefectoPorId(Long id);
    void crearDefecto(Defecto defecto);

    void anular(Long ordenVentaId) throws BadRequestException;
}