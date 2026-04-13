package com.franquicias.backend.repository;

import com.franquicias.backend.domain.Franquicia;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranquiciaRepository extends ReactiveMongoRepository<Franquicia, String> {
}
