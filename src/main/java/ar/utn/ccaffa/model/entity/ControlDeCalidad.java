package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "controles_calidad")
public class ControlDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "control_calidad_id")
    private Long id;

    @Column(name = "R7")
    private Integer R7;

    @Column(name = "fecha_control")
    private LocalDate fechaControl;

    @Column(name = "espesor_medido")
    private Float espesorMedido;

    @Column(name = "ancho_medido")
    private Float anchoMedido;

    @Column(name = "rebaba_medio")
    private Float rebabaMedio;

    @Column(length = 50)
    private String estado;

    @OneToMany(mappedBy = "controlDeCalidad")
    private List<MedidaDeCalidad> medidasDeCalidad;

    @OneToOne
    @JoinColumn(name = "certificado_de_calidad_id")
    private CertificadoDeCalidad certificadoDeCalidad;

    @Transient
    private List<Defecto> defectos;
} 