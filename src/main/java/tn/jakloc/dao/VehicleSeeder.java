package tn.jakloc.dao;

import tn.jakloc.dao.interfaces.VehicleRepository;
import tn.jakloc.model.FuelType;
import tn.jakloc.model.Transmission;
import tn.jakloc.model.Vehicle;
import tn.jakloc.model.VehicleCategory;
import tn.jakloc.model.VehicleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class VehicleSeeder {

    private VehicleSeeder() {
    }

    public static void seed(VehicleRepository repository) {
        if (!repository.findAll().isEmpty()) {
            return;
        }

        repository.save(vehicle("Porsche", "911 Carrera S", VehicleCategory.SPORTS,
                Transmission.AUTOMATIC, FuelType.PETROL, 2023, 4, 2,
                "520", 18400, "Tunis - La Marsa",
                "A precise, theatre-like drive for coastal escapes and weekend getaways.",
                VehicleStatus.AVAILABLE));

        repository.save(vehicle("Mercedes-Benz", "GLE 450", VehicleCategory.SUV,
                Transmission.AUTOMATIC, FuelType.HYBRID, 2024, 5, 5,
                "390", 9200, "Tunis - Centre Ville",
                "Quiet, spacious and composed, with room for the whole crew.",
                VehicleStatus.AVAILABLE));

        repository.save(vehicle("BMW", "i4 eDrive40", VehicleCategory.ELECTRIC,
                Transmission.AUTOMATIC, FuelType.ELECTRIC, 2024, 5, 4,
                "340", 7600, "Aeroport Tunis-Carthage",
                "Silent acceleration and a cabin built for long, easy drives.",
                VehicleStatus.AVAILABLE));

        repository.save(vehicle("Range Rover", "Velar", VehicleCategory.SUV,
                Transmission.AUTOMATIC, FuelType.DIESEL, 2023, 5, 5,
                "450", 21300, "Hammamet",
                "A calm, elevated ride with enough presence for any occasion.",
                VehicleStatus.AVAILABLE));

        repository.save(vehicle("Audi", "A6 55 TFSI", VehicleCategory.LUXURY,
                Transmission.AUTOMATIC, FuelType.PETROL, 2024, 5, 4,
                "360", 12400, "Tunis - Les Berges du Lac",
                "Understated comfort with a sharp, modern edge.",
                VehicleStatus.AVAILABLE));

        repository.save(vehicle("Volkswagen", "Golf 8", VehicleCategory.HATCHBACK,
                Transmission.MANUAL, FuelType.PETROL, 2023, 5, 5,
                "180", 24800, "Sousse",
                "Compact, efficient and genuinely fun on city streets.",
                VehicleStatus.AVAILABLE));

        repository.save(vehicle("Mercedes-Benz", "V-Class", VehicleCategory.VAN,
                Transmission.AUTOMATIC, FuelType.DIESEL, 2023, 7, 5,
                "380", 30100, "Aeroport Tunis-Carthage",
                "Seven seats, generous luggage space and effortless cruising.",
                VehicleStatus.RENTED));

        repository.save(vehicle("Dacia", "Duster", VehicleCategory.SUV,
                Transmission.MANUAL, FuelType.DIESEL, 2022, 5, 5,
                "140", 46500, "Tozeur",
                "A dependable companion for the road less travelled.",
                VehicleStatus.MAINTENANCE));
    }

    private static Vehicle vehicle(String brand, String model, VehicleCategory category,
            Transmission transmission, FuelType fuelType, int year, int seats, int doors,
            String dailyRate, int mileage, String location, String description,
            VehicleStatus status) {

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setCategory(category);
        vehicle.setTransmission(transmission);
        vehicle.setFuelType(fuelType);
        vehicle.setYear(year);
        vehicle.setSeats(seats);
        vehicle.setDoors(doors);
        vehicle.setDailyRate(new BigDecimal(dailyRate));
        vehicle.setMileage(mileage);
        vehicle.setLocation(location);
        vehicle.setDescription(description);
        vehicle.setStatus(status);
        vehicle.setCreatedAt(LocalDateTime.now());
        return vehicle;
    }
}
