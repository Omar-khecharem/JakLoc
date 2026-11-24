package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;

import java.io.IOException;

/**
 * Client profile route.
 *
 * <p>The page is read-only in this foundation step and displays the user
 * already stored in the authenticated session. Profile editing and password
 * changes will reuse the existing user repository and password utility.</p>
 */
@WebServlet(name = "ClientProfileServlet", urlPatterns = AppConstants.CLIENT_PROFILE_PATH)
public class ClientProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/client/profile.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
