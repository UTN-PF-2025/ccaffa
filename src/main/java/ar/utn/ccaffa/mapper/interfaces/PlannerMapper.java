package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.planner.PlannerDTO;
import ar.utn.ccaffa.planner.PlannerGA;

import java.util.List;

public interface PlannerMapper {

    PlannerGA toEntity(PlannerDTO plannerDTO);

}
