package com.franquicias.backend.application.usecase;

import com.franquicias.backend.application.dto.ProductoMaximoStock;
import com.franquicias.backend.application.port.out.FranquiciaPersistencePort;
import com.franquicias.backend.application.support.ErrorMessages;
import com.franquicias.backend.domain.Franquicia;
import com.franquicias.backend.domain.Producto;
import com.franquicias.backend.domain.Sucursal;
import com.franquicias.backend.service.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FranquiciaUseCase {

    private final FranquiciaPersistencePort franquiciaPersistencePort;

    public Mono<Franquicia> crearFranquicia(String nombre) {
        log.info("Creando franquicia con nombre={}", nombre);
        Franquicia franquicia = Franquicia.builder()
                .nombre(nombre)
                .sucursales(new ArrayList<>())
                .build();
        return franquiciaPersistencePort.save(franquicia);
    }

    public Mono<Franquicia> agregarSucursal(String franquiciaId, String nombreSucursal) {
        log.info("Agregando sucursal a franquiciaId={} nombre={}", franquiciaId, nombreSucursal);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = Sucursal.builder()
                            .id(UUID.randomUUID().toString())
                            .nombre(nombreSucursal)
                            .productos(new ArrayList<>())
                            .build();
                    franquicia.getSucursales().add(sucursal);
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, Integer stock) {
        log.info("Agregando producto a franquiciaId={} sucursalId={} nombre={} stock={}", franquiciaId, sucursalId, nombreProducto, stock);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    Producto producto = Producto.builder()
                            .id(UUID.randomUUID().toString())
                            .nombre(nombreProducto)
                            .stock(stock)
                            .build();
                    sucursal.getProductos().add(producto);
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
        log.info("Eliminando producto de franquiciaId={} sucursalId={} productoId={}", franquiciaId, sucursalId, productoId);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    boolean removed = sucursal.getProductos().removeIf(producto -> producto.getId().equals(productoId));
                    if (!removed) {
                        return Mono.error(new NotFoundException(ErrorMessages.PRODUCTO_NO_ENCONTRADO));
                    }
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    public Mono<Franquicia> actualizarStockProducto(String franquiciaId, String sucursalId, String productoId, Integer stock) {
        log.info("Actualizando stock de franquiciaId={} sucursalId={} productoId={} stock={}", franquiciaId, sucursalId, productoId, stock);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    Producto producto = obtenerProducto(sucursal, productoId);
                    producto.setStock(stock);
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    public Mono<List<ProductoMaximoStock>> obtenerProductoMaximoStockPorSucursal(String franquiciaId) {
        log.info("Consultando producto maximo stock por sucursal para franquiciaId={}", franquiciaId);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .map(franquicia -> franquicia.getSucursales().stream()
                        .map(sucursal -> buscarProductoConMayorStock(sucursal)
                                .map(producto -> new ProductoMaximoStock(
                                        sucursal.getId(),
                                        sucursal.getNombre(),
                                        producto.getId(),
                                        producto.getNombre(),
                                        producto.getStock()
                                )))
                        .flatMap(Optional::stream)
                        .toList());
    }

    public Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nombre) {
        log.info("Actualizando nombre de franquiciaId={} nuevoNombre={}", franquiciaId, nombre);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    franquicia.setNombre(nombre);
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    public Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nombre) {
        log.info("Actualizando nombre sucursal de franquiciaId={} sucursalId={} nuevoNombre={}", franquiciaId, sucursalId, nombre);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    sucursal.setNombre(nombre);
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    public Mono<Franquicia> actualizarNombreProducto(String franquiciaId, String sucursalId, String productoId, String nombre) {
        log.info("Actualizando nombre producto de franquiciaId={} sucursalId={} productoId={} nuevoNombre={}",
                franquiciaId, sucursalId, productoId, nombre);
        return franquiciaPersistencePort.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorMessages.FRANQUICIA_NO_ENCONTRADA)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = obtenerSucursal(franquicia, sucursalId);
                    Producto producto = obtenerProducto(sucursal, productoId);
                    producto.setNombre(nombre);
                    return franquiciaPersistencePort.save(franquicia);
                });
    }

    private Optional<Producto> buscarProductoConMayorStock(Sucursal sucursal) {
        if (sucursal.getProductos() == null || sucursal.getProductos().isEmpty()) {
            return Optional.empty();
        }
        return sucursal.getProductos().stream()
                .max((a, b) -> Integer.compare(a.getStock(), b.getStock()));
    }

    private Sucursal obtenerSucursal(Franquicia franquicia, String sucursalId) {
        List<Sucursal> sucursales = franquicia.getSucursales();
        if (sucursales == null) {
            throw new NotFoundException(ErrorMessages.SUCURSAL_NO_ENCONTRADA);
        }
        return sucursales.stream()
                .filter(sucursal -> sucursal.getId().equals(sucursalId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessages.SUCURSAL_NO_ENCONTRADA));
    }

    private Producto obtenerProducto(Sucursal sucursal, String productoId) {
        List<Producto> productos = sucursal.getProductos();
        if (productos == null) {
            throw new NotFoundException(ErrorMessages.PRODUCTO_NO_ENCONTRADO);
        }
        return productos.stream()
                .filter(producto -> producto.getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCTO_NO_ENCONTRADO));
    }
}
