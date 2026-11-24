package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.service.VehicleService;
import tn.jakloc.util.AppConstants;

import java.io.IOException;

@WebServlet(name = "ClientDashboardServlet", urlPatterns = AppConstants.CLIENT_DASHBOARD_PATH)
public class ClientDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/client/dashboard.jsp";

    private VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        Object service = getServletContext().getAttribute(AppConstants.VEHICLE_SERVICE);
        if (!(service instanceof VehicleService)) {
            throw new ServletException("Vehicle service is not available.");
        }
        vehicleService = (VehicleService) service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("availableVehicleCount", vehicleService.getAvailableCount());
        request.setAttribute("fleetCategoryCount", vehicleService.getAvailableCategoryCount());
        request.setAttribute("lowestAvailableRate", vehicleService.getLowestAvailableRate());
        request.setAttribute("featuredVehicles", vehicleService.getFeaturedVehicles(3));

        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
