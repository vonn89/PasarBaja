/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.BebanPenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.BebanPenjualanHeadDAO;
import com.excellentsystem.PasarBaja.DAO.KeuanganDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tgl;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.BebanPenjualanHead;
import com.excellentsystem.PasarBaja.Model.KategoriKeuangan;
import com.excellentsystem.PasarBaja.Model.KategoriTransaksi;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.NewBebanPenjualanController;
import com.excellentsystem.PasarBaja.View.Dialog.NewKeuanganController;
import com.excellentsystem.PasarBaja.View.Dialog.TransferKeuanganController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class KeuanganController {

    @FXML
    private TreeTableView<Keuangan> keuanganTable;
    @FXML
    private TreeTableColumn<Keuangan, String> noKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> tglKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> tipeKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> kategoriColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> deskripsiColumn;
    @FXML
    private TreeTableColumn<Keuangan, Number> jumlahRpColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> kodeUserColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label saldoAkhirField;
    @FXML
    private DatePicker tglMulaiPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    @FXML
    private ComboBox<String> TipeKeuanganCombo;
    private double saldoAkhir = 0;
    private double saldoAwal = 0;
    private final TreeItem<Keuangan> root = new TreeItem<>();
    private ObservableList<Keuangan> allKeuangan = FXCollections.observableArrayList();
    private ObservableList<Keuangan> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noKeuanganColumn.setCellValueFactory(param -> param.getValue().getValue().noKeuanganProperty());
        noKeuanganColumn.setCellFactory(col -> Function.getWrapTreeTableCell(noKeuanganColumn));

        tipeKeuanganColumn.setCellValueFactory(param -> param.getValue().getValue().tipeKeuanganProperty());
        tipeKeuanganColumn.setCellFactory(col -> Function.getWrapTreeTableCell(tipeKeuanganColumn));

        kategoriColumn.setCellValueFactory(param -> param.getValue().getValue().kategoriProperty());
        kategoriColumn.setCellFactory(col -> Function.getWrapTreeTableCell(kategoriColumn));

        deskripsiColumn.setCellValueFactory(param -> param.getValue().getValue().deskripsiProperty());
        deskripsiColumn.setCellFactory(col -> Function.getWrapTreeTableCell(deskripsiColumn));

        kodeUserColumn.setCellValueFactory(param -> param.getValue().getValue().kodeUserProperty());
        kodeUserColumn.setCellFactory(col -> Function.getWrapTreeTableCell(kodeUserColumn));

        tglKeuanganColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(new SimpleDateFormat("HH:mm").format(tglSql.parse(cellData.getValue().getValue().getTglKeuangan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglKeuanganColumn.setCellFactory(col -> Function.getWrapTreeTableCell(tglKeuanganColumn));
        tglKeuanganColumn.setComparator(Function.sortDate(new SimpleDateFormat("HH:mm")));

        jumlahRpColumn.setCellValueFactory(param -> param.getValue().getValue().jumlahRpProperty());
        jumlahRpColumn.setCellFactory(col -> Function.getTreeTableCell());

        tglMulaiPicker.setConverter(Function.getTglConverter());
        tglMulaiPicker.setValue(LocalDate.now().minusMonths(1));
        tglMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPicker));
        final ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Transaksi");
        addNew.setOnAction((ActionEvent event) -> {
            showNewKeuangan();
        });
        MenuItem addNewBebanPenjualan = new MenuItem("Add New Beban Penjualan");
        addNewBebanPenjualan.setOnAction((ActionEvent event) -> {
            showNewBebanPenjualan();
        });
        MenuItem transfer = new MenuItem("Transfer Keuangan");
        transfer.setOnAction((ActionEvent event) -> {
            showTransfer();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getKeuangan();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Transaksi") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Transfer Keuangan") && o.isStatus()) {
                rm.getItems().add(transfer);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().add(addNewBebanPenjualan);
        rm.getItems().addAll(refresh);
        keuanganTable.setContextMenu(rm);
        keuanganTable.setRowFactory(ttv -> {
            TreeTableRow<Keuangan> row = new TreeTableRow<Keuangan>() {
                @Override
                public void updateItem(Keuangan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Transaksi");
                        addNew.setOnAction((ActionEvent event) -> {
                            showNewKeuangan();
                        });
                        MenuItem addNewBebanPenjualan = new MenuItem("Add New Beban Penjualan");
                        addNewBebanPenjualan.setOnAction((ActionEvent event) -> {
                            showNewBebanPenjualan();
                        });
                        MenuItem transfer = new MenuItem("Transfer Keuangan");
                        transfer.setOnAction((ActionEvent event) -> {
                            showTransfer();
                        });
                        MenuItem lihatKeuangan = new MenuItem("Detail Keuangan");
                        lihatKeuangan.setOnAction((ActionEvent e) -> {
                            showDetailKeuangan(item);
                        });
                        MenuItem detailBebanPenjualan = new MenuItem("Detail Beban Penjualan");
                        detailBebanPenjualan.setOnAction((ActionEvent e) -> {
                            detailBebanPenjualan(item);
                        });
                        MenuItem batal = new MenuItem("Batal Transaksi");
                        batal.setOnAction(e -> {
                            batal(item);
                        });
                        MenuItem batalBebanPenjualan = new MenuItem("Batal Beban Penjualan");
                        batalBebanPenjualan.setOnAction(e -> {
                            batalBebanPenjualan(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getKeuangan();
                        });
                        Boolean status = false;
                        Boolean statusBebanPenjualan = false;
                        if (item.getNoKeuangan().startsWith("KK")) {
                            for (KategoriTransaksi k : sistem.getListKategoriTransaksi()) {
                                if (item.getKategori().equals(k.getKodeKategori())) {
                                    status = true;
                                }
                            }
                            if (item.getKategori().equals("Beban Penjualan Langsung")) {
                                statusBebanPenjualan = true;
                            }
                        }
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Transaksi") && o.isStatus()) {
                                rm.getItems().add(addNew);
                                rm.getItems().add(addNewBebanPenjualan);
                            }
                            if (o.getJenis().equals("Detail Transaksi") && o.isStatus()) {
                                if (statusBebanPenjualan) {
                                    rm.getItems().addAll(detailBebanPenjualan);
                                } else if (status) {
                                    rm.getItems().add(lihatKeuangan);
                                }
                            }
                            if (o.getJenis().equals("Batal Transaksi") && o.isStatus()) {
                                if (statusBebanPenjualan) {
                                    rm.getItems().addAll(batalBebanPenjualan);
                                } else if (status) {
                                    rm.getItems().add(batal);
                                }
                            }
                            if (o.getJenis().equals("Transfer Keuangan") && o.isStatus()) {
                                rm.getItems().add(transfer);
                            }
                            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                                rm.getItems().add(export);
                            }
                        }
                        rm.getItems().addAll(refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
        allKeuangan.addListener((ListChangeListener.Change<? extends Keuangan> change) -> {
            searchKeuangan();
        });
        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchKeuangan();
        });
        filterData.addAll(allKeuangan);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> listKeuangan = FXCollections.observableArrayList();
        for (KategoriKeuangan kk : sistem.getListKategoriKeuangan()) {
            listKeuangan.add(kk.getKodeKeuangan());
        }
        TipeKeuanganCombo.setItems(listKeuangan);
        TipeKeuanganCombo.getSelectionModel().selectFirst();
        getKeuangan();
    }

    @FXML
    private void getKeuangan() {
        Task<List<Keuangan>> task = new Task<List<Keuangan>>() {
            @Override
            public List<Keuangan> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    saldoAwal = KeuanganDAO.getSaldoAwal(con, tglMulaiPicker.getValue().toString(), TipeKeuanganCombo.getSelectionModel().getSelectedItem());
                    saldoAkhir = KeuanganDAO.getSaldoAkhir(con, tglAkhirPicker.getValue().toString(), TipeKeuanganCombo.getSelectionModel().getSelectedItem());
                    return KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            TipeKeuanganCombo.getSelectionModel().getSelectedItem(),
                            tglMulaiPicker.getValue().toString(),
                            tglAkhirPicker.getValue().toString());
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            saldoAkhirField.setText(df.format(saldoAkhir));
            allKeuangan.clear();
            allKeuangan.addAll(task.getValue());
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    private Boolean checkColumn(String column) {
        if (column != null) {
            if (column.toLowerCase().contains(searchField.getText().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void searchKeuangan() {
        try {
            filterData.clear();
            for (Keuangan temp : allKeuangan) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoKeuangan())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglKeuangan())))
                            || checkColumn(temp.getTipeKeuangan())
                            || checkColumn(temp.getKategori())
                            || checkColumn(temp.getDeskripsi())
                            || checkColumn(df.format(temp.getJumlahRp()))
                            || checkColumn(temp.getKodeUser())) {
                        filterData.add(temp);
                    }
                }
            }
            setTable();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void setTable() throws Exception {
        if (keuanganTable.getRoot() != null) {
            keuanganTable.getRoot().getChildren().clear();
        }
        List<String> tanggal = new ArrayList<>();
        for (Keuangan temp : filterData) {
            if (!tanggal.contains(temp.getTglKeuangan().substring(0, 10))) {
                tanggal.add(temp.getTglKeuangan().substring(0, 10));
            }
        }
        for (String temp : tanggal) {
            Keuangan tglKeu = new Keuangan();
            tglKeu.setNoKeuangan(tgl.format(tglBarang.parse(temp)));
            tglKeu.setJumlahRp(saldoAwal);
            TreeItem<Keuangan> parent = new TreeItem<>(tglKeu);
            for (Keuangan temp2 : filterData) {
                if (temp.equals(temp2.getTglKeuangan().substring(0, 10))) {
                    TreeItem<Keuangan> child = new TreeItem<>(temp2);
                    parent.getChildren().addAll(child);
                    saldoAwal = saldoAwal + temp2.getJumlahRp();
                }
            }
            root.getChildren().add(parent);
        }
        keuanganTable.setRoot(root);
    }

    private void showNewKeuangan() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewKeuangan.fxml");
        NewKeuanganController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setKategoriTransaksi(sistem.getListKategoriTransaksi());
        x.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(x.jumlahRpField.getText().replaceAll(",", ""))
                    || "".equals(x.jumlahRpField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah Rp masih kosong");
            } else if (x.kategoriCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kategori belum dipilih");
            } else if (x.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Double jumlahRp = Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", ""));
                            for (KategoriTransaksi k : sistem.getListKategoriTransaksi()) {
                                if (k.getKodeKategori().equals(x.kategoriCombo.getSelectionModel().getSelectedItem())) {
                                    if (k.getJenisTransaksi().equals("Beban")) {
                                        jumlahRp = jumlahRp * -1;
                                    }
                                }
                            }
                            Keuangan k = new Keuangan();
                            k.setTipeKeuangan(x.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            k.setKategori(x.kategoriCombo.getSelectionModel().getSelectedItem());
                            k.setDeskripsi(x.keteranganField.getText());
                            k.setJumlahRp(jumlahRp);
                            k.setKodeUser(sistem.getUser().getKodeUser());
                            return Service.newKeuangan(con, k);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent e) -> {
                    mainApp.closeLoading();
                    getKeuangan();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Transaksi Keuangan berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
                    }
                });
                task.setOnFailed((e) -> {
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                    mainApp.closeLoading();
                });
                new Thread(task).start();
            }
        });
    }

    private void showNewBebanPenjualan() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewBebanPenjualan.fxml");
        NewBebanPenjualanController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setNewBebanPenjualan();
        x.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(x.jumlahRpField.getText().replaceAll(",", ""))
                    || "".equals(x.jumlahRpField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah Rp masih kosong");
            } else if (x.keteranganField.getText().equals("")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Keterangan masih kosong");
            } else if (x.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else if (x.listDetail.isEmpty()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Penjualan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            BebanPenjualanHead b = new BebanPenjualanHead();
                            b.setKeterangan(x.keteranganField.getText());
                            b.setTotalBebanPenjualan(Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", "")));
                            b.setTipeKeuangan(x.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            b.setKodeUser(sistem.getUser().getKodeUser());
                            b.setTglBatal("2000-01-01 00:00:00");
                            b.setUserBatal("");
                            b.setStatus("true");
                            b.setListBebanPenjualanDetail(x.listDetail);
                            return Service.newBebanPenjualan(con, b);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent e) -> {
                    mainApp.closeLoading();
                    getKeuangan();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Beban penjualan berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
                    }
                });
                task.setOnFailed((e) -> {
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                    mainApp.closeLoading();
                });
                new Thread(task).start();
            }
        });
    }

    private void detailBebanPenjualan(Keuangan k) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, child, "View/Dialog/NewBebanPenjualan.fxml");
        NewBebanPenjualanController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, child);
        controller.setDetailBebanPenjualan(k.getDeskripsi());
    }

    private void batalBebanPenjualan(Keuangan keu) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Batal beban penjualan " + keu.getDeskripsi() + " ?");
        controller.OK.setOnAction((ActionEvent e) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        BebanPenjualanHead b = BebanPenjualanHeadDAO.get(con, keu.getDeskripsi());
                        b.setListBebanPenjualanDetail(BebanPenjualanDetailDAO.getAllByNoBeban(con, keu.getDeskripsi()));
                        return Service.batalBebanPenjualan(con, b);
                    }
                }
            };
            task.setOnRunning((ex) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((WorkerStateEvent ex) -> {
                mainApp.closeLoading();
                getKeuangan();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Batal beban penjualan berhasil disimpan");
                } else {
                    mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
                }
            });
            task.setOnFailed((ex) -> {
                task.getException().printStackTrace();
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                mainApp.closeLoading();
            });
            new Thread(task).start();
        });
    }

    private void showDetailKeuangan(Keuangan k) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewKeuangan.fxml");
        NewKeuanganController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setDetailKeuangan(k);
    }

    private void showTransfer() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/TransferKeuangan.fxml");
        TransferKeuanganController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.saveButton.setOnAction((ActionEvent ev) -> {
            if (x.dariCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kode keuangan asal transfer belum dipilih");
            } else if (x.keCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kode keuangan tujuan transfer belum dipilih");
            } else if (x.dariCombo.getSelectionModel().getSelectedItem().equals(x.keCombo.getSelectionModel().getSelectedItem())) {
                mainApp.showMessage(Modality.NONE, "Warning", "Asal dan tujuan transfer tidak boleh sama");
            } else if (x.jumlahRpField.getText().equals("0")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah Rp yang akan ditransfer masih kosong");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            return Service.transferKeuangan(
                                    con, x.dariCombo.getSelectionModel().getSelectedItem(),
                                    x.keCombo.getSelectionModel().getSelectedItem(), x.keteranganField.getText(),
                                    Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", "")));
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getKeuangan();
                    String status = task.getValue();
                    if (status.equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Transfer Keuangan berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", status);
                    }
                });
                task.setOnFailed((e) -> {
                    mainApp.closeLoading();
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                });
                new Thread(task).start();
            }
        });
    }

    private void batal(Keuangan keu) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Batal transaksi keuangan " + keu.getNoKeuangan() + "-" + keu.getKategori() + " ?");
        controller.OK.setOnAction((ActionEvent e) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.batalTransaksi(con, keu);
                    }
                }
            };
            task.setOnRunning((ex) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((WorkerStateEvent ex) -> {
                mainApp.closeLoading();
                getKeuangan();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Batal transaksi berhasil disimpan");
                } else {
                    mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
                }
            });
            task.setOnFailed((ex) -> {
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                mainApp.closeLoading();
            });
            new Thread(task).start();
        });
    }

    private void exportExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select location to export");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Document 2007", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Excel Document 1997-2007", "*.xls")
            );
            File file = fileChooser.showSaveDialog(mainApp.MainStage);
            if (file != null) {
                Workbook workbook;
                if (file.getPath().endsWith("xlsx")) {
                    workbook = new XSSFWorkbook();
                } else if (file.getPath().endsWith("xls")) {
                    workbook = new HSSFWorkbook();
                } else {
                    throw new IllegalArgumentException("The specified file is not Excel file");
                }
                Sheet sheet = workbook.createSheet("Data Keuangan");
                int rc = 0;
                int c = 5;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tanggal : "
                        + tgl.format(tglBarang.parse(tglMulaiPicker.getValue().toString())) + "-"
                        + tgl.format(tglBarang.parse(tglAkhirPicker.getValue().toString())));
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tipe Keuangan : " + TipeKeuanganCombo.getSelectionModel().getSelectedItem());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Keuangan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Keuangan");
                sheet.getRow(rc).getCell(2).setCellValue("Kategori");
                sheet.getRow(rc).getCell(3).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(4).setCellValue("Jumlah Rp");
                rc++;
                for (Keuangan b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoKeuangan());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglKeuangan())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getKategori());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getDeskripsi());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getJumlahRp());
                    rc++;
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Saldo Akhir");
                sheet.getRow(rc).getCell(4).setCellValue(saldoAkhir);
                for (int i = 0; i < c; i++) {
                    sheet.autoSizeColumn(i);
                }
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }
            }
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
            e.printStackTrace();
        }
    }
}
