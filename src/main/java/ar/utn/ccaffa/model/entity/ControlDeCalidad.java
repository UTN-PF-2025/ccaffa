package ar.utn.ccaffa.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "controles_calidad")
public class ControlDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "control_calidad_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "fecha_control")
    private LocalDateTime fechaControl;

    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    @Column(name = "espesor_medido")
    private Float espesorMedido;

    @Column(name = "ancho_medido")
    private Float anchoMedido;

    @Column(name = "rebaba_medio")
    private Float rebabaMedio;

    @Column(length = 50)
    private String estado;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "control_de_calidad_id")
    @JsonManagedReference("control-medida")
    private List<MedidaDeCalidad> medidasDeCalidad;

    @OneToOne
    @JoinColumn(name = "certificado_de_calidad_id")
    private CertificadoDeCalidad certificadoDeCalidad;

    @OneToMany(mappedBy = "controlDeCalidad", cascade = CascadeType.ALL)
    @JsonManagedReference("control-defecto")
    private List<Defecto> defectos;

    @Column(name = "orden_de_trabajo_id")
    private String ordenDeTrabajoId;
} 