package vehicleshop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import vehicleshop.models.Car;
import vehicleshop.models.Customer;
import vehicleshop.models.Motorcycle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contentArea;
    @FXML
    private Button dashboardBtn;
    @FXML
    private Button vehiclesBtn;
    @FXML
    private Button customersBtn;
    @FXML
    private Button exitBtn;

    // Shared data across views
    private static ArrayList<Car> cars = new ArrayList<>();
    private static ArrayList<Motorcycle> motorcycles = new ArrayList<>();
    private static ArrayList<Customer> customers = new ArrayList<>();

    private Button activeNavButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load data from files
        Car.loadCars(cars);
        Motorcycle.loadMotorcycles(motorcycles);
        Customer.loadCustomers(customers);

        // Show dashboard by default
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        loadView("/views/DashboardView.fxml");
        setActiveNavButton(dashboardBtn);
    }

    @FXML
    public void showVehicles() {
        loadView("/views/VehicleListView.fxml");
        setActiveNavButton(vehiclesBtn);
    }

    @FXML
    public void showCustomers() {
        loadView("/views/CustomerListView.fxml");
        setActiveNavButton(customersBtn);
    }

    @FXML
    public void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("All data will be saved automatically.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveAllData();
            System.exit(0);
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load view: " + fxmlPath);
        }
    }

    private void setActiveNavButton(Button button) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("nav-button-active");
        }
        activeNavButton = button;
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().add("nav-button-active");
        }
    }

    public static void saveAllData() {
        Car.saveCars(cars);
        Motorcycle.saveMotorcycles(motorcycles);
        Customer.saveCustomers(customers);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Static accessors for shared data
    public static ArrayList<Car> getCars() {
        return cars;
    }

    public static ArrayList<Motorcycle> getMotorcycles() {
        return motorcycles;
    }

    public static ArrayList<Customer> getCustomers() {
        return customers;
    }
}
