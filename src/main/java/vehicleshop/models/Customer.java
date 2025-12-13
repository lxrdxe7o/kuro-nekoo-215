package vehicleshop.models;

import javafx.beans.property.*;
import java.io.*;
import java.util.ArrayList;

public class Customer implements Serializable {
    
    private String name;
    private String address;
    private String phone;
    private String email;
    private String dob;
    private String id;

    private final transient StringProperty nameProperty = new SimpleStringProperty();
    private final transient StringProperty addressProperty = new SimpleStringProperty();
    private final transient StringProperty phoneProperty = new SimpleStringProperty();
    private final transient StringProperty emailProperty = new SimpleStringProperty();
    private final transient StringProperty dobProperty = new SimpleStringProperty();
    private final transient StringProperty idProperty = new SimpleStringProperty();

    public Customer() {
    }

    public Customer(String name, String address, String phone, String email, String dob, String id) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.id = id;
        
        // Initialize JavaFX properties
        this.nameProperty.set(name);
        this.addressProperty.set(address);
        this.phoneProperty.set(phone);
        this.emailProperty.set(email);
        this.dobProperty.set(dob);
        this.idProperty.set(id);
    }

    // JavaFX Property accessors for TableView binding
    public StringProperty namePropertyProperty() { return nameProperty; }
    public StringProperty addressPropertyProperty() { return addressProperty; }
    public StringProperty phonePropertyProperty() { return phoneProperty; }
    public StringProperty emailPropertyProperty() { return emailProperty; }
    public StringProperty dobPropertyProperty() { return dobProperty; }
    public StringProperty idPropertyProperty() { return idProperty; }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getDob() { return dob; }
    public String getId() { return id; }

    public void setName(String name) {
        this.name = name;
        this.nameProperty.set(name);
    }

    public void setAddress(String address) {
        this.address = address;
        this.addressProperty.set(address);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneProperty.set(phone);
    }

    public void setEmail(String email) {
        this.email = email;
        this.emailProperty.set(email);
    }

    public void setDob(String dob) {
        this.dob = dob;
        this.dobProperty.set(dob);
    }

    public void setId(String id) {
        this.id = id;
        this.idProperty.set(id);
    }

    public String toDataString() {
        return name + "," + address + "," + phone + "," + email + "," + dob + "," + id;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nAddress: " + address + "\nPhone: " + phone +
               "\nEmail: " + email + "\nDOB: " + dob + "\nID: " + id;
    }

    public void updateCustomerInfo(String name, String address, String phone, String email, String dob, String id) {
        setName(name);
        setAddress(address);
        setPhone(phone);
        setEmail(email);
        setDob(dob);
        setId(id);
    }

    public static void saveCustomers(ArrayList<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.dat"))) {
            for (Customer customer : customers) {
                writer.write(customer.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    public static void loadCustomers(ArrayList<Customer> customers) {
        File file = new File("customers.dat");
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    customers.add(new Customer(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    public static Customer findByName(ArrayList<Customer> customers, String name) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return customer;
            }
        }
        return null;
    }

    public static Customer findById(ArrayList<Customer> customers, String id) {
        for (Customer customer : customers) {
            if (customer.getId().equalsIgnoreCase(id)) {
                return customer;
            }
        }
        return null;
    }
}
