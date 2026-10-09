package com.example.demo.repository;

import com.example.demo.model.Lista;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpa;

    public ListaRepositoryAdapter(ListaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Lista save(Lista lista) {
        return toDomain(jpa.save(toEntity(lista)));
    }

    @Override
    public List<Lista> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    private Lista toDomain(ListaEntity e) {
        return new Lista(e.getId(), e.getNombre());
    }

    private ListaEntity toEntity(Lista l) {
        return new ListaEntity(l.getId(), l.getNombre());
    }
}