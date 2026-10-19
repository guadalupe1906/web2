package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity savedEntity = usuarioJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioJpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    // Métodos de mapeo (pueden ir aquí o en un mapper separado)
    private UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .passwordHash(usuario.getPasswordHash())
                .rol(usuario.getRol())
                .build();
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .rol(entity.getRol())
                .build();
    }
}