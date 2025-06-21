package ar.utn.ccaffa.model.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "certificados_calidad")
@Data
public class CertificadoDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_de_certificado")
    private String numeroDeCertificado;
    
    @Column(name = "fecha_de_emision")
    private LocalDate fechaDeEmision;
    
    @ManyToOne
    @JoinColumn(name = "aprobador_id")
    private Empleado aprobador;
    
    @OneToOne
    @JoinColumn(name = "control_de_calidad_id")
    private ControlDeCalidad controlDeCalidad;
}
