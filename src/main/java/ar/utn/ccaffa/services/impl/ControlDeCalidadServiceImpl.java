package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.EstadoControlDeCalidadEnum;
import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.enums.EstadoOrdenVentaEnum;
import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.ControlDeCalidadRepository;
import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.dto.ControlDeProcesoDto;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.repository.interfaces.MedidaDeCalidadRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenVentaRepository;
import ar.utn.ccaffa.repository.interfaces.ProveedorRepository;
import ar.utn.ccaffa.repository.interfaces.UsuarioRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoMaquinaRepository;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ControlDeCalidadServiceImpl implements ControlDeCalidadService {

    private final ControlDeCalidadRepository controlDeCalidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final MedidaDeCalidadRepository medidaDeCalidadRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository;

    @Override
    public ControlDeCalidad createControlDeCalidad(CreateControlDeCalidadRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getEmpleadoId()));

        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findById(request.getOrdenDeTrabajoId())
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + request.getOrdenDeTrabajoId()));

        ControlDeCalidad control = new ControlDeCalidad();
        control.setUsuario(usuario);
        control.setOrdenDeTrabajoId(ordenDeTrabajo.getId().toString());
        control.setEstado(EstadoControlDeCalidadEnum.PENDIENTE);

        control.setAnchoMedido(0.0f); // Valor por defecto
        control.setEspesorMedido(0.0f); // Valor por defecto
        control.setRebabaMedio(0.0f); // Valor por defecto
        control.setMedidasDeCalidad(Collections.emptyList());
        control.setDefectos(Collections.emptyList());

        ordenDeTrabajo.setControlDeCalidad(control);
        controlDeCalidadRepository.save(control);
        ordenDeTrabajoRepository.save(ordenDeTrabajo);

        return control;
    }

    @Override
    public ControlDeCalidad addMedida(Long controlDeCalidadId, AddMedidaRequest request) {
        ControlDeCalidad control = controlDeCalidadRepository.findById(controlDeCalidadId)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + controlDeCalidadId));

        MedidaDeCalidad nuevaMedida = new MedidaDeCalidad();
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

    @Override
    public ControlDeProcesoDto getControlDeProceso(Long controlDeCalidadId) {
        ControlDeCalidad control = controlDeCalidadRepository.findByIdWithMedidas(controlDeCalidadId)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + controlDeCalidadId));

        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findByIdFetchRollo(Long.parseLong(control.getOrdenDeTrabajoId()))
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + control.getOrdenDeTrabajoId()));

        return createControlDeProcesoDto(ordenDeTrabajo, control);
    }

    private ControlDeProcesoDto createControlDeProcesoDto(OrdenDeTrabajo ordenDeTrabajo, ControlDeCalidad control) {
        Rollo rollo = ordenDeTrabajo.getRollo();
        Proveedor proveedor = null;
        if (rollo != null && rollo.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(rollo.getProveedorId()).orElse(null);
        }

        OrdenVenta ordenDeVenta = ordenDeTrabajo.getOrdenDeVenta();
        Cliente cliente = null;
        Especificacion especificacion = null;
        if (ordenDeVenta != null) {
            cliente = ordenDeVenta.getCliente();
            especificacion = ordenDeVenta.getEspecificacion();
        }
        OrdenDeTrabajoMaquina maquinaAsignada = ordenDeTrabajoMaquinaRepository.findTopByOrdenDeTrabajo_IdOrderByFechaInicioDesc(ordenDeTrabajo.getId());

        return ControlDeProcesoDto.builder()
                .idControl(control.getId())
                .idCliente(cliente != null ? cliente.getId() : null)
                .nombreCliente(cliente != null ? cliente.getName() : null)
                .idOrden(ordenDeTrabajo.getId())
                .fechaInicio(control.getFechaControl())
                .fechaFin(control.getFechaFinalizacion())
                .idMaquina(maquinaAsignada != null ? maquinaAsignada.getMaquina().getId() : null)
                .nombreMaquina(maquinaAsignada != null ? maquinaAsignada.getMaquina().getNombre() : null)
                .idOperario(control.getUsuario().getId())
                .nombreOperario(control.getUsuario().getNombre())
                .cantidad(rollo != null ? rollo.getPesoKG().doubleValue() : null)
                .tipoMaterial(rollo != null ? rollo.getTipoMaterial().name() : null)
                .ancho(rollo != null ? rollo.getAnchoMM() : null)
                .toleranciaAncho(especificacion != null ? especificacion.getToleranciaAncho() : null)
                .espesor(rollo != null ? rollo.getEspesorMM() : null)
                .toleranciaEspesor(especificacion != null ? especificacion.getToleranciaEspesor() : null)
                .dureza(null) // Campo no disponible en el modelo actual
                .tamanoRebaba(control.getRebabaMedio())
                .idProveedor(proveedor != null ? proveedor.getId() : null)
                .nombreProveedor(proveedor != null ? proveedor.getNombre() : null)
                .codigoEtiquetaMp(rollo != null ? rollo.getCodigoProveedor() : null)
                .medidas(control.getMedidasDeCalidad())
                .build();
    }

    @Override
    public ControlDeProcesoDto getControlDeProcesoByOrdenTrabajo(Long ordenTrabajoId) {
        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findByIdFetchRollo(ordenTrabajoId)
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + ordenTrabajoId));
        List<ControlDeCalidad> controlCalidad = this.controlDeCalidadRepository.findByOrdenDeTrabajoId(ordenTrabajoId);
        ControlDeCalidad control = controlCalidad.isEmpty() ? null : controlCalidad.getFirst();

        return createControlDeProcesoDto(ordenDeTrabajo, control);
    }

    @Override
    public List<ControlDeCalidad> getAllControlesCalidad() {
        // Evita N+1 cargando usuario, medidas y defectos en menos consultas (override con @EntityGraph)
        return controlDeCalidadRepository.findAll();
    }

    @Override
    public ControlDeCalidad finalizarControl(Long id) {
        ControlDeCalidad control = controlDeCalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));
        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findById(Long.parseLong(control.getOrdenDeTrabajoId())).orElseThrow(
            () -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + control.getOrdenDeTrabajoId()));
        OrdenVenta ordenVenta = ordenDeTrabajo.getOrdenDeVenta();
        if (!control.getDefectos().isEmpty() || control.getEstado().equals(EstadoControlDeCalidadEnum.A_CORREGIR)) {
            control.setEstado(EstadoControlDeCalidadEnum.DEFECTUOSO);
            ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.DEFECTUOSO);
            ordenVenta.setEstado(EstadoOrdenVentaEnum.REPLANIFICAR);
        } else {
            control.setEstado(EstadoControlDeCalidadEnum.FINALIZADO);
            ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.FINALIZADA);
            ordenVenta.setEstado(EstadoOrdenVentaEnum.TRABAJO_FINALIZADO);
        }
        control.setFechaFinalizacion(LocalDateTime.now());
        ordenDeTrabajo.setFechaFin(LocalDateTime.now());
        ordenDeTrabajoRepository.save(ordenDeTrabajo);
        return controlDeCalidadRepository.save(control);
    }

    @Override
    public ControlDeCalidad iniciarControl(Long id) {

        OrdenDeTrabajoMaquina ordenTrabajoMaquina = ordenDeTrabajoMaquinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo Maquina no encontrada con ID: " + id));
        
        ordenTrabajoMaquina.setEstado(EstadoOrdenTrabajoMaquinaEnum.EN_CURSO);
        OrdenDeTrabajo ordenDeTrabajo = ordenTrabajoMaquina.getOrdenDeTrabajo();
        ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.EN_CURSO);
    
        ControlDeCalidad control = ordenDeTrabajo.getControlDeCalidad();
        control.setEstado(EstadoControlDeCalidadEnum.EN_PROCESO);
        control.setFechaControl(LocalDateTime.now());

       OrdenVenta ordenVenta = ordenDeTrabajo.getOrdenDeVenta();
       ordenVenta.setEstado(EstadoOrdenVentaEnum.EN_CURSO);

        ordenVentaRepository.save(ordenVenta);
        ordenDeTrabajoRepository.save(ordenDeTrabajo);
        ordenDeTrabajoMaquinaRepository.save(ordenTrabajoMaquina);
        return controlDeCalidadRepository.save(control);
    }

    @Override
    public ControlDeCalidad marcarComoACorregir(Long id) {
        ControlDeCalidad control = controlDeCalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));
        control.setEstado(EstadoControlDeCalidadEnum.A_CORREGIR);
        return controlDeCalidadRepository.save(control);
    }


}
