package tn.jakloc.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import tn.jakloc.dao.Database;
import tn.jakloc.dao.InMemoryUserRepository;
import tn.jakloc.dao.InMemoryVehicleRepository;
import tn.jakloc.dao.JdbcUserRepository;
import tn.jakloc.dao.JdbcVehicleRepository;
import tn.jakloc.dao.VehicleSeeder;
import tn.jakloc.dao.interfaces.UserRepository;
import tn.jakloc.dao.interfaces.VehicleRepository;
import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.model.Role;
import tn.jakloc.model.User;
import tn.jakloc.service.AuthService;
import tn.jakloc.service.VehicleService;

import java.time.LocalDateTime;

/**
 * Application startup wiring.
 *
 * <p>One repository and one service per module are created for the whole
 * application and stored in the ServletContext. Servlets read them from there
 * instead of creating their own instances, so the in-memory data survives
 * between requests.</p>
 *
 * <p>When JDBC repositories are added later, only this class needs to change:
 * replace the in-memory implementations with the JDBC ones.</p>
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Database.configure(event.getServletContext());

        UserRepository userRepository;
        VehicleRepository vehicleRepository;
        boolean persistent = true;

        try {
            userRepository = new JdbcUserRepository();
            vehicleRepository = new JdbcVehicleRepository();
            // Touch both tables so a missing database or schema triggers the fallback.
            userRepository.findAll();
            vehicleRepository.findAll();
        } catch (RuntimeException ex) {
            event.getServletContext().log(
                    "MySQL is not available, JakLoc will use in-memory storage for this run. "
                    + "Run database/jakloc.sql and check JAKLOC_DB_URL, JAKLOC_DB_USER and "
                    + "JAKLOC_DB_PASSWORD. Cause: " + ex.getMessage());
            userRepository = new InMemoryUserRepository();
            vehicleRepository = new InMemoryVehicleRepository();
            persistent = false;
        }

        AuthService authService = new AuthService(userRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository);

        seedDemoAccounts(userRepository);
        VehicleSeeder.seed(vehicleRepository);

        event.getServletContext().setAttribute(AppConstants.USER_REPOSITORY, userRepository);
        event.getServletContext().setAttribute(AppConstants.AUTH_SERVICE, authService);
        event.getServletContext().setAttribute(AppConstants.VEHICLE_REPOSITORY, vehicleRepository);
        event.getServletContext().setAttribute(AppConstants.VEHICLE_SERVICE, vehicleService);
        event.getServletContext().log(persistent
                ? "JakLoc started: MySQL persistence is active."
                : "JakLoc started: in-memory storage is active.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to release: connections are opened per operation.
    }

    /**
     * Creates the demo ADMIN and CLIENT accounts when they are missing so both
     * areas can be demonstrated. Credentials are documented in README.
     */
    private void seedDemoAccounts(UserRepository userRepository) {
        seedUser(userRepository, AppConstants.DEMO_ADMIN_EMAIL,
                AppConstants.DEMO_ADMIN_PASSWORD, "JakLoc Administrator",
                "+216 000 000 000", Role.ADMIN);
        seedUser(userRepository, AppConstants.DEMO_CLIENT_EMAIL,
                AppConstants.DEMO_CLIENT_PASSWORD, "Demo Client",
                "+216 200 000 000", Role.CLIENT);
    }

    private void seedUser(UserRepository userRepository, String email, String rawPassword,
            String fullName, String phone, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(PasswordUtil.hash(rawPassword));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        try {
            userRepository.save(user);
        } catch (DuplicateEmailException ex) {
            // Cannot happen at startup, but never let it break deployment.
            throw new IllegalStateException("Could not seed the demo account " + email + ".", ex);
        }
    }
}
