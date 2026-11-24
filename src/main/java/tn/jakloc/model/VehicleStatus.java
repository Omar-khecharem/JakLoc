package tn.jakloc.model;

public enum VehicleStatus {
    AVAILABLE("Available", "available",
            "Ready to reserve now. Choose your dates in the booking step."),
    RENTED("Currently rented", "rented",
            "This car is currently on rent. It will return to the fleet soon."),
    MAINTENANCE("In maintenance", "maintenance",
            "This car is in maintenance and cannot be reserved right now.");

    private final String label;
    private final String cssClass;
    private final String description;

    VehicleStatus(String label, String cssClass, String description) {
        this.label = label;
        this.cssClass = cssClass;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}
