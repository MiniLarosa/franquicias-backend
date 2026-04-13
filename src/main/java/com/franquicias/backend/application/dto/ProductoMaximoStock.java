package com.franquicias.backend.application.dto;

public record ProductoMaximoStock(
        String sucursalId,
        String sucursalNombre,
        String productoId,
        String productoNombre,
        Integer stock
) {
}
