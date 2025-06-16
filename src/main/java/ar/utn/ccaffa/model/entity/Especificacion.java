package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "especificaciones")
@Data
public class Especificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "especificacion_id")
    private Integer id;

    private Float ancho;
    private Float espesor;
    private Integer cantidad;

    @Column(name = "tipo_material", length = 50)
    private String tipoMaterial;

    @Column(name = "peso_max_por_rollo")
    private Float pesoMaxPorRollo;

    @Column(name = "tipo_de_embalaje", length = 50)
    private String tipoDeEmbalaje;

    @Column(name = "tolerancia_ancho")
    private Float toleranciaAncho;

    @Column(name = "tolerancia_espesor")
    private Float toleranciaEspesor;

    @Column(name = "diametro_interno")
    private Float diametroInterno;

    @Column(name = "diametro_externo")
    private Float diametroExterno;
} 