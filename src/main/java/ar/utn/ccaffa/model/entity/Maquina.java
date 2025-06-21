package ar.utn.ccaffa.model.entity;

import java.util.List;

import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maquina")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Maquina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private MaquinaTipoEnum tipo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "velocidad_trabajo")
    private Float velocidadTrabajo;

    @Column(name = "es_activa")
    private Boolean esActiva;

    @ManyToMany(mappedBy = "maquinas")
    private List<OrdenDeTrabajo> ordenDeTrabajos;
} 