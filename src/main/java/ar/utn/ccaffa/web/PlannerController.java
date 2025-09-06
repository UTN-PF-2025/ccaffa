package ar.utn.ccaffa.web;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.mapper.interfaces.PlannerMapper;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.planner.Pair;
import ar.utn.ccaffa.planner.PlannerDTO;
import ar.utn.ccaffa.planner.PlannerGA;
import ar.utn.ccaffa.services.interfaces.MaquinaService;
import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoService;
import ar.utn.ccaffa.services.interfaces.OrdenVentaService;
import ar.utn.ccaffa.services.interfaces.RolloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/planner")
@Slf4j
public class PlannerController {

    private final RolloService rolloService;
    private final OrdenVentaService ordenVentaService;

    private final MaquinaService maquinaService;
    private final OrdenDeTrabajoService ordenDeTrabajoService;
    private final PlannerMapper plannerMapper;



    public PlannerController(RolloService rolloService, OrdenVentaService ordenVentaService, MaquinaService maquinaService, OrdenDeTrabajoService ordenDeTrabajoService, PlannerMapper plannerMapper) {
        this.rolloService = rolloService;
        this.ordenVentaService = ordenVentaService;
        this.maquinaService = maquinaService;
        this.ordenDeTrabajoService = ordenDeTrabajoService;
        this.plannerMapper = plannerMapper;
    }




    @PostMapping
    public ResponseEntity<Pair> generatePlan(@RequestBody PlannerDTO plannerInfo) {
        PlannerGA plannerGA = plannerMapper.toEntity(plannerInfo);
        List<Long> maquinasIDs = new ArrayList<>();
        List<Long> rollosIDs = new ArrayList<>();
        List<Maquina> maquinas;
        List<Rollo> rollos;

        List<OrdenVenta> ordenVentas = this.ordenVentaService.findByIdIn(plannerInfo.ordenesDeVentaIDs);

        plannerGA.setOrdenesDeVenta(ordenVentas);

        if (plannerInfo.usarTodasLasMaquinas){
            maquinas = this.maquinaService.findAllIDsOfAvailableMachinesEntity();
            maquinasIDs.addAll(maquinas.stream().map(m -> m.getId()).toList());
        }
        else {
            maquinas = this.maquinaService.findByIdIn(maquinasIDs);
            maquinasIDs.addAll(plannerInfo.getMaquinasIDs());
        }
        maquinasIDs.add(0L);
        plannerGA.setMaquinasIDs(maquinasIDs);
        plannerGA.setMaquinas(maquinas);

        if (plannerInfo.usarTodosLosRollosDisponibles){
            rollos = this.rolloService.findEntitiesByEstado(EstadoRollo.DISPONIBLE);
            rollosIDs.addAll(rollos.stream().map(r -> r.getId()).toList());
        }else {
            rollos = this.rolloService.findEntitiesByIdIn(rollosIDs);
            rollosIDs.addAll(plannerInfo.getRolloIDs());
        }

        plannerGA.setRollosIDs(rollosIDs);
        plannerGA.setRollos(rollos);

        LocalDateTime fechaDesde = ordenVentas.stream().min(Comparator.comparing(OrdenVenta::getFechaCreacion)).get().getFechaCreacion();
        LocalDateTime fechaHasta = ordenVentas.stream().max(Comparator.comparing(OrdenVenta::getFechaEntregaEstimada)).get().getFechaEntregaEstimada();

        List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas = this.ordenDeTrabajoService
                .findOrdenDeTrabajoMaquinaByEstadoAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn("Progamada", fechaDesde, fechaHasta, maquinas);

        plannerGA.setOrdenesDeTrabajoMaquina(ordenDeTrabajoMaquinas);

        Pair<List<OrdenDeTrabajo>, List<Rollo>> result = plannerGA.execute();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}