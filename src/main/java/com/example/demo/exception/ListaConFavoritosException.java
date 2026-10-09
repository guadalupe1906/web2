package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ListaConFavoritosException extends RuntimeException {
    public ListaConFavoritosException(Long id) {
        super("No se puede eliminar la lista " + id + " porque todavía tiene favoritos");
    }
}