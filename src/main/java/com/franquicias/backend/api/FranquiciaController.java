package com.franquicias.backend.api;

import com.franquicias.backend.application.dto.ProductoMaximoStock;
import com.franquicias.backend.application.usecase.FranquiciaUseCase;
import com.franquicias.backend.api.mapper.FranquiciaResponseMapper;
import com.franquicias.backend.api.request.ActualizarStockRequest;
import com.franquicias.backend.api.request.ActualizarNombreRequest;
import com.franquicias.backend.api.request.CrearFranquiciaRequest;
import com.franquicias.backend.api.request.CrearProductoRequest;
import com.franquicias.backend.api.request.CrearSucursalRequest;
import com.franquicias.backend.api.response.FranquiciaResponse;
import com.franquicias.backend.api.response.ProductoMaximoStockResponse;
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
public class FranquiciaController {

    private final FranquiciaUseCase franquiciaUseCase;
    private final FranquiciaResponseMapper franquiciaResponseMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranquiciaResponse> crearFranquicia(@Valid @RequestBody CrearFranquiciaRequest request) {
        log.info("Request crearFranquicia nombre={}", request.nombre());
        return franquiciaUseCase.crearFranquicia(request.nombre())
                .map(franquiciaResponseMapper::toResponse);
    }

    @PostMapping("/{franquiciaId}/sucursales")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranquiciaResponse> agregarSucursal(
            @PathVariable String franquiciaId,
            @Valid @RequestBody CrearSucursalRequest request
    ) {
        log.info("Request agregarSucursal franquiciaId={} nombre={}", franquiciaId, request.nombre());
        return franquiciaUseCase.agregarSucursal(franquiciaId, request.nombre())
                .map(franquiciaResponseMapper::toResponse);
    }

    @PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranquiciaResponse> agregarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody CrearProductoRequest request
    ) {
        log.info("Request agregarProducto franquiciaId={} sucursalId={} nombre={} stock={}",
                franquiciaId, sucursalId, request.nombre(), request.stock());
        return franquiciaUseCase.agregarProducto(franquiciaId, sucursalId, request.nombre(), request.stock())
                .map(franquiciaResponseMapper::toResponse);
    }

    @DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    public Mono<FranquiciaResponse> eliminarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId
    ) {
        log.info("Request eliminarProducto franquiciaId={} sucursalId={} productoId={}", franquiciaId, sucursalId, productoId);
        return franquiciaUseCase.eliminarProducto(franquiciaId, sucursalId, productoId)
                .map(franquiciaResponseMapper::toResponse);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock")
    public Mono<FranquiciaResponse> actualizarStock(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarStockRequest request
    ) {
        log.info("Request actualizarStock franquiciaId={} sucursalId={} productoId={} stock={}",
                franquiciaId, sucursalId, productoId, request.stock());
        return franquiciaUseCase.actualizarStockProducto(franquiciaId, sucursalId, productoId, request.stock())
                .map(franquiciaResponseMapper::toResponse);
    }

    @GetMapping("/{franquiciaId}/productos/max-stock-por-sucursal")
    public Mono<List<ProductoMaximoStockResponse>> obtenerMaximoStockPorSucursal(@PathVariable String franquiciaId) {
        log.info("Request obtenerMaximoStockPorSucursal franquiciaId={}", franquiciaId);
        return franquiciaUseCase.obtenerProductoMaximoStockPorSucursal(franquiciaId)
                .map(productos -> productos.stream()
                        .map(franquiciaResponseMapper::toProductoMaximoStockResponse)
                        .toList());
    }

    @PatchMapping("/{franquiciaId}/nombre")
    public Mono<FranquiciaResponse> actualizarNombreFranquicia(
            @PathVariable String franquiciaId,
            @Valid @RequestBody ActualizarNombreRequest request
    ) {
        log.info("Request actualizarNombreFranquicia franquiciaId={} nombre={}", franquiciaId, request.nombre());
        return franquiciaUseCase.actualizarNombreFranquicia(franquiciaId, request.nombre())
                .map(franquiciaResponseMapper::toResponse);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/nombre")
    public Mono<FranquiciaResponse> actualizarNombreSucursal(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody ActualizarNombreRequest request
    ) {
        log.info("Request actualizarNombreSucursal franquiciaId={} sucursalId={} nombre={}",
                franquiciaId, sucursalId, request.nombre());
        return franquiciaUseCase.actualizarNombreSucursal(franquiciaId, sucursalId, request.nombre())
                .map(franquiciaResponseMapper::toResponse);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre")
    public Mono<FranquiciaResponse> actualizarNombreProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarNombreRequest request
    ) {
        log.info("Request actualizarNombreProducto franquiciaId={} sucursalId={} productoId={} nombre={}",
                franquiciaId, sucursalId, productoId, request.nombre());
        return franquiciaUseCase.actualizarNombreProducto(franquiciaId, sucursalId, productoId, request.nombre())
                .map(franquiciaResponseMapper::toResponse);
    }
}
