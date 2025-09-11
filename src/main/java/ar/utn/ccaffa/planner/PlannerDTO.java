package ar.utn.ccaffa.planner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDTO {
    public List<Long> ordenesDeVentaIDs;
    public boolean usarTodasLasMaquinas;
    public List<Long> maquinasIDs;
    public boolean usarTodosLosRollosDisponibles;
    public List<Long> rolloIDs;
    public int horasDeGracia;
    public int horaDeInicioLaboral;
    public int horaDeFinLaboral;
    public int anchoDesperdicio;
    public int largoDesperdicio;
    private int MULTIPLIER_OF_WASTE;
    private int TOP_DAYS_IN_ADVANCE;
    private int POPULATION_SIZE_PER_SALE;
}
