package tn.jakloc.dao;

import tn.jakloc.dao.interfaces.VehicleRepository;
import tn.jakloc.model.Vehicle;
import tn.jakloc.model.VehicleStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryVehicleRepository implements VehicleRepository {

    private final Map<Long, Vehicle> vehiclesById = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong();

    @Override
    public List<Vehicle> findAll() {
        return vehiclesById.values().stream()
                .sorted(Comparator.comparingLong(Vehicle::getId))
                .toList();
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        return Optional.ofNullable(vehiclesById.get(id));
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) {
        if (status == null) {
            return List.of();
        }
        return vehiclesById.values().stream()
                .filter(vehicle -> vehicle.getStatus() == status)
                .sorted(Comparator.comparingLong(Vehicle::getId))
                .toList();
    }

    @Override
    public synchronized Vehicle save(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null.");
        }
        if (vehicle.getId() == 0L) {
            vehicle.setId(nextId.incrementAndGet());
        }
        vehiclesById.put(vehicle.getId(), vehicle);
        return vehicle;
    }
}
