package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.model.dto.MedidaDeCalidadDto;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.MedidaDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.ControlDeCalidadRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.services.interfaces.ControlCalidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ControlCalidadServiceImpl implements ControlCalidadService {

    private final ControlDeCalidadRepository controlDeCalidadRepository;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;

    public ControlCalidadServiceImpl(ControlDeCalidadRepository controlDeCalidadRepository, 
                                   OrdenDeTrabajoRepository ordenDeTrabajoRepository) {
        this.controlDeCalidadRepository = controlDeCalidadRepository;
        this.ordenDeTrabajoRepository = ordenDeTrabajoRepository;
    }

    @Override
    public ControlDeCalidad iniciar(Long ordenTrabajoId, List<MedidaDeCalidadDto> medidasDeCalidad) {
        // Buscar la orden de trabajo
        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findById(ordenTrabajoId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", "id", ordenTrabajoId));

        // Validar que la fecha de inicio sea hoy
        LocalDate hoy = LocalDate.now();
        if (ordenDeTrabajo.getFechaInicio() == null || !ordenDeTrabajo.getFechaInicio().toLocalDate().equals(hoy)) {
            throw new IllegalStateException("La orden de trabajo debe tener fecha de inicio del día de hoy para iniciar el control de calidad");
        }

        // Crear el nuevo control de calidad
        ControlDeCalidad controlDeCalidad = new ControlDeCalidad();
        controlDeCalidad.setFechaControl(hoy);
        controlDeCalidad.setEstado("INICIADO");

        // Convertir DTOs a entidades y establecer relaciones
        if (medidasDeCalidad != null) {
            List<MedidaDeCalidad> medidas = medidasDeCalidad.stream()
                    .map(dto -> {
                        MedidaDeCalidad medida = new MedidaDeCalidad();
                        // Mapear campos según el tipo de medida del DTO
                        if ("espesor".equals(dto.getTipo())) {
                            medida.setEspesorMedido(dto.getValor());
                        } else if ("ancho".equals(dto.getTipo())) {
                            medida.setAnchoMedido(dto.getValor());
                        } else if ("rebaba".equals(dto.getTipo())) {
                            medida.setRebabaMedio(dto.getValor());
                        }
                        medida.setControlDeCalidad(controlDeCalidad);
                        return medida;
                    })
                    .toList();
            controlDeCalidad.setMedidasDeCalidad(medidas);
        }

        // Guardar en la base de datos
        return controlDeCalidadRepository.save(controlDeCalidad);
    }
} 