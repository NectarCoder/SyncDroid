package com.syncdroids.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file for the main view
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Set up the main stage
        stage.setTitle("SyncDroid");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
        stage.setScene(scene);
        stage.show();
    }
}
