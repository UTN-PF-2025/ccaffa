package ar.utn.ccaffa.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "ordenes_de_venta")
public class OrdenDeVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_venta_id")
    private Long id;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "R4")
    private Integer R4;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @Column(length = 50)
    private String estado;

    @Column(length = 1000)
    private String observaciones;

    // Relación con OrdenDeTrabajo
    @ManyToOne
    @JoinColumn(name = "R5")
    private OrdenDeTrabajo ordenDeTrabajo;
}
