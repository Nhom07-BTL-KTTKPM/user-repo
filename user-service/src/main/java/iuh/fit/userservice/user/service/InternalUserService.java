package iuh.fit.userservice.user.service;

import iuh.fit.shared.error.BusinessException;
import iuh.fit.shared.error.ErrorCode;
import iuh.fit.userservice.user.dto.InternalUserAuthProfileResponse;
import iuh.fit.userservice.user.dto.InternalUserRegisterRequest;
import iuh.fit.userservice.user.entity.Account;
import iuh.fit.userservice.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalUserService {

    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public InternalUserAuthProfileResponse register(InternalUserRegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (accountRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Email already exists",
                    Map.of("email", email)
            );
        }

        Account account = new Account();
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRole(DEFAULT_ROLE);
        account.setActive(Boolean.TRUE);
        account.setFullName(request.fullName().trim());
        account.setPhoneNumber(request.phoneNumber().trim());

        try {
            Account saved = accountRepository.save(account);
            return toAuthProfile(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Email already exists",
                    Map.of("email", email)
            );
        }
    }

    @Transactional(readOnly = true)
    public InternalUserAuthProfileResponse getByEmail(String rawEmail) {
        String email = normalizeEmail(rawEmail);

        Account account = accountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "User not found",
                        Map.of("email", email)
                ));

        return toAuthProfile(account);
    }

    private static InternalUserAuthProfileResponse toAuthProfile(Account account) {
        return new InternalUserAuthProfileResponse(
                account.getId(),
                account.getEmail(),
                account.getRole(),
                account.getPasswordHash(),
                account.getActive()
        );
    }

    private static String normalizeEmail(String email) {
        if (email == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Email must not be null");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
