package tn.jakloc.dao;

import tn.jakloc.dao.interfaces.UserRepository;
import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.model.User;
import tn.jakloc.util.ValidationUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * First-version repository that keeps users in memory.
 *
 * <p>Data is lost when the server restarts, which is acceptable for the first
 * milestone. The class is thread-safe enough for a classroom demo: saves are
 * synchronized and reads use a concurrent map.</p>
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong();

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        if (normalizedEmail == null || normalizedEmail.isEmpty()) {
            return Optional.empty();
        }
        return usersById.values().stream()
                .filter(user -> normalizedEmail.equals(ValidationUtil.normalizeEmail(user.getEmail())))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return usersById.values().stream()
                .sorted(Comparator.comparingLong(User::getId))
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public synchronized User save(User user) throws DuplicateEmailException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(user.getEmail());
        boolean emailAlreadyUsed = usersById.values().stream()
                .anyMatch(existing -> existing.getId() != user.getId()
                        && normalizedEmail.equals(ValidationUtil.normalizeEmail(existing.getEmail())));

        if (emailAlreadyUsed) {
            throw new DuplicateEmailException("An account with this email already exists.");
        }

        if (user.getId() == 0L) {
            user.setId(nextId.incrementAndGet());
        }
        usersById.put(user.getId(), user);
        return user;
    }
}
