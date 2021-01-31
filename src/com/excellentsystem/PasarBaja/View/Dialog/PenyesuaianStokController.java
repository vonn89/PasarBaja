/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.DAO.PenyesuaianStokBarangDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import com.excellentsystem.PasarBaja.Model.PenyesuaianStokBarang;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author excellent
 */
public class PenyesuaianStokController {

    @FXML
    private Label title;
    @FXML
    private Label kodeBarangLabel;
    @FXML
    private Label qtyLabel;
    @FXML
    public TextField kodeBarangField;
    @FXML
    public TextField qtyField;
    @FXML
    public ComboBox<String> statusCombo;
    @FXML
    public TextArea catatanField;
    @FXML
    public Button saveButton;
    @FXML
    private Button cancelButton;
    private Main mainApp;
    private Stage owner;
    private Stage stage;
    private ObservableList<String> allStatus = FXCollections.observableArrayList();

    public void initialize() {
        // TODO
    }

    public void setMainApp(Main main, Stage owner, Stage stage) {
        this.mainApp = main;
        this.owner = owner;
        this.stage = stage;
        Function.setNumberField(qtyField);
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
        allStatus.clear();
        allStatus.add("Penambahan Stok");
        allStatus.add("Pengurangan Stok");
        statusCombo.setItems(allStatus);
    }

    public void setBarang(String kodeBarang) {
        title.setText("Penyesuaian Stok Barang");
        kodeBarangLabel.setText("Barang");
        qtyLabel.setText("Qty");
        kodeBarangField.setText(kodeBarang);
        qtyField.setText("0");
    }

    public void setPenyesuaianStokBarang(String noPenyesuaian) {
        try (Connection con = Koneksi.getConnection()) {
            PenyesuaianStokBarang p = PenyesuaianStokBarangDAO.get(con, noPenyesuaian);
            p.setBarang(BarangDAO.get(con, p.getKodeBarang()));

            qtyField.setDisable(true);
            statusCombo.setDisable(true);
            catatanField.setDisable(true);
            saveButton.setVisible(false);
            cancelButton.setVisible(false);

            setBarang(p.getKodeBarang());
            if (p.getQty() < 0) {
                statusCombo.getSelectionModel().select("Pengurangan Stok");
                qtyField.setText(df.format(p.getQty() * -1));
            } else {
                statusCombo.getSelectionModel().select("Penambahan Stok");
                qtyField.setText(df.format(p.getQty()));
            }
            catatanField.setText(p.getCatatan());
        } catch (Exception e) {
            e.printStackTrace();
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }
}
