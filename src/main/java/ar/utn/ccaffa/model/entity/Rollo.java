package ar.utn.ccaffa.model.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rollos")
public class Rollo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rollo_id")
    private Long id;

    @Column(name = "codigo_proveedor", length = 50)
    private String codigoProveedor;

    private Float peso;
    private Float ancho;
    private Float espesor;

    @Column(name = "tipo_material", length = 50)
    private String tipoMaterial;

    @Column(length = 50)
    private String estado;

    @Column(name = "fecha_de_ingreso")
    private LocalDate fechaDeIngreso;

    @Column(name = "codigo_de_barras", length = 50)
    private char[] CodigoDeBarras;

    @Column(name = "rollo_padre_id")
    private Rollo rolloPadre;



} 