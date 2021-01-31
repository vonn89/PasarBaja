/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.Customer;
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
public class DetailCustomerController {

    @FXML
    public TextField kodeCustomerField;
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
        this.owner = owner;
        this.stage = stage;
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    public void setCustomerDetail(Customer customer) {
        kodeCustomerField.setText("");
        namaField.setText("");
        alamatField.setText("");
        kotaField.setText("");
        emailField.setText("");
        kontakPersonField.setText("");
        noTelpField.setText("");
        noHandphoneField.setText("");
        if (customer != null) {
            kodeCustomerField.setText(customer.getKodeCustomer());
            namaField.setText(customer.getNama());
            alamatField.setText(customer.getAlamat());
            kotaField.setText(customer.getKota());
            emailField.setText(customer.getEmail());
            kontakPersonField.setText(customer.getKontakPerson());
            noTelpField.setText(customer.getNoTelp());
            noHandphoneField.setText(customer.getNoHandphone());
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
