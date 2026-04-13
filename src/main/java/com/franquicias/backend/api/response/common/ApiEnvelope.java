package com.franquicias.backend.api.response.common;

public record ApiEnvelope<T>(
        boolean success,
        String message,
        T data,
        PaginationResponse pagination
) {
    public static <T> ApiEnvelope<T> success(String message, T data) {
        return new ApiEnvelope<>(true, message, data, null);
    }

    public static <T> ApiEnvelope<T> success(String message, T data, PaginationResponse pagination) {
        return new ApiEnvelope<>(true, message, data, pagination);
    }

    public static <T> ApiEnvelope<T> error(String message, T data) {
        return new ApiEnvelope<>(false, message, data, null);
    }
}
