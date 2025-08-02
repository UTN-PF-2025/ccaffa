package ar.utn.ccaffa.handler;

import ar.utn.ccaffa.exceptions.ErrorResponse;
import ar.utn.ccaffa.exceptions.ResourceNotFoundException;
import ar.utn.ccaffa.exceptions.UnprocessableContentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductoNotFound(
            ResourceNotFoundException ex) {


        ErrorResponse error = ErrorResponse.builder()
                .status("NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessableContentException.class)
    public ResponseEntity<ErrorResponse> handleProductoNotFound(
            UnprocessableContentException ex) {


        ErrorResponse error = ErrorResponse.builder()
                .status("UNPROCESSABLE_ENTITY")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
