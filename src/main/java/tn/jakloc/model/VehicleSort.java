package tn.jakloc.model;

import java.util.Locale;

public enum VehicleSort {
    RECOMMENDED("Recommended"),
    PRICE_ASC("Price: low to high"),
    PRICE_DESC("Price: high to low"),
    NAME_ASC("Name: A-Z"),
    SEATS_DESC("Most seats"),
    NEWEST("Newest first");

    private final String label;

    VehicleSort(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static VehicleSort fromParam(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return RECOMMENDED;
        }

        String normalized = rawValue.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        for (VehicleSort sort : values()) {
            if (sort.name().equals(normalized)) {
                return sort;
            }
        }
        return RECOMMENDED;
    }
}
