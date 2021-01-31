/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.Supplier;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class DetailSupplierController {

    @FXML
    public TextField kodeSupplierField;
    @FXML
    public TextField namaField;
    @FXML
    public TextArea alamatField;
    @FXML
    public TextField kotaField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField kontakPersonField;
    @FXML
    public TextField noTelpField;
    @FXML
    public TextField noHandphoneField;
    @FXML
    public Button saveButton;
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.owner = owner;
        stage.setOnCloseRequest((e) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    public void setSupplierDetail(Supplier supplier) {
        kodeSupplierField.setText("");
        namaField.setText("");
        alamatField.setText("");
        kotaField.setText("");
        emailField.setText("");
        kontakPersonField.setText("");
        noTelpField.setText("");
        noHandphoneField.setText("");
        if (supplier != null) {
            kodeSupplierField.setText(supplier.getKodeSupplier());
            namaField.setText(supplier.getNama());
            alamatField.setText(supplier.getAlamat());
            kotaField.setText(supplier.getKota());
            emailField.setText(supplier.getEmail());
            kontakPersonField.setText(supplier.getKontakPerson());
            noTelpField.setText(supplier.getNoTelp());
            noHandphoneField.setText(supplier.getNoHandphone());
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
