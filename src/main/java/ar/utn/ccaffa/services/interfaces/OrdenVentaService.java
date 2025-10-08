package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.CancelacionSimulacionDto;
import ar.utn.ccaffa.model.dto.FiltroOrdenVentaDTO;
import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.apache.coyote.BadRequestException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface OrdenVentaService {
    List<OrdenVentaDto> findAll();
    
    OrdenVentaDto findById(Long id);

    List<OrdenVentaDto> searchByFiltros(FiltroOrdenVentaDTO filtros);

    OrdenVentaDto save(OrdenVentaDto ordenVenta);

    List<OrdenVentaDto> setToProgamada(List<Long> ids);
    
    void deleteById(Long id);

    void anular(Long ordenVentaId) throws BadRequestException;

    CancelacionSimulacionDto simularCancelacion(Long id);

    void finalizar(Long ordenVentaId, OrdenDeTrabajo ordenDeTrabajo);

    List<OrdenVenta> findByIdIn(List<Long> ids);

    Boolean trabajoFinalizado(Long id);
}