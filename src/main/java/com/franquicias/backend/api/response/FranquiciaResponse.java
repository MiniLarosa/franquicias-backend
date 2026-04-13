package com.franquicias.backend.api.response;

import java.util.List;

public record FranquiciaResponse(
        String id,
        String nombre,
        List<SucursalResponse> sucursales
) {
}
