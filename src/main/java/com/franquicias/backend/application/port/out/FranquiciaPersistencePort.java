package com.franquicias.backend.application.port.out;

import com.franquicias.backend.domain.Franquicia;
import reactor.core.publisher.Mono;

public interface FranquiciaPersistencePort {

    Mono<Franquicia> findById(String id);

    Mono<Franquicia> save(Franquicia franquicia);
}
