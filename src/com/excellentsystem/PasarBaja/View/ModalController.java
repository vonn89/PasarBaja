/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.KeuanganDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.NewModalController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.time.LocalDate;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
public class ModalController {

    @FXML
    TableView<Keuangan> modalTable;
    @FXML
    private TableColumn<Keuangan, String> noPerubahanModalColumn;
    @FXML
    private TableColumn<Keuangan, String> tglPerubahanModalColumn;
    @FXML
    private TableColumn<Keuangan, String> kategoriColumn;
    @FXML
    private TableColumn<Keuangan, String> deskripsiColumn;
    @FXML
    private TableColumn<Keuangan, Number> jumlahRpColumn;
    @FXML
    private TableColumn<Keuangan, String> kodeUserColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label modalAkhirField;
    @FXML
    private DatePicker tglMulaiPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    private Double modalAkhir;
    private Main mainApp;
    private ObservableList<Keuangan> allKeuangan = FXCollections.observableArrayList();
    private ObservableList<Keuangan> filterData = FXCollections.observableArrayList();

    public void initialize() {
        noPerubahanModalColumn.setCellValueFactory(cellData -> cellData.getValue().noKeuanganProperty());
        noPerubahanModalColumn.setCellFactory(col -> Function.getWrapTableCell(noPerubahanModalColumn));

        tglPerubahanModalColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglKeuangan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPerubahanModalColumn.setCellFactory(col -> Function.getWrapTableCell(tglPerubahanModalColumn));
        tglPerubahanModalColumn.setComparator(Function.sortDate(tglLengkap));

        kategoriColumn.setCellValueFactory(cellData -> cellData.getValue().kategoriProperty());
        kategoriColumn.setCellFactory(col -> Function.getWrapTableCell(kategoriColumn));

        deskripsiColumn.setCellValueFactory(cellData -> cellData.getValue().deskripsiProperty());
        deskripsiColumn.setCellFactory(col -> Function.getWrapTableCell(deskripsiColumn));

        kodeUserColumn.setCellValueFactory(cellData -> cellData.getValue().kodeUserProperty());
        kodeUserColumn.setCellFactory(col -> Function.getWrapTableCell(kodeUserColumn));

        jumlahRpColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahRpProperty());
        jumlahRpColumn.setCellFactory(col -> Function.getTableCell());

