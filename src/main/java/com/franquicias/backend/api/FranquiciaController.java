package com.franquicias.backend.api;

import com.franquicias.backend.api.request.ActualizarStockRequest;
import com.franquicias.backend.api.request.CrearFranquiciaRequest;
import com.franquicias.backend.api.request.CrearProductoRequest;
import com.franquicias.backend.api.request.CrearSucursalRequest;
import com.franquicias.backend.api.response.FranquiciaResponse;
import com.franquicias.backend.api.response.ProductoResponse;
import com.franquicias.backend.api.response.SucursalResponse;
import com.franquicias.backend.domain.Franquicia;
import com.franquicias.backend.service.FranquiciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequiredArgsConstructor
public class FranquiciaController {

    private final FranquiciaService franquiciaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranquiciaResponse> crearFranquicia(@Valid @RequestBody CrearFranquiciaRequest request) {
        return franquiciaService.crearFranquicia(request.nombre())
                .map(this::toResponse);
    }

    @PostMapping("/{franquiciaId}/sucursales")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranquiciaResponse> agregarSucursal(
            @PathVariable String franquiciaId,
            @Valid @RequestBody CrearSucursalRequest request
    ) {
        return franquiciaService.agregarSucursal(franquiciaId, request.nombre())
                .map(this::toResponse);
    }

    @PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranquiciaResponse> agregarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @Valid @RequestBody CrearProductoRequest request
    ) {
        return franquiciaService.agregarProducto(franquiciaId, sucursalId, request.nombre(), request.stock())
                .map(this::toResponse);
    }

    @DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    public Mono<FranquiciaResponse> eliminarProducto(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId
    ) {
        return franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId)
                .map(this::toResponse);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock")
    public Mono<FranquiciaResponse> actualizarStock(
            @PathVariable String franquiciaId,
            @PathVariable String sucursalId,
            @PathVariable String productoId,
            @Valid @RequestBody ActualizarStockRequest request
    ) {
        return franquiciaService.actualizarStockProducto(franquiciaId, sucursalId, productoId, request.stock())
                .map(this::toResponse);
    }

    private FranquiciaResponse toResponse(Franquicia franquicia) {
        List<SucursalResponse> sucursales = franquicia.getSucursales().stream()
                .map(sucursal -> new SucursalResponse(
                        sucursal.getId(),
                        sucursal.getNombre(),
                        sucursal.getProductos().stream()
                                .map(producto -> new ProductoResponse(
                                        producto.getId(),
                                        producto.getNombre(),
                                        producto.getStock()
                                ))
                                .toList()
                ))
                .toList();
        return new FranquiciaResponse(franquicia.getId(), franquicia.getNombre(), sucursales);
    }
}
