package com.expresofast.dto;

import java.time.LocalDateTime;

public class EnvioDTO {
    private Long id;
    private String codigoRastreo;
    private String destinatario;
    private String direccionDestino;
    private Double montoFlete;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoRastreo() { return codigoRastreo; }
    public void setCodigoRastreo(String codigoRastreo) { this.codigoRastreo = codigoRastreo; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public Double getMontoFlete() { return montoFlete; }
    public void setMontoFlete(Double montoFlete) { this.montoFlete = montoFlete; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
