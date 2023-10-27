module com.syncdroids.syncdroid {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.syncdroids.ui to javafx.fxml;
    exports com.syncdroids.ui;
}