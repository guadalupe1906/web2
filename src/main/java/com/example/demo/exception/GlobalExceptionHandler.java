package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// Centraliza el manejo de excepciones de TODOS los controllers.
// Cada método acá abajo "escucha" un tipo de excepción particular
// y la convierte en una respuesta HTTP prolija y consistente.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Caso: favorito inexistente (lo lanzamos nosotros desde FavoritoService) -> 404
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(NoSuchElementException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Caso: falla la validación del @Valid en el DTO de entrada -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación",
                detalles
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Caso: falla al consumir DummyJSON (caída, timeout, etc.) -> 502 Bad Gateway
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> manejarErrorApiExterna(RestClientException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_GATEWAY.value(),
                "Error al consultar el servicio externo de productos",
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }
}