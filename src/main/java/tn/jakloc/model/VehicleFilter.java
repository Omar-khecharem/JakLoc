package tn.jakloc.model;

import java.math.BigDecimal;

public class VehicleFilter {

    private String query = "";
    private VehicleCategory category;
    private Transmission transmission;
    private FuelType fuelType;
    private BigDecimal maxPrice;
    private VehicleSort sort = VehicleSort.RECOMMENDED;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    public void setCategory(VehicleCategory category) {
        this.category = category;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public VehicleSort getSort() {
        return sort;
    }

    public void setSort(VehicleSort sort) {
        this.sort = sort;
    }

    public boolean isFiltered() {
        return (query != null && !query.isBlank())
                || category != null
                || transmission != null
                || fuelType != null
                || maxPrice != null;
    }
}
