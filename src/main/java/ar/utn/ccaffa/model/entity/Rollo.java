package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.enums.TipoRollo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rollo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rollo implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", insertable = false, updatable = false)
    private Proveedor proveedor;


    @Column(name = "proveedor_id", nullable = false)
    @NotNull(message = "El id de proveedor es obligatorio")
    private Long proveedorId;

    @Column(name = "codigo_proveedor", nullable = false)
    @NotNull(message = "El codigo de rollo proviniente del proveedor es obligatorio")
    private String codigoProveedor;

    @Column(name = "peso", nullable = false)
    @NotNull(message = "El peso es obligatorio")
    private Float pesoKG;

    @Column(name = "ancho", nullable = false)
    @NotNull(message = "El ancho es obligatorio")
    private Float anchoMM;

    @Column(name = "espesor", nullable = false)
    @NotNull(message = "El ancho es obligatorio")
    private Float espesorMM;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false)
    @NotNull(message = "El tipo de material es obligatorio")
    private TipoMaterial tipoMaterial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rollo", nullable = false)
    @NotNull(message = "El tipo de rollo es obligatorio")
    private TipoRollo tipoRollo = TipoRollo.MATERIA_PRIMA;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private EstadoRollo estado;

    @Column(name = "fecha_ingreso", nullable = false)
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    @Column(name = "rollo_padre_id", insertable = false, updatable = false)
    private Long rolloPadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_padre_id")
    private Rollo rolloPadre;

    @Column(name = "ordeDeTrabajoAsociadaID")
    private Long ordeDeTrabajoAsociadaID;

    @Column(name = "asociadoAOrdenDeTrabajo", nullable = false)
    @NotNull(message = "El campo asociadoAOrdenDeTrabajo es obligatorio")
    @ColumnDefault("false")
    private Boolean asociadaAOrdenDeTrabajo = false;

    @OneToMany(mappedBy = "rolloPadre", fetch = FetchType.LAZY)
    private List<Rollo> hijos = new ArrayList<>();
    // Método helper para obtener el rollo padre cuando se necesite

    public float getLargo(){
        return (float) (this.getPesoKG() / (this.getAnchoMM() * this.getEspesorMM() * 0.008));
    }

    public static float calcularPeso(Float ancho, Float espesorMM, Float largo){
        return (float) (ancho * espesorMM * largo* 0.008);
    }
    @Override
    public Object clone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }

}