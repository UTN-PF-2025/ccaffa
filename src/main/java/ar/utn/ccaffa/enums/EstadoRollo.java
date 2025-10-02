package ar.utn.ccaffa.enums;

public enum EstadoRollo {
    PLANIFICADO,
    DISPONIBLE,
    AGOTADO,
    DIVIDIDO,
    CANCELADO,
    DESPERDICIO,
    VERIFICAR;

    public static boolean is(String estado, EstadoRollo estadoBuscadoEnum){
        return estado.equals(estadoBuscadoEnum.name());
    }
}
