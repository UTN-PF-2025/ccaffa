package ar.utn.ccaffa.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeTrabajoMaquinaId implements Serializable {
    
    private Long ordenTrabajoId;
    private Long maquinaId;
} 