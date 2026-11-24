package tn.jakloc.servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.jakloc.model.FuelType;
import tn.jakloc.model.Transmission;
import tn.jakloc.model.Vehicle;
import tn.jakloc.model.VehicleCategory;
import tn.jakloc.model.VehicleFilter;
import tn.jakloc.model.VehicleSort;
import tn.jakloc.service.VehicleService;
import tn.jakloc.util.AppConstants;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "VehicleCatalogServlet", urlPatterns = AppConstants.CLIENT_VEHICLES_PATH)
public class VehicleCatalogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/client/vehicles/list.jsp";

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

        List<String> errors = new ArrayList<>();
        VehicleFilter filter = readFilter(request, errors);
        List<Vehicle> vehicles = vehicleService.search(filter);

        request.setAttribute("vehicles", vehicles);
        request.setAttribute("filter", filter);
        request.setAttribute("resultCount", vehicles.size());
        request.setAttribute("availableCount", vehicleService.getAvailableCount());
        request.setAttribute("lowestAvailableRate", vehicleService.getLowestAvailableRate());
        request.setAttribute("categories", VehicleCategory.values());
        request.setAttribute("transmissions", Transmission.values());
        request.setAttribute("fuelTypes", FuelType.values());
        request.setAttribute("sortOptions", VehicleSort.values());
        request.setAttribute("errors", errors);

        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private VehicleFilter readFilter(HttpServletRequest request, List<String> errors) {
        VehicleFilter filter = new VehicleFilter();

        String query = trimToEmpty(request.getParameter("query"));
        if (!query.isEmpty()) {
            filter.setQuery(query);
        }

        filter.setCategory(parseEnum(request.getParameter("category"),
                VehicleCategory.class, "category", errors));
        filter.setTransmission(parseEnum(request.getParameter("transmission"),
                Transmission.class, "transmission", errors));
        filter.setFuelType(parseEnum(request.getParameter("fuelType"),
                FuelType.class, "fuel type", errors));
        filter.setMaxPrice(parseMaxPrice(request.getParameter("maxPrice"), errors));
        filter.setSort(VehicleSort.fromParam(request.getParameter("sort")));

        return filter;
    }

    private BigDecimal parseMaxPrice(String rawValue, List<String> errors) {
        String value = trimToEmpty(rawValue);
        if (value.isEmpty()) {
            return null;
        }

        try {
            BigDecimal maxPrice = new BigDecimal(value);
            if (maxPrice.signum() < 0) {
                errors.add("The maximum daily rate cannot be negative.");
                return null;
            }
            return maxPrice;
        } catch (NumberFormatException ex) {
            errors.add("The maximum daily rate must be a number.");
            return null;
        }
    }

    private <E extends Enum<E>> E parseEnum(String rawValue, Class<E> type,
            String label, List<String> errors) {
        String value = trimToEmpty(rawValue);
        if (value.isEmpty()) {
            return null;
        }

        try {
            return Enum.valueOf(type, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            errors.add("The selected " + label + " filter is not recognised.");
            return null;
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
