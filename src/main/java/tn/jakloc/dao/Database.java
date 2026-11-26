package tn.jakloc.dao;

import jakarta.servlet.ServletContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single shared MySQL configuration used by every JDBC repository.
 *
 * <p>Values are resolved in this order: {@code web.xml} context parameters,
 * environment variables, system properties, then defaults. This lets the
 * project run on a local XAMPP/MySQL install without code changes while still
 * allowing an override at deploy time.</p>
 */
public final class Database {

    public static final String URL_PARAM = "jakloc.db.url";
    public static final String USER_PARAM = "jakloc.db.user";
    public static final String PASSWORD_PARAM = "jakloc.db.password";

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/jakloc"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
            + "&useUnicode=true&characterEncoding=UTF-8";

    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final boolean DRIVER_LOADED = loadDriver();

    private static String url = DEFAULT_URL;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;

    private Database() {
    }

    /**
     * Reads the connection settings from {@code web.xml}. Call this once during
     * application startup before any repository is used.
     */
    public static void configure(ServletContext context) {
        url = resolve(context, URL_PARAM, "JAKLOC_DB_URL", DEFAULT_URL);
        user = resolve(context, USER_PARAM, "JAKLOC_DB_USER", DEFAULT_USER);
        password = resolve(context, PASSWORD_PARAM, "JAKLOC_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        if (!DRIVER_LOADED) {
            throw new SQLException(
                    "MySQL JDBC driver not found. Add mysql-connector-j to WEB-INF/lib.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    private static boolean loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static String resolve(ServletContext context, String param, String envKey,
            String fallback) {
        String value = context == null ? null : context.getInitParameter(param);
        if (value == null || value.isBlank()) {
            value = System.getenv(envKey);
        }
        if (value == null || value.isBlank()) {
            value = System.getProperty(param);
        }
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
