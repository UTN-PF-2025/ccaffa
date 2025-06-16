package ar.utn.ccaffa.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Entity
@Data
public class MedidaDeCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "espesor_medido")
    private Float espesorMedido;
    @Column(name = "ancho_medido")
    private Float anchoMedido;
    @Column(name = "rebaba_medio")
    private Float rebabaMedio;
}
