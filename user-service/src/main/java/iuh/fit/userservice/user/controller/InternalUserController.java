package iuh.fit.userservice.user.controller;

import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.trace.TraceIdConstants;
import iuh.fit.userservice.user.dto.InternalUserAuthProfileResponse;
import iuh.fit.userservice.user.dto.InternalUserRegisterRequest;
import iuh.fit.userservice.user.service.InternalUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/internal")
public class InternalUserController {

    private final InternalUserService internalUserService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InternalUserAuthProfileResponse>> register(
            @Valid @RequestBody InternalUserRegisterRequest request,
            HttpServletRequest servletRequest
    ) {
        InternalUserAuthProfileResponse profile = internalUserService.register(request);
        ApiResponse<InternalUserAuthProfileResponse> payload = ApiResponse.success(
            profile,
            "User registered successfully",
                resolveTraceId(servletRequest)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(payload);
    }

    @GetMapping(value = "/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InternalUserAuthProfileResponse>> getByEmail(
            @RequestParam("email") @NotBlank @Email @Size(max = 255) String email,
            HttpServletRequest servletRequest
    ) {
        InternalUserAuthProfileResponse profile = internalUserService.getByEmail(email);
        ApiResponse<InternalUserAuthProfileResponse> payload = ApiResponse.success(
            profile,
            "User fetched successfully",
                resolveTraceId(servletRequest)
        );

        return ResponseEntity.ok(payload);
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
