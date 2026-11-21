package tn.jakloc.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import tn.jakloc.model.Role;
import tn.jakloc.model.User;

/**
 * Small helper around HttpSession. Centralizing session writes here guarantees
 * that every login uses the same attribute names and the same session
 * fixation protection.
 */
public final class SessionUtil {

    private SessionUtil() {
        // Utility class: do not instantiate.
    }

    /**
     * Authenticates the user in the session.
     *
     * <p>The previous session is invalidated first, then a brand-new session
     * is created. This prevents session fixation: an attacker cannot reuse a
     * session id that existed before login.</p>
     */
    public static void login(HttpServletRequest request, User user) {
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(AppConstants.SESSION_USER, user);
        session.setAttribute(AppConstants.SESSION_ROLE, user.getRole());
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_USER);
        return value instanceof User ? (User) value : null;
    }

    public static Role getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_ROLE);
        return value instanceof Role ? (Role) value : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUser(request) != null;
    }

    /**
     * Returns the context-relative dashboard path for a role.
     */
    public static String dashboardPath(User user) {
        return user.getRole() == Role.ADMIN
                ? AppConstants.ADMIN_DASHBOARD_PATH
                : AppConstants.CLIENT_DASHBOARD_PATH;
    }
}
