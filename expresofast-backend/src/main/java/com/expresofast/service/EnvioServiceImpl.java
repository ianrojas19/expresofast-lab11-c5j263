package com.expresofast.service;

import com.expresofast.dto.EnvioDTO;
import com.expresofast.dto.EnvioRegistroDTO;
import com.expresofast.dto.PaqueteDTO;
import com.expresofast.model.Envio;
import com.expresofast.model.Paquete;
import com.expresofast.repository.EnvioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository repository;

    public EnvioServiceImpl(EnvioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioDTO obtenerPorRastreo(String codigo) {
        Envio envio = repository.findByCodigoRastreo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Envío no encontrado"));
        return mapToDTO(envio);
    }

    /**
     * Inserta el envío y todos sus paquetes en una única transacción:
     * si falla cualquier paquete, se revierte también el envío.
     */
    @Override
    @Transactional
    public EnvioDTO registrarEnvio(EnvioRegistroDTO dto) {
        String tracking = dto.getNumeroTracking().trim();
        if (repository.existsByCodigoRastreo(tracking)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este número de rastreo ya está en uso");
        }
        if (!dto.getFechaEntregaEstimada().isAfter(dto.getFechaDespacho())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de entrega estimada debe ser posterior a la fecha de despacho");
        }

        Envio envio = new Envio();
        envio.setCodigoRastreo(tracking);
        envio.setDestinatario(dto.getDestinatario());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setMontoFlete(dto.getMontoFlete());
        envio.setEstado("PENDIENTE");
        envio.setFechaDespacho(dto.getFechaDespacho());
        envio.setFechaEntregaEstimada(dto.getFechaEntregaEstimada());

        for (PaqueteDTO p : dto.getPaquetes()) {
            Paquete paquete = new Paquete();
            paquete.setDescripcion(p.getDescripcion());
            paquete.setPesoKg(p.getPesoKg());
            envio.agregarPaquete(paquete);
        }

        return mapToDTO(repository.save(envio));
    }

    @Override
    @Transactional
    public EnvioDTO actualizarEstado(Long id, String estado) {
        Envio envio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Envío no encontrado"));
        envio.setEstado(estado);
        return mapToDTO(repository.save(envio));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTracking(String trackingNumber) {
        return repository.existsByCodigoRastreo(trackingNumber.trim());
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
        dto.setFechaDespacho(envio.getFechaDespacho());
        dto.setFechaEntregaEstimada(envio.getFechaEntregaEstimada());
        dto.setPaquetes(envio.getPaquetes().stream().map(p -> {
            PaqueteDTO pd = new PaqueteDTO();
            pd.setId(p.getId());
            pd.setDescripcion(p.getDescripcion());
            pd.setPesoKg(p.getPesoKg());
            return pd;
        }).collect(Collectors.toList()));
        return dto;
    }
}