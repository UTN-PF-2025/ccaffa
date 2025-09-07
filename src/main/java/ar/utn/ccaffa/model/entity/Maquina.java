package ar.utn.ccaffa.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Maquina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private MaquinaTipoEnum tipo;

    @Column(name = "velocidad_Trabajo_MetrosPorMinuto", nullable = false)
    private Float velocidadTrabajoMetrosPorMinuto;

    @Column(name = "espesorMaximoMilimetros", nullable = false)
    private Float espesorMaximoMilimetros;

    @Column(name = "espesorMinimoMilimetros", nullable = false)
    private Float espesorMinimoMilimetros;

    @Column(name = "anchoMaximoMilimetros", nullable = false)
    private Float anchoMaximoMilimetros;

    @Column(name = "anchoMinimoMilimetros", nullable = false)
    private Float anchoMinimoMilimetros;

    @Column(name = "es_activa")
    private Boolean esActiva;

    @OneToMany(mappedBy = "maquina", cascade = CascadeType.ALL)
    private List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas;

    public Long minutosParaProcesarEspecifiacion(Especificacion especificacion, Rollo rollo) {
        return (long) ( especificacion.neededLengthOfRoll(rollo)/velocidadTrabajoMetrosPorMinuto);
    }

}

