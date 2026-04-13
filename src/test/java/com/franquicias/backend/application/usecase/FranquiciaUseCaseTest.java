package com.franquicias.backend.application.usecase;

import com.franquicias.backend.application.port.out.FranquiciaPersistencePort;
import com.franquicias.backend.domain.Franquicia;
import com.franquicias.backend.domain.Producto;
import com.franquicias.backend.domain.Sucursal;
import com.franquicias.backend.service.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranquiciaUseCaseTest {

    @Mock
    private FranquiciaPersistencePort franquiciaPersistencePort;

    private FranquiciaUseCase franquiciaUseCase;

    @BeforeEach
    void setUp() {
        franquiciaUseCase = new FranquiciaUseCase(franquiciaPersistencePort);
    }

    @Test
    void crearFranquiciaDebeGuardarYRetornarEntidad() {
        when(franquiciaPersistencePort.save(any(Franquicia.class))).thenAnswer(invocation -> {
            Franquicia franquicia = invocation.getArgument(0);
            franquicia.setId("f1");
            return Mono.just(franquicia);
        });

        StepVerifier.create(franquiciaUseCase.crearFranquicia("Franquicia Test"))
                .assertNext(franquicia -> {
                    assertEquals("f1", franquicia.getId());
                    assertEquals("Franquicia Test", franquicia.getNombre());
                    assertNotNull(franquicia.getSucursales());
                    assertEquals(0, franquicia.getSucursales().size());
                })
                .verifyComplete();
    }

    @Test
    void agregarSucursalDebeAgregarSucursalCuandoFranquiciaExiste() {
        Franquicia franquicia = franquiciaBase();
        when(franquiciaPersistencePort.findById("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaPersistencePort.save(any(Franquicia.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(franquiciaUseCase.agregarSucursal("f1", "Sucursal Norte"))
                .assertNext(resultado -> {
                    assertEquals(1, resultado.getSucursales().size());
                    assertEquals("Sucursal Norte", resultado.getSucursales().getFirst().getNombre());
                    assertNotNull(resultado.getSucursales().getFirst().getId());
                })
                .verifyComplete();
    }

    @Test
    void agregarSucursalDebeRetornarErrorCuandoFranquiciaNoExiste() {
        when(franquiciaPersistencePort.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(franquiciaUseCase.agregarSucursal("missing", "Sucursal Norte"))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException
                                && "Franquicia no encontrada".equals(error.getMessage()))
                .verify();
    }

    @Test
    void agregarProductoDebeAgregarProductoCuandoSucursalExiste() {
        Franquicia franquicia = franquiciaConSucursalYProductos(new ArrayList<>());
        when(franquiciaPersistencePort.findById("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaPersistencePort.save(any(Franquicia.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(franquiciaUseCase.agregarProducto("f1", "s1", "Coca Cola", 25))
                .assertNext(resultado -> {
                    Producto producto = resultado.getSucursales().getFirst().getProductos().getFirst();
                    assertEquals("Coca Cola", producto.getNombre());
                    assertEquals(25, producto.getStock());
                    assertNotNull(producto.getId());
                })
                .verifyComplete();
    }

    @Test
    void eliminarProductoDebeQuitarProductoCuandoExiste() {
        Producto producto = Producto.builder().id("p1").nombre("Coca Cola").stock(25).build();
        Franquicia franquicia = franquiciaConSucursalYProductos(new ArrayList<>(List.of(producto)));
        when(franquiciaPersistencePort.findById("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaPersistencePort.save(any(Franquicia.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(franquiciaUseCase.eliminarProducto("f1", "s1", "p1"))
                .assertNext(resultado -> assertEquals(0, resultado.getSucursales().getFirst().getProductos().size()))
                .verifyComplete();
    }

    @Test
    void eliminarProductoDebeRetornarErrorCuandoProductoNoExiste() {
        Franquicia franquicia = franquiciaConSucursalYProductos(new ArrayList<>());
        when(franquiciaPersistencePort.findById("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaUseCase.eliminarProducto("f1", "s1", "missing"))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException
                                && "Producto no encontrado en la sucursal".equals(error.getMessage()))
                .verify();
    }

    @Test
    void actualizarStockProductoDebeActualizarValor() {
        Producto producto = Producto.builder().id("p1").nombre("Coca Cola").stock(10).build();
        Franquicia franquicia = franquiciaConSucursalYProductos(new ArrayList<>(List.of(producto)));
        when(franquiciaPersistencePort.findById("f1")).thenReturn(Mono.just(franquicia));
        when(franquiciaPersistencePort.save(any(Franquicia.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(franquiciaUseCase.actualizarStockProducto("f1", "s1", "p1", 50))
                .assertNext(resultado -> assertEquals(50, resultado.getSucursales().getFirst().getProductos().getFirst().getStock()))
                .verifyComplete();
    }

    @Test
    void obtenerProductoMaximoStockPorSucursalDebeRetornarSoloSucursalesConProductos() {
        Sucursal sucursalConProductos = Sucursal.builder()
                .id("s1")
                .nombre("Sucursal Norte")
                .productos(new ArrayList<>(List.of(
                        Producto.builder().id("p1").nombre("A").stock(10).build(),
                        Producto.builder().id("p2").nombre("B").stock(20).build()
                )))
                .build();

        Sucursal sucursalSinProductos = Sucursal.builder()
                .id("s2")
                .nombre("Sucursal Sur")
                .productos(new ArrayList<>())
                .build();

        Franquicia franquicia = Franquicia.builder()
                .id("f1")
                .nombre("Franquicia Test")
                .sucursales(new ArrayList<>(List.of(sucursalConProductos, sucursalSinProductos)))
                .build();

        when(franquiciaPersistencePort.findById("f1")).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaUseCase.obtenerProductoMaximoStockPorSucursal("f1"))
                .assertNext(resultado -> {
                    assertEquals(1, resultado.size());
                    assertEquals("s1", resultado.getFirst().sucursalId());
                    assertEquals("p2", resultado.getFirst().productoId());
                    assertEquals(20, resultado.getFirst().stock());
                })
                .verifyComplete();
    }

    private Franquicia franquiciaBase() {
        return Franquicia.builder()
                .id("f1")
                .nombre("Franquicia Test")
                .sucursales(new ArrayList<>())
                .build();
    }

    private Franquicia franquiciaConSucursalYProductos(List<Producto> productos) {
        Sucursal sucursal = Sucursal.builder()
                .id("s1")
                .nombre("Sucursal Norte")
                .productos(productos)
                .build();
        return Franquicia.builder()
                .id("f1")
                .nombre("Franquicia Test")
                .sucursales(new ArrayList<>(List.of(sucursal)))
                .build();
    }
}
