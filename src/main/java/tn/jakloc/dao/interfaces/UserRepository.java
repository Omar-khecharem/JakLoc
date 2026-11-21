package tn.jakloc.dao.interfaces;

import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for users.
 *
 * <p>Authentication code depends only on this interface. Today the
 * implementation is in memory; later a JDBC implementation can replace it
 * without changing servlets or JSP pages.</p>
 */
public interface UserRepository {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean existsByEmail(String email);

    /**
     * Inserts or updates a user and assigns an id when the user is new.
     *
     * @throws DuplicateEmailException when another account uses the same email
     */
    User save(User user) throws DuplicateEmailException;
}
