package backend.api;

import java.util.UUID;

public record ApiResponse<T>(int code, String message, T data, String requestId) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data, newRequestId());
    }

    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, null, newRequestId());
    }

    private static String newRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
