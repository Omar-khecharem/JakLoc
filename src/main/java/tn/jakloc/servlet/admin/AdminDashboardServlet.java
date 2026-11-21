package tn.jakloc.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;

import java.io.IOException;

/**
 * Placeholder ADMIN landing page.
 *
 * <p>Only users with the ADMIN role can reach it. The administration features
 * themselves are intentionally not implemented in this milestone.</p>
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = AppConstants.ADMIN_DASHBOARD_PATH)
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                .forward(request, response);
    }
}
