package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.Empleado;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.ControlDeCalidadRepository;
import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.entity.MedidaDeCalidad;
import ar.utn.ccaffa.repository.interfaces.EmpleadoRepository;
import ar.utn.ccaffa.repository.interfaces.MedidaDeCalidadRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ControlDeCalidadServiceImpl implements ControlDeCalidadService {

    private final ControlDeCalidadRepository controlDeCalidadRepository;
    private final EmpleadoRepository empleadoRepository;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final MedidaDeCalidadRepository medidaDeCalidadRepository;

    @Override
    public ControlDeCalidad createControlDeCalidad(CreateControlDeCalidadRequest request) {
        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + request.getEmpleadoId()));

        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findById(request.getOrdenDeTrabajoId())
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + request.getOrdenDeTrabajoId()));

        ControlDeCalidad control = new ControlDeCalidad();
        control.setEmpleado(empleado);
        control.setOrdenDeTrabajoId(ordenDeTrabajo.getId().toString());
        control.setFechaControl(LocalDate.now());
        control.setEstado("listo");

        control.setAnchoMedido(0.0f); // Valor por defecto
        control.setEspesorMedido(0.0f); // Valor por defecto
        control.setRebabaMedio(0.0f); // Valor por defecto
        control.setMedidasDeCalidad(Collections.emptyList());
        control.setDefectos(Collections.emptyList());

        return controlDeCalidadRepository.save(control);
    }

    @Override
    public ControlDeCalidad addMedida(Long controlDeCalidadId, AddMedidaRequest request) {
        ControlDeCalidad control = controlDeCalidadRepository.findById(controlDeCalidadId)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + controlDeCalidadId));

        MedidaDeCalidad nuevaMedida = new MedidaDeCalidad();
        nuevaMedida.setControlDeCalidad(control);
        nuevaMedida.setEspesorMedido(request.getEspesorMedido());
        nuevaMedida.setAnchoMedido(request.getAnchoMedido());
        nuevaMedida.setRebabaMedio(request.getRebabaMedio());
        medidaDeCalidadRepository.save(nuevaMedida);

        // Añadir la nueva medida a la lista existente
        control.getMedidasDeCalidad().add(nuevaMedida);

        // Recalcular promedios
        double avgEspesor = control.getMedidasDeCalidad().stream().mapToDouble(MedidaDeCalidad::getEspesorMedido).average().orElse(0.0);
        double avgAncho = control.getMedidasDeCalidad().stream().mapToDouble(MedidaDeCalidad::getAnchoMedido).average().orElse(0.0);
        double avgRebaba = control.getMedidasDeCalidad().stream().mapToDouble(MedidaDeCalidad::getRebabaMedio).average().orElse(0.0);

        control.setEspesorMedido((float) avgEspesor);
        control.setAnchoMedido((float) avgAncho);
        control.setRebabaMedio((float) avgRebaba);

        return controlDeCalidadRepository.save(control);
    }
}
