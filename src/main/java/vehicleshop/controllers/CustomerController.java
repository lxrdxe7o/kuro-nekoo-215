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
import vehicleshop.models.Customer;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> idCol;
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String> emailCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> dobCol;
    @FXML
    private TableColumn<Customer, Void> actionsCol;
    @FXML
    private TextField searchField;
    @FXML
    private Label resultCount;

    private ObservableList<Customer> allCustomers;
    private FilteredList<Customer> filteredCustomers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadCustomers();
    }

    private void setupTable() {
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        addressCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        dobCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDob()));

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
                    Customer c = getTableView().getItems().get(getIndex());
                    handleEdit(c);
                });

                deleteBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    handleDelete(c);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });
    }

    private void loadCustomers() {
        allCustomers = FXCollections.observableArrayList();
        allCustomers.addAll(MainController.getCustomers());

        filteredCustomers = new FilteredList<>(allCustomers, p -> true);
        customerTable.setItems(filteredCustomers);
        updateResultCount();
    }

    @FXML
    public void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        filteredCustomers.setPredicate(customer -> {
            if (searchText.isEmpty())
                return true;
            return customer.getName().toLowerCase().contains(searchText)
                    || customer.getEmail().toLowerCase().contains(searchText)
                    || customer.getId().toLowerCase().contains(searchText);
        });

        updateResultCount();
    }

    private void updateResultCount() {
        int count = filteredCustomers.size();
        resultCount.setText(count + " customer" + (count != 1 ? "s" : ""));
    }

    @FXML
    public void handleAddCustomer() {
        showCustomerDialog(null);
        loadCustomers();
    }

    private void handleEdit(Customer customer) {
        showCustomerDialog(customer);
        loadCustomers();
    }

    private void handleDelete(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText("Delete " + customer.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            MainController.getCustomers().remove(customer);
            Customer.saveCustomers(MainController.getCustomers());
            loadCustomers();
        }
    }

    public static void showCustomerDialog(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(customer == null ? "Add Customer" : "Edit Customer");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField dobField = new TextField();
        TextField idField = new TextField();

        nameField.setPromptText("Full name");
        addressField.setPromptText("Street address");
        phoneField.setPromptText("Phone number");
        emailField.setPromptText("Email address");
        dobField.setPromptText("Date of birth");
        idField.setPromptText("Customer ID");

        if (customer != null) {
            nameField.setText(customer.getName());
            addressField.setText(customer.getAddress());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail());
            dobField.setText(customer.getDob());
            idField.setText(customer.getId());
            idField.setDisable(true);
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("DOB:"), 0, 4);
        grid.add(dobField, 1, 4);
        grid.add(new Label("ID:"), 0, 5);
        grid.add(idField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(450);

        // Apply dark theme stylesheet
        dialog.getDialogPane().getStylesheets()
                .add(CustomerController.class.getResource("/styles/styles.css").toExternalForm());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String dob = dobField.getText().trim();
                String id = idField.getText().trim();

                if (name.isEmpty() || id.isEmpty()) {
                    showError("Name and ID are required.");
                    return null;
                }

                if (customer != null) {
                    customer.updateCustomerInfo(name, address, phone, email, dob, id);
                    Customer.saveCustomers(MainController.getCustomers());
                    return customer;
                } else {
                    Customer newCustomer = new Customer(name, address, phone, email, dob, id);
                    MainController.getCustomers().add(newCustomer);
                    Customer.saveCustomers(MainController.getCustomers());
                    return newCustomer;
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
