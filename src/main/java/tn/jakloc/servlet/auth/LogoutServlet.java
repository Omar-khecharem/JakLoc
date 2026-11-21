package tn.jakloc.servlet.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;
import tn.jakloc.util.SessionUtil;

import java.io.IOException;

/**
 * Ends the authenticated session.
 *
 * <p>Only POST is supported, so a link or prefetch cannot log the user out by
 * accident. The filter guarantees that only authenticated users reach it.</p>
 */
@WebServlet(name = "LogoutServlet", urlPatterns = AppConstants.LOGOUT_PATH)
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUtil.logout(request);
        response.sendRedirect(request.getContextPath() + AppConstants.LOGIN_PATH + "?logout=1");
    }
}
