package com.franquicias.backend.api.mapper;

import com.franquicias.backend.api.response.FranquiciaResponse;
import com.franquicias.backend.api.response.ProductoResponse;
import com.franquicias.backend.api.response.SucursalResponse;
import com.franquicias.backend.application.dto.ProductoMaximoStock;
import com.franquicias.backend.api.response.ProductoMaximoStockResponse;
import com.franquicias.backend.domain.Franquicia;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FranquiciaResponseMapper {

    public FranquiciaResponse toResponse(Franquicia franquicia) {
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

    public ProductoMaximoStockResponse toProductoMaximoStockResponse(ProductoMaximoStock producto) {
        return new ProductoMaximoStockResponse(
                producto.sucursalId(),
                producto.sucursalNombre(),
                producto.productoId(),
                producto.productoNombre(),
                producto.stock()
        );
    }
}
