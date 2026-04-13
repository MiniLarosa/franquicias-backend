package com.franquicias.backend.api.response;

import java.util.List;

public record SucursalResponse(
        String id,
        String nombre,
        List<ProductoResponse> productos
) {
}
