/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.KategoriTransaksiDAO;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.KategoriTransaksi;
import com.excellentsystem.PasarBaja.Services.Service;
import java.sql.Connection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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
public class KategoriTransaksiController {

    @FXML
    private TableView<KategoriTransaksi> kategoriTable;
    @FXML
    private TableColumn<KategoriTransaksi, String> kodeKategoriTransaksiColumn;
    @FXML
    private TableColumn<KategoriTransaksi, String> jenisKategoriTransaksiColumn;

    @FXML
    private TextField kodeKategoriTransaksiField;
    @FXML
    private ComboBox<String> jenisKategoriTransaksiCombo;
    private ObservableList<KategoriTransaksi> allKategoriTransaksi = FXCollections.observableArrayList();

    private String status;
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeKategoriTransaksiColumn.setCellValueFactory(cellData -> cellData.getValue().kodeKategoriProperty());
        jenisKategoriTransaksiColumn.setCellValueFactory(cellData -> cellData.getValue().jenisTransaksiProperty());
        kodeKategoriTransaksiField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                jenisKategoriTransaksiCombo.requestFocus();
            }
        });
        jenisKategoriTransaksiCombo.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                saveKategoriTransaksi();
            }
        });
        kategoriTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectKategoriTransaksi(newValue));

        final ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Kategori Transaksi");
        addNew.setOnAction((ActionEvent event) -> {
            newKategoriTransaksi();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getKategoriTransaksi();
        });
        rm.getItems().addAll(addNew, refresh);
        kategoriTable.setContextMenu(rm);
        kategoriTable.setRowFactory(table -> {
            TableRow<KategoriTransaksi> row = new TableRow<KategoriTransaksi>() {
                @Override
                public void updateItem(KategoriTransaksi item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Kategori Transaksi");
                        addNew.setOnAction((ActionEvent event) -> {
                            newKategoriTransaksi();
                        });
                        MenuItem hapus = new MenuItem("Delete Kategori Transaksi");
                        hapus.setOnAction((ActionEvent event) -> {
                            deleteKategoriTransaksi(item);
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getKategoriTransaksi();
                        });
                        rm.getItems().addAll(addNew, hapus, refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.owner = owner;
        kategoriTable.setItems(allKategoriTransaksi);
        ObservableList<String> allJenis = FXCollections.observableArrayList();
        allJenis.add("Pendapatan");
        allJenis.add("Beban");
        jenisKategoriTransaksiCombo.setItems(allJenis);
        getKategoriTransaksi();
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
        newKategoriTransaksi();
    }

    private void getKategoriTransaksi() {
        Task<List<KategoriTransaksi>> task = new Task<List<KategoriTransaksi>>() {
            @Override
            public List<KategoriTransaksi> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return KategoriTransaksiDAO.getAllByStatus(con, "true");
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            mainApp.closeLoading();
            allKategoriTransaksi.clear();
            allKategoriTransaksi.addAll(task.getValue());
            kategoriTable.refresh();
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    private void newKategoriTransaksi() {
        status = "insert";
        kodeKategoriTransaksiField.setText("");
        jenisKategoriTransaksiCombo.getSelectionModel().select("");
        kodeKategoriTransaksiField.setDisable(false);
    }

    private void selectKategoriTransaksi(KategoriTransaksi k) {
        if (k != null) {
            status = "update";
            kodeKategoriTransaksiField.setText(k.getKodeKategori());
            jenisKategoriTransaksiCombo.getSelectionModel().select(k.getJenisTransaksi());
            kodeKategoriTransaksiField.setDisable(true);
        } else {
            status = "";
            kodeKategoriTransaksiField.setText("");
            jenisKategoriTransaksiCombo.getSelectionModel().clearSelection();
            kodeKategoriTransaksiField.setDisable(false);
        }
    }

    private void deleteKategoriTransaksi(KategoriTransaksi temp) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Delete Kategori Transaksi " + temp.getKodeKategori() + " ?");
        controller.OK.setOnAction((ActionEvent ex) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.deleteKategoriTransaksi(con, temp);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getKategoriTransaksi();
                String status = task.getValue();
                if (status.equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Kategori Transaksi berhasil dihapus");
                    kodeKategoriTransaksiField.setText("");
                    jenisKategoriTransaksiCombo.getSelectionModel().select(null);
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
    private void saveKategoriTransaksi() {
        if (kodeKategoriTransaksiField.getText().equals("")) {
            mainApp.showMessage(Modality.NONE, "Warning", "Kode kategori masih kosong");
        } else if (jenisKategoriTransaksiCombo.getSelectionModel().getSelectedItem() == null) {
            mainApp.showMessage(Modality.NONE, "Warning", "Jenis Transaksi belum dipilih");
        } else {
            Boolean s = true;
            for (KategoriTransaksi k : allKategoriTransaksi) {
                if (k.getKodeKategori().equals(kodeKategoriTransaksiField.getText())) {
                    s = false;
                }
            }
            if (s || status.equals("update")) {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            KategoriTransaksi k = new KategoriTransaksi();
                            k.setKodeKategori(kodeKategoriTransaksiField.getText());
                            k.setJenisTransaksi(jenisKategoriTransaksiCombo.getSelectionModel().getSelectedItem());
                            k.setStatus("true");
                            if (status.equals("insert")) {
                                return Service.newKategoriTransaksi(con, k);
                            } else if (status.equals("update")) {
                                return Service.updateKategoriTransaksi(con, k);
                            } else {
                                return "Status insert/update error";
                            }
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getKategoriTransaksi();
                    String statusupdate = task.getValue();
                    if (statusupdate.equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Kategori Transaksi berhasil disimpan");
                        selectKategoriTransaksi(null);
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", statusupdate);
                    }
                });
                task.setOnFailed((e) -> {
                    task.getException().printStackTrace();
                    mainApp.closeLoading();
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                });
                new Thread(task).start();
            } else {
                mainApp.showMessage(Modality.NONE, "Warning", "Kode Kategori Transaksi sudah terdaftar");
            }
        }
    }

    @FXML
    private void close() {
        mainApp.closeDialog(owner, stage);
    }
}
