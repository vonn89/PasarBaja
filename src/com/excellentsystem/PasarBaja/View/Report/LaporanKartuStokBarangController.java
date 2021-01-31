/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.StokBarangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tgl;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.StokBarang;
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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
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
 * @author Xtreme
 */
public class LaporanKartuStokBarangController {

    @FXML
    private TableView<StokBarang> barangTable;
    @FXML
    private TableColumn<StokBarang, String> kodeBarangColumn;
    @FXML
    private TableColumn<StokBarang, String> namaBarangColumn;
    @FXML
    private TableColumn<StokBarang, String> satuanColumn;

    @FXML
    private TableColumn<StokBarang, String> tanggalColumn;
    @FXML
    private TableColumn<StokBarang, Number> mutasiAwalColumn;
    @FXML
    private TableColumn<StokBarang, Number> mutasiInColumn;
    @FXML
    private TableColumn<StokBarang, Number> mutasiOutColumn;
    @FXML
    private TableColumn<StokBarang, Number> mutasiAkhirColumn;

    @FXML
    private Label kodeBarangLabel;
    @FXML
    private DatePicker tglAwalPicker;
    @FXML
    private DatePicker tglAkhirPicker;

    private ObservableList<StokBarang> allBarang = FXCollections.observableArrayList();
    private StokBarang stokBarang;
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().namaBarangProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().satuanProperty());
        tanggalColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tgl.format(tglBarang.parse(cellData.getValue().getTanggal())));
            } catch (Exception ex) {
                return null;
            }
        });
        tanggalColumn.setComparator(Function.sortDate(tgl));
        mutasiAwalColumn.setCellValueFactory(cellData -> cellData.getValue().stokAwalProperty());
        mutasiAwalColumn.setCellFactory(col -> Function.getTableCell());
        mutasiInColumn.setCellValueFactory(cellData -> cellData.getValue().stokMasukProperty());
        mutasiInColumn.setCellFactory(col -> Function.getTableCell());
        mutasiOutColumn.setCellValueFactory(cellData -> cellData.getValue().stokKeluarProperty());
        mutasiOutColumn.setCellFactory(col -> Function.getTableCell());
        mutasiAkhirColumn.setCellValueFactory(cellData -> cellData.getValue().stokAkhirProperty());
        mutasiAkhirColumn.setCellFactory(col -> Function.getTableCell());

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
            getBarang();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        barangTable.setContextMenu(rm);
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

    public void setBarang(StokBarang s) {
        stokBarang = s;
        kodeBarangLabel.setText(s.getKodeBarang());
        getBarang();
    }

    @FXML
    private void getBarang() {
        Task<List<StokBarang>> task = new Task<List<StokBarang>>() {
            @Override
            public List<StokBarang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<StokBarang> temp = StokBarangDAO.getAllByTanggalAndBarang(con,
                            tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString(),
                            stokBarang.getKodeBarang());
                    for (StokBarang stok : temp) {
                        stok.setBarang(stokBarang.getBarang());
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
            allBarang.sort(Comparator.comparing(StokBarang::getTanggal));
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
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
                Sheet sheet = workbook.createSheet("Laporan Kartu Stok Barang");
                int rc = 0;
                int c = 5;
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
                sheet.getRow(rc).getCell(1).setCellValue("Stok Awal");
                sheet.getRow(rc).getCell(2).setCellValue("Stok Masuk");
                sheet.getRow(rc).getCell(3).setCellValue("Stok Keluar");
                sheet.getRow(rc).getCell(4).setCellValue("Stok Akhir");
                rc++;

                for (StokBarang s : allBarang) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(tgl.format(tglBarang.parse(s.getTanggal())));
                    sheet.getRow(rc).getCell(1).setCellValue(s.getStokAwal());
                    sheet.getRow(rc).getCell(2).setCellValue(s.getStokMasuk());
                    sheet.getRow(rc).getCell(3).setCellValue(s.getStokKeluar());
                    sheet.getRow(rc).getCell(4).setCellValue(s.getStokAkhir());
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
