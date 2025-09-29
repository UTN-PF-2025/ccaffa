package ar.utn.ccaffa.model.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certificados_calidad")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_de_certificado")
    private String numeroDeCertificado;
    
    @Column(name = "fecha_de_emision")
    private LocalDate fechaDeEmision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobador_id")
    private Empleado aprobador;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;
    
    @OneToOne
    @JoinColumn(name = "control_de_calidad_id")
    private ControlDeCalidad controlDeCalidad;
}
