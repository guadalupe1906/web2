package com.example.demo.repository;

import com.example.demo.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}