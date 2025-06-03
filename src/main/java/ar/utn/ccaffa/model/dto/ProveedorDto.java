package ar.utn.ccaffa.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProveedorDto {
    private Long id;
    private String name;
}
