package com.franquicias.backend.api.response.common;

public record PaginationResponse(
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
}
