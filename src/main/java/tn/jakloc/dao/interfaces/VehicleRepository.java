package tn.jakloc.dao.interfaces;

import tn.jakloc.model.Vehicle;
import tn.jakloc.model.VehicleStatus;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    List<Vehicle> findAll();

    Optional<Vehicle> findById(long id);

    List<Vehicle> findByStatus(VehicleStatus status);

    Vehicle save(Vehicle vehicle);
}
