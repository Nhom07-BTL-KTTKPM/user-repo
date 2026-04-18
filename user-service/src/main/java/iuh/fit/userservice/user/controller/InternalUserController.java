package iuh.fit.userservice.user.controller;

import iuh.fit.shared.api.ApiError;
import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.trace.TraceIdConstants;
import iuh.fit.userservice.user.dto.InternalUserAuthProfileResponse;
import iuh.fit.userservice.user.dto.InternalUserRegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/internal")
public class InternalUserController {

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InternalUserAuthProfileResponse>> register(
            @Valid @RequestBody InternalUserRegisterRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiError error = new ApiError(
                "NOT_IMPLEMENTED",
                "MVP contract is locked. Register implementation will be added in next phase.",
                Map.of("endpoint", "POST /api/v1/user/internal/register"),
                null
        );

        ApiResponse<InternalUserAuthProfileResponse> payload = ApiResponse.failure(
                "Endpoint is not implemented yet",
                error,
                resolveTraceId(servletRequest)
        );

        return ResponseEntity.status(501).body(payload);
    }

    @GetMapping(value = "/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InternalUserAuthProfileResponse>> getByEmail(
            @RequestParam("email") String email,
            HttpServletRequest servletRequest
    ) {
        ApiError error = new ApiError(
                "NOT_IMPLEMENTED",
                "MVP contract is locked. Get-by-email implementation will be added in next phase.",
                Map.of("endpoint", "GET /api/v1/user/internal/by-email", "email", email),
                null
        );

        ApiResponse<InternalUserAuthProfileResponse> payload = ApiResponse.failure(
                "Endpoint is not implemented yet",
                error,
                resolveTraceId(servletRequest)
        );

        return ResponseEntity.status(501).body(payload);
    }

    private static String resolveTraceId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object traceAttr = request.getAttribute(TraceIdConstants.REQUEST_ATTRIBUTE);
        if (traceAttr instanceof String traceId && !traceId.isBlank()) {
            return traceId;
        }

        String headerTraceId = request.getHeader(TraceIdConstants.HEADER_NAME);
        if (headerTraceId == null || headerTraceId.isBlank()) {
            return null;
        }
        return headerTraceId;
    }
}
