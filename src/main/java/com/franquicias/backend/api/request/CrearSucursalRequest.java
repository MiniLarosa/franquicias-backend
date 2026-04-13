package com.franquicias.backend.api.request;

import jakarta.validation.constraints.NotBlank;

public record CrearSucursalRequest(
        @NotBlank(message = "El nombre de la sucursal es obligatorio")
        String nombre
) {
}
