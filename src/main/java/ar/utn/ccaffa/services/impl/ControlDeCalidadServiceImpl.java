package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.*;
import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.model.dto.CreateControlDeCalidadRequest;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.*;
import ar.utn.ccaffa.model.dto.AddMedidaRequest;
import ar.utn.ccaffa.model.dto.ControlDeProcesoDto;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private final RolloRepository rolloRepository;
    private final RolloProductoRepository rolloProductoRepository;

    @Override
    public ControlDeCalidad createControlDeCalidad(CreateControlDeCalidadRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getEmpleadoId()));

        ControlDeCalidad control = getNewControlDeCalidad(usuario, request.getOrdenDeTrabajoMaquinaId());
        controlDeCalidadRepository.save(control);

        return control;
    }

    private static ControlDeCalidad getNewControlDeCalidad(Usuario usuario, Long ordenTrabajoMaquinaId) {
        ControlDeCalidad control = new ControlDeCalidad();
        control.setUsuario(usuario);
        control.setOrdenDeTrabajoMaquinaId(ordenTrabajoMaquinaId);
        control.setEstado(EstadoControlDeCalidadEnum.PENDIENTE);

        control.setAnchoMedio(0.0f); // Valor por defecto
        control.setEspesorMedio(0.0f); // Valor por defecto
        control.setRebabaMedio(0.0f); // Valor por defecto
        control.setMedidasDeCalidad(Collections.emptyList());
        control.setDefectos(Collections.emptyList());
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

        control.setEspesorMedio((float) avgEspesor);
        control.setAnchoMedio((float) avgAncho);
        control.setRebabaMedio((float) avgRebaba);

        return controlDeCalidadRepository.save(control);
    }

    @Override
    @Transactional(readOnly = true)
    public ControlDeProcesoDto getControlDeProceso(Long controlDeCalidadId) {
        ControlDeCalidad control = controlDeCalidadRepository.findByIdWithMedidas(controlDeCalidadId)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + controlDeCalidadId));

        OrdenDeTrabajoMaquina ordenDeTrabajoMaquina = ordenDeTrabajoMaquinaRepository.findById(control.getOrdenDeTrabajoMaquinaId())
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + control.getOrdenDeTrabajoMaquinaId()));

        return createControlDeProcesoDto(ordenDeTrabajoMaquina, control);
    }

    private ControlDeProcesoDto createControlDeProcesoDto(OrdenDeTrabajoMaquina ordenDeTrabajoMaquina, ControlDeCalidad control) {
        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoMaquina.getOrdenDeTrabajo();
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

        return ControlDeProcesoDto.builder()
                .idControl(control.getId())
                .idCliente(cliente != null ? cliente.getId() : null)
                .nombreCliente(cliente != null ? cliente.getName() : null)
                .idOrden(ordenDeTrabajo.getId())
                .fechaInicio(control.getFechaControl())
                .fechaFin(control.getFechaFinalizacion())
                .idMaquina(ordenDeTrabajoMaquina.getMaquina().getId())
                .nombreMaquina(ordenDeTrabajoMaquina.getMaquina().getNombre())
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
                .defectos(control.getDefectos())
                .estado(control.getEstado())
                .build();
    }

  /*  @Override
    @Transactional(readOnly = true)
    public ControlDeProcesoDto getControlDeProcesoByOrdenTrabajo(Long ordenTrabajoId) {
        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoRepository.findByIdFetchRollo(ordenTrabajoId)
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + ordenTrabajoId));
        List<ControlDeCalidad> controlCalidad = this.controlDeCalidadRepository.findByOrdenDeTrabajoId(ordenTrabajoId);
        ControlDeCalidad control = controlCalidad.isEmpty() ? null : controlCalidad.getFirst();

        return createControlDeProcesoDto(ordenDeTrabajo, control);
    }*/

    @Override
    public List<ControlDeCalidad> getAllControlesCalidad() {
        // Evita N+1 cargando usuario, medidas y defectos en menos consultas (override con @EntityGraph)
        return controlDeCalidadRepository.findAll();
    }

    @Override
    public ControlDeCalidad getControlDeCalidadById(Long id) {
        return controlDeCalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));
    }

    @Override
    public ControlDeCalidad finalizarControl(Long id) {

        ControlDeCalidad control = controlDeCalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));

        OrdenDeTrabajoMaquina ordenDeTrabajoMaquina = ordenDeTrabajoMaquinaRepository.findById(control.getOrdenDeTrabajoMaquinaId()).orElseThrow(
            () -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + control.getOrdenDeTrabajoMaquinaId()));

        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoMaquina.getOrdenDeTrabajo();
        OrdenVenta ordenVenta = ordenDeTrabajo.getOrdenDeVenta();

        control.setFechaFinalizacion(LocalDateTime.now());
        ordenDeTrabajoMaquina.setFechaFin(LocalDateTime.now());

        // Si Termino la primera OTM
        if (ordenDeTrabajo.esPrimeraOTM(ordenDeTrabajoMaquina)) {
            Rollo rolloDeOrdenDeTrabajo = ordenDeTrabajo.getRollo();
            List<Rollo> rollosHijos = rolloDeOrdenDeTrabajo.getHijos();
            for (Rollo rh : rollosHijos) {
                rh.setEstado(EstadoRollo.DISPONIBLE);
                rh.setFechaIngreso(LocalDateTime.now());
            }
            rolloRepository.saveAll(rollosHijos);
            control.setRollosHijos(rollosHijos);
        }

        if (!control.getDefectos().isEmpty() || control.getEstado().equals(EstadoControlDeCalidadEnum.A_CORREGIR)) {
            control.setEstado(EstadoControlDeCalidadEnum.DEFECTUOSO);
            ordenDeTrabajoMaquina.setEstado(EstadoOrdenTrabajoMaquinaEnum.DEFECTUOSO);

            ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.DEFECTUOSO);
            ordenDeTrabajo.setFechaFin(LocalDateTime.now());

            ordenDeTrabajo.getOrdenDeTrabajoMaquinas().forEach(otm -> { if(otm != ordenDeTrabajoMaquina) otm.anular();});

            ordenVenta.setEstado(EstadoOrdenVentaEnum.REPLANIFICAR);
            ordenVenta.setRazonReplanifiaciom("El rollo producido no cumple con los estandares de calidad. Está defectuoso");

            RolloProducto rolloProducto = this.generarRolloProducto(control, ordenDeTrabajo, EstadoRolloProducto.DEFECTUOSO);

            rolloProductoRepository.save(rolloProducto);


        } else {
            control.setEstado(EstadoControlDeCalidadEnum.FINALIZADO);
            ordenDeTrabajoMaquina.setEstado(EstadoOrdenTrabajoMaquinaEnum.FINALIZADA);

            if (ordenDeTrabajo.todosLosProcesosEstanFinalizados()){
                // Finalizó la última OTM
                ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.FINALIZADA);
                ordenDeTrabajo.setFechaFin(LocalDateTime.now());
                ordenVenta.setEstado(EstadoOrdenVentaEnum.TRABAJO_FINALIZADO);

                RolloProducto rolloProducto = this.generarRolloProducto(control, ordenDeTrabajo, EstadoRolloProducto.ELABORADO);
                rolloProductoRepository.save(rolloProducto);
            }

        }


        ordenDeTrabajoMaquinaRepository.save(ordenDeTrabajoMaquina);
        ordenDeTrabajoRepository.save(ordenDeTrabajo);

        return controlDeCalidadRepository.save(control);
    }

    public RolloProducto generarRolloProducto(ControlDeCalidad controlDeCalidad, OrdenDeTrabajo ordenDeTrabajo, EstadoRolloProducto estado){
        return RolloProducto.builder().
                rolloPadre(ordenDeTrabajo.getRollo())
                .tipoMaterial(ordenDeTrabajo.getRollo().getTipoMaterial())
                .estado(estado)
                .ordenDeTrabajo(ordenDeTrabajo)
                .fechaIngreso(LocalDateTime.now())
                .anchoMM(controlDeCalidad.getAnchoMedio())
                .espesorMM(controlDeCalidad.getEspesorMedio())
                .pesoKG(ordenDeTrabajo.getOrdenDeVenta().getEspecificacion().getCantidad()).build();
    }

    @Override
    public ControlDeCalidad iniciarControl(Long id) {

        OrdenDeTrabajoMaquina ordenTrabajoMaquina = ordenDeTrabajoMaquinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de Trabajo Maquina no encontrada con ID: " + id));
        
        ordenTrabajoMaquina.setEstado(EstadoOrdenTrabajoMaquinaEnum.EN_CURSO);

        OrdenDeTrabajo ordenDeTrabajo = ordenTrabajoMaquina.getOrdenDeTrabajo();
        ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.EN_CURSO);
        if (ordenDeTrabajo.esPrimeraOTM(ordenTrabajoMaquina)){
            ordenDeTrabajo.setFechaInicio(LocalDateTime.now());
        }

        ControlDeCalidad control = controlDeCalidadRepository.findByOrdenDeTrabajoMaquinaId(id);

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
