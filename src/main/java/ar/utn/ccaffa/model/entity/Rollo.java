package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Rollo")
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
    private Float peso;

    @Column(name = "ancho", nullable = false)
    @NotNull(message = "El ancho es obligatorio")
    private Float ancho;

    @Column(name = "espesor", nullable = false)
    @NotNull(message = "El ancho es obligatorio")
    private Float espesor;

    @Column(name = "tipo_material", nullable = false)
    @NotNull(message = "El tipo de material es obligatorio")
    private String tipoMaterial;

    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private String estado;

    @Column(name = "fecha_ingreso", nullable = false)
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_padre_id")
    private Rollo rollo_padre;


}