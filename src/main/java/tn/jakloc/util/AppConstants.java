package tn.jakloc.util;

/**
 * Central place for attribute names and route paths shared by servlets,
 * filters and JSP pages. Using constants avoids typos such as
 * "authUser" versus "authenticatedUser".
 */
public final class AppConstants {

    /** ServletContext attribute that holds the single AuthService instance. */
    public static final String AUTH_SERVICE = "authService";

    /** ServletContext attribute that holds the shared UserRepository. */
    public static final String USER_REPOSITORY = "userRepository";

    /** Session attribute that holds the authenticated {@code User}. */
    public static final String SESSION_USER = "authenticatedUser";

    /** Session attribute that holds the authenticated user's {@code Role}. */
    public static final String SESSION_ROLE = "userRole";

    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register";
    public static final String LOGOUT_PATH = "/logout";
    public static final String CLIENT_DASHBOARD_PATH = "/client/dashboard";
    public static final String ADMIN_DASHBOARD_PATH = "/admin/dashboard";

    /** Demo account created at application startup. Documented in README.md. */
    public static final String DEMO_ADMIN_EMAIL = "admin@jakloc.tn";
    public static final String DEMO_ADMIN_PASSWORD = "Admin@123";

    private AppConstants() {
        // Utility class: do not instantiate.
    }
}
