package iuh.fit.userservice.user.dto;

import java.util.UUID;

public record InternalUserAuthProfileResponse(
        UUID accountId,
        String email,
        String role,
        String passwordHash,
        Boolean active
) {
}
