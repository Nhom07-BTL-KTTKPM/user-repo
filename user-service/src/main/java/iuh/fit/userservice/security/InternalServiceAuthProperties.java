package iuh.fit.userservice.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "security.internal-service")
public class InternalServiceAuthProperties {

    private String headerName = "X-Internal-Api-Key";
    private String apiKey = "change-me-in-prod";
}
