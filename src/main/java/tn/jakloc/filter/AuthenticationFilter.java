package tn.jakloc.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.model.Role;
import tn.jakloc.model.User;
import tn.jakloc.util.AppConstants;
import tn.jakloc.util.SessionUtil;

import java.io.IOException;
import java.util.Set;

/**
 * Central route guard for the whole application.
 *
 * <p>Rules:</p>
 * <ul>
 *   <li>Only public paths are reachable without a session.</li>
 *   <li>Authenticated users are redirected away from login/register.</li>
 *   <li>{@code /admin/*} requires ADMIN and {@code /client/*} requires CLIENT.</li>
 *   <li>Protected responses are marked no-store so the browser Back button
 *       does not show private pages after logout.</li>
 * </ul>
 *
 * <p>Future modules are protected automatically: any new path that is not in
 * the public list requires authentication.</p>
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/",
            "/index.jsp",
            "/login",
            "/register",
            "/favicon.ico",
            "/favicon.svg"
    );

    private static final Set<String> AUTH_PAGES = Set.of(
            "/",
            "/index.jsp",
            "/login",
            "/register"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        if (!(servletRequest instanceof HttpServletRequest)
                || !(servletResponse instanceof HttpServletResponse)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI().substring(request.getContextPath().length());
        int semicolon = path.indexOf(';');
        if (semicolon >= 0) {
            path = path.substring(0, semicolon);
        }
        if (path.isEmpty()) {
            path = "/";
        }

        User user = SessionUtil.getUser(request);
        boolean publicPath = isPublicPath(path);

        if (!publicPath && user == null) {
            response.sendRedirect(request.getContextPath() + AppConstants.LOGIN_PATH + "?authRequired=1");
            return;
        }

        if (user != null && AUTH_PAGES.contains(path)) {
            response.sendRedirect(request.getContextPath() + SessionUtil.dashboardPath(user));
            return;
        }

        if (user != null && path.startsWith("/admin/") && user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (user != null && path.startsWith("/client/") && user.getRole() != Role.CLIENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (user != null) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path) || path.startsWith("/assets/");
    }
}
