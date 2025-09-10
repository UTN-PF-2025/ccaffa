package ar.utn.ccaffa.enums;

public enum EstadoOrdenTrabajoEnum {
    PROGRAMADA,
    EN_CURSO,
    FINALIZADA,
    ANULADA;


    public static boolean is(EstadoOrdenTrabajoEnum estado, EstadoOrdenTrabajoEnum estadoBuscado){
        return estado.equals(estadoBuscado);
    }

    public static boolean in(EstadoOrdenTrabajoEnum estado, EstadoOrdenTrabajoEnum... estados){
        for(EstadoOrdenTrabajoEnum estadoBuscado : estados){
            if(estado.equals(estadoBuscado)){
                return true;
            }
        }
        return false;
    }
}
