package vehicleshop.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vehicleshop.models.Car;
import vehicleshop.models.Motorcycle;
import vehicleshop.models.Vehicle;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class VehicleController implements Initializable {

    @FXML
    private TableView<Vehicle> vehicleTable;
    @FXML
    private TableColumn<Vehicle, String> typeCol;
    @FXML
    private TableColumn<Vehicle, String> makeCol;
    @FXML
    private TableColumn<Vehicle, String> modelCol;
    @FXML
    private TableColumn<Vehicle, String> yearCol;
    @FXML
    private TableColumn<Vehicle, String> colorCol;
    @FXML
    private TableColumn<Vehicle, String> priceCol;
    @FXML
    private TableColumn<Vehicle, String> vinCol;
    @FXML
    private TableColumn<Vehicle, Void> actionsCol;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private Label resultCount;

    private ObservableList<Vehicle> allVehicles;
    private FilteredList<Vehicle> filteredVehicles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        loadVehicles();
    }

    private void setupTable() {
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        makeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMake()));
        modelCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModel()));
        yearCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getYear()));
        colorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getColor()));
        priceCol.setCellValueFactory(
                data -> new SimpleStringProperty("$" + String.format("%,.2f", data.getValue().getPrice())));
        vinCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVin()));

        // Actions column
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox actions = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("btn-icon");
                deleteBtn.getStyleClass().add("btn-icon");
                actions.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Vehicle v = getTableView().getItems().get(getIndex());
                    handleEdit(v);
                });

                deleteBtn.setOnAction(e -> {
                    Vehicle v = getTableView().getItems().get(getIndex());
                    handleDelete(v);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });
    }

    private void setupFilters() {
        typeFilter.setItems(FXCollections.observableArrayList("All Types", "Car", "Motorcycle"));
        typeFilter.setValue("All Types");
    }

    private void loadVehicles() {
        allVehicles = FXCollections.observableArrayList();
        allVehicles.addAll(MainController.getCars());
        allVehicles.addAll(MainController.getMotorcycles());

        filteredVehicles = new FilteredList<>(allVehicles, p -> true);
        vehicleTable.setItems(filteredVehicles);
        updateResultCount();
    }

    @FXML
    public void handleSearch() {
        applyFilters();
    }

    @FXML
    public void handleFilter() {
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String typeValue = typeFilter.getValue();

        filteredVehicles.setPredicate(vehicle -> {
            boolean matchesSearch = searchText.isEmpty() || vehicle.getMake().toLowerCase().contains(searchText)
                    || vehicle.getModel().toLowerCase().contains(searchText)
                    || vehicle.getVin().toLowerCase().contains(searchText);

            boolean matchesType = "All Types".equals(typeValue) || vehicle.getType().equals(typeValue);

            return matchesSearch && matchesType;
        });

        updateResultCount();
    }

    private void updateResultCount() {
        int count = filteredVehicles.size();
        resultCount.setText(count + " vehicle" + (count != 1 ? "s" : ""));
    }

    @FXML
    public void handleAddVehicle() {
        showVehicleDialog(null, null);
        loadVehicles();
    }

    private void handleEdit(Vehicle vehicle) {
        showVehicleDialog(vehicle, vehicle.getType());
        loadVehicles();
    }

    private void handleDelete(Vehicle vehicle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Vehicle");
        alert.setHeaderText("Delete " + vehicle.getMake() + " " + vehicle.getModel() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (vehicle instanceof Car) {
                MainController.getCars().remove(vehicle);
                Car.saveCars(MainController.getCars());
            } else if (vehicle instanceof Motorcycle) {
                MainController.getMotorcycles().remove(vehicle);
                Motorcycle.saveMotorcycles(MainController.getMotorcycles());
            }
            loadVehicles();
        }
    }

    public static void showVehicleDialog(Vehicle vehicle, String defaultType) {
        Dialog<Vehicle> dialog = new Dialog<>();
        dialog.setTitle(vehicle == null ? "Add Vehicle" : "Edit Vehicle");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        ComboBox<String> typeField = new ComboBox<>(FXCollections.observableArrayList("Car", "Motorcycle"));
        TextField makeField = new TextField();
        TextField modelField = new TextField();
        TextField yearField = new TextField();
        TextField colorField = new TextField();
        TextField priceField = new TextField();
        TextField vinField = new TextField();

        typeField.setPromptText("Select type");
        makeField.setPromptText("e.g., Toyota");
        modelField.setPromptText("e.g., Camry");
        yearField.setPromptText("e.g., 2024");
        colorField.setPromptText("e.g., Red");
        priceField.setPromptText("e.g., 25000");
        vinField.setPromptText("e.g., 1HGBH41JXMN109186");

        if (vehicle != null) {
            typeField.setValue(vehicle.getType());
            typeField.setDisable(true);
            makeField.setText(vehicle.getMake());
            modelField.setText(vehicle.getModel());
            yearField.setText(vehicle.getYear());
            colorField.setText(vehicle.getColor());
            priceField.setText(String.valueOf(vehicle.getPrice()));
            vinField.setText(vehicle.getVin());
        } else if (defaultType != null) {
            typeField.setValue(defaultType);
        }

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Make:"), 0, 1);
        grid.add(makeField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);
        grid.add(modelField, 1, 2);
        grid.add(new Label("Year:"), 0, 3);
        grid.add(yearField, 1, 3);
        grid.add(new Label("Color:"), 0, 4);
        grid.add(colorField, 1, 4);
        grid.add(new Label("Price:"), 0, 5);
        grid.add(priceField, 1, 5);
        grid.add(new Label("VIN:"), 0, 6);
        grid.add(vinField, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(450);

        // Apply dark theme stylesheet
        dialog.getDialogPane().getStylesheets()
                .add(VehicleController.class.getResource("/styles/styles.css").toExternalForm());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String type = typeField.getValue();
                    String make = makeField.getText().trim();
                    String model = modelField.getText().trim();
                    String year = yearField.getText().trim();
                    String color = colorField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    String vin = vinField.getText().trim();

                    if (type == null || make.isEmpty() || model.isEmpty() || vin.isEmpty()) {
                        showError("Please fill in all required fields.");
                        return null;
                    }

                    if (vehicle != null) {
                        vehicle.setMake(make);
                        vehicle.setModel(model);
                        vehicle.setYear(year);
                        vehicle.setColor(color);
                        vehicle.setPrice(price);
                        vehicle.setVin(vin);

                        if (vehicle instanceof Car) {
                            Car.saveCars(MainController.getCars());
                        } else {
                            Motorcycle.saveMotorcycles(MainController.getMotorcycles());
                        }
                        return vehicle;
                    } else {
                        if ("Car".equals(type)) {
                            Car car = new Car(make, model, year, color, price, vin, type);
                            MainController.getCars().add(car);
                            Car.saveCars(MainController.getCars());
                            return car;
                        } else {
                            Motorcycle motorcycle = new Motorcycle(make, model, year, color, price, vin, type);
                            MainController.getMotorcycles().add(motorcycle);
                            Motorcycle.saveMotorcycles(MainController.getMotorcycles());
                            return motorcycle;
                        }
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid price format. Please enter a valid number.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
