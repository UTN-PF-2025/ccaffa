package ar.utn.ccaffa.model.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
