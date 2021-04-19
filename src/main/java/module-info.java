module edu.semo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    opens edu.semo.casim to javafx.fxml;
    exports edu.semo.casim;
}