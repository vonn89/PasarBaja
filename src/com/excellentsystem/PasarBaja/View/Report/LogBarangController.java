/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.LogBarangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.LogBarang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.StokBarang;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembelianController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPenjualanController;
import com.excellentsystem.PasarBaja.View.Dialog.PenyesuaianStokController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
 * @author excellent
 */
public class LogBarangController {

    @FXML
    private TableView<LogBarang> barangTable;
    @FXML
    private TableColumn<LogBarang, String> tanggalColumn;
    @FXML
    private TableColumn<LogBarang, String> kategoriColumn;
    @FXML
    private TableColumn<LogBarang, String> keteranganColumn;
    @FXML
    private TableColumn<LogBarang, Number> stokAwalColumn;
    @FXML
    private TableColumn<LogBarang, Number> stokMasukColumn;
    @FXML
    private TableColumn<LogBarang, Number> stokKeluarColumn;
    @FXML
    private TableColumn<LogBarang, Number> stokAkhirColumn;
    @FXML
    private TableColumn<LogBarang, Number> nilaiAwalColumn;
    @FXML
    private TableColumn<LogBarang, Number> nilaiMasukColumn;
    @FXML
    private TableColumn<LogBarang, Number> nilaiKeluarColumn;
    @FXML
    private TableColumn<LogBarang, Number> nilaiAkhirColumn;
    @FXML
    private Label kodeBarangLabel;
    @FXML
    private DatePicker tglAwalPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    private Stage stage;
    private Main mainApp;
    private Stage owner;
    private final ObservableList<LogBarang> allBarang = FXCollections.observableArrayList();
    private StokBarang stokBarang;

