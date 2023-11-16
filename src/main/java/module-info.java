module com.syncdroids {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.net;

    exports com.syncdroids.ui;
    opens com.syncdroids.ui to javafx.fxml;
}