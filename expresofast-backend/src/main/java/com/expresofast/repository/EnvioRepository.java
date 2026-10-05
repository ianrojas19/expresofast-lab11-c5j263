package com.expresofast.repository;

import com.expresofast.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByCodigoRastreo(String codigoRastreo);
    boolean existsByCodigoRastreo(String codigoRastreo);
}