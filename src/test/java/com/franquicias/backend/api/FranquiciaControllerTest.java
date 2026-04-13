package com.franquicias.backend.api;

import com.franquicias.backend.application.dto.ProductoMaximoStock;
import com.franquicias.backend.application.usecase.FranquiciaUseCase;
import com.franquicias.backend.api.mapper.FranquiciaResponseMapper;
import com.franquicias.backend.api.request.ActualizarNombreRequest;
import com.franquicias.backend.api.request.ActualizarStockRequest;
import com.franquicias.backend.api.request.CrearFranquiciaRequest;
import com.franquicias.backend.api.request.CrearProductoRequest;
import com.franquicias.backend.api.request.CrearSucursalRequest;
import com.franquicias.backend.domain.Franquicia;
import com.franquicias.backend.domain.Producto;
import com.franquicias.backend.domain.Sucursal;
import com.franquicias.backend.service.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {FranquiciaController.class, HealthController.class})
@Import({GlobalExceptionHandler.class, FranquiciaResponseMapper.class})
class FranquiciaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranquiciaUseCase franquiciaUseCase;

    @Test
    void crearFranquiciaDebeResponder201() {
        Franquicia franquicia = franquiciaConDatos();
        when(franquiciaUseCase.crearFranquicia("Franquicia Test")).thenReturn(Mono.just(franquicia));

        webTestClient.post()
                .uri("/api/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CrearFranquiciaRequest("Franquicia Test"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.nombre").isEqualTo("Franquicia Test")
                .jsonPath("$.data.sucursales[0].nombre").isEqualTo("Sucursal Norte");
    }

    @Test
    void crearFranquiciaConNombreVacioDebeResponder400() {
        webTestClient.post()
                .uri("/api/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CrearFranquiciaRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void agregarSucursalDebeResponder201() {
        Franquicia franquicia = franquiciaConDatos();
        when(franquiciaUseCase.agregarSucursal("f1", "Sucursal Norte")).thenReturn(Mono.just(franquicia));

        webTestClient.post()
                .uri("/api/franquicias/f1/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CrearSucursalRequest("Sucursal Norte"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo("f1");
    }

    @Test
    void agregarProductoDebeResponder201() {
        Franquicia franquicia = franquiciaConDatos();
        when(franquiciaUseCase.agregarProducto("f1", "s1", "Coca Cola", 25)).thenReturn(Mono.just(franquicia));

        webTestClient.post()
                .uri("/api/franquicias/f1/sucursales/s1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CrearProductoRequest("Coca Cola", 25))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.sucursales[0].productos[0].stock").isEqualTo(25);
    }

    @Test
    void actualizarStockConValorNegativoDebeResponder400() {
        webTestClient.patch()
                .uri("/api/franquicias/f1/sucursales/s1/productos/p1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ActualizarStockRequest(-1))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void obtenerMaximoStockDebeResponder200() {
        List<ProductoMaximoStock> respuesta = List.of(
                new ProductoMaximoStock("s1", "Sucursal Norte", "p1", "Coca Cola", 25)
        );
        when(franquiciaUseCase.obtenerProductoMaximoStockPorSucursal("f1")).thenReturn(Mono.just(respuesta));

        webTestClient.get()
                .uri("/api/franquicias/f1/productos/max-stock-por-sucursal")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].sucursalId").isEqualTo("s1")
                .jsonPath("$.data[0].productoNombre").isEqualTo("Coca Cola");
    }

    @Test
    void errorNotFoundDebeResponder404() {
        when(franquiciaUseCase.actualizarNombreFranquicia(eq("f1"), anyString()))
                .thenReturn(Mono.error(new NotFoundException("Franquicia no encontrada")));

        webTestClient.patch()
                .uri("/api/franquicias/f1/nombre")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ActualizarNombreRequest("Nueva"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.data.error").isEqualTo("Franquicia no encontrada");
    }

    @Test
    void healthDebeResponder200() {
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.status").isEqualTo("UP");
    }

    private Franquicia franquiciaConDatos() {
        Producto producto = Producto.builder().id("p1").nombre("Coca Cola").stock(25).build();
        Sucursal sucursal = Sucursal.builder()
                .id("s1")
                .nombre("Sucursal Norte")
                .productos(new ArrayList<>(List.of(producto)))
                .build();
        return Franquicia.builder()
                .id("f1")
                .nombre("Franquicia Test")
                .sucursales(new ArrayList<>(List.of(sucursal)))
                .build();
    }
}
