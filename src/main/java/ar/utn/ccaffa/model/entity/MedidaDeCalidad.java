package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medidas_calidad")
@Data
public class MedidaDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "espesor_medido")
    private Float espesorMedido;
    @Column(name = "ancho_medido")
    private Float anchoMedido;
    @Column(name = "rebaba_medido")
    private Float rebabaMedido;
}
