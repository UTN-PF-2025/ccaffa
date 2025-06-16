package ar.utn.ccaffa.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class CertificadoDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero_de_certificado")
    private String numeroDeCertificado;
    @Column(name = "fecha_de_emision")
    private LocalDate fechaDeEmision;
    @Column(name = "aprobador_id")
    private Empleado aprobador;
    @OneToOne
    private ControlDeCalidad controlDeCalidad;
}
