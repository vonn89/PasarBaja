/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.DAO.LogBarangDAO;
import com.excellentsystem.PasarBaja.DAO.StokBarangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tgl;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.LogBarang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PenyesuaianStokBarang;
import com.excellentsystem.PasarBaja.Model.StokBarang;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.PenyesuaianStokController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
public class LaporanBarangController {

    @FXML
    private TableView<StokBarang> barangTable;
    @FXML
    private TableColumn<StokBarang, String> kodeBarangColumn;
    @FXML
    private TableColumn<StokBarang, String> namaBarangColumn;
    @FXML
    private TableColumn<StokBarang, String> satuanColumn;
    @FXML
    private TableColumn<StokBarang, Number> hargaJualColumn;
    @FXML
    private TableColumn<StokBarang, Number> nilaiColumn;
    @FXML
    private TableColumn<StokBarang, Number> mutasiAkhirColumn;

    @FXML
    private TextField searchField;
    @FXML
    private DatePicker tglStokPicker;
    @FXML
    private Label totalQtyField;
    @FXML
    private Label totalNilaiField;
    @FXML
    private Label totalJualField;

    private ObservableList<StokBarang> allBarang = FXCollections.observableArrayList();
    private ObservableList<StokBarang> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().namaBarangProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().satuanProperty());
        hargaJualColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().hargaJualProperty());
        nilaiColumn.setCellValueFactory(cellData -> {
            double nilai = 0;
            if (cellData.getValue().getLogBarang().getStokAkhir() != 0) {
                nilai = cellData.getValue().getLogBarang().getNilaiAkhir()
                        / cellData.getValue().getLogBarang().getStokAkhir();
            }
            return new SimpleDoubleProperty(nilai);
        });
        hargaJualColumn.setCellFactory(col -> Function.getTableCell());
        nilaiColumn.setCellFactory(col -> Function.getTableCell());
        mutasiAkhirColumn.setCellValueFactory(cellData -> cellData.getValue().stokAkhirProperty());
        mutasiAkhirColumn.setCellFactory(col -> Function.getTableCell());

        tglStokPicker.setConverter(Function.getTglConverter());
        tglStokPicker.setValue(LocalDate.now());
        tglStokPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellDisableAfter(LocalDate.now()));
        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getBarang();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        barangTable.setContextMenu(rm);
        barangTable.setRowFactory((TableView<StokBarang> tableView) -> {
            final TableRow<StokBarang> row = new TableRow<StokBarang>() {
                @Override
                public void updateItem(StokBarang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem kartu = new MenuItem("Lihat Kartu Stok");
                        kartu.setOnAction((ActionEvent e) -> {
                            showKartuStok(item);
                        });
                        MenuItem logBarang = new MenuItem("Lihat Log Barang");
                        logBarang.setOnAction((ActionEvent e) -> {
                            showLogBarang(item);
                        });
                        MenuItem penyesuaian = new MenuItem("Penyesuaian Stok Barang");
                        penyesuaian.setOnAction((ActionEvent e) -> {
                            showPenyesuaianStok(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getBarang();
                        });
                        rm.getItems().addAll(kartu, logBarang);
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Penyesuaian Stok Barang") && o.isStatus()) {
                                rm.getItems().addAll(penyesuaian);
                            }
                            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                                rm.getItems().addAll(export);
                            }
                        }
                        rm.getItems().addAll(refresh);
                        setContextMenu(rm);
                    }
                }
            };
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    if (row.getItem() != null) {
                        showLogBarang(row.getItem());
                    }
                }
            });
            return row;
        });

        allBarang.addListener((ListChangeListener.Change<? extends StokBarang> change) -> {
            searchBarang();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchBarang();
                });
        filterData.addAll(allBarang);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        barangTable.setItems(filterData);
        getBarang();
    }

    public void setTanggal(LocalDate tglAkhir) {
        tglStokPicker.setValue(tglAkhir);
        getBarang();
    }

    @FXML
    private void getBarang() {
        Task<List<StokBarang>> task = new Task<List<StokBarang>>() {
            @Override
            public List<StokBarang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<Barang> listBarang = BarangDAO.getAllByStatus(con, "true");
                    List<LogBarang> listLogBarang = LogBarangDAO.getAllByTanggal(con, tglStokPicker.getValue().toString());
                    List<StokBarang> temp = StokBarangDAO.getAllByTanggal(con, tglStokPicker.getValue().toString());
                    List<StokBarang> listStok = new ArrayList<>();
                    for (StokBarang s : temp) {
                        for (Barang b : listBarang) {
                            if (b.getKodeBarang().equals(s.getKodeBarang())) {
                                s.setBarang(b);
                            }
                        }
                        for (LogBarang b : listLogBarang) {
                            if (b.getKodeBarang().equals(s.getKodeBarang())) {
                                s.setLogBarang(b);
                            }
                        }
                        if (s.getLogBarang() == null) {
                            LogBarang l = new LogBarang();
                            l.setTanggal(tglStokPicker.getValue().toString());
                            l.setKodeBarang(s.getKodeBarang());
                            l.setKategori("");
                            l.setKeterangan("");
                            l.setStokAwal(0);
                            l.setNilaiAwal(0);
                            l.setStokMasuk(0);
                            l.setNilaiMasuk(0);
                            l.setStokKeluar(0);
                            l.setNilaiKeluar(0);
                            l.setStokAkhir(0);
                            l.setNilaiAkhir(0);
                            s.setLogBarang(l);
                        }
                        if (s.getBarang() != null) {
                            listStok.add(s);
                        }
                    }
                    return listStok;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allBarang.clear();
            allBarang.addAll(task.getValue());
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

    private void searchBarang() {
        filterData.clear();
        for (StokBarang temp : allBarang) {
            if (searchField.getText() == null || searchField.getText().equals("")) {
                filterData.add(temp);
            } else {
                if (checkColumn(temp.getKodeBarang())
                        || checkColumn(temp.getBarang().getNamaBarang())
                        || checkColumn(temp.getBarang().getSatuan())
                        || checkColumn(df.format(temp.getBarang().getBerat()))
                        || checkColumn(df.format(temp.getBarang().getHargaJual()))) {
                    filterData.add(temp);
                }
            }
        }
        hitungTotal();
    }

    private void hitungTotal() {
        double totalQty = 0;
        double totalHPP = 0;
        double totalJual = 0;
        for (StokBarang temp : filterData) {
            totalQty = totalQty + temp.getStokAkhir();
            totalHPP = totalHPP + temp.getLogBarang().getNilaiAkhir();
            totalJual = totalJual + (temp.getBarang().getHargaJual() * temp.getStokAkhir());
        }
        totalQtyField.setText(df.format(totalQty));
        totalNilaiField.setText(df.format(totalHPP));
        totalJualField.setText(df.format(totalJual));
    }

    private void showLogBarang(StokBarang s) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/LogBarang.fxml");
        LogBarangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setBarang(s);
    }

    private void showKartuStok(StokBarang b) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/LaporanKartuStokBarang.fxml");
        LaporanKartuStokBarangController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setBarang(b);
    }

    private void showPenyesuaianStok(StokBarang s) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/PenyesuaianStok.fxml");
        PenyesuaianStokController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setBarang(s.getKodeBarang());
        x.saveButton.setOnAction((event) -> {
            if (Double.parseDouble(x.qtyField.getText().replaceAll(",", "")) <= 0) {
                mainApp.showMessage(Modality.NONE, "Warning", "Qty tidak boleh kosong");
            } else if (x.statusCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Status penyesuaian stok belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            double qty = -Double.parseDouble(x.qtyField.getText().replaceAll(",", ""));
                            if (x.statusCombo.getSelectionModel().getSelectedItem().equals("Penambahan Stok")) {
                                qty = Double.parseDouble(x.qtyField.getText().replaceAll(",", ""));
                            }
                            PenyesuaianStokBarang p = new PenyesuaianStokBarang();
                            p.setKodeBarang(s.getKodeBarang());
                            p.setQty(qty);
                            p.setNilai(0);
                            p.setCatatan(x.catatanField.getText());
                            p.setKodeUser(sistem.getUser().getKodeUser());
                            p.setStatus("true");
                            return Service.savePenyesuaianStokBarang(con, p);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent e) -> {
                    mainApp.closeLoading();
                    getBarang();
                    String status = task.getValue();
                    if (status.equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Penyesuaian Stok Barang berhasil disimpan");
                        mainApp.closeDialog(mainApp.MainStage, stage);
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", status);
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
                Sheet sheet = workbook.createSheet("Laporan Barang");
                int rc = 0;
                int c = 6;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tanggal : "
                        + tgl.format(tglBarang.parse(tglStokPicker.getValue().toString())));
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(1).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(2).setCellValue("Satuan");
                sheet.getRow(rc).getCell(3).setCellValue("Nilai Pokok");
                sheet.getRow(rc).getCell(4).setCellValue("Harga Jual");
                sheet.getRow(rc).getCell(5).setCellValue("Stok Akhir");
                rc++;
                double totalStok = 0;
                for (StokBarang b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getKodeBarang());
                    sheet.getRow(rc).getCell(1).setCellValue(b.getBarang().getNamaBarang());
                    sheet.getRow(rc).getCell(2).setCellValue(b.getBarang().getSatuan());
                    double nilai = 0;
                    if (b.getLogBarang().getStokAkhir() != 0) {
                        nilai = b.getLogBarang().getNilaiAkhir() / b.getLogBarang().getStokAkhir();
                    }
                    sheet.getRow(rc).getCell(3).setCellValue(nilai);
                    sheet.getRow(rc).getCell(4).setCellValue(b.getBarang().getHargaJual());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getStokAkhir());
                    rc++;
                    totalStok = totalStok + b.getStokAkhir();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total");
                sheet.getRow(rc).getCell(5).setCellValue(totalStok);
                rc++;
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
