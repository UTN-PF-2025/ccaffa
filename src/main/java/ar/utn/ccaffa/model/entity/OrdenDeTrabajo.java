package ar.utn.ccaffa.model.entity;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoEnum;
import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.TipoRollo;
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
    @Enumerated(EnumType.STRING)
    private EstadoOrdenTrabajoEnum estado;

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
    @JoinColumn(name = "rollo_producto_id")
    private Rollo rolloProducto;

    public boolean yaComenzo() {
        return !EstadoOrdenTrabajoEnum.is(this.estado, EstadoOrdenTrabajoEnum.PROGRAMADA);
    }

    public boolean todosLosProcesosEstanFinalizados(){
        return this.getOrdenDeTrabajoMaquinas().stream().allMatch(otm -> otm.getEstado() == EstadoOrdenTrabajoMaquinaEnum.FINALIZADA);
    }

    public boolean esPrimeraOTM(OrdenDeTrabajoMaquina ordenDeTrabajoMaquina){
        return this.getOrdenDeTrabajoMaquinas().getFirst() == ordenDeTrabajoMaquina;
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
        crearHijoProducto(rolloHijos);

        this.getRollo().setOrdeDeTrabajoAsociadaID(this.getId());
        this.getRollo().setAsociadaAOrdenDeTrabajo(true);

        this.getOrdenDeTrabajoMaquinas().getFirst().setRolloAUsar(this.getRollo());
        if (this.getOrdenDeTrabajoMaquinas().size() == 2){
            this.getOrdenDeTrabajoMaquinas().get(1).setRolloAUsar(this.getRolloProducto());
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
            rolloHijos.add(crearHijo(anchoSobrante, this.neededLengthFromOriginalRoll(), TipoRollo.MATERIA_PRIMA));
        } ;
    }

    private void crearHijoPorLargo(List<Rollo> rolloHijos){
        if (!equalsD(this.getRollo().getLargo(), this.neededLengthFromOriginalRoll())){
            Float largoSobrante = this.getRollo().getLargo() - this.neededLengthFromOriginalRoll();
            rolloHijos.add(crearHijo(this.getRollo().getAnchoMM(), largoSobrante, TipoRollo.MATERIA_PRIMA));
        } ;
    }

    private void crearHijoProducto(List<Rollo> rolloHijos){
        Rollo hijoProducto = crearHijo(this.getOrdenDeVenta().getEspecificacion().getAncho(), 10f, TipoRollo.PRODUCTO);
        this.setRolloProducto(hijoProducto);
        hijoProducto.setAsociadaAOrdenDeTrabajo(true);
        hijoProducto.setOrdeDeTrabajoAsociadaID(this.getId());
        hijoProducto.setPesoKG(this.getOrdenDeVenta().getEspecificacion().getCantidad());
        hijoProducto.setEspesorMM(this.getOrdenDeVenta().getEspecificacion().getEspesor());
        rolloHijos.add(hijoProducto);
    }

    private void validarDimensiones(){
        if ( this.neededWidth() > this.getRollo().getAnchoMM() || this.neededLengthFromOriginalRoll() > this.getRollo().getLargo()) {
            throw new IllegalArgumentException("El bloque requerido es más grande que el original.");
        }
    }

    private Rollo crearHijo(Float ancho, Float largo, TipoRollo tipoRollo){
        Float pesoCalculado = Rollo.calcularPeso(ancho, this.getRollo().getEspesorMM(), largo);
        return Rollo.builder()
                .proveedorId(this.getRollo().getProveedorId())
                .codigoProveedor(this.getRollo().getCodigoProveedor())
                .anchoMM(ancho)
                .pesoKG(pesoCalculado)
                .espesorMM(this.getRollo().getEspesorMM())
                .tipoMaterial(this.getRollo().getTipoMaterial())
                .tipoRollo(tipoRollo)
                .estado(EstadoRollo.PLANIFICADO)
                .fechaIngreso(fechaFinalizacionPrimeraMaquina())
                .rolloPadre(this.getRollo())
                .ordeDeTrabajoAsociadaID(null)
                .asociadaAOrdenDeTrabajo(false)
                .build();
    }




}