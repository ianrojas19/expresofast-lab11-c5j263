package com.expresofast.controller;

import com.expresofast.dto.EnvioDTO;
import com.expresofast.dto.EnvioRegistroDTO;
import com.expresofast.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/envios", "/api/envios"})
@CrossOrigin(origins = "http://localhost:4200")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> obtenerTodos() {
        return ResponseEntity.ok(envioService.obtenerTodos());
    }

    @GetMapping("/rastreo/{codigo}")
    public ResponseEntity<EnvioDTO> obtenerPorRastreo(@PathVariable String codigo) {
        return ResponseEntity.ok(envioService.obtenerPorRastreo(codigo));
    }

    /** Validación asíncrona: indica si el número de rastreo ya existe. */
    @GetMapping("/check-tracking/{trackingNumber}")
    public ResponseEntity<Map<String, Boolean>> checkTracking(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(Map.of("existe", envioService.existeTracking(trackingNumber)));
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> registrarEnvio(@Valid @RequestBody EnvioRegistroDTO dto) {
        return ResponseEntity.ok(envioService.registrarEnvio(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioDTO> actualizarEstado(@PathVariable Long id, @RequestBody String estado) {
        // Acepta texto plano ("ENTREGADO"), JSON string o {"estado": "..."}
        String nuevoEstado = estado.replace("\"", "").replace("{", "").replace("}", "").replace("estado:", "").trim();
        return ResponseEntity.ok(envioService.actualizarEstado(id, nuevoEstado));
    }
}