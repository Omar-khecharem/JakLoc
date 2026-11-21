package tn.jakloc.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing helper backed by jBCrypt.
 *
 * <p>BCrypt is intentionally slow: it makes brute-force attacks expensive.
 * The work factor (12) is a reasonable default for a university project and
 * can be raised later without changing stored hashes.</p>
 *
 * <p>jBCrypt 0.4 is bundled in {@code WEB-INF/lib/jbcrypt-0.4.jar}.</p>
 */
public final class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    private PasswordUtil() {
        // Utility class: do not instantiate.
    }

    /**
     * Hashes a raw password with a fresh random salt.
     *
     * @param rawPassword the password entered by the user
     * @return a BCrypt hash safe to store in the database
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Checks a raw password against a stored BCrypt hash.
     *
     * @return {@code true} only when the password matches
     */
    public static boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, storedHash);
        } catch (IllegalArgumentException ex) {
            // A malformed hash must never crash the login page.
            return false;
        }
    }
}
