/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.HutangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Hutang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.Pembayaran;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailHutangController;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.NewHutangController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembayaranController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembelianController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPemesananController;
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
public class HutangController {

    @FXML
    private TableView<Hutang> hutangTable;
    @FXML
    private TableColumn<Hutang, String> noHutangColumn;
    @FXML
    private TableColumn<Hutang, String> tglHutangColumn;
    @FXML
    private TableColumn<Hutang, String> tipeHutangColumn;
    @FXML
    private TableColumn<Hutang, String> keteranganColumn;
    @FXML
    private TableColumn<Hutang, Number> jumlahHutangColumn;
    @FXML
    private TableColumn<Hutang, Number> pembayaranColumn;
    @FXML
    private TableColumn<Hutang, Number> sisaHutangColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalHutangField;
    @FXML
    private Label totalPembayaranField;
    @FXML
    private ComboBox<String> groupByCombo;
    private ObservableList<Hutang> allHutang = FXCollections.observableArrayList();
    private ObservableList<Hutang> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noHutangColumn.setCellValueFactory(cellData -> cellData.getValue().noHutangProperty());
        noHutangColumn.setCellFactory(col -> Function.getWrapTableCell(noHutangColumn));

        tipeHutangColumn.setCellValueFactory(cellData -> cellData.getValue().kategoriProperty());
        tipeHutangColumn.setCellFactory(col -> Function.getWrapTableCell(tipeHutangColumn));

        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        keteranganColumn.setCellFactory(col -> Function.getWrapTableCell(keteranganColumn));

        tglHutangColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglHutang())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglHutangColumn.setCellFactory(col -> Function.getWrapTableCell(tglHutangColumn));
        tglHutangColumn.setComparator(Function.sortDate(tglLengkap));

        jumlahHutangColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahHutangProperty());
        jumlahHutangColumn.setCellFactory(col -> Function.getTableCell());

        pembayaranColumn.setCellValueFactory(cellData -> cellData.getValue().pembayaranProperty());
        pembayaranColumn.setCellFactory(col -> Function.getTableCell());

        sisaHutangColumn.setCellValueFactory(cellData -> cellData.getValue().sisaHutangProperty());
        sisaHutangColumn.setCellFactory(col -> Function.getTableCell());

        ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Hutang");
        addNew.setOnAction((ActionEvent e) -> {
            showNewHutang();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getHutang();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Hutang") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        hutangTable.setContextMenu(rm);
        hutangTable.setRowFactory(tv -> {
            TableRow<Hutang> row = new TableRow<Hutang>() {
                @Override
                public void updateItem(Hutang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Hutang");
                        addNew.setOnAction((ActionEvent e) -> {
                            showNewHutang();
                        });
                        MenuItem lihat = new MenuItem("Detail Hutang");
                        lihat.setOnAction((ActionEvent e) -> {
                            showDetailHutang(item);
                        });
                        MenuItem lihatPembelian = new MenuItem("Detail Pembelian");
                        lihatPembelian.setOnAction((ActionEvent e) -> {
                            showDetailPembelian(item);
                        });
                        MenuItem lihatPemesanan = new MenuItem("Detail Pemesanan");
                        lihatPemesanan.setOnAction((ActionEvent e) -> {
                            showDetailPemesanan(item);
                        });
                        MenuItem bayar = new MenuItem("Pembayaran Hutang");
                        bayar.setOnAction((ActionEvent e) -> {
                            showPembayaran(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getHutang();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Hutang") && o.isStatus()) {
                                rm.getItems().add(addNew);
                            }
                            if (o.getJenis().equals("Detail Hutang") && o.isStatus()) {
                                rm.getItems().add(lihat);
                            }
                            if (o.getJenis().equals("Detail Pembelian") && o.isStatus()
                                    && item.getKategori().equals("Hutang Pembelian")) {
                                rm.getItems().add(lihatPembelian);
                            }
                            if (o.getJenis().equals("Detail Pemesanan") && o.isStatus()
                                    && item.getKategori().equals("Terima Pembayaran Down Payment")) {
                                rm.getItems().add(lihatPemesanan);
                            }
                            if (o.getJenis().equals("Pembayaran Hutang") && o.isStatus()
                                    && item.getStatus().equals("open")
                                    && !item.getKategori().equals("Terima Pembayaran Down Payment")) {
                                rm.getItems().add(bayar);
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
                            if (o.getJenis().equals("Detail Hutang") && o.isStatus()) {
                                showDetailHutang(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allHutang.addListener((ListChangeListener.Change<? extends Hutang> change) -> {
            searchHutang();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchHutang();
                });
        filterData.addAll(allHutang);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.clear();
        groupBy.add("Belum Lunas");
        groupBy.add("Lunas");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().select("Belum Lunas");
        getHutang();
        hutangTable.setItems(filterData);
    }

    @FXML
    private void getHutang() {
        Task<List<Hutang>> task = new Task<List<Hutang>>() {
            @Override
            public List<Hutang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    String status = "close";
                    if (groupByCombo.getSelectionModel().getSelectedItem().equals("Belum Lunas")) {
                        status = "open";
                    }
                    return HutangDAO.getAllByStatus(con, status);
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allHutang.clear();
            allHutang.addAll(task.getValue());
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

    private void searchHutang() {
        try {
            filterData.clear();
            for (Hutang temp : allHutang) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoHutang())
                            || checkColumn(df.format(temp.getJumlahHutang()))
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglHutang())))
                            || checkColumn(temp.getKategori())
                            || checkColumn(temp.getKeterangan())
                            || checkColumn(temp.getTipeKeuangan())
                            || checkColumn(df.format(temp.getPembayaran()))
                            || checkColumn(df.format(temp.getSisaHutang()))) {
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
        double belumTerbayar = 0;
        double sudahTerbayar = 0;
        for (Hutang temp : filterData) {
            belumTerbayar = belumTerbayar + temp.getSisaHutang();
            sudahTerbayar = sudahTerbayar + temp.getPembayaran();
        }
        totalHutangField.setText(df.format(belumTerbayar));
        totalPembayaranField.setText(df.format(sudahTerbayar));
    }

    private void showDetailPembelian(Hutang hutang) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPembelian.fxml");
        NewPembelianController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPembelian(hutang.getKeterangan());
    }

    private void showDetailPemesanan(Hutang hutang) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPemesanan.fxml");
        NewPemesananController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPemesanan(hutang.getKeterangan());
    }

    private void showDetailHutang(Hutang hutang) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailHutang.fxml");
        DetailHutangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setDetail(hutang);
        x.pembayaranHutangTable.setRowFactory((TableView<Pembayaran> tableView) -> {
            final TableRow<Pembayaran> row = new TableRow<Pembayaran>() {
                @Override
                public void updateItem(Pembayaran item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(null);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem batal = new MenuItem("Batal Pembayaran Hutang");
                        batal.setOnAction((ActionEvent e) -> {
                            batalPembayaran(item, stage);
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Batal Pembayaran Hutang") && o.isStatus()) {
                                rm.getItems().add(batal);
                            }
                        }
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
    }

    private void batalPembayaran(Pembayaran pembayaran, Stage stage) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Batal pembayaran " + pembayaran.getNoPembayaran() + " ?");
        controller.OK.setOnAction((ActionEvent e) -> {
            mainApp.closeMessage();
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.batalPembayaranHutang(con, pembayaran);
                    }
                }
            };
            task.setOnRunning((ex) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((WorkerStateEvent ex) -> {
                mainApp.closeLoading();
                getHutang();
                if (task.getValue().equals("true")) {
                    mainApp.closeDialog(mainApp.MainStage, stage);
                    mainApp.showMessage(Modality.NONE, "Success", "Data pembayaran berhasil dibatal");
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

    private void showNewHutang() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewHutang.fxml");
        NewHutangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(x.jumlahRpField.getText().replaceAll(",", "")) || "".equals(x.jumlahRpField.getText().replaceAll(",", ""))) {
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
                            Hutang h = new Hutang();
                            h.setKategori(x.kategoriCombo.getSelectionModel().getSelectedItem());
                            h.setKeterangan(x.keteranganField.getText());
                            h.setTipeKeuangan(x.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            h.setJumlahHutang(Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", "")));
                            h.setPembayaran(0);
                            h.setSisaHutang(Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", "")));
                            h.setKodeUser(sistem.getUser().getKodeUser());
                            h.setStatus("open");
                            return Service.newHutang(con, h);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getHutang();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Data hutang berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
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

    private void showPembayaran(Hutang h) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPembayaran.fxml");
        NewPembayaranController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        if (h.getKategori().equals("Hutang Pembelian")) {
            controller.setPembayaranPembelian(h.getKeterangan());
        } else {
            controller.setPembayaranHutang(h);
        }
        controller.saveButton.setOnAction((event) -> {
            double jumlahBayar = Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", ""));
            if (jumlahBayar > h.getSisaHutang()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah yang dibayar melebihi dari sisa pembayaran");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Pembayaran pembayaran = new Pembayaran();
                            pembayaran.setNoHutang(h.getNoHutang());
                            pembayaran.setJumlahPembayaran(Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", "")));
                            pembayaran.setTipeKeuangan(controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            pembayaran.setCatatan("");
                            pembayaran.setKodeUser(sistem.getUser().getKodeUser());
                            pembayaran.setTglBatal("2000-01-01 00:00:00");
                            pembayaran.setUserBatal("");
                            pembayaran.setStatus("true");
                            pembayaran.setHutang(h);
                            return Service.newPembayaranHutang(con, pembayaran);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getHutang();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Pembayaran hutang berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
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
                Sheet sheet = workbook.createSheet("Data Hutang");
                int rc = 0;
                int c = 8;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Status : " + groupByCombo.getSelectionModel().getSelectedItem());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Hutang");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Hutang");
                sheet.getRow(rc).getCell(2).setCellValue("Kategori");
                sheet.getRow(rc).getCell(3).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(4).setCellValue("Total Hutang");
                sheet.getRow(rc).getCell(5).setCellValue("Pembayaran");
                sheet.getRow(rc).getCell(6).setCellValue("Sisa Hutang");
                rc++;
                double hutang = 0;
                double pembayaran = 0;
                double sisaHutang = 0;
                for (Hutang b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoHutang());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglHutang())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getKategori());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getKeterangan());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getJumlahHutang());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getPembayaran());
                    sheet.getRow(rc).getCell(6).setCellValue(b.getSisaHutang());
                    rc++;
                    hutang = hutang + b.getJumlahHutang();
                    pembayaran = pembayaran + b.getPembayaran();
                    sisaHutang = sisaHutang + b.getSisaHutang();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(4).setCellValue(hutang);
                sheet.getRow(rc).getCell(5).setCellValue(pembayaran);
                sheet.getRow(rc).getCell(6).setCellValue(sisaHutang);
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
