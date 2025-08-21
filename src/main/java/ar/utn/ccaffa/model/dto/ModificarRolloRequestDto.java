package ar.utn.ccaffa.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModificarRolloRequestDto {

    @NotNull(message = "El id del rollo es obligatorio")
    private Long id;

    @Positive(message = "El peso debe ser un valor positivo")
    private Float pesoKG;

    @Positive(message = "El ancho debe ser un valor positivo")
    private Float anchoMM;
} 