package ar.utn.ccaffa.model.entity;

import java.sql.Time;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ordenes_de_trabajo")
@Data
public class OrdenDeTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_trabajo_id")
    private Long id;

    private String nombre;

    @Column(name = "fecha_inicio")
    private Time fechaInicio;

    @Column(name = "fecha_fin")
    private Time fechaFin;

    @Column(length = 50)
    private String estado;

    @Column(length = 100)
    private String observaciones;

    @Column(name = "fecha_estimada_inicio")
    private Time fechaEstimadaDeInicio;

    @Column(name = "fecha_estimada_fin")
    private Time fechaEstimadaDeFin;

    @Column(name = "activa")
    private Boolean activa = true;

    @OneToMany(mappedBy = "ordenDeTrabajo")
    private List<OrdenDeVenta> ordenesDeVenta;

    @ManyToOne
    @JoinColumn(name = "rollo_id")
    private Rollo rollo;

    @ManyToOne
    @JoinColumn(name = "control_calidad_id")
    private ControlDeCalidad controlDeCalidad;

    @ManyToMany
    @JoinTable(
        name = "ordenes_trabajo_maquinas",
        joinColumns = @JoinColumn(name = "orden_trabajo_id"),
        inverseJoinColumns = @JoinColumn(name = "maquina_id")
    )
    private List<Maquina> maquinas;

}