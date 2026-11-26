package tn.jakloc.dao;

import tn.jakloc.dao.interfaces.UserRepository;
import tn.jakloc.exception.DuplicateEmailException;
import tn.jakloc.model.Role;
import tn.jakloc.model.User;
import tn.jakloc.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL-backed user repository. Emails are stored normalized (lowercase) so
 * lookups and the unique index behave consistently.
 */
public class JdbcUserRepository implements UserRepository {

    private static final String COLUMNS =
            "id, full_name, email, password_hash, phone, role, created_at";

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not find user by id.", ex);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        if (normalizedEmail == null || normalizedEmail.isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not find user by email.", ex);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
            return users;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not list users.", ex);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        if (normalizedEmail == null || normalizedEmail.isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not check user email.", ex);
        }
    }

    @Override
    public User save(User user) throws DuplicateEmailException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(user.getEmail());
        try {
            if (user.getId() == 0L) {
                return insert(user, normalizedEmail);
            }
            return update(user, normalizedEmail);
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new DuplicateEmailException("An account with this email already exists.");
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) {
                throw new DuplicateEmailException("An account with this email already exists.");
            }
            throw new IllegalStateException("Could not save user.", ex);
        }
    }

    private User insert(User user, String email) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, phone, role, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, user, email);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        }
    }

    private User update(User user, String email) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, password_hash = ?, "
                + "phone = ?, role = ?, created_at = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, user, email);
            statement.setLong(7, user.getId());
            statement.executeUpdate();
            return user;
        }
    }

    private void bind(PreparedStatement statement, User user, String email) throws SQLException {
        statement.setString(1, user.getFullName());
        statement.setString(2, email);
        statement.setString(3, user.getPasswordHash());
        statement.setString(4, user.getPhone() == null ? "" : user.getPhone());
        statement.setString(5, user.getRole() == null ? Role.CLIENT.name() : user.getRole().name());
        LocalDateTime createdAt = user.getCreatedAt() == null ? LocalDateTime.now() : user.getCreatedAt();
        statement.setTimestamp(6, Timestamp.valueOf(createdAt));
    }

    private User map(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setPhone(resultSet.getString("phone"));
        String role = resultSet.getString("role");
        if (role != null) {
            user.setRole(Role.valueOf(role));
        }
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    }
}
