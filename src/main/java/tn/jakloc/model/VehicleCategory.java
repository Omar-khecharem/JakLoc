package tn.jakloc.model;

public enum VehicleCategory {
    SUV("SUV", "suv"),
    SEDAN("Sedan", "sedan"),
    HATCHBACK("Hatchback", "hatchback"),
    LUXURY("Luxury", "luxury"),
    VAN("Van", "van"),
    PICKUP("Pickup", "pickup"),
    SPORTS("Sports", "sports"),
    ELECTRIC("Electric", "electric");

    private final String label;
    private final String cssClass;

    VehicleCategory(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}