    public void initialize() {
        tanggalColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTanggal())));
            } catch (Exception ex) {
                return null;
            }
        });
        tanggalColumn.setComparator(Function.sortDate(tglLengkap));
        kategoriColumn.setCellValueFactory(cellData -> cellData.getValue().kategoriProperty());
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        stokAwalColumn.setCellValueFactory(cellData -> cellData.getValue().stokAwalProperty());
        stokAwalColumn.setCellFactory(col -> Function.getTableCell());
        stokMasukColumn.setCellValueFactory(cellData -> cellData.getValue().stokMasukProperty());
        stokMasukColumn.setCellFactory(col -> Function.getTableCell());
        stokKeluarColumn.setCellValueFactory(cellData -> cellData.getValue().stokKeluarProperty());
        stokKeluarColumn.setCellFactory(col -> Function.getTableCell());
        stokAkhirColumn.setCellValueFactory(cellData -> cellData.getValue().stokAkhirProperty());
        stokAkhirColumn.setCellFactory(col -> Function.getTableCell());
        nilaiAwalColumn.setCellValueFactory(cellData -> cellData.getValue().nilaiAwalProperty());
        nilaiAwalColumn.setCellFactory(col -> Function.getTableCell());
        nilaiMasukColumn.setCellValueFactory(cellData -> cellData.getValue().nilaiMasukProperty());
        nilaiMasukColumn.setCellFactory(col -> Function.getTableCell());
        nilaiKeluarColumn.setCellValueFactory(cellData -> cellData.getValue().nilaiKeluarProperty());
        nilaiKeluarColumn.setCellFactory(col -> Function.getTableCell());
        nilaiAkhirColumn.setCellValueFactory(cellData -> cellData.getValue().nilaiAkhirProperty());
        nilaiAkhirColumn.setCellFactory(col -> Function.getTableCell());
        tglAwalPicker.setConverter(Function.getTglConverter());
        tglAwalPicker.setValue(LocalDate.now().minusMonths(1));
        tglAwalPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglAwalPicker));
        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getLogBarang();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        barangTable.setContextMenu(rm);
        barangTable.setRowFactory(ttv -> {
            TableRow<LogBarang> row = new TableRow<LogBarang>() {
                @Override
                public void updateItem(LogBarang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        ContextMenu rm = new ContextMenu();
                        MenuItem detailPenjualan = new MenuItem("Detail Penjualan");
                        detailPenjualan.setOnAction((ActionEvent e) -> {
                            lihatDetailPenjualan(item);
                        });
                        MenuItem detailPembelian = new MenuItem("Detail Pembelian");
                        detailPembelian.setOnAction((ActionEvent e) -> {
                            lihatDetailPembelian(item);
                        });
                        MenuItem detailPenyesuaian = new MenuItem("Detail Penyesuaian Stok");
                        detailPenyesuaian.setOnAction((ActionEvent event) -> {
                            showDetailPenyesuaianStok(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getLogBarang();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Detail Penjualan") && o.isStatus()
                                    && (item.getKategori().equals("Penjualan") || item.getKategori().equals("Batal Penjualan"))) {
                                rm.getItems().add(detailPenjualan);
                            }
                            if (o.getJenis().equals("Detail Pembelian") && o.isStatus()
                                    && (item.getKategori().equals("Pembelian") || item.getKategori().equals("Batal Pembelian"))) {
                                rm.getItems().add(detailPembelian);
                            }
                        }
                        if (item.getKategori().equals("Penyesuaian Stok Barang")) {
                            rm.getItems().add(detailPenyesuaian);
                        }
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                                rm.getItems().addAll(export);
                            }
                        }
                        rm.getItems().addAll(refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        barangTable.setItems(allBarang);
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
        stage.setHeight(mainApp.screenSize.getHeight() * 0.9);
        stage.setWidth(mainApp.screenSize.getWidth() * 0.9);
        stage.setX((mainApp.screenSize.getWidth() - stage.getWidth()) / 2);
        stage.setY((mainApp.screenSize.getHeight() - stage.getHeight()) / 2);
    }

    public void setBarang(StokBarang b) {
        stokBarang = b;
        kodeBarangLabel.setText(b.getKodeBarang());
        getLogBarang();
    }

    @FXML
    private void getLogBarang() {
        Task<List<LogBarang>> task = new Task<List<LogBarang>>() {
            @Override
            public List<LogBarang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<LogBarang> allStok = LogBarangDAO.getAllByTanggalAndBarang(con,
                            tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString(),
                            stokBarang.getKodeBarang());
                    return allStok;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            mainApp.closeLoading();
            allBarang.clear();
            allBarang.addAll(task.getValue());
            allBarang.sort(Comparator.comparing(LogBarang::getTanggal));
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    private void lihatDetailPenjualan(LogBarang log) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/NewPenjualan.fxml");
        NewPenjualanController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.setDetailPenjualan(log.getKeterangan());
    }

    private void lihatDetailPembelian(LogBarang log) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/NewPembelian.fxml");
        NewPembelianController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.setDetailPembelian(log.getKeterangan());
    }

    private void showDetailPenyesuaianStok(LogBarang log) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/PenyesuaianStok.fxml");
        PenyesuaianStokController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.setPenyesuaianStokBarang(log.getKeterangan());
    }

    @FXML
    private void close() {
        mainApp.closeDialog(owner, stage);
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
                Sheet sheet = workbook.createSheet("Laporan Log Barang");
                int rc = 0;
                int c = 11;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(1).setCellValue(stokBarang.getKodeBarang());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(1).setCellValue(stokBarang.getBarang().getNamaBarang());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Satuan");
                sheet.getRow(rc).getCell(1).setCellValue(stokBarang.getBarang().getSatuan());
                rc++;

                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Tanggal");
                sheet.getRow(rc).getCell(1).setCellValue("Kategori");
                sheet.getRow(rc).getCell(2).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(3).setCellValue("Stok Awal");
                sheet.getRow(rc).getCell(4).setCellValue("Nilai Awal");
                sheet.getRow(rc).getCell(5).setCellValue("Stok Masuk");
                sheet.getRow(rc).getCell(6).setCellValue("Nilai Masuk");
                sheet.getRow(rc).getCell(7).setCellValue("Stok Keluar");
                sheet.getRow(rc).getCell(8).setCellValue("Nilai Keluar");
                sheet.getRow(rc).getCell(9).setCellValue("Stok Akhir");
                sheet.getRow(rc).getCell(10).setCellValue("Nilai Akhir");
                rc++;

                for (LogBarang s : allBarang) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(tglLengkap.format(tglSql.parse(s.getTanggal())));
                    sheet.getRow(rc).getCell(1).setCellValue(s.getKategori());
                    sheet.getRow(rc).getCell(2).setCellValue(s.getKeterangan());
                    sheet.getRow(rc).getCell(3).setCellValue(s.getStokAwal());
                    sheet.getRow(rc).getCell(4).setCellValue(s.getNilaiAwal());
                    sheet.getRow(rc).getCell(5).setCellValue(s.getStokMasuk());
                    sheet.getRow(rc).getCell(6).setCellValue(s.getNilaiMasuk());
                    sheet.getRow(rc).getCell(7).setCellValue(s.getStokKeluar());
                    sheet.getRow(rc).getCell(8).setCellValue(s.getNilaiKeluar());
                    sheet.getRow(rc).getCell(9).setCellValue(s.getStokAkhir());
                    sheet.getRow(rc).getCell(10).setCellValue(s.getNilaiAkhir());
                    rc++;
                }
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
