package ar.utn.ccaffa.exceptions;

public class UnprocessableContentException extends RuntimeException {

    public UnprocessableContentException(String resourceName) {
        super(String.format("%s no se puede procesar debido a error de negocio", resourceName));
    }

}
