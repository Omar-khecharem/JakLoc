package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;

import java.io.IOException;

/**
 * Placeholder CLIENT landing page.
 *
 * <p>The route is already protected by {@code AuthenticationFilter}. Moomen's
 * vehicle catalogue will be added around this page later; the authentication
 * module must not implement it now.</p>
 */
@WebServlet(name = "ClientDashboardServlet", urlPatterns = AppConstants.CLIENT_DASHBOARD_PATH)
public class ClientDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/client/dashboard.jsp")
                .forward(request, response);
    }
}
