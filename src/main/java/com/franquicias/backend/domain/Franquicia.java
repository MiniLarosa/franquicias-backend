package com.franquicias.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "franquicias")
public class Franquicia {

    @Id
    private String id;

    private String nombre;

    @Builder.Default
    private List<Sucursal> sucursales = new ArrayList<>();
}
