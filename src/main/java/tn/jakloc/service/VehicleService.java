package tn.jakloc.service;

import tn.jakloc.dao.interfaces.VehicleRepository;
import tn.jakloc.model.Vehicle;
import tn.jakloc.model.VehicleFilter;
import tn.jakloc.model.VehicleSort;
import tn.jakloc.model.VehicleStatus;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> search(VehicleFilter filter) {
        VehicleFilter safeFilter = filter == null ? new VehicleFilter() : filter;
        Comparator<Vehicle> comparator = comparatorFor(safeFilter.getSort());

        return vehicleRepository.findAll().stream()
                .filter(vehicle -> matches(vehicle, safeFilter))
                .sorted(comparator)
                .toList();
    }

    public List<Vehicle> getFeaturedVehicles(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return search(new VehicleFilter()).stream()
                .filter(Vehicle::isAvailable)
                .limit(limit)
                .toList();
    }

    public Optional<Vehicle> findById(long id) {
        if (id <= 0L) {
            return Optional.empty();
        }
        return vehicleRepository.findById(id);
    }

    public long getAvailableCount() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).size();
    }

    public long getAvailableCategoryCount() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).stream()
                .map(Vehicle::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    public BigDecimal getLowestAvailableRate() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).stream()
                .map(Vehicle::getDailyRate)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    private boolean matches(Vehicle vehicle, VehicleFilter filter) {
        String query = filter.getQuery();
        if (query != null && !query.isBlank()) {
            String needle = query.trim().toLowerCase(Locale.ROOT);
            String haystack = String.join(" ",
                    safe(vehicle.getBrand()),
                    safe(vehicle.getModel()),
                    safe(vehicle.getLocation()),
                    vehicle.getCategory() == null ? "" : vehicle.getCategory().getLabel())
                    .toLowerCase(Locale.ROOT);
            if (!haystack.contains(needle)) {
                return false;
            }
        }

        if (filter.getCategory() != null && vehicle.getCategory() != filter.getCategory()) {
            return false;
        }
        if (filter.getTransmission() != null && vehicle.getTransmission() != filter.getTransmission()) {
            return false;
        }
        if (filter.getFuelType() != null && vehicle.getFuelType() != filter.getFuelType()) {
            return false;
        }
        if (filter.getMaxPrice() != null) {
            BigDecimal rate = vehicle.getDailyRate();
            if (rate == null || rate.compareTo(filter.getMaxPrice()) > 0) {
                return false;
            }
        }
        return true;
    }

    private Comparator<Vehicle> comparatorFor(VehicleSort sort) {
        Comparator<Vehicle> byPrice = Comparator.comparing(
                Vehicle::getDailyRate, Comparator.nullsLast(BigDecimal::compareTo));
        Comparator<Vehicle> byYearDesc = Comparator.comparingInt(Vehicle::getYear).reversed();
        Comparator<Vehicle> byNewest = Comparator.comparing(
                        Vehicle::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed();
        Comparator<Vehicle> byAvailableFirst = Comparator.comparingInt(
                vehicle -> vehicle.isAvailable() ? 0 : 1);

        return switch (sort) {
            case PRICE_ASC -> byPrice.thenComparingLong(Vehicle::getId);
            case PRICE_DESC -> byPrice.reversed().thenComparingLong(Vehicle::getId);
            case NAME_ASC -> Comparator.comparing(
                    vehicle -> vehicle.getDisplayName().toLowerCase(Locale.ROOT));
            case SEATS_DESC -> Comparator.comparingInt(Vehicle::getSeats)
                    .reversed()
                    .thenComparing(byPrice);
            case NEWEST -> byYearDesc.thenComparing(byNewest);
            default -> byAvailableFirst
                    .thenComparing(byYearDesc)
                    .thenComparing(byPrice);
        };
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
