module com.syncdroids {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.net;
    requires org.apache.commons.io;

    exports com.syncdroids.ui;
    exports com.syncdroids.fileengine;
    exports com.syncdroids.fileengine.exception;
    opens com.syncdroids.ui to javafx.fxml;
}