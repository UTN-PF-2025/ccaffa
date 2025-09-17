package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.model.dto.Bloque;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ordenes_de_trabajo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"ordenDeVenta", "ordenDeTrabajoMaquinas"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdenDeTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_trabajo_id")
    private Long id;

    private String nombre;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(length = 50)
    private String estado;

    @Column(length = 100)
    private String observaciones;

    @Column(name = "fecha_estimada_inicio")
    private LocalDateTime fechaEstimadaDeInicio;

    @Column(name = "fecha_estimada_fin")
    private LocalDateTime fechaEstimadaDeFin;

    @Column(name = "activa")
    private Boolean activa = true;

    @JoinColumn(name = "orden_venta_id")
    @ManyToOne()
    private OrdenVenta ordenDeVenta;

    @OneToMany(mappedBy = "ordenDeTrabajo", cascade = CascadeType.ALL)
    private List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinas;

    @ManyToOne
    @JoinColumn(name = "rollo_id", referencedColumnName = "id")
    private Rollo rollo;

    @ManyToOne
    @JoinColumn(name = "control_calidad_id")
    private ControlDeCalidad controlDeCalidad;

    public boolean yaComenzo() {
        return !getEstado().equalsIgnoreCase("Pendiente");
    }

    public boolean tieneMaquinaAsociada(Maquina maquina){
        return this.getOrdenDeTrabajoMaquinas().stream().anyMatch(m -> m.getMaquina() == maquina);
    }


    public OrdenDeTrabajoMaquina primeraOrdenMaquina(){
        return this.getOrdenDeTrabajoMaquinas().get(0);
    }

    public LocalDateTime fechaFinalizacionPrimeraMaquina(){
        return this.primeraOrdenMaquina().getFechaFin();
    }

    public LocalDateTime fechaInicioPrimeraMaquina(){
        return this.primeraOrdenMaquina().getFechaInicio();
    }

    static boolean equalsD(Float a, Float b) { return Math.abs(a-b) < 1e-9; }
    public boolean rolloIgualQueEspecificacion(){
        return
                equalsD(this.getRollo().getAnchoMM(), this.neededWidth())
                && equalsD(this.getRollo().getEspesorMM(), this.neededThickness())
                && equalsD(this.getRollo().getLargo(), this.neededLengthFromOriginalRoll());
    }
    public List<Rollo> procesarRollo(){
        if (this.getOrdenDeTrabajoMaquinas().isEmpty() ){
            if(this.rolloIgualQueEspecificacion()){
                this.getRollo().setEstado(EstadoRollo.AGOTADO);
                return new ArrayList<>();
            }
            throw new IllegalStateException("Se necesita configurar una maquina para este rollo y orden de venta");
        }


        List<Rollo> rolloHijos = new ArrayList<>();

        validarDimensiones();
        crearHijoPorAncho(rolloHijos);
        crearHijoPorLargo(rolloHijos);

        if (!rolloHijos.isEmpty()){
            this.getRollo().setEstado(EstadoRollo.DIVIDO);
        }

        return rolloHijos;

    }

    public Float neededWidth(){
        return this.getOrdenDeVenta().getEspecificacion().getAncho();
    }
    public Float neededThickness(){
        return this.getOrdenDeVenta().getEspecificacion().getAncho();
    }
    public Float neededLengthFromOriginalRoll(){
        Especificacion especificacion = this.getOrdenDeVenta().getEspecificacion();
        return especificacion.neededLengthOfRoll(this.getRollo());
    }

    private void crearHijoPorAncho(List<Rollo> rolloHijos){
        if (!equalsD(this.getRollo().getAnchoMM(), this.neededWidth())){
            Float anchoSobrante = this.getRollo().getAnchoMM() - this.neededWidth();
            rolloHijos.add(crearHijo(anchoSobrante, this.neededLengthFromOriginalRoll()));
        } ;
    }

    private void crearHijoPorLargo(List<Rollo> rolloHijos){
        if (!equalsD(this.getRollo().getLargo(), this.neededLengthFromOriginalRoll())){
            Float largoSobrante = this.getRollo().getLargo() - this.neededLengthFromOriginalRoll();
            rolloHijos.add(crearHijo(this.getRollo().getAnchoMM(), largoSobrante));
        } ;
    }

    private void validarDimensiones(){
        if ( this.neededWidth() > this.getRollo().getAnchoMM() || this.neededLengthFromOriginalRoll() > this.getRollo().getLargo()) {
            throw new IllegalArgumentException("El bloque requerido es más grande que el original.");
        }
    }

    private Rollo crearHijo(Float ancho, Float largo){
        Float pesoCalculado = Rollo.calcularPeso(ancho, this.getRollo().getEspesorMM(), largo);
        return Rollo.builder()
                .proveedorId(this.getRollo().getProveedorId())
                .codigoProveedor(this.getRollo().getCodigoProveedor())
                .anchoMM(ancho)
                .pesoKG(pesoCalculado)
                .espesorMM(this.getRollo().getEspesorMM())
                .tipoMaterial(this.getRollo().getTipoMaterial())
                .estado(EstadoRollo.DISPONIBLE)
                .fechaIngreso(fechaFinalizacionPrimeraMaquina())
                .rolloPadre(this.getRollo())
                .build();
    }




}