package ar.utn.ccaffa.services.interfaces;

import java.util.List;

import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.dto.ControlDeProcesoDto;
import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;

public interface ControlDeCalidadService {
    ControlDeCalidad createControlDeCalidad(CreateControlDeCalidadRequest request);

    ControlDeCalidad addMedida(Long controlDeCalidadId, AddMedidaRequest request);

    ControlDeCalidad finalizarControl(Long id);

    ControlDeProcesoDto getControlDeProceso(Long controlDeCalidadId);

    List<ControlDeCalidad> getAllControlesCalidad();

    ControlDeCalidad iniciarControl(Long id);

    ControlDeCalidad marcarComoACorregir(Long id);
}
