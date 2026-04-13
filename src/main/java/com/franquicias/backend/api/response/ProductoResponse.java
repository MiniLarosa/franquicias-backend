package com.franquicias.backend.api.response;

public record ProductoResponse(
        String id,
        String nombre,
        Integer stock
) {
}
