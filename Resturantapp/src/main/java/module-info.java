module com.example.resturantapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.resturantapp to javafx.fxml;
    exports com.example.resturantapp;
}