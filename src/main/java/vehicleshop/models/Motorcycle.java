package vehicleshop.models;

import javafx.beans.property.*;
import java.io.*;
import java.util.ArrayList;

public class Motorcycle extends VehicleType implements Vehicle {

    private final StringProperty makeProperty = new SimpleStringProperty();
    private final StringProperty modelProperty = new SimpleStringProperty();
    private final StringProperty yearProperty = new SimpleStringProperty();
    private final StringProperty colorProperty = new SimpleStringProperty();
    private final DoubleProperty priceProperty = new SimpleDoubleProperty();
    private final StringProperty vinProperty = new SimpleStringProperty();
    private final StringProperty typeProperty = new SimpleStringProperty();

    public Motorcycle() {
        this.typeProperty.set("Motorcycle");
    }

    public Motorcycle(String make, String model, String year, String color, double price, String vin, String type) {
        super(make, type);
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.price = price;
        this.vin = vin;
        this.type = type;
        
        // Initialize JavaFX properties
        this.makeProperty.set(make);
        this.modelProperty.set(model);
        this.yearProperty.set(year);
        this.colorProperty.set(color);
        this.priceProperty.set(price);
        this.vinProperty.set(vin);
        this.typeProperty.set(type);
    }

    // JavaFX Property accessors for TableView binding
    public StringProperty makePropertyProperty() { return makeProperty; }
    public StringProperty modelPropertyProperty() { return modelProperty; }
    public StringProperty yearPropertyProperty() { return yearProperty; }
    public StringProperty colorPropertyProperty() { return colorProperty; }
    public DoubleProperty pricePropertyProperty() { return priceProperty; }
    public StringProperty vinPropertyProperty() { return vinProperty; }
    public StringProperty typePropertyProperty() { return typeProperty; }

    @Override
    public String getMake() { return make; }

    @Override
    public String getModel() { return model; }

    @Override
    public String getYear() { return year; }

    @Override
    public String getColor() { return color; }

    @Override
    public double getPrice() { return price; }

    @Override
    public String getVin() { return vin; }

    @Override
    public String getType() { return type; }

    @Override
    public void setMake(String make) {
        this.make = make;
        this.makeProperty.set(make);
    }

    @Override
    public void setModel(String model) {
        this.model = model;
        this.modelProperty.set(model);
    }

    @Override
    public void setYear(String year) {
        this.year = year;
        this.yearProperty.set(year);
    }

    @Override
    public void setColor(String color) {
        this.color = color;
        this.colorProperty.set(color);
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
        this.priceProperty.set(price);
    }

    @Override
    public void setVin(String vin) {
        this.vin = vin;
        this.vinProperty.set(vin);
    }

    @Override
    public void setType(String type) {
        this.type = type;
        this.typeProperty.set(type);
    }

    public String toDataString() {
        return make + "," + model + "," + year + "," + color + "," + price + "," + vin + "," + type;
    }

    @Override
    public String toString() {
        return "Make: " + make + "\nModel: " + model + "\nYear: " + year + 
               "\nColor: " + color + "\nPrice: $" + String.format("%.2f", price) + 
               "\nVIN: " + vin + "\nType: " + type;
    }

    @Override
    public double discountedPrice(int discount) {
        return price - (price * discount / 100);
    }

    public double discountedPrice(double discount) {
        return price - (price * discount / 100);
    }

    @Override
    public void updateVehicleInfo(String make, String model, String year, String color, double price, String vin) {
        setMake(make);
        setModel(model);
        setYear(year);
        setColor(color);
        setPrice(price);
        setVin(vin);
    }

    public static void saveMotorcycles(ArrayList<Motorcycle> motorcycles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("motorcycles.dat"))) {
            for (Motorcycle motorcycle : motorcycles) {
                writer.write(motorcycle.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving motorcycles: " + e.getMessage());
        }
    }

    public static void loadMotorcycles(ArrayList<Motorcycle> motorcycles) {
        File file = new File("motorcycles.dat");
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    motorcycles.add(new Motorcycle(parts[0], parts[1], parts[2], parts[3], 
                                                   Double.parseDouble(parts[4]), parts[5], parts[6]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading motorcycles: " + e.getMessage());
        }
    }

    public static Motorcycle findByModel(ArrayList<Motorcycle> motorcycles, String model) {
        for (Motorcycle motorcycle : motorcycles) {
            if (motorcycle.getModel().equalsIgnoreCase(model)) {
                return motorcycle;
            }
        }
        return null;
    }

    public static Motorcycle findByVin(ArrayList<Motorcycle> motorcycles, String vin) {
        for (Motorcycle motorcycle : motorcycles) {
            if (motorcycle.getVin().equalsIgnoreCase(vin)) {
                return motorcycle;
            }
        }
        return null;
    }
}
