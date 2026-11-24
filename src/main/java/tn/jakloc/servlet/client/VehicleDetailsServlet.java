package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.model.Vehicle;
import tn.jakloc.service.VehicleService;
import tn.jakloc.util.AppConstants;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "VehicleDetailsServlet",
        urlPatterns = AppConstants.CLIENT_VEHICLE_DETAILS_PATTERN)
public class VehicleDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/client/vehicles/details.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/client/vehicles/details-error.jsp";

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

        long vehicleId = parseVehicleId(request.getPathInfo());

        if (vehicleId <= 0L) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid vehicle link",
                    "The vehicle reference in this link is not valid. Choose a car from the catalogue and try again.");
            return;
        }

        Optional<Vehicle> vehicle = vehicleService.findById(vehicleId);
        if (vehicle.isEmpty()) {
            showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                    "Vehicle not found",
                    "This vehicle does not exist or is no longer part of the JakLoc fleet.");
            return;
        }

        request.setAttribute("vehicle", vehicle.get());
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private long parseVehicleId(String pathInfo) {
        if (pathInfo == null) {
            return -1L;
        }

        String value = pathInfo;
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }

        if (value.isEmpty() || value.indexOf('/') >= 0) {
            return -1L;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return -1L;
            }
        }

        try {
            long id = Long.parseLong(value);
            return id > 0L ? id : -1L;
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response,
            int status, String title, String message)
            throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("errorCode", Integer.toString(status));
        request.setAttribute("errorTitle", title);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
    }
}
