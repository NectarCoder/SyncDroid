module com.syncdroids {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.net;
    requires org.apache.commons.io;

    exports com.syncdroids.ui;
    opens com.syncdroids.ui to javafx.fxml;
}