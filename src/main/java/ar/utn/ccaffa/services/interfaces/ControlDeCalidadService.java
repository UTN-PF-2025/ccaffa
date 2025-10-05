package ar.utn.ccaffa.services.interfaces;

import java.util.List;

import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.Rollo;

public interface ControlDeCalidadService {
    ControlDeCalidad createControlDeCalidad(CreateControlDeCalidadRequest request);

    ControlDeCalidad addMedida(Long controlDeCalidadId, AddMedidaRequest request);

    List<RolloDto> finalizarControl(Long id);

    ControlDeProcesoDto getControlDeProceso(Long controlDeCalidadId);

   // ControlDeProcesoDto getControlDeProcesoByOrdenTrabajo(Long ordenTrabajoId);

    List<ControlDeCalidad> getAllControlesCalidad();

    ControlDeCalidad getControlDeCalidadById(Long id);

    ControlDeCalidad iniciarControl(Long id);

    ControlDeCalidad marcarComoACorregir(Long id);

}
