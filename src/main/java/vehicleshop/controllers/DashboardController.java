package vehicleshop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalCarsLabel;
    @FXML
    private Label totalMotorcyclesLabel;
    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label totalValueLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateStats();
    }

    private void updateStats() {
        int carCount = MainController.getCars().size();
        int motorcycleCount = MainController.getMotorcycles().size();
        int customerCount = MainController.getCustomers().size();

        double totalValue = MainController.getCars().stream().mapToDouble(c -> c.getPrice()).sum();
        totalValue += MainController.getMotorcycles().stream().mapToDouble(m -> m.getPrice()).sum();

        totalCarsLabel.setText(String.valueOf(carCount));
        totalMotorcyclesLabel.setText(String.valueOf(motorcycleCount));
        totalCustomersLabel.setText(String.valueOf(customerCount));
        totalValueLabel.setText("$" + String.format("%,.0f", totalValue));
    }

    @FXML
    public void handleAddCar() {
        VehicleController.showVehicleDialog(null, "Car");
        updateStats();
    }

    @FXML
    public void handleAddMotorcycle() {
        VehicleController.showVehicleDialog(null, "Motorcycle");
        updateStats();
    }

    @FXML
    public void handleAddCustomer() {
        CustomerController.showCustomerDialog(null);
        updateStats();
    }
}
