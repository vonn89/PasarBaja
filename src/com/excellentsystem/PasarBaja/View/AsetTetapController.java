/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.AsetTetapDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.AsetTetap;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailAsetTetapController;
import com.excellentsystem.PasarBaja.View.Dialog.NewAsetTetapController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
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
public class AsetTetapController {

    @FXML
    private TableView<AsetTetap> asetTetapTable;
    @FXML
    private TableColumn<AsetTetap, String> noAsetTetapColumn;
    @FXML
    private TableColumn<AsetTetap, String> namaColumn;
    @FXML
    private TableColumn<AsetTetap, String> kategoriColumn;
    @FXML
    private TableColumn<AsetTetap, String> keteranganColumn;
    @FXML
    private TableColumn<AsetTetap, String> masaPakaiColumn;
    @FXML
    private TableColumn<AsetTetap, Number> nilaiAwalColumn;
    @FXML
    private TableColumn<AsetTetap, Number> penyusutanColumn;
    @FXML
    private TableColumn<AsetTetap, Number> nilaiAkhirColumn;
    @FXML
    private TableColumn<AsetTetap, Number> hargaJualColumn;
    @FXML
    private TableColumn<AsetTetap, String> tglBeliColumn;
    @FXML
    private TableColumn<AsetTetap, String> userBeliColumn;
    @FXML
    private TableColumn<AsetTetap, String> tglJualColumn;
    @FXML
    private TableColumn<AsetTetap, String> userJualColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalAsetTetapLabel;
    @FXML
    private Label totalPenyusutanLabel;
    @FXML
    private ComboBox<String> groupByCombo;

    private final ObservableList<AsetTetap> allAsetTetap = FXCollections.observableArrayList();
    private final ObservableList<AsetTetap> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noAsetTetapColumn.setCellValueFactory(cellData -> cellData.getValue().noAsetProperty());
        noAsetTetapColumn.setCellFactory(col -> Function.getWrapTableCell(noAsetTetapColumn));

        namaColumn.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        namaColumn.setCellFactory(col -> Function.getWrapTableCell(namaColumn));

        kategoriColumn.setCellValueFactory(cellData -> cellData.getValue().kategoriProperty());
        kategoriColumn.setCellFactory(col -> Function.getWrapTableCell(kategoriColumn));

        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        keteranganColumn.setCellFactory(col -> Function.getWrapTableCell(keteranganColumn));

        userBeliColumn.setCellValueFactory(cellData -> cellData.getValue().userBeliProperty());
        userBeliColumn.setCellFactory(col -> Function.getWrapTableCell(userBeliColumn));

        userJualColumn.setCellValueFactory(cellData -> cellData.getValue().userJualProperty());
        userJualColumn.setCellFactory(col -> Function.getWrapTableCell(userJualColumn));

        masaPakaiColumn.setCellValueFactory(cellData -> {
            String masaPakai = "-";
            if (cellData.getValue().getMasaPakai() != 0) {
                masaPakai = String.valueOf(cellData.getValue().getMasaPakai()) + " Bulan";
            }
            return new SimpleStringProperty(masaPakai);
        });
        masaPakaiColumn.setCellFactory(col -> Function.getWrapTableCell(masaPakaiColumn));

        tglBeliColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglBeli())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglBeliColumn.setCellFactory(col -> Function.getWrapTableCell(tglBeliColumn));
        tglBeliColumn.setComparator(Function.sortDate(tglLengkap));

        tglJualColumn.setCellValueFactory(cellData -> {
            try {
                String tglJual = "-";
                if (cellData.getValue().getStatus().equals("false")) {
                    tglJual = tglLengkap.format(tglSql.parse(cellData.getValue().getTglJual()));
                }
                return new SimpleStringProperty(tglJual);
            } catch (Exception ex) {
                return null;
            }
        });
        tglJualColumn.setCellFactory(col -> Function.getWrapTableCell(tglJualColumn));
        tglJualColumn.setComparator(Function.sortDate(tglLengkap));

        nilaiAwalColumn.setCellValueFactory(cellData -> cellData.getValue().nilaiAwalProperty());
        nilaiAwalColumn.setCellFactory(col -> Function.getTableCell());

        penyusutanColumn.setCellValueFactory(cellData -> cellData.getValue().penyusutanProperty());
        penyusutanColumn.setCellFactory(col -> Function.getTableCell());

        nilaiAkhirColumn.setCellValueFactory(cellData -> cellData.getValue().nilaiAkhirProperty());
        nilaiAkhirColumn.setCellFactory(col -> Function.getTableCell());

        hargaJualColumn.setCellValueFactory(cellData -> cellData.getValue().hargaJualProperty());
        hargaJualColumn.setCellFactory(col -> Function.getTableCell());

        allAsetTetap.addListener((ListChangeListener.Change<? extends AsetTetap> change) -> {
            searchAsetTetap();
        });
        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchAsetTetap();
        });
        filterData.addAll(allAsetTetap);

        final ContextMenu rm = new ContextMenu();
        MenuItem beli = new MenuItem("Pembelian Aset Tetap");
        beli.setOnAction((ActionEvent e) -> {
            showBeliAsetTetap();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getAsetTetap();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Pembelian Aset Tetap") && o.isStatus()) {
                rm.getItems().add(beli);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        asetTetapTable.setContextMenu(rm);
        asetTetapTable.setRowFactory((TableView<AsetTetap> tableView) -> {
            final TableRow<AsetTetap> row = new TableRow<AsetTetap>() {
                @Override
                public void updateItem(AsetTetap item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem beli = new MenuItem("Pembelian Aset Tetap");
                        beli.setOnAction((ActionEvent e) -> {
                            showBeliAsetTetap();
                        });
                        MenuItem jual = new MenuItem("Penjualan Aset Tetap");
                        jual.setOnAction((ActionEvent e) -> {
                            showJualAsetTetap(item);
                        });
                        MenuItem detail = new MenuItem("Detail Aset Tetap");
                        detail.setOnAction((ActionEvent e) -> {
                            showDetailAsetTetap(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getAsetTetap();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Pembelian Aset Tetap") && o.isStatus()) {
                                rm.getItems().add(beli);
                            }
                            if (o.getJenis().equals("Penjualan Aset Tetap") && o.isStatus()) {
                                rm.getItems().add(jual);
                            }
                            if (o.getJenis().equals("Detail Aset Tetap") && o.isStatus()) {
                                rm.getItems().add(detail);
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
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    if (row.getItem() != null) {
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Detail Aset Tetap") && o.isStatus()) {
                                showDetailAsetTetap(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        asetTetapTable.setItems(filterData);
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.add("Tersedia");
        groupBy.add("Terjual");
        groupBy.add("Semua");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().select("Tersedia");
        getAsetTetap();
    }

    @FXML
    private void getAsetTetap() {
        Task<List<AsetTetap>> task = new Task<List<AsetTetap>>() {
            @Override
            public List<AsetTetap> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    String status = "%";
                    if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tersedia")) {
                        status = "open";
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Terjual")) {
                        status = "close";
                    }
                    return AsetTetapDAO.getAllByStatus(con, status);
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allAsetTetap.clear();
            allAsetTetap.addAll(task.getValue());
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

    private void searchAsetTetap() {
        try {
            filterData.clear();
            for (AsetTetap temp : allAsetTetap) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoAset())
                            || checkColumn(temp.getNama())
                            || checkColumn(temp.getKategori())
                            || checkColumn(temp.getKeterangan())
                            || checkColumn(String.valueOf(temp.getMasaPakai()))
                            || checkColumn(df.format(temp.getNilaiAwal()))
                            || checkColumn(df.format(temp.getPenyusutan()))
                            || checkColumn(df.format(temp.getNilaiAkhir()))
                            || checkColumn(df.format(temp.getHargaJual()))
                            || checkColumn(temp.getUserBeli())
                            || checkColumn(temp.getUserJual())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglBeli())))
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglJual())))) {
                        filterData.add(temp);
                    }
                }
            }
            hitungTotal();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void hitungTotal() {
        double totalAsetTetap = 0;
        double totalPenyusutan = 0;
        for (AsetTetap temp : filterData) {
            totalAsetTetap = totalAsetTetap + temp.getNilaiAkhir();
            totalPenyusutan = totalPenyusutan + temp.getPenyusutan();
        }
        totalAsetTetapLabel.setText(df.format(totalAsetTetap));
        totalPenyusutanLabel.setText(df.format(totalPenyusutan));
    }

    private void showBeliAsetTetap() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewAsetTetap.fxml");
        NewAsetTetapController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(controller.hargaField.getText().replaceAll(",", ""))
                    || "".equals(controller.hargaField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Harga beli masih kosong");
            } else if (controller.kategoriCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kategori belum dipilih");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            int masaPakai = (Integer.parseInt(controller.tahunField.getText()) * 12) + Integer.parseInt(controller.bulanField.getText());
                            AsetTetap asetTetap = new AsetTetap();
                            asetTetap.setNama(controller.namaField.getText());
                            asetTetap.setKategori(controller.kategoriCombo.getSelectionModel().getSelectedItem());
                            asetTetap.setKeterangan(controller.keteranganField.getText());
                            asetTetap.setMasaPakai(masaPakai);
                            asetTetap.setNilaiAwal(Double.parseDouble(controller.hargaField.getText().replaceAll(",", "")));
                            asetTetap.setPenyusutan(0);
                            asetTetap.setNilaiAkhir(Double.parseDouble(controller.hargaField.getText().replaceAll(",", "")));
                            asetTetap.setHargaJual(0);
                            asetTetap.setStatus("open");
                            asetTetap.setTglJual("2000-01-01 00:00:00");
                            asetTetap.setUserJual("");
                            asetTetap.setUserBeli(sistem.getUser().getKodeUser());
                            return Service.pembelianAsetTetap(con, asetTetap,
                                    controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    try {
                        mainApp.closeLoading();
                        getAsetTetap();
                        if (task.getValue().equals("true")) {
                            mainApp.showMessage(Modality.NONE, "Success", "Pembelian Aset Tetap berhasil disimpan");
                            mainApp.closeDialog(mainApp.MainStage, stage);
                        } else {
                            mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                        }
                    } catch (Exception ex) {
                        mainApp.showMessage(Modality.NONE, "Error", ex.toString());
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

    private void showJualAsetTetap(AsetTetap aset) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewAsetTetap.fxml");
        NewAsetTetapController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setPenjualanAset(aset);
        controller.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(controller.hargaField.getText().replaceAll(",", ""))
                    || "".equals(controller.hargaField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Harga jual masih kosong");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            aset.setHargaJual(Double.parseDouble(controller.hargaField.getText().replaceAll(",", "")));
                            aset.setStatus("close");
                            aset.setUserJual(sistem.getUser().getKodeUser());
                            return Service.penjualanAsetTetap(con, aset,
                                    controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent e) -> {
                    try {
                        mainApp.closeLoading();
                        getAsetTetap();
                        if (task.getValue().equals("true")) {
                            mainApp.showMessage(Modality.NONE, "Success", "Penjualan aset tetap berhasil disimpan");
                            mainApp.closeDialog(mainApp.MainStage, stage);
                        } else {
                            mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                        }
                    } catch (Exception ex) {
                        mainApp.showMessage(Modality.NONE, "Error", ex.toString());
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

    private void showDetailAsetTetap(AsetTetap aset) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailAsetTetap.fxml");
        DetailAsetTetapController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setDetail(aset);
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

                Sheet sheet = workbook.createSheet("Data Aset Tetap");
                int rc = 0;
                int c = 11;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Status : " + groupByCombo.getSelectionModel().getSelectedItem());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Aset");
                sheet.getRow(rc).getCell(1).setCellValue("Nama");
                sheet.getRow(rc).getCell(2).setCellValue("Kategori");
                sheet.getRow(rc).getCell(3).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(4).setCellValue("Masa Pakai");
                sheet.getRow(rc).getCell(5).setCellValue("Nilai Awal");
                sheet.getRow(rc).getCell(6).setCellValue("Penyusutan");
                sheet.getRow(rc).getCell(7).setCellValue("Nilai Akhir");
                sheet.getRow(rc).getCell(8).setCellValue("Harga Jual");
                sheet.getRow(rc).getCell(9).setCellValue("Tgl Beli");
                sheet.getRow(rc).getCell(10).setCellValue("Tgl Jual");
                rc++;
                double nilaiAwal = 0;
                double penyusutan = 0;
                double nilaiAkhir = 0;
                double hargaJual = 0;
                for (AsetTetap s : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(s.getNoAset());
                    sheet.getRow(rc).getCell(1).setCellValue(s.getNama());
                    sheet.getRow(rc).getCell(3).setCellValue(s.getKategori());
                    sheet.getRow(rc).getCell(2).setCellValue(s.getKeterangan());
                    sheet.getRow(rc).getCell(4).setCellValue(s.getMasaPakai());
                    sheet.getRow(rc).getCell(5).setCellValue(s.getNilaiAwal());
                    sheet.getRow(rc).getCell(6).setCellValue(s.getPenyusutan());
                    sheet.getRow(rc).getCell(7).setCellValue(s.getNilaiAkhir());
                    sheet.getRow(rc).getCell(8).setCellValue(s.getHargaJual());
                    sheet.getRow(rc).getCell(9).setCellValue(tglLengkap.format(tglSql.parse(s.getTglBeli())));
                    if ("false".equals(s.getStatus())) {
                        sheet.getRow(rc).getCell(10).setCellValue(s.getTglJual());
                    }
                    rc++;

                    nilaiAwal = nilaiAwal + s.getNilaiAwal();
                    penyusutan = penyusutan + s.getPenyusutan();
                    nilaiAkhir = nilaiAkhir + s.getNilaiAkhir();
                    hargaJual = hargaJual + s.getHargaJual();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(5).setCellValue(nilaiAwal);
                sheet.getRow(rc).getCell(6).setCellValue(penyusutan);
                sheet.getRow(rc).getCell(7).setCellValue(nilaiAkhir);
                sheet.getRow(rc).getCell(8).setCellValue(hargaJual);
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
