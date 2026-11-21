package tn.jakloc.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Server-side validation rules shared by the registration and login forms.
 * Client-side HTML validation is only a convenience; these checks are the
 * authoritative ones.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+0-9 ().-]{8,20}$");

    private ValidationUtil() {
        // Utility class: do not instantiate.
    }

    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean isValidFullName(String fullName) {
        String value = trimToEmpty(fullName);
        return value.length() >= 3 && value.length() <= 80;
    }

    public static boolean isValidEmail(String email) {
        String value = trimToEmpty(email);
        return value.length() <= 254 && EMAIL_PATTERN.matcher(value).matches();
    }

    public static boolean isValidPhone(String phone) {
        String value = trimToEmpty(phone);
        if (!PHONE_PATTERN.matcher(value).matches()) {
            return false;
        }
        int digits = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                digits++;
            }
        }
        return digits >= 8 && digits <= 15;
    }

    /**
     * Password policy: 8 to 64 characters, with at least one letter and one
     * digit. The upper limit keeps the password below BCrypt's 72-byte limit.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        int length = password.length();
        if (length < 8 || length > 64) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        return hasLetter && hasDigit;
    }
}
