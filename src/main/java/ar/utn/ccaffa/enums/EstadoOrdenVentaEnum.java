package ar.utn.ccaffa.enums;

public enum EstadoOrdenVentaEnum {
    A_PLANIFICAR,
    PROGRAMADA,
    EN_CURSO,
    FINALIZADA,
    ANULADA,
    REPLANIFICAR;

    public static boolean is(EstadoOrdenVentaEnum estado, EstadoOrdenVentaEnum estadoBuscado){
        return estado.equals(estadoBuscado);
    }
}
