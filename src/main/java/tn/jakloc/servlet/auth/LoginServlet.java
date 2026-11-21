package tn.jakloc.servlet.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.model.User;
import tn.jakloc.service.AuthService;
import tn.jakloc.util.AppConstants;
import tn.jakloc.util.SessionUtil;
import tn.jakloc.util.ValidationUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles the login form (GET) and credential checking (POST).
 *
 * <p>Credentials are only accepted through POST. On success the session is
 * regenerated and the user is redirected to the dashboard for their role.</p>
 */
@WebServlet(name = "LoginServlet", urlPatterns = AppConstants.LOGIN_PATH)
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LOGIN_VIEW = "/WEB-INF/views/auth/login.jsp";

    private AuthService authService;

    @Override
    public void init() {
        authService = (AuthService) getServletContext().getAttribute(AppConstants.AUTH_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = ValidationUtil.trimToEmpty(request.getParameter("email"));
        String password = request.getParameter("password");

        Map<String, String> errors = new LinkedHashMap<>();

        if (email.isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Enter a valid email address.");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "Password is required.");
        }

        if (!errors.isEmpty()) {
            showFormAgain(request, response, errors, email);
            return;
        }

        Optional<User> authenticatedUser = authService.authenticate(email, password);

        if (authenticatedUser.isEmpty()) {
            errors.put("login", "Invalid email or password.");
            showFormAgain(request, response, errors, email);
            return;
        }

        User user = authenticatedUser.get();
        SessionUtil.login(request, user);
        response.sendRedirect(request.getContextPath() + SessionUtil.dashboardPath(user));
    }

    private void showFormAgain(HttpServletRequest request, HttpServletResponse response,
                               Map<String, String> errors, String email)
            throws ServletException, IOException {
        request.setAttribute("errors", errors);
        request.setAttribute("email", email);
        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }
}
