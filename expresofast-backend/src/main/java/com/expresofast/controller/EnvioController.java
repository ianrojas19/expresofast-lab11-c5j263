package com.expresofast.controller;

import com.expresofast.dto.CrearEnvioDTO;
import com.expresofast.dto.EnvioDTO;
import com.expresofast.service.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/envios")
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

    @PostMapping
    public ResponseEntity<EnvioDTO> registrarEnvio(@RequestBody CrearEnvioDTO dto) {
        return ResponseEntity.ok(envioService.registrarEnvio(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioDTO> actualizarEstado(@PathVariable Long id, @RequestBody String estado) {
        // the JSON sent will likely be a string or object. 
        // Let's assume the frontend sends the string plain or wrapped in an object.
        // Usually it's better to expect a Map or a specific DTO, but for simplicity, let's parse a plain string
        // Actually, if it's sent as JSON e.g. "ENTREGADO", Spring can parse it if we use String.
        // Let's use a simple wrapper to be safe.
        // For now, let's keep String but wait, the PDF says: PATCH /api/v1/envios/{id}/estado
        // We'll map the body directly to the estado variable.
        // Wait, often times it's sent as `{"estado": "NUEVO_ESTADO"}`.
        // I will assume it's sent as plain text. Let's make it work with both.
        String nuevoEstado = estado.replace("\"", "").replace("{", "").replace("}", "").replace("estado:", "").trim();
        return ResponseEntity.ok(envioService.actualizarEstado(id, nuevoEstado));
    }
}