        tglMulaiPicker.setConverter(Function.getTglConverter());
        tglMulaiPicker.setValue(LocalDate.now().minusMonths(1));
        tglMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPicker));
        final ContextMenu rm = new ContextMenu();
        MenuItem tambah = new MenuItem("Tambah Modal");
        tambah.setOnAction((ActionEvent event) -> {
            showTambahModal();
        });
        MenuItem ambil = new MenuItem("Ambil Modal");
        ambil.setOnAction((ActionEvent event) -> {
            showAmbilModal();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getModal();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Tambah Modal") && o.isStatus()) {
                rm.getItems().add(tambah);
            }
            if (o.getJenis().equals("Ambil Modal") && o.isStatus()) {
                rm.getItems().add(ambil);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        modalTable.setContextMenu(rm);
        modalTable.setRowFactory(ttv -> {
            TableRow<Keuangan> row = new TableRow<Keuangan>() {
                @Override
                public void updateItem(Keuangan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        ContextMenu rm = new ContextMenu();
                        MenuItem tambah = new MenuItem("Tambah Modal");
                        tambah.setOnAction((ActionEvent event) -> {
                            showTambahModal();
                        });
                        MenuItem ambil = new MenuItem("Ambil Modal");
                        ambil.setOnAction((ActionEvent event) -> {
                            showAmbilModal();
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getModal();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Tambah Modal") && o.isStatus()) {
                                rm.getItems().add(tambah);
                            }
                            if (o.getJenis().equals("Ambil Modal") && o.isStatus()) {
                                rm.getItems().add(ambil);
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
            searchModal();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchModal();
                });
        filterData.addAll(allKeuangan);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        getModal();
        modalTable.setItems(filterData);
    }

    @FXML
    private void getModal() {
        Task<List<Keuangan>> task = new Task<List<Keuangan>>() {
            @Override
            public List<Keuangan> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    modalAkhir = KeuanganDAO.getSaldoAkhir(con, tglAkhirPicker.getValue().toString(), "Modal");
                    List<Keuangan> modal = KeuanganDAO.getAllByTipeKeuanganAndTanggal(con, "Modal",
                            tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString());
                    List<Keuangan> keuanganBefore = KeuanganDAO.getAllByTanggal(
                            con, "", tglAkhirPicker.getValue().toString());
                    double ur = 0;
                    for (Keuangan k : keuanganBefore) {
                        if (k.getTipeKeuangan().equals("Penjualan")) {
                            ur = ur + k.getJumlahRp();
                        }
                        if (k.getTipeKeuangan().equals("Pendapatan/Beban")) {
                            ur = ur + k.getJumlahRp();
                        }
                        if (k.getTipeKeuangan().equals("HPP")) {
                            ur = ur - k.getJumlahRp();
                        }
                        if (k.getTipeKeuangan().equals("Retur Penjualan")) {
                            ur = ur - k.getJumlahRp();
                        }
                    }
                    modalAkhir = modalAkhir + ur;
                    Keuangan k = new Keuangan();
                    k.setNoKeuangan("KK-0001");
                    k.setTglKeuangan(tglSql.format(Function.getServerDate(con)));
                    k.setTipeKeuangan("Modal");
                    k.setKategori("Untung/Rugi");
                    k.setDeskripsi("");
                    k.setJumlahRp(ur);
                    k.setKodeUser("System");
                    modal.add(k);
                    return modal;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allKeuangan.clear();
            allKeuangan.addAll(task.getValue());
            modalAkhirField.setText(df.format(modalAkhir));
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

    private void searchModal() {
        try {
            filterData.clear();
            for (Keuangan temp : allKeuangan) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoKeuangan())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglKeuangan())))
                            || checkColumn(temp.getKategori())
                            || checkColumn(temp.getDeskripsi())
                            || checkColumn(df.format(temp.getJumlahRp()))
                            || checkColumn(temp.getKodeUser())) {
                        filterData.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void showTambahModal() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewModal.fxml");
        NewModalController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setTitle("Tambah Modal");
        controller.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(controller.jumlahRpField.getText().replaceAll(",", ""))
                    || "".equals(controller.jumlahRpField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah Rp masih kosong");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Keuangan k = new Keuangan();
                            k.setTipeKeuangan(controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            k.setKategori("Tambah Modal");
                            k.setDeskripsi(controller.keteranganField.getText());
                            k.setJumlahRp(Double.parseDouble(controller.jumlahRpField.getText().replaceAll(",", "")));
                            k.setKodeUser(sistem.getUser().getKodeUser());
                            return Service.newModal(con, k);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent e) -> {
                    mainApp.closeLoading();
                    getModal();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Penambahan modal berhasil disimpan");
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

    private void showAmbilModal() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewModal.fxml");
        NewModalController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setTitle("Ambil Modal");
        controller.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(controller.jumlahRpField.getText().replaceAll(",", ""))
                    || "".equals(controller.jumlahRpField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah Rp masih kosong");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Keuangan modal = new Keuangan();
                            modal.setTipeKeuangan(controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            modal.setKategori("Ambil Modal");
                            modal.setDeskripsi(controller.keteranganField.getText());
                            modal.setJumlahRp(Double.parseDouble(controller.jumlahRpField.getText().replaceAll(",", "")) * -1);
                            modal.setKodeUser(sistem.getUser().getKodeUser());
                            return Service.newModal(con, modal);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent e) -> {
                    mainApp.closeLoading();
                    getModal();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Pengambilan modal berhasil disimpan");
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
                Sheet sheet = workbook.createSheet("Data Modal");
                int rc = 0;
                int c = 5;
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
                double jumlahRp = 0;
                for (Keuangan b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoKeuangan());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglKeuangan())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getKategori());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getDeskripsi());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getJumlahRp());
                    rc++;
                    jumlahRp = jumlahRp + b.getJumlahRp();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(4).setCellValue(jumlahRp);
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
