package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.enums.*;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.*;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.repository.interfaces.*;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.services.interfaces.ControlDeCalidadService;
import ar.utn.ccaffa.services.interfaces.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ControlDeCalidadServiceImpl implements ControlDeCalidadService {

    private final ControlDeCalidadRepository controlDeCalidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrdenDeTrabajoRepository ordenDeTrabajoRepository;
    private final MedidaDeCalidadRepository medidaDeCalidadRepository;
    private final ProveedorService proveedorService;
    private final OrdenVentaRepository ordenVentaRepository;
    private final OrdenDeTrabajoMaquinaRepository ordenDeTrabajoMaquinaRepository;
    private final RolloRepository rolloRepository;
    private final RolloMapper rolloMapper;

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
        nuevaMedida.setRebabaMedido(request.getRebabaMedido());
        medidaDeCalidadRepository.save(nuevaMedida);

        // Añadir la nueva medida a la lista existente
        control.getMedidasDeCalidad().add(nuevaMedida);

        // Recalcular promedios
        double avgEspesor = control.getMedidasDeCalidad().stream().mapToDouble(MedidaDeCalidad::getEspesorMedido).average().orElse(0.0);
        double avgAncho = control.getMedidasDeCalidad().stream().mapToDouble(MedidaDeCalidad::getAnchoMedido).average().orElse(0.0);
        double avgRebaba = control.getMedidasDeCalidad().stream().mapToDouble(MedidaDeCalidad::getRebabaMedido).average().orElse(0.0);

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
        Rollo rolloAUsar = ordenDeTrabajoMaquina.getRolloAUsar();

        ProveedorDto proveedor = this.proveedorService.findById(rolloAUsar.getProveedorId());

        OrdenVenta ordenDeVenta = ordenDeTrabajo.getOrdenDeVenta();
        Cliente cliente = ordenDeVenta.getCliente();
        Especificacion especificacion = ordenDeVenta.getEspecificacion();

        return ControlDeProcesoDto.builder()
                .idControl(control.getId())
                .idCliente(cliente.getId())
                .nombreCliente(cliente.getName())
                .idOrden(ordenDeTrabajo.getId())
                .fechaInicio(control.getFechaControl())
                .fechaFin(control.getFechaFinalizacion())
                .idMaquina(ordenDeTrabajoMaquina.getMaquina().getId())
                .nombreMaquina(ordenDeTrabajoMaquina.getMaquina().getNombre())
                .tipoMaquina(ordenDeTrabajoMaquina.getMaquina().getTipo().name())
                .idOperario(control.getUsuario().getId())
                .idProveedor(rolloAUsar.getId())
                .nombreProveedor(proveedor.getNombre())
                .codigoProveedor(rolloAUsar.getCodigoProveedor())
                .nombreOperario(control.getUsuario().getNombre())
                .idRolloAUsar(rolloAUsar.getId())
                .tipoMaterial(rolloAUsar.getTipoMaterial().name())
                .pesoOriginal(rolloAUsar.getPesoKG())
                .anchoOriginal(rolloAUsar.getAnchoMM())
                .espesorOriginal(rolloAUsar.getEspesorMM())
                .toleranciaAncho(especificacion.getToleranciaAncho())
                .toleranciaEspesor(especificacion.getToleranciaEspesor())
                .pesoDeseado(especificacion.getCantidad())
                .anchoDeseado(especificacion.getAncho())
                .espesorDeseado(especificacion.getEspesor())
                .rebabaMedio(control.getRebabaMedio())
                .anchoMedio(control.getAnchoMedio())
                .espesorMedio(control.getEspesorMedio())
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
    public Page<ControlDeCalidad> getAllControlesCalidad(Pageable pageable) {
        // Evita N+1 cargando usuario, medidas y defectos en menos consultas (override con @EntityGraph)
        return controlDeCalidadRepository.findAll(pageable);
    }

    @Override
    public Page<ControlDeCalidad> filtrarControlesCalidad(FiltroControlDeCalidad filtros, Pageable pageable) {
        Specification<ControlDeCalidad> spec = crearSpecification(filtros);
        return controlDeCalidadRepository.findAll(spec, pageable);
    }
    
    private Specification<ControlDeCalidad> crearSpecification(FiltroControlDeCalidad filtros) {
        Specification<ControlDeCalidad> spec = Specification.where(null);

        if (filtros.getUsuarioId()!= null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("usuario").get("id"), filtros.getUsuarioId()));
        }

        if (filtros.getEstados() != null) {
            spec = spec.and((root, query, cb) -> cb.in(root.get("estado")).value(filtros.getEstados()));
        }

        if (filtros.getOrdenDeTrabajoMaquinaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ordenDeTrabajoMaquinaId"), filtros.getOrdenDeTrabajoMaquinaId()));
        }

        return spec;
    }

    @Override
    public ControlDeCalidad getControlDeCalidadById(Long id) {
        return controlDeCalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));
    }

    @Override
    public List<RolloDto> finalizarControl(Long id) {

        ControlDeCalidad control = controlDeCalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));

        OrdenDeTrabajoMaquina ordenDeTrabajoMaquina = ordenDeTrabajoMaquinaRepository.findById(control.getOrdenDeTrabajoMaquinaId()).orElseThrow(
            () -> new RuntimeException("Orden de Trabajo no encontrada con ID: " + control.getOrdenDeTrabajoMaquinaId()));

        OrdenDeTrabajo ordenDeTrabajo = ordenDeTrabajoMaquina.getOrdenDeTrabajo();
        OrdenVenta ordenVenta = ordenDeTrabajo.getOrdenDeVenta();

        control.setFechaFinalizacion(LocalDateTime.now());
        ordenDeTrabajoMaquina.setFechaFin(LocalDateTime.now());

        List<Rollo> rollosAEtiquetar = new ArrayList<Rollo>();

        // Si Termino la primera OTM
        if (ordenDeTrabajo.esPrimeraOTM(ordenDeTrabajoMaquina)) {
            Rollo rolloDeOrdenDeTrabajo = (Rollo) Hibernate.unproxy(ordenDeTrabajo.getRollo());
            List<Rollo> rollosHijos = rolloDeOrdenDeTrabajo.getHijos().stream().toList();
            for (Rollo rh : rollosHijos) {
                rh.setEstado(EstadoRollo.DISPONIBLE);
                rh.setFechaIngreso(LocalDateTime.now());
            }
            rolloDeOrdenDeTrabajo.setEstado(EstadoRollo.DIVIDIDO);
            rolloRepository.save(rolloDeOrdenDeTrabajo);
            rolloRepository.saveAll(rollosHijos);
            rollosAEtiquetar.addAll(rollosHijos);
        } else {
            // Termino la segunda
            rollosAEtiquetar.add(ordenDeTrabajo.getRolloProducto());
        }

        if (!control.getDefectos().isEmpty() || control.getEstado().equals(EstadoControlDeCalidadEnum.A_CORREGIR)) {
            control.setEstado(EstadoControlDeCalidadEnum.DEFECTUOSO);
            ordenDeTrabajoMaquina.setEstado(EstadoOrdenTrabajoMaquinaEnum.DEFECTUOSO);

            ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.DEFECTUOSO);
            ordenDeTrabajo.setFechaFin(LocalDateTime.now());

            ordenDeTrabajo.getOrdenDeTrabajoMaquinas().forEach(otm -> { if(otm != ordenDeTrabajoMaquina) otm.anular();});

            ordenVenta.setEstado(EstadoOrdenVentaEnum.REPLANIFICAR);
            ordenVenta.setRazonReplanifiacion("El rollo producido no cumple con los estandares de calidad. Está defectuoso");

            Rollo rolloProducto = ordenDeTrabajo.getRolloProducto();
            rolloProducto.setEstado(EstadoRollo.DEFECTUOSO);
            this.actualizarRolloProducto(control, ordenDeTrabajo, rolloProducto);
            rolloRepository.save(rolloProducto);


        } else {
            control.setEstado(EstadoControlDeCalidadEnum.FINALIZADO);
            ordenDeTrabajoMaquina.setEstado(EstadoOrdenTrabajoMaquinaEnum.FINALIZADA);

            if (ordenDeTrabajo.todosLosProcesosEstanFinalizados()){
                // Finalizó la última OTM
                ordenDeTrabajo.setEstado(EstadoOrdenTrabajoEnum.FINALIZADA);
                ordenDeTrabajo.setFechaFin(LocalDateTime.now());
                ordenVenta.setEstado(EstadoOrdenVentaEnum.TRABAJO_FINALIZADO);

                Rollo rolloProducto = ordenDeTrabajo.getRolloProducto();
                rolloProducto.setEstado(EstadoRollo.ELABORADO);
                this.actualizarRolloProducto(control, ordenDeTrabajo, rolloProducto);
                rolloRepository.save(rolloProducto);
            }

        }


        ordenDeTrabajoMaquinaRepository.save(ordenDeTrabajoMaquina);
        ordenDeTrabajoRepository.save(ordenDeTrabajo);

        controlDeCalidadRepository.save(control);
        return this.rolloMapper.toDtoListOnlyWithRolloPadreID(rollosAEtiquetar);
    }

    public void actualizarRolloProducto(ControlDeCalidad controlDeCalidad, OrdenDeTrabajo ordenDeTrabajo, Rollo rolloProducto){
        rolloProducto.setAnchoMM(controlDeCalidad.getAnchoMedio());
        rolloProducto.setEspesorMM(controlDeCalidad.getEspesorMedio());
        rolloProducto.setPesoKG(ordenDeTrabajo.getOrdenDeVenta().getEspecificacion().getCantidad());
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

        OrdenVenta ordenVenta = ordenDeTrabajo.getOrdenDeVenta();
        ordenVenta.setEstado(EstadoOrdenVentaEnum.EN_CURSO);

        Rollo rolloPadre = ordenDeTrabajo.getRollo();
        Rollo rolloProducto = rolloPadre.getHijos().stream().filter(rh -> rh.getTipoRollo() == TipoRollo.PRODUCTO).toList().getFirst();

        rolloPadre.setEstado(EstadoRollo.EN_PROCESAMIENTO);
        rolloProducto.setEstado(EstadoRollo.EN_PROCESAMIENTO);

        ControlDeCalidad control = controlDeCalidadRepository.findByOrdenDeTrabajoMaquinaId(id);

        control.setEstado(EstadoControlDeCalidadEnum.EN_PROCESO);
        control.setFechaControl(LocalDateTime.now());

        rolloRepository.save(rolloPadre);
        rolloRepository.save(rolloProducto);
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
