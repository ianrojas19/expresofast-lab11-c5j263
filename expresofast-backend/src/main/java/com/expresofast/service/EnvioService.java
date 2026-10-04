package com.expresofast.service;

import com.expresofast.dto.CrearEnvioDTO;
import com.expresofast.dto.EnvioDTO;
import java.util.List;

public interface EnvioService {
    List<EnvioDTO> obtenerTodos();
    EnvioDTO obtenerPorRastreo(String codigo);
    EnvioDTO registrarEnvio(CrearEnvioDTO dto);
    EnvioDTO actualizarEstado(Long id, String estado);
}
