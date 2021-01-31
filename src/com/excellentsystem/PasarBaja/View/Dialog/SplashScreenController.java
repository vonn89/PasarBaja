/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Excellent
 */
public class SplashScreenController {

    @FXML
    VBox vbox;

    public void initialize() {
    }

    public void setSplashScreen(ProgressBar p, Label l) {
        vbox.getChildren().add(l);
        vbox.getChildren().add(p);
    }
}
