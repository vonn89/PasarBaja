/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.KategoriHutangDAO;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.KategoriHutang;
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
public class KategoriHutangController {

    @FXML
    private TableView<KategoriHutang> kategoriHutangTable;
    @FXML
    private TableColumn<KategoriHutang, String> kodeKategoriHutangColumn;

    @FXML
    private TextField kodeKategoriHutangField;
    private ObservableList<KategoriHutang> allKategoriHutang = FXCollections.observableArrayList();

    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeKategoriHutangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeKategoriProperty());

        final ContextMenu rm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            kategoriHutangTable.refresh();
        });
        rm.getItems().addAll(refresh);
        kategoriHutangTable.setContextMenu(rm);
        kategoriHutangTable.setRowFactory(table -> {
            TableRow<KategoriHutang> row = new TableRow<KategoriHutang>() {
                @Override
                public void updateItem(KategoriHutang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem hapus = new MenuItem("Delete Kategori Hutang");
                        hapus.setOnAction((ActionEvent event) -> {
                            deleteKategoriHutang(item);
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getKategoriHutang();
                        });
                        rm.getItems().addAll(hapus, refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
        kodeKategoriHutangField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                saveKategoriHutang();
            }
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.owner = owner;
        kategoriHutangTable.setItems(allKategoriHutang);
        getKategoriHutang();
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    private void getKategoriHutang() {
        Task<List<KategoriHutang>> task = new Task<List<KategoriHutang>>() {
            @Override
            public List<KategoriHutang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return KategoriHutangDAO.getAll(con);
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            mainApp.closeLoading();
            allKategoriHutang.clear();
            allKategoriHutang.addAll(task.getValue());
            kategoriHutangTable.refresh();
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    private void deleteKategoriHutang(KategoriHutang temp) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Delete Kategori Hutang " + temp.getKodeKategori() + " ?");
        controller.OK.setOnAction((ActionEvent ex) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.deleteKategoriHutang(con, temp);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getKategoriHutang();
                String status = task.getValue();
                if (status.equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Kategori Hutang berhasil dihapus");
                    kodeKategoriHutangField.setText("");
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
    private void saveKategoriHutang() {
        if (kodeKategoriHutangField.getText().equals("")) {
            mainApp.showMessage(Modality.NONE, "Warning", "Kode kategori masih kosong");
        } else {
            Boolean s = true;
            for (KategoriHutang k : allKategoriHutang) {
                if (k.getKodeKategori().equals(kodeKategoriHutangField.getText())) {
                    s = false;
                }
            }
            if (s) {
                KategoriHutang k = new KategoriHutang();
                k.setKodeKategori(kodeKategoriHutangField.getText());
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            return Service.newKategoriHutang(con, k);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getKategoriHutang();
                    String status = task.getValue();
                    if (status.equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Kategori Hutang berhasil disimpan");
                        kodeKategoriHutangField.setText("");
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
