package com.franquicias.backend.api.request;

import jakarta.validation.constraints.NotBlank;

public record CrearFranquiciaRequest(
        @NotBlank(message = "El nombre de la franquicia es obligatorio")
        String nombre
) {
}
