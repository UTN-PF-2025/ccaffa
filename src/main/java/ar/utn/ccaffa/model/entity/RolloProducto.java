package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.EstadoRolloProducto;
import ar.utn.ccaffa.enums.TipoMaterial;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rollo_producto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RolloProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private EstadoRolloProducto estado;

    @Column(name = "fecha_ingreso", nullable = false)
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    @Column(name = "rollo_padre_id", insertable = false, updatable = false)
    private Long rolloPadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_padre_id", nullable = false)
    @NotNull(message = "El rollo padre es obligatorio")
    private Rollo rolloPadre;

    @Column(name = "orden_de_trabajo_id", insertable = false, updatable = false)
    private Long ordenDeTrabajoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_de_trabajo_id", nullable = false)
    @NotNull(message = "La orden de trabajo es obligatoria")
    private OrdenDeTrabajo ordenDeTrabajo;



}