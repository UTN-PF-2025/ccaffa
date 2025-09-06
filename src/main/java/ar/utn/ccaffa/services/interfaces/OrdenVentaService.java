package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.FiltroOrdenVentaDTO;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.apache.coyote.BadRequestException;

import java.time.LocalDate;
import java.util.List;

public interface OrdenVentaService {
    List<OrdenVentaDto> findAll();
    
    OrdenVentaDto findById(Long id);

    List<OrdenVentaDto> searchByFiltros(FiltroOrdenVentaDTO filtros);

    OrdenVenta save(OrdenVentaDto ordenVenta);
    
    void deleteById(Long id);

    void anular(Long ordenVentaId) throws BadRequestException;

    void finalizar(Long ordenVentaId);

    List<OrdenVenta> findByIdIn(List<Long> ids);
}