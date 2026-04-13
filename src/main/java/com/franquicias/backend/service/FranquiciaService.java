package com.franquicias.backend.service;

import com.franquicias.backend.domain.Franquicia;
import com.franquicias.backend.domain.Producto;
import com.franquicias.backend.domain.Sucursal;
import com.franquicias.backend.repository.FranquiciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;

    public Mono<Franquicia> crearFranquicia(String nombre) {
        Franquicia franquicia = Franquicia.builder()
                .nombre(nombre)
                .sucursales(new ArrayList<>())
                .build();
        return franquiciaRepository.save(franquicia);
    }

    public Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(franquicia -> {
                    Sucursal sucursal = Sucursal.builder()
                            .id(UUID.randomUUID().toString())
                            .nombre(nombreSucursal)
                            .productos(new ArrayList<>())
                            .build();
                    franquicia.getSucursales().add(sucursal);
                    return franquiciaRepository.save(franquicia);
                });
    }

    public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, Integer stock) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    Producto producto = Producto.builder()
                            .id(UUID.randomUUID().toString())
                            .nombre(nombreProducto)
                            .stock(stock)
                            .build();
                    sucursal.getProductos().add(producto);
                    return franquiciaRepository.save(franquicia);
                });
    }

    public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    boolean removed = sucursal.getProductos().removeIf(producto -> producto.getId().equals(productoId));
                    if (!removed) {
                        return Mono.error(new NotFoundException("Producto no encontrado en la sucursal"));
                    }
                    return franquiciaRepository.save(franquicia);
                });
    }

    public Mono<Franquicia> actualizarStockProducto(String franquiciaId, String sucursalId, String productoId, Integer stock) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    Producto producto = obtenerProducto(sucursal, productoId);
                    producto.setStock(stock);
                    return franquiciaRepository.save(franquicia);
                });
    }

    private Sucursal obtenerSucursal(Franquicia franquicia, String sucursalId) {
        List<Sucursal> sucursales = franquicia.getSucursales();
        if (sucursales == null) {
            throw new NotFoundException("Sucursal no encontrada en la franquicia");
        }
        return sucursales.stream()
                .filter(sucursal -> sucursal.getId().equals(sucursalId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada en la franquicia"));
    }

    private Producto obtenerProducto(Sucursal sucursal, String productoId) {
        List<Producto> productos = sucursal.getProductos();
        if (productos == null) {
            throw new NotFoundException("Producto no encontrado en la sucursal");
        }
        return productos.stream()
                .filter(producto -> producto.getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Producto no encontrado en la sucursal"));
    }
}
