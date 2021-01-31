/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.KategoriKeuanganDAO;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.KategoriKeuangan;
import com.excellentsystem.PasarBaja.Services.Service;
import java.sql.Connection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Yunaz
 */
public class KategoriKeuanganController {

    @FXML
    private TableView<KategoriKeuangan> tipeKeuanganTable;
    @FXML
    private TableColumn<KategoriKeuangan, String> kodeKategoriKeuanganColumn;

    @FXML
    private TextField kodeKategoriKeuanganField;
    private ObservableList<KategoriKeuangan> allKategoriKeuangan = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeKategoriKeuanganColumn.setCellValueFactory(cellData -> cellData.getValue().kodeKeuanganProperty());

        final ContextMenu rm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getKategoriKeuangan();
        });
        rm.getItems().addAll(refresh);
        tipeKeuanganTable.setContextMenu(rm);
        tipeKeuanganTable.setRowFactory(table -> {
            TableRow<KategoriKeuangan> row = new TableRow<KategoriKeuangan>() {
                @Override
                public void updateItem(KategoriKeuangan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem hapus = new MenuItem("Delete Tipe Keuangan");
                        hapus.setOnAction((ActionEvent event) -> {
                            deleteKategoriKeuangan(item);
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getKategoriKeuangan();
                        });
                        rm.getItems().addAll(hapus, refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
        kodeKategoriKeuanganField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                saveKategoriKeuangan();
            }
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.owner = owner;
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
        tipeKeuanganTable.setItems(allKategoriKeuangan);
        getKategoriKeuangan();
    }

    private void getKategoriKeuangan() {
        Task<List<KategoriKeuangan>> task = new Task<List<KategoriKeuangan>>() {
            @Override
            public List<KategoriKeuangan> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return KategoriKeuanganDAO.getAll(con);
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            mainApp.closeLoading();
            allKategoriKeuangan.clear();
            allKategoriKeuangan.addAll(task.getValue());
            tipeKeuanganTable.refresh();
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    @FXML
    private void close() {
        mainApp.closeDialog(owner, stage);
    }

    private void deleteKategoriKeuangan(KategoriKeuangan temp) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Delete Tipe Keuangan " + temp.getKodeKeuangan() + " ?");
        controller.OK.setOnAction((ActionEvent ex) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.deleteKategoriKeuangan(con, temp);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getKategoriKeuangan();
                String status = task.getValue();
                if (status.equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Tipe Keuangan berhasil dihapus");
                    kodeKategoriKeuanganField.setText("");
                } else {
                    mainApp.showMessage(Modality.NONE, "Failed", status);
                }
            });
            task.setOnFailed((e) -> {
                mainApp.closeLoading();
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            });
            new Thread(task).start();
        });
    }

    @FXML
    private void saveKategoriKeuangan() {
        if (kodeKategoriKeuanganField.getText().equals("")) {
            mainApp.showMessage(Modality.NONE, "Warning", "Kode keuangan masih kosong");
        } else {
            Boolean s = true;
            for (KategoriKeuangan k : allKategoriKeuangan) {
                if (k.getKodeKeuangan().equals(kodeKategoriKeuanganField.getText())) {
                    s = false;
                }
            }
            if (kodeKategoriKeuanganField.getText().toUpperCase().equals("HUTANG")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("PIUTANG")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("STOK BAHAN")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("STOK BARANG")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("ASET TETAP")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("MODAL")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("PENJUALAN")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("RETUR PENJUALAN")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("HPP")
                    || kodeKategoriKeuanganField.getText().toUpperCase().equals("PENDAPATAN/BEBAN")) {
                s = false;
            }
            if (s) {
                KategoriKeuangan k = new KategoriKeuangan();
                k.setKodeKeuangan(kodeKategoriKeuanganField.getText());
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            return Service.newKategoriKeuangan(con, k);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getKategoriKeuangan();
                    String status = task.getValue();
                    if (status.equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Tipe Keuangan berhasil disimpan");
                        kodeKategoriKeuanganField.setText("");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", status);
                    }
                });
                task.setOnFailed((e) -> {
                    mainApp.closeLoading();
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                });
                new Thread(task).start();
            } else {
                mainApp.showMessage(Modality.NONE, "Warning", "Kode tipe keuangan sudah terdaftar/tidak dapat digunakan");
            }
        }
    }

}
