package com.example.demo.service;


import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.RegistroRequest;
import com.example.demo.DTO.UsuarioResponse;
import com.example.demo.DTO.LoginResponse;
import com.example.demo.exception.EmailYaExisteException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtService; // ajustá al paquete donde creaste JwtService

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

     @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getId(), usuario.getRol());
        return new LoginResponse(token);
    }
}