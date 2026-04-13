package com.franquicias.backend.api.request;

import jakarta.validation.constraints.NotBlank;

public record ActualizarNombreRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
