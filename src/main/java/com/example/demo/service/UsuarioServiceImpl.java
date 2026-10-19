package com.example.demo.service;

import com.example.demo.DTO.RegistroRequest;
import com.example.demo.DTO.UsuarioResponse;
import com.example.demo.exception.EmailYaExisteException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailYaExisteException(request.getEmail());
        }

        // Se guarda la contraseña NUNCA en texto plano, siempre hasheada
        Usuario nuevoUsuario = Usuario.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol("USER") // Rol por defecto USER
                .build();

        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        return UsuarioResponse.builder()
                .id(guardado.getId())
                .email(guardado.getEmail())
                .rol(guardado.getRol())
                .build();
    }
}