package iuh.fit.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.shared.api.ApiError;
import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.error.ErrorCode;
import iuh.fit.shared.trace.TraceIdConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final InternalServiceAuthFilter internalServiceAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/api/v1/user/internal/**").hasRole("INTERNAL_SERVICE")
                        .anyRequest().denyAll()
                )
                .addFilterBefore(internalServiceAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, request, ErrorCode.UNAUTHORIZED, "Unauthorized")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, request, ErrorCode.FORBIDDEN, "Forbidden")
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            ErrorCode errorCode,
            String detailMessage
    ) throws IOException {
        ApiError error = new ApiError(
                errorCode.code(),
                detailMessage,
                Map.of(),
                List.of()
        );

        ApiResponse<Void> payload = ApiResponse.failure(
                errorCode.defaultMessage(),
                error,
                resolveTraceId(request)
        );

        response.setStatus(errorCode.httpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), payload);
    }

    private static String resolveTraceId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        Object traceIdAttr = request.getAttribute(TraceIdConstants.REQUEST_ATTRIBUTE);
        if (traceIdAttr instanceof String traceId && !traceId.isBlank()) {
            return traceId;
        }

        String headerTraceId = request.getHeader(TraceIdConstants.HEADER_NAME);
        if (headerTraceId == null || headerTraceId.isBlank()) {
            return null;
        }
        return headerTraceId;
    }
}
