package com.franquicias.backend.api;

import com.franquicias.backend.application.usecase.FranquiciaUseCase;
import com.franquicias.backend.api.mapper.FranquiciaResponseMapper;
import com.franquicias.backend.api.request.ActualizarStockRequest;
import com.franquicias.backend.api.request.ActualizarNombreRequest;
import com.franquicias.backend.api.request.CrearFranquiciaRequest;
import com.franquicias.backend.api.request.CrearProductoRequest;
import com.franquicias.backend.api.request.CrearSucursalRequest;
import com.franquicias.backend.api.response.FranquiciaResponse;
import com.franquicias.backend.api.response.ProductoMaximoStockResponse;
import com.franquicias.backend.api.response.common.ApiEnvelope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/franquicias")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Franquicias", description = "Operaciones para gestionar franquicias, sucursales y productos")
public class FranquiciaController {

    private final FranquiciaUseCase franquiciaUseCase;
    private final FranquiciaResponseMapper franquiciaResponseMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear franquicia", description = "Crea una nueva franquicia con nombre y lista de sucursales vacia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Franquicia creada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> crearFranquicia(@Valid @RequestBody CrearFranquiciaRequest request) {
        log.info("Request crearFranquicia nombre={}", request.nombre());
        return franquiciaUseCase.crearFranquicia(request.nombre())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Franquicia creada", response));
    }

    @PostMapping("/{franquiciaId}/sucursales")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar sucursal", description = "Agrega una sucursal a una franquicia existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucursal agregada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> agregarSucursal(
            @PathVariable String franquiciaId,
            @Valid @RequestBody CrearSucursalRequest request
    ) {
        log.info("Request agregarSucursal franquiciaId={} nombre={}", franquiciaId, request.nombre());
        return franquiciaUseCase.agregarSucursal(franquiciaId, request.nombre())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Sucursal agregada", response));
    }

    @PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar producto", description = "Agrega un producto con stock a una sucursal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> agregarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody CrearProductoRequest request
    ) {
        log.info("Request agregarProducto franquiciaId={} sucursalId={} nombre={} stock={}",
                franquiciaId, sucursalId, request.nombre(), request.stock());
        return franquiciaUseCase.agregarProducto(franquiciaId, sucursalId, request.nombre(), request.stock())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Producto agregado", response));
    }

    @DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto de una sucursal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> eliminarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId
    ) {
        log.info("Request eliminarProducto franquiciaId={} sucursalId={} productoId={}", franquiciaId, sucursalId, productoId);
        return franquiciaUseCase.eliminarProducto(franquiciaId, sucursalId, productoId)
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Producto eliminado", response));
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock")
    @Operation(summary = "Actualizar stock", description = "Actualiza el stock de un producto en una sucursal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> actualizarStock(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarStockRequest request
    ) {
        log.info("Request actualizarStock franquiciaId={} sucursalId={} productoId={} stock={}",
                franquiciaId, sucursalId, productoId, request.stock());
        return franquiciaUseCase.actualizarStockProducto(franquiciaId, sucursalId, productoId, request.stock())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Stock actualizado", response));
    }

    @GetMapping("/{franquiciaId}/productos/max-stock-por-sucursal")
    @Operation(summary = "Producto con maximo stock por sucursal",
            description = "Retorna, para la franquicia indicada, el producto de mayor stock por cada sucursal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<List<ProductoMaximoStockResponse>>> obtenerMaximoStockPorSucursal(@PathVariable String franquiciaId) {
        log.info("Request obtenerMaximoStockPorSucursal franquiciaId={}", franquiciaId);
        return franquiciaUseCase.obtenerProductoMaximoStockPorSucursal(franquiciaId)
                .map(productos -> productos.stream()
                        .map(franquiciaResponseMapper::toProductoMaximoStockResponse)
                        .toList())
                .map(response -> ApiEnvelope.success("Consulta realizada", response));
    }

    @PatchMapping("/{franquiciaId}/nombre")
    @Operation(summary = "Actualizar nombre de franquicia", description = "Actualiza el nombre de una franquicia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> actualizarNombreFranquicia(
            @PathVariable String franquiciaId,
            @Valid @RequestBody ActualizarNombreRequest request
    ) {
        log.info("Request actualizarNombreFranquicia franquiciaId={} nombre={}", franquiciaId, request.nombre());
        return franquiciaUseCase.actualizarNombreFranquicia(franquiciaId, request.nombre())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Nombre de franquicia actualizado", response));
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/nombre")
    @Operation(summary = "Actualizar nombre de sucursal", description = "Actualiza el nombre de una sucursal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> actualizarNombreSucursal(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody ActualizarNombreRequest request
    ) {
        log.info("Request actualizarNombreSucursal franquiciaId={} sucursalId={} nombre={}",
                franquiciaId, sucursalId, request.nombre());
        return franquiciaUseCase.actualizarNombreSucursal(franquiciaId, sucursalId, request.nombre())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Nombre de sucursal actualizado", response));
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre")
    @Operation(summary = "Actualizar nombre de producto", description = "Actualiza el nombre de un producto en una sucursal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiEnvelope.class)))
    })
    public Mono<ApiEnvelope<FranquiciaResponse>> actualizarNombreProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarNombreRequest request
    ) {
        log.info("Request actualizarNombreProducto franquiciaId={} sucursalId={} productoId={} nombre={}",
                franquiciaId, sucursalId, productoId, request.nombre());
        return franquiciaUseCase.actualizarNombreProducto(franquiciaId, sucursalId, productoId, request.nombre())
                .map(franquiciaResponseMapper::toResponse)
                .map(response -> ApiEnvelope.success("Nombre de producto actualizado", response));
    }
}
