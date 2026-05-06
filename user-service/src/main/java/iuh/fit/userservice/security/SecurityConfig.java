package iuh.fit.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.shared.api.ApiError;
import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.error.ErrorCode;
import iuh.fit.shared.trace.TraceIdConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(AuthJwtProperties.class)
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final AuthJwtProperties jwtProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, request, ErrorCode.UNAUTHORIZED, "Unauthorized")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, request, ErrorCode.FORBIDDEN, "Forbidden")
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer());
        decoder.setJwtValidator(validator);
        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::mapAuthorities);
        return converter;
    }

    private SecretKey jwtSecretKey() {
        byte[] raw = jwtProperties.getSecret() == null
                ? new byte[0]
                : jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);

        if (raw.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256");
        }

        return new SecretKeySpec(raw, "HmacSHA256");
    }

    private static Collection<GrantedAuthority> mapAuthorities(Jwt jwt) {
        Object claim = jwt.getClaims().get("role");
        if (claim instanceof String role) {
            String normalized = normalizeRole(role);
            if (normalized == null) {
                return List.of();
            }
            return List.of((GrantedAuthority) new SimpleGrantedAuthority(normalized));
        }
        if (claim instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(SecurityConfig::normalizeRole)
                    .filter(Objects::nonNull)
                    .map(roleName -> (GrantedAuthority) new SimpleGrantedAuthority(roleName))
                    .toList();
        }
        return List.of();
    }

    private static String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        String trimmed = role.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String upper = trimmed.toUpperCase(Locale.ROOT);
        return upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
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
