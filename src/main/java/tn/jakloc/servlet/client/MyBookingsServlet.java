package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.util.AppConstants;

import java.io.IOException;

/**
 * Client bookings route.
 *
 * <p>Reservation data is not implemented yet. The page currently renders the
 * shared client layout and an honest empty state so the navigation can be
 * tested end to end.</p>
 */
@WebServlet(name = "MyBookingsServlet", urlPatterns = AppConstants.CLIENT_BOOKINGS_PATH)
public class MyBookingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/client/bookings/list.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
