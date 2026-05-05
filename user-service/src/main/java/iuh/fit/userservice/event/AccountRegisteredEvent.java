package iuh.fit.userservice.event;

public record AccountRegisteredEvent(
        String accountId,
        String email,
        String fullName,
        String phoneNumber
) {
}
