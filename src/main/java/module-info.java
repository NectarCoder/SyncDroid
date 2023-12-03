module com.syncdroids {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.net;
    requires org.apache.commons.io;

    exports com.syncdroids.ui;
    exports com.syncdroids.fileengine;
    opens com.syncdroids.ui to javafx.fxml, javafx.controls, com.dlsc.formsfx;
}