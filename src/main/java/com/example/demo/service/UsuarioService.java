package com.example.demo.service;

import com.example.demo.DTO.RegistroRequest;
import com.example.demo.DTO.UsuarioResponse;

public interface UsuarioService {
    UsuarioResponse registrar(RegistroRequest request);
}