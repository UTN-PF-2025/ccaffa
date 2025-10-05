package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.TipoMaterial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "especificacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Especificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ancho")
    private Float ancho;

    @Column(name = "espesor")
    private Float espesor;

    @Column(name = "cantidad")
    private Float cantidad;

    @Column(name = "tipo_material")
    private TipoMaterial tipoMaterial;

    @Column(name = "peso_maximo_por_rollo")
    private Float pesoMaximoPorRollo;

    @Column(name = "tipo_de_embalaje")
    private String tipoDeEmbalaje;

    @Column(name = "tolerancia_ancho")
    private Float toleranciaAncho;

    @Column(name = "tolerancia_espesor")
    private Float toleranciaEspesor;

    @Column(name = "diametro_interno")
    private Float diametroInterno;

    @Column(name = "diametro_externo")
    private Float diametroExterno;

    public float neededLengthOfRoll(Rollo rollo){
        return (float) (this.getCantidad() /(0.008 * this.getAncho() * rollo.getEspesorMM()));
    }
    public float lenghtOfRoll(){
        return (float) (this.getCantidad() /(0.008 * this.getAncho() * this.getEspesor()));
    }

} 