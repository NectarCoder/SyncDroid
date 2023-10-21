module com.syncdroids.syncdroid {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.syncdroids.syncdroid to javafx.fxml;
    exports com.syncdroids.syncdroid;
}