package vehicleshop.models;

import java.io.Serializable;

public abstract class VehicleType implements Serializable {
    protected String make;
    protected String model;
    protected String year;
    protected String color;
    protected double price;
    protected String vin;
    protected String type;

    public VehicleType() {
    }

    public VehicleType(String make, String type) {
        this.make = make;
        this.type = type;
    }

    public abstract void updateVehicleInfo(String make, String model, String year, String color, double price, String vin);

    public abstract double discountedPrice(int discount);
}
