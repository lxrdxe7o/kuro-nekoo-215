module vehicleshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;

    opens vehicleshop to javafx.fxml;
    opens vehicleshop.controllers to javafx.fxml;
    opens vehicleshop.models to javafx.fxml;
    opens vehicleshop.components to javafx.fxml;

    exports vehicleshop;
    exports vehicleshop.controllers;
    exports vehicleshop.models;
    exports vehicleshop.components;
}
