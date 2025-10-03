package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes_trabajo_maquinas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdenDeTrabajoMaquina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_trabajo_id")
    private OrdenDeTrabajo ordenDeTrabajo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id")
    private Maquina maquina;
    
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoOrdenTrabajoMaquinaEnum estado;
    
    @Column(name = "observaciones")
    private String observaciones;

    public void anular() {
        List<EstadoOrdenTrabajoMaquinaEnum> estadosPreviosNecesarios = List.of(EstadoOrdenTrabajoMaquinaEnum.EN_CURSO, EstadoOrdenTrabajoMaquinaEnum.PROGRAMADA);
        if (estadosPreviosNecesarios.contains(this.estado)){
            this.setEstado(EstadoOrdenTrabajoMaquinaEnum.ANULADA);
        }

    }
}

