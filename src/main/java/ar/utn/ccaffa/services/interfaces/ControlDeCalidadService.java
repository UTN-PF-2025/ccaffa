package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;

public interface ControlDeCalidadService {
    ControlDeCalidad createControlDeCalidad(CreateControlDeCalidadRequest request);

    ControlDeCalidad addMedida(Long controlDeCalidadId, AddMedidaRequest request);
}

