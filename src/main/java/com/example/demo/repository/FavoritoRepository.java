package com.example.demo.repository;

import com.example.demo.model.Favorito;

import java.util.List;
import java.util.Optional;

// Define QUÉ operaciones existen, sin decir CÓMO se implementan.
// Esto permite que el día de mañana (TP2) cambies esta implementación 
// por una con base de datos real, sin tocar el resto del código.
public interface FavoritoRepository {
    Favorito save(Favorito favorito);
    List<Favorito> findAll();
    Optional<Favorito> findById(Long id);
    Favorito update(Favorito favorito);
    void deleteById(Long id);
    boolean existsById(Long id);
}