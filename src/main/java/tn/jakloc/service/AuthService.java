package tn.jakloc.service;

import tn.jakloc.dao.interfaces.UserRepository;
import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.model.Role;
import tn.jakloc.model.User;
import tn.jakloc.util.PasswordUtil;
import tn.jakloc.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Authentication business logic.
 *
 * <p>Servlets never hash passwords or talk to the repository directly: they
 * call this service. That keeps the HTTP layer small and makes the rules easy
 * to test and reuse.</p>
 */
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new CLIENT account.
     *
     * <p>Field format validation is done by the servlet so it can display
     * field-level messages. This method enforces the business rules:
     * normalized unique email, hashed password, CLIENT role, creation date.</p>
     *
     * @throws DuplicateEmailException when the email is already registered
     */
    public User register(String fullName, String email, String phone, String rawPassword)
            throws DuplicateEmailException {

        String normalizedEmail = ValidationUtil.normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("An account with this email already exists.");
        }

        User user = new User();
        user.setFullName(ValidationUtil.trimToEmpty(fullName));
        user.setEmail(normalizedEmail);
        user.setPhone(ValidationUtil.trimToEmpty(phone));
        user.setPasswordHash(PasswordUtil.hash(rawPassword));
        user.setRole(Role.CLIENT);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Verifies credentials.
     *
     * @return the matching user, or an empty Optional when the email is
     *         unknown or the password is wrong. The caller shows one generic
     *         message for both cases so it does not reveal which emails exist.
     */
    public Optional<User> authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            return Optional.empty();
        }

        return userRepository.findByEmail(email)
                .filter(user -> PasswordUtil.matches(rawPassword, user.getPasswordHash()));
    }
}
