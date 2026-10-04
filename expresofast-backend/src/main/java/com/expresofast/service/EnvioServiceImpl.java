package com.expresofast.service;

import com.expresofast.dto.CrearEnvioDTO;
import com.expresofast.dto.EnvioDTO;
import com.expresofast.model.Envio;
import com.expresofast.repository.EnvioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

@Service
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository repository;

    public EnvioServiceImpl(EnvioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EnvioDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public EnvioDTO obtenerPorRastreo(String codigo) {
        Envio envio = repository.findByCodigoRastreo(codigo)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        return mapToDTO(envio);
    }

    @Override
    public EnvioDTO registrarEnvio(CrearEnvioDTO dto) {
        Envio envio = new Envio();
        envio.setDestinatario(dto.getDestinatario());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setMontoFlete(dto.getMontoFlete());
        envio.setEstado("PENDIENTE");
        
        // Generate random code like EXP-2026-XXXX
        Random r = new Random();
        int code = 1000 + r.nextInt(9000);
        envio.setCodigoRastreo("EXP-2026-" + code);
        
        Envio guardado = repository.save(envio);
        return mapToDTO(guardado);
    }

    @Override
    public EnvioDTO actualizarEstado(Long id, String estado) {
        Envio envio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        envio.setEstado(estado);
        Envio actualizado = repository.save(envio);
        return mapToDTO(actualizado);
    }

    private EnvioDTO mapToDTO(Envio envio) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(envio.getId());
        dto.setCodigoRastreo(envio.getCodigoRastreo());
        dto.setDestinatario(envio.getDestinatario());
        dto.setDireccionDestino(envio.getDireccionDestino());
        dto.setMontoFlete(envio.getMontoFlete());
        dto.setEstado(envio.getEstado());
        dto.setFechaCreacion(envio.getFechaCreacion());
        return dto;
    }
}
