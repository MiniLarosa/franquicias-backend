package com.franquicias.backend.api.response;

public record ProductoMaximoStockResponse(
        String sucursalId,
        String sucursalNombre,
        String productoId,
        String productoNombre,
        Integer stock
) {
}
