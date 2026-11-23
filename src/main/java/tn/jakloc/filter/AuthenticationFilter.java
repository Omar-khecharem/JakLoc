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

// Deny-by-default route guard: public paths, session check, role gates, no-store.
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    // Auth-form pages; a logged-in user is bounced to their dashboard instead.
    private static final Set<String> AUTH_PAGES = Set.of(
            "/", "/index.jsp", AppConstants.LOGIN_PATH, AppConstants.REGISTER_PATH);

    // Public paths that are not auth forms.
    private static final Set<String> PUBLIC_EXTRA = Set.of("/favicon.ico", "/favicon.svg");

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

        // Context-relative path, with any ;jsessionid suffix stripped.
        String path = request.getRequestURI().substring(request.getContextPath().length());
        int semicolon = path.indexOf(';');
        if (semicolon >= 0) {
            path = path.substring(0, semicolon);
        }
        if (path.isEmpty()) {
            path = "/";
        }

        User user = SessionUtil.getUser(request);
        boolean authPage = AUTH_PAGES.contains(path);
        boolean publicPath = authPage || PUBLIC_EXTRA.contains(path) || path.startsWith("/assets/");

        // Anonymous hit on a private path -> login page with a notice.
        if (!publicPath && user == null) {
            response.sendRedirect(request.getContextPath() + AppConstants.LOGIN_PATH + "?authRequired=1");
            return;
        }

        // Logged-in user on an auth form -> their role dashboard.
        if (user != null && authPage) {
            response.sendRedirect(request.getContextPath() + SessionUtil.dashboardPath(user));
            return;
        }

        // Area role gates: /admin/* needs ADMIN, /client/* needs CLIENT.
        if (user != null && path.startsWith("/admin/") && user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (user != null && path.startsWith("/client/") && user.getRole() != Role.CLIENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Private responses are never cached, so Back after logout shows nothing.
        if (user != null) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
        }

        chain.doFilter(request, response);
    }
}