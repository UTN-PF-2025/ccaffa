package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Maquina")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Maquina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private MaquinaTipoEnum tipo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "velocidad_trabajo")
    private Float velocidadTrabajo;
} 