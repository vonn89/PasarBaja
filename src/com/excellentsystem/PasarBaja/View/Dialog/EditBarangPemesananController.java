/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import java.sql.Connection;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class EditBarangPemesananController {

    @FXML
    private TextField namaBarangField;
    @FXML
    public TextField keteranganField;
    @FXML
    public TextField catatanInternField;
    @FXML
    public TextField qtyField;
    @FXML
    private TextField satuanField;
    @FXML
    public TextField hargaJualField;
    @FXML
    public TextField totalField;
    @FXML
    public Button addButton;
    public Barang barang;
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        hargaJualField.setOnKeyReleased((event) -> {
            try {
                String string = hargaJualField.getText();
                if (string.indexOf(".") > 0) {
                    String string2 = string.substring(string.indexOf(".") + 1, string.length());
                    if (string2.contains(".")) {
                        hargaJualField.undo();
                    } else if (!string2.equals("") && Double.parseDouble(string2) != 0) {
                        hargaJualField.setText(df.format(Double.parseDouble(string.replaceAll(",", ""))));
                    }
                } else {
                    hargaJualField.setText(df.format(Double.parseDouble(string.replaceAll(",", ""))));
                }
                hargaJualField.end();
            } catch (NumberFormatException e) {
                hargaJualField.undo();
            }
            hitungTotal();
        });
        qtyField.setOnKeyReleased((event) -> {
            try {
                String string = qtyField.getText();
                if (string.indexOf(".") > 0) {
                    String string2 = string.substring(string.indexOf(".") + 1, string.length());
                    if (string2.contains(".")) {
                        qtyField.undo();
                    } else if (!string2.equals("") && Double.parseDouble(string2) != 0) {
                        qtyField.setText(df.format(Double.parseDouble(string.replaceAll(",", ""))));
                    }
                } else {
                    qtyField.setText(df.format(Double.parseDouble(string.replaceAll(",", ""))));
                }
                qtyField.end();
            } catch (NumberFormatException e) {
                qtyField.undo();
            }
            hitungTotal();
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        stage.setOnCloseRequest((e) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    @FXML
    private void hitungTotal() {
        if (qtyField.getText().equals("")) {
            qtyField.setText("0");
        }
        if (hargaJualField.getText().equals("")) {
            hargaJualField.setText("0");
        }
        totalField.setText(df.format(
                Double.parseDouble(qtyField.getText().replaceAll(",", ""))
                * Double.parseDouble(hargaJualField.getText().replaceAll(",", ""))
        ));
    }

    public void editBarang(PemesananDetail d) {
        Task<Barang> task = new Task<Barang>() {
            @Override
            public Barang call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return BarangDAO.get(con, d.getKodeBarang());
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((ev) -> {
            mainApp.closeLoading();
            barang = task.getValue();
            namaBarangField.setText(d.getNamaBarang());
            keteranganField.setText(d.getKeterangan());
            catatanInternField.setText(d.getCatatanIntern());
            qtyField.setText(df.format(d.getQty()));
            satuanField.setText(d.getSatuan());
            hargaJualField.setText(df.format(d.getHargaJual()));
            totalField.setText(df.format(d.getTotal()));
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
