package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.OrdenVentaDto;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.model.entity.OrdenVenta;

import java.util.List;

public interface OrdenVentaService {
    List<OrdenVentaDto> findAll();
    
    OrdenVentaDto findById(Long id);
    
    OrdenVenta save(OrdenVentaDto ordenVenta);
    
    void deleteById(Long id);
    Defecto obtenerDefectoPorId(Long id);
    void crearDefecto(Defecto defecto);
}