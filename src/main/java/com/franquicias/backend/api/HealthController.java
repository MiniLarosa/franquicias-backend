package com.franquicias.backend.api;

import com.franquicias.backend.api.response.common.ApiEnvelope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Tag(name = "Health", description = "Endpoints de estado del servicio")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Retorna el estado del servicio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio activo",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<Map<String, String>>> health() {
        return Mono.just(ApiEnvelope.success("Servicio activo", Map.of("status", "UP")));
    }
}
