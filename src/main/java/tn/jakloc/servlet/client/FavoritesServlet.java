package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;

import java.io.IOException;

/**
 * Client favorites route.
 *
 * <p>Favorite persistence is added in a later milestone. For now this route
 * proves the protected client shell and navigation without showing sample
 * vehicles.</p>
 */
@WebServlet(name = "FavoritesServlet", urlPatterns = AppConstants.CLIENT_FAVORITES_PATH)
public class FavoritesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/client/favorites/list.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
