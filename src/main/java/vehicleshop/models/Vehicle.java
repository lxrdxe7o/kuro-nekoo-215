package vehicleshop.models;

import java.io.Serializable;

public interface Vehicle extends Serializable {
    String getMake();
    String getModel();
    String getYear();
    String getColor();
    double getPrice();
    String getVin();
    String getType();

    void setMake(String make);
    void setModel(String model);
    void setYear(String year);
    void setColor(String color);
    void setPrice(double price);
    void setVin(String vin);
    void setType(String type);
}
