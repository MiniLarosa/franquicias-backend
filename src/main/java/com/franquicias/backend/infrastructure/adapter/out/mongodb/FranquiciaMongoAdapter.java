package com.franquicias.backend.infrastructure.adapter.out.mongodb;

import com.franquicias.backend.application.port.out.FranquiciaPersistencePort;
import com.franquicias.backend.domain.Franquicia;
import com.franquicias.backend.repository.FranquiciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranquiciaMongoAdapter implements FranquiciaPersistencePort {

    private final FranquiciaRepository franquiciaRepository;

    @Override
    public Mono<Franquicia> findById(String id) {
        return franquiciaRepository.findById(id);
    }

    @Override
    public Mono<Franquicia> save(Franquicia franquicia) {
        return franquiciaRepository.save(franquicia);
    }
}
