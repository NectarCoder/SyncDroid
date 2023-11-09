package com.syncdroids.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class HelloController {
    public MenuItem exit;
    
    @FXML
    protected void exitProgram() {
        System.exit(0);
    }

    protected void launchAboutDialog() {

    }
}