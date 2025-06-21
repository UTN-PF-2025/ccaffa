package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoMaterial;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rollo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rollo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private EstadoRollo estado;

    @Column(name = "fecha_ingreso", nullable = false)
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_padre_id")
    private Rollo rolloPadreId;

    // Método helper para obtener el rollo padre cuando lo necesites
    @Transient
    public Rollo getRolloPadre(RolloRepository rolloRepository) {
        if (rolloPadreId != null) {
            return rolloRepository.findById(rolloPadreId).orElse(null);
        }
        return null;
    }

}