package tn.jakloc.exception;

/**
 * Thrown when an account already exists for an email address.
 *
 * <p>The message is safe to display to the user; it never contains SQL or
 * technical details.</p>
 */
public class DuplicateEmailException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String message) {
        super(message);
    }
}
