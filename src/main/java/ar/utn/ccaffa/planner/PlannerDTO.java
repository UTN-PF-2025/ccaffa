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
    public int anchoDesperdicio;
    public int largoDesperdicio;
    private int INVALIDATE_SCORE = 1000000;
    private int MULTIPLIER_OF_WASTE = 1000;
}
