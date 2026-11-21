package tn.jakloc.servlet.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.service.AuthService;
import tn.jakloc.util.AppConstants;
import tn.jakloc.util.ValidationUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles both the registration form (GET) and account creation (POST).
 *
 * <p>The servlet validates field formats and renders the JSP again when there
 * are errors. The actual business rules (unique email, hashing, CLIENT role)
 * live in {@link AuthService}.</p>
 */
@WebServlet(name = "RegisterServlet", urlPatterns = AppConstants.REGISTER_PATH)
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String REGISTER_VIEW = "/WEB-INF/views/auth/register.jsp";

    private AuthService authService;

    @Override
    public void init() {
        authService = (AuthService) getServletContext().getAttribute(AppConstants.AUTH_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = ValidationUtil.trimToEmpty(request.getParameter("fullName"));
        String email = ValidationUtil.trimToEmpty(request.getParameter("email"));
        String phone = ValidationUtil.trimToEmpty(request.getParameter("phone"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        Map<String, String> errors = new LinkedHashMap<>();

        if (fullName.isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (!ValidationUtil.isValidFullName(fullName)) {
            errors.put("fullName", "Full name must be between 3 and 80 characters.");
        }

        if (email.isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Enter a valid email address.");
        }

        if (phone.isEmpty()) {
            errors.put("phone", "Phone number is required.");
        } else if (!ValidationUtil.isValidPhone(phone)) {
            errors.put("phone", "Enter a valid phone number (8 to 15 digits).");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "Password is required.");
        } else if (!ValidationUtil.isValidPassword(password)) {
            errors.put("password", "Password must be 8 to 64 characters and include a letter and a number.");
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errors.put("confirmPassword", "Please confirm your password.");
        } else if (password != null && !password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }

        if (!errors.isEmpty()) {
            showFormAgain(request, response, errors, fullName, email, phone);
            return;
        }

        try {
            authService.register(fullName, email, phone, password);
            response.sendRedirect(request.getContextPath() + AppConstants.LOGIN_PATH + "?registered=1");
        } catch (DuplicateEmailException ex) {
            errors.put("email", ex.getMessage());
            showFormAgain(request, response, errors, fullName, email, phone);
        }
    }

    private void showFormAgain(HttpServletRequest request, HttpServletResponse response,
                               Map<String, String> errors, String fullName,
                               String email, String phone)
            throws ServletException, IOException {
        request.setAttribute("errors", errors);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.getRequestDispatcher(REGISTER_VIEW).forward(request, response);
    }
}
