package tn.jakloc.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import tn.jakloc.dao.InMemoryUserRepository;
import tn.jakloc.dao.interfaces.UserRepository;
import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.model.Role;
import tn.jakloc.model.User;
import tn.jakloc.service.AuthService;

import java.time.LocalDateTime;

/**
 * Application startup wiring.
 *
 * <p>One repository and one AuthService are created for the whole application
 * and stored in the ServletContext. Servlets read them from there instead of
 * creating their own instances, so the in-memory data survives between
 * requests.</p>
 *
 * <p>When the JDBC repository is added later, only this class needs to change:
 * replace {@code new InMemoryUserRepository()} with the JDBC implementation.</p>
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        UserRepository userRepository = new InMemoryUserRepository();
        AuthService authService = new AuthService(userRepository);

        seedDemoAdmin(userRepository);

        event.getServletContext().setAttribute(AppConstants.USER_REPOSITORY, userRepository);
        event.getServletContext().setAttribute(AppConstants.AUTH_SERVICE, authService);
        event.getServletContext().log("JakLoc started: in-memory user repository ready.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to release for the in-memory implementation.
    }

    /**
     * Creates one ADMIN account so the admin route can be demonstrated before
     * an administration module exists. Credentials are documented in README.
     */
    private void seedDemoAdmin(UserRepository userRepository) {
        if (userRepository.existsByEmail(AppConstants.DEMO_ADMIN_EMAIL)) {
            return;
        }

        User admin = new User();
        admin.setFullName("JakLoc Administrator");
        admin.setEmail(AppConstants.DEMO_ADMIN_EMAIL);
        admin.setPhone("+216 000 000 000");
        admin.setPasswordHash(PasswordUtil.hash(AppConstants.DEMO_ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());

        try {
            userRepository.save(admin);
        } catch (DuplicateEmailException ex) {
            // Cannot happen at startup, but never let it break deployment.
            throw new IllegalStateException("Could not seed the demo admin account.", ex);
        }
    }
}
