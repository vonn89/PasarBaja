/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import com.excellentsystem.PasarBaja.Model.Barang;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yunaz
 */
public class DetailBarangController {

    @FXML
    public TextField kodeBarangField;
    @FXML
    public TextField namaBarangField;
    @FXML
    public TextField beratField;
    @FXML
    public TextField satuanField;
    @FXML
    public TextField hargaJualField;

    @FXML
    public Button saveButton;
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        Function.setNumberField(beratField);
        Function.setNumberField(hargaJualField);

    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        stage.setOnCloseRequest((e) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    public void setBarang(Barang b) {
        kodeBarangField.setDisable(false);
        hargaJualField.setDisable(false);
        kodeBarangField.setText("");
        namaBarangField.setText("");
        beratField.setText("0");
        satuanField.setText("");
        hargaJualField.setText("0");
        if (b != null) {
            kodeBarangField.setDisable(true);
            kodeBarangField.setText(b.getKodeBarang());
            namaBarangField.setText(b.getNamaBarang());
            beratField.setText(df.format(b.getBerat()));
            satuanField.setText(b.getSatuan());
            hargaJualField.setText(df.format(b.getHargaJual()));
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
