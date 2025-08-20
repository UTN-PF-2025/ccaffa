package ar.utn.ccaffa.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "ordenes_de_trabajo")
@Data
@EqualsAndHashCode(exclude = {"ordenDeVenta", "ordenDeTrabajoMaquinas"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdenDeTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_trabajo_id")
    private Long id;

    private String nombre;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(length = 50)
    private String estado;

    @Column(length = 100)
    private String observaciones;

    @Column(name = "fecha_estimada_inicio")
    private LocalDateTime fechaEstimadaDeInicio;

    @Column(name = "fecha_estimada_fin")
    private LocalDateTime fechaEstimadaDeFin;

    @Column(name = "activa")
    private Boolean activa = true;

    
    @OneToOne(mappedBy = "ordenDeTrabajo")
    private OrdenVenta ordenDeVenta;

    @OneToMany(mappedBy = "ordenDeTrabajo", cascade = CascadeType.ALL)
    private List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas;

    @ManyToOne
    @JoinColumn(name = "rollo_id", referencedColumnName = "id")
    private Rollo rollo;

    @ManyToOne
    @JoinColumn(name = "control_calidad_id")
    private ControlDeCalidad controlDeCalidad;

    public boolean yaComenzo() {
        return !getEstado().equalsIgnoreCase("Pendiente");
    }
}