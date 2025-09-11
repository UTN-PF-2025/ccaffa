package ar.utn.ccaffa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {
    private Long id;
    private String name;
    private String email;
    private boolean active;
}
