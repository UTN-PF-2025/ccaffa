package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.MedidaDeCalidadDto;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;

import java.util.List;

public interface ControlCalidadService {
    
    ControlDeCalidad iniciar(Long ordenTrabajoId, List<MedidaDeCalidadDto> medidasDeCalidad);
} 