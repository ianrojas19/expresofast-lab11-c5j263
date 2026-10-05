package com.expresofast.service;

import com.expresofast.dto.EnvioDTO;
import com.expresofast.dto.EnvioRegistroDTO;
import java.util.List;

public interface EnvioService {
    List<EnvioDTO> obtenerTodos();
    EnvioDTO obtenerPorRastreo(String codigo);
    EnvioDTO registrarEnvio(EnvioRegistroDTO dto);
    EnvioDTO actualizarEstado(Long id, String estado);
    boolean existeTracking(String trackingNumber);
}