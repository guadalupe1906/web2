package com.example.demo.repository;

import com.example.demo.model.Lista;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpa;
    private final UsuarioJpaRepository usuarioJpaRepository; // Inyectamos el repositorio de usuarios

    public ListaRepositoryAdapter(ListaJpaRepository jpa, UsuarioJpaRepository usuarioJpaRepository) {
        this.jpa = jpa;
        this.usuarioJpaRepository = usuarioJpaRepository;
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
        // Extraemos el ID del usuario asociado si la relación no es nula
        Long usuarioId = (e.getUsuario() != null) ? e.getUsuario().getId() : null;
        return new Lista(e.getId(), e.getNombre(), usuarioId);
    }

    private ListaEntity toEntity(Lista l) {
        ListaEntity entity = new ListaEntity();
        entity.setId(l.getId());
        entity.setNombre(l.getNombre());
        
        // Buscamos y asociamos la entidad UsuarioEntity utilizando el usuarioId del dominio
        if (l.getUsuarioId() != null) {
            UsuarioEntity usuarioEntity = usuarioJpaRepository.findById(l.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + l.getUsuarioId()));
            entity.setUsuario(usuarioEntity);
        }
        
        return entity;
    }
}