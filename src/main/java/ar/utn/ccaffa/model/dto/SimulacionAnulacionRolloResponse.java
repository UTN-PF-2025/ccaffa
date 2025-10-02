package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SimulacionAnulacionRolloResponse {
    private List<Long> ids;
}
