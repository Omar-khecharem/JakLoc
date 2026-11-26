package tn.jakloc.dao;

import tn.jakloc.dao.interfaces.VehicleRepository;
import tn.jakloc.model.FuelType;
import tn.jakloc.model.Transmission;
import tn.jakloc.model.Vehicle;
import tn.jakloc.model.VehicleCategory;
import tn.jakloc.model.VehicleStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL-backed vehicle repository. Enum values are stored by name
 * ({@code SUV}, {@code AUTOMATIC}, ...) so the model stays independent from
 * the database schema.
 */
public class JdbcVehicleRepository implements VehicleRepository {

    private static final String COLUMNS =
            "id, brand, model, category, transmission, fuel_type, model_year, seats, "
            + "doors, daily_rate, mileage, location, image_url, description, status, created_at";

    @Override
    public List<Vehicle> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM vehicles ORDER BY id";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                vehicles.add(map(resultSet));
            }
            return vehicles;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not list vehicles.", ex);
        }
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM vehicles WHERE id = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not find vehicle by id.", ex);
        }
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) {
        if (status == null) {
            return List.of();
        }

        String sql = "SELECT " + COLUMNS + " FROM vehicles WHERE status = ? ORDER BY id";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vehicles.add(map(resultSet));
                }
            }
            return vehicles;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not list vehicles by status.", ex);
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null.");
        }

        try {
            if (vehicle.getId() == 0L) {
                return insert(vehicle);
            }
            return update(vehicle);
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not save vehicle.", ex);
        }
    }

    private Vehicle insert(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (brand, model, category, transmission, fuel_type, "
                + "model_year, seats, doors, daily_rate, mileage, location, image_url, "
                + "description, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, vehicle);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    vehicle.setId(keys.getLong(1));
                }
            }
            return vehicle;
        }
    }

    private Vehicle update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET brand = ?, model = ?, category = ?, transmission = ?, "
                + "fuel_type = ?, model_year = ?, seats = ?, doors = ?, daily_rate = ?, "
                + "mileage = ?, location = ?, image_url = ?, description = ?, status = ?, "
                + "created_at = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, vehicle);
            statement.setLong(16, vehicle.getId());
            statement.executeUpdate();
            return vehicle;
        }
    }

    private void bind(PreparedStatement statement, Vehicle vehicle) throws SQLException {
        statement.setString(1, vehicle.getBrand());
        statement.setString(2, vehicle.getModel());
        statement.setString(3, name(vehicle.getCategory(), VehicleCategory.SUV));
        statement.setString(4, name(vehicle.getTransmission(), Transmission.AUTOMATIC));
        statement.setString(5, name(vehicle.getFuelType(), FuelType.PETROL));
        statement.setInt(6, vehicle.getYear());
        statement.setInt(7, vehicle.getSeats());
        statement.setInt(8, vehicle.getDoors());
        statement.setBigDecimal(9,
                vehicle.getDailyRate() == null ? BigDecimal.ZERO : vehicle.getDailyRate());
        statement.setInt(10, vehicle.getMileage());
        statement.setString(11, vehicle.getLocation() == null ? "" : vehicle.getLocation());
        statement.setString(12, vehicle.getImageUrl());
        statement.setString(13, vehicle.getDescription());
        statement.setString(14, name(vehicle.getStatus(), VehicleStatus.AVAILABLE));
        LocalDateTime createdAt = vehicle.getCreatedAt() == null ? LocalDateTime.now() : vehicle.getCreatedAt();
        statement.setTimestamp(15, Timestamp.valueOf(createdAt));
    }

    private Vehicle map(ResultSet resultSet) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(resultSet.getLong("id"));
        vehicle.setBrand(resultSet.getString("brand"));
        vehicle.setModel(resultSet.getString("model"));
        vehicle.setCategory(enumValue(VehicleCategory.class, resultSet.getString("category")));
        vehicle.setTransmission(enumValue(Transmission.class, resultSet.getString("transmission")));
        vehicle.setFuelType(enumValue(FuelType.class, resultSet.getString("fuel_type")));
        vehicle.setYear(resultSet.getInt("model_year"));
        vehicle.setSeats(resultSet.getInt("seats"));
        vehicle.setDoors(resultSet.getInt("doors"));
        vehicle.setDailyRate(resultSet.getBigDecimal("daily_rate"));
        vehicle.setMileage(resultSet.getInt("mileage"));
        vehicle.setLocation(resultSet.getString("location"));
        vehicle.setImageUrl(resultSet.getString("image_url"));
        vehicle.setDescription(resultSet.getString("description"));
        vehicle.setStatus(enumValue(VehicleStatus.class, resultSet.getString("status")));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            vehicle.setCreatedAt(createdAt.toLocalDateTime());
        }
        return vehicle;
    }

    private <E extends Enum<E>> E enumValue(Class<E> type, String value) {
        return value == null ? null : Enum.valueOf(type, value);
    }

    private <E extends Enum<E>> String name(E value, E fallback) {
        return (value == null ? fallback : value).name();
    }
}
