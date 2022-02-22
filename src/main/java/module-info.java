module com.example.animalsimulation {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.animalsimulation to javafx.fxml;
    exports com.example.animalsimulation;
}