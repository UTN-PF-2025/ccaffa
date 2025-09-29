package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.PlannerMapper;
import ar.utn.ccaffa.planner.PlannerDTO;
import ar.utn.ccaffa.planner.PlannerGA;
import org.springframework.stereotype.Component;


@Component
public class PlannerMapperImpl implements PlannerMapper {

    @Override
    public PlannerGA toEntity(PlannerDTO plannerDTO) {
        if (plannerDTO == null) {
            return null;
        }

        return PlannerGA.builder()
                .grace_hours(plannerDTO.getHorasDeGracia())
                .maquinasIDs(plannerDTO.getMaquinasIDs())
                .ordenesDeVentaIDs(plannerDTO.getOrdenesDeVentaIDs())
                .rollosIDs(plannerDTO.getRolloIDs())
                .MIN_WIDTH(plannerDTO.anchoDesperdicio)
                .MIN_LENGTH(plannerDTO.largoDesperdicio)
                .MULTIPLIER_OF_WASTE(plannerDTO.getMULTIPLIER_OF_WASTE())
                .TOP_DAYS_IN_ADVANCE(plannerDTO.getTOP_DAYS_IN_ADVANCE())
                .POPULATION_SIZE_PER_SALE(plannerDTO.getPOPULATION_SIZE_PER_SALE())
                .horaDeInicioLaboral(plannerDTO.getHoraDeInicioLaboral())
                .horaDeFinLaboral(plannerDTO.getHoraDeFinLaboral())
                .valid_days_of_the_week(plannerDTO.getValid_days_of_the_week())
                .build();
    }

} 