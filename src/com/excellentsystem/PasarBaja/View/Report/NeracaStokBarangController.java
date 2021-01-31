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
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.LogBarang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.StokBarang;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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
public class NeracaStokBarangController {

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
    private Label totalQtyField;
    @FXML
    private Label totalNilaiField;
    @FXML
    private Label totalJualField;

    private ObservableList<StokBarang> allBarang = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage owner;
    private Stage stage;

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

        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
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
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                        });
                        rm.getItems().addAll(kartu, logBarang);
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
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    if (row.getItem() != null) {
                        showLogBarang(row.getItem());
                    }
                }
            });
            return row;
        });
        barangTable.setItems(allBarang);
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
        stage.setHeight(mainApp.screenSize.getHeight() * 0.9);
        stage.setWidth(mainApp.screenSize.getWidth() * 0.9);
        stage.setX((mainApp.screenSize.getWidth() - stage.getWidth()) / 2);
        stage.setY((mainApp.screenSize.getHeight() - stage.getHeight()) / 2);
    }

    public void getBarang(LocalDate tglAkhir) {
        Task<List<StokBarang>> task = new Task<List<StokBarang>>() {
            @Override
            public List<StokBarang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<Barang> listBarang = BarangDAO.getAllByStatus(con, "%");
                    List<LogBarang> listLogBarang = LogBarangDAO.getAllByTanggal(con, tglAkhir.toString());
                    List<StokBarang> temp = StokBarangDAO.getAllByTanggal(con, tglAkhir.toString());
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
                            l.setTanggal(tglAkhir.toString());
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
                    }
                    return temp;
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
            hitungTotal();
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    private void hitungTotal() {
        double totalQty = 0;
        double totalHPP = 0;
        double totalJual = 0;
        for (StokBarang temp : allBarang) {
            totalQty = totalQty + temp.getStokAkhir();
            totalHPP = totalHPP + temp.getLogBarang().getNilaiAkhir();
            totalJual = totalJual + (temp.getBarang().getHargaJual() * temp.getStokAkhir());
        }
        totalQtyField.setText(df.format(totalQty));
        totalNilaiField.setText(df.format(totalHPP));
        totalJualField.setText(df.format(totalJual));
    }

    private void showLogBarang(StokBarang s) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Report/LogBarang.fxml");
        LogBarangController x = loader.getController();
        x.setMainApp(mainApp, stage, child);
        x.setBarang(s);
    }

    private void showKartuStok(StokBarang b) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Report/LaporanKartuStokBarang.fxml");
        LaporanKartuStokBarangController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.setBarang(b);
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
                Sheet sheet = workbook.createSheet("Laporan Neraca - Stok Barang");
                int rc = 0;
                int c = 6;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(1).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(2).setCellValue("Satuan");
                sheet.getRow(rc).getCell(3).setCellValue("Nilai Pokok");
                sheet.getRow(rc).getCell(4).setCellValue("Harga Jual");
                sheet.getRow(rc).getCell(5).setCellValue("Stok Akhir");
                rc++;
                double totalStok = 0;
                for (StokBarang b : allBarang) {
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
                sheet.getRow(rc).getCell(4).setCellValue("Total");
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
