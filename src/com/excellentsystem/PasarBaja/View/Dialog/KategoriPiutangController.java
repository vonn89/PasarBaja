/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.KategoriPiutangDAO;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.KategoriPiutang;
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
public class KategoriPiutangController {

    @FXML
    private TableView<KategoriPiutang> kategoriPiutangTable;
    @FXML
    private TableColumn<KategoriPiutang, String> kodeKategoriPiutangColumn;

    @FXML
    private TextField kodeKategoriPiutangField;
    private ObservableList<KategoriPiutang> allKategoriPiutang = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeKategoriPiutangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeKategoriProperty());

        final ContextMenu rm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            kategoriPiutangTable.refresh();
        });
        rm.getItems().addAll(refresh);
        kategoriPiutangTable.setContextMenu(rm);
        kategoriPiutangTable.setRowFactory(table -> {
            TableRow<KategoriPiutang> row = new TableRow<KategoriPiutang>() {
                @Override
                public void updateItem(KategoriPiutang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem hapus = new MenuItem("Delete Kategori Piutang");
                        hapus.setOnAction((ActionEvent event) -> {
                            deleteKategoriPiutang(item);
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getKategoriPiutang();
                        });
                        rm.getItems().addAll(hapus, refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
        kodeKategoriPiutangField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                saveKategoriPiutang();
            }
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.owner = owner;
        kategoriPiutangTable.setItems(allKategoriPiutang);
        getKategoriPiutang();
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    private void getKategoriPiutang() {
        Task<List<KategoriPiutang>> task = new Task<List<KategoriPiutang>>() {
            @Override
            public List<KategoriPiutang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return KategoriPiutangDAO.getAll(con);
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            mainApp.closeLoading();
            allKategoriPiutang.clear();
            allKategoriPiutang.addAll(task.getValue());
            kategoriPiutangTable.refresh();
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    private void deleteKategoriPiutang(KategoriPiutang temp) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Delete Kategori Piutang " + temp.getKodeKategori() + " ?");
        controller.OK.setOnAction((ActionEvent ex) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.deleteKategoriPiutang(con, temp);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getKategoriPiutang();
                String status = task.getValue();
                if (status.equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Kategori Piutang berhasil dihapus");
                    kodeKategoriPiutangField.setText("");
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
    private void saveKategoriPiutang() {
        if (kodeKategoriPiutangField.getText().equals("")) {
            mainApp.showMessage(Modality.NONE, "Warning", "Kode kategori masih kosong");
        } else {
            Boolean s = true;
            for (KategoriPiutang k : allKategoriPiutang) {
                if (k.getKodeKategori().equals(kodeKategoriPiutangField.getText())) {
                    s = false;
                }
            }
            if (kodeKategoriPiutangField.getText().equals("Piutang Penjualan")) {
                s = false;
            }
            if (s) {
                KategoriPiutang k = new KategoriPiutang();
                k.setKodeKategori(kodeKategoriPiutangField.getText());
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            return Service.newKategoriPiutang(con, k);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getKategoriPiutang();
                    String status = task.getValue();
                    if (status.equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Kategori Piutang berhasil disimpan");
                        kodeKategoriPiutangField.setText("");
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
                mainApp.showMessage(Modality.NONE, "Warning", "Kode Kategori sudah terdaftar");
            }
        }
    }

    @FXML
    private void close() {
        mainApp.closeDialog(owner, stage);
    }

}
