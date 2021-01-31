/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanHeadDAO;
import com.excellentsystem.PasarBaja.DAO.PiutangDAO;
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
import com.excellentsystem.PasarBaja.Model.Customer;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import com.excellentsystem.PasarBaja.Model.Piutang;
import com.excellentsystem.PasarBaja.Model.TerimaPembayaran;
import com.excellentsystem.PasarBaja.PrintOut.PrintOut;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailBebanPenjualanController;
import com.excellentsystem.PasarBaja.View.Dialog.DetailPiutangController;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembayaranController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPemesananController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPenjualanController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
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
public class PenjualanController {

    @FXML
    private TableView<PenjualanHead> penjualanTable;
    @FXML
    private TableColumn<PenjualanHead, String> noPenjualanColumn;
    @FXML
    private TableColumn<PenjualanHead, String> tglPenjualanColumn;
    @FXML
    private TableColumn<PenjualanHead, String> namaCustomerColumn;
    @FXML
    private TableColumn<PenjualanHead, String> alamatCustomerColumn;
    @FXML
    private TableColumn<PenjualanHead, Number> totalPenjualanColumn;
    @FXML
    private TableColumn<PenjualanHead, Number> totalBebanPenjualanColumn;
    @FXML
    private TableColumn<PenjualanHead, Number> pembayaranColumn;
    @FXML
    private TableColumn<PenjualanHead, Number> sisaPembayaranColumn;
    @FXML
    private TableColumn<PenjualanHead, String> catatanColumn;
    @FXML
    private TableColumn<PenjualanHead, String> kodeUserColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalPenjualanField;
    @FXML
    private Label belumTerbayarField;
    @FXML
    private Label sudahTerbayarField;
    @FXML
    private DatePicker tglMulaiPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    @FXML
    private ComboBox<String> groupByCombo;
    private ObservableList<PenjualanHead> allPenjualan = FXCollections.observableArrayList();
    private ObservableList<PenjualanHead> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPenjualanColumn.setCellValueFactory(cellData -> cellData.getValue().noPenjualanProperty());
        noPenjualanColumn.setCellFactory(col -> Function.getWrapTableCell(noPenjualanColumn));

        namaCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().namaProperty());
        namaCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(namaCustomerColumn));

        alamatCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().alamatProperty());
        alamatCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(alamatCustomerColumn));

        catatanColumn.setCellValueFactory(cellData -> cellData.getValue().catatanProperty());
        catatanColumn.setCellFactory(col -> Function.getWrapTableCell(catatanColumn));

        kodeUserColumn.setCellValueFactory(cellData -> cellData.getValue().kodeUserProperty());
        kodeUserColumn.setCellFactory(col -> Function.getWrapTableCell(kodeUserColumn));

        tglPenjualanColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglPenjualan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPenjualanColumn.setCellFactory(col -> Function.getWrapTableCell(tglPenjualanColumn));
        tglPenjualanColumn.setComparator(Function.sortDate(tglLengkap));

        totalPenjualanColumn.setCellValueFactory(celldata -> celldata.getValue().totalPenjualanProperty());
        totalPenjualanColumn.setCellFactory(col -> Function.getTableCell());

        totalBebanPenjualanColumn.setCellValueFactory(celldata -> celldata.getValue().totalBebanPenjualanProperty());
        totalBebanPenjualanColumn.setCellFactory(col -> Function.getTableCell());

        pembayaranColumn.setCellValueFactory(celldata -> celldata.getValue().pembayaranProperty());
        pembayaranColumn.setCellFactory(col -> Function.getTableCell());

        sisaPembayaranColumn.setCellValueFactory(celldata -> celldata.getValue().sisaPembayaranProperty());
        sisaPembayaranColumn.setCellFactory(col -> Function.getTableCell());

        tglMulaiPicker.setConverter(Function.getTglConverter());
        tglMulaiPicker.setValue(LocalDate.now().minusMonths(1));
        tglMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPicker));

        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getPenjualan();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        penjualanTable.setContextMenu(rm);
        penjualanTable.setRowFactory((TableView<PenjualanHead> tableView) -> {
            final TableRow<PenjualanHead> row = new TableRow<PenjualanHead>() {
                @Override
                public void updateItem(PenjualanHead item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem pemesanan = new MenuItem("Detail Pemesanan");
                        pemesanan.setOnAction((ActionEvent e) -> {
                            lihatDetailPemesanan(item);
                        });
                        MenuItem detail = new MenuItem("Detail Penjualan");
                        detail.setOnAction((ActionEvent e) -> {
                            lihatDetailPenjualan(item);
                        });
                        MenuItem detailBeban = new MenuItem("Detail Beban Penjualan");
                        detailBeban.setOnAction((ActionEvent e) -> {
                            detailBebanPenjualan(item);
                        });
                        MenuItem pembayaran = new MenuItem("Detail Pembayaran Penjualan");
                        pembayaran.setOnAction((ActionEvent e) -> {
                            showDetailPiutang(item);
                        });
                        MenuItem invoice = new MenuItem("Print Invoice");
                        invoice.setOnAction((ActionEvent e) -> {
                            printInvoice(item);
                        });
                        MenuItem bayar = new MenuItem("Terima Pembayaran");
                        bayar.setOnAction((ActionEvent e) -> {
                            showPembayaran(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getPenjualan();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Detail Penjualan") && o.isStatus()) {
                                rm.getItems().add(detail);
                            }
                            if (o.getJenis().equals("Detail Transaksi") && o.isStatus()) {
                                rm.getItems().add(detailBeban);
                            }
                            if (o.getJenis().equals("Detail Pembayaran Penjualan") && o.isStatus()
                                    && item.getPembayaran() > 0) {
                                rm.getItems().add(pembayaran);
                            }
                            if (o.getJenis().equals("Detail Pemesanan") && o.isStatus()) {
                                rm.getItems().add(pemesanan);
                            }
                            if (o.getJenis().equals("Terima Pembayaran") && o.isStatus() && item.getSisaPembayaran() > 0) {
                                rm.getItems().add(bayar);
                            }
                            if (o.getJenis().equals("Print Invoice") && o.isStatus()) {
                                rm.getItems().addAll(invoice);
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
                            if (o.getJenis().equals("Detail Penjualan") && o.isStatus()) {
                                lihatDetailPenjualan(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allPenjualan.addListener((ListChangeListener.Change<? extends PenjualanHead> change) -> {
            searchPenjualan();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchPenjualan();
                });
        filterData.addAll(allPenjualan);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.clear();
        groupBy.add("Semua");
        groupBy.add("Belum Lunas");
        groupBy.add("Lunas");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().selectFirst();
        getPenjualan();
        penjualanTable.setItems(filterData);
    }

    @FXML
    private void getPenjualan() {
        Task<List<PenjualanHead>> task = new Task<List<PenjualanHead>>() {
            @Override
            public List<PenjualanHead> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<PenjualanHead> allPenjualan = PenjualanHeadDAO.getAllByDateAndStatus(con,
                            tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), "true");
                    List<Customer> allCustomer = CustomerDAO.getAllByStatus(con, "%");
                    List<PenjualanHead> listPenjualan = new ArrayList<>();
                    for (PenjualanHead p : allPenjualan) {
                        for (Customer c : allCustomer) {
                            if (p.getKodeCustomer().equals(c.getKodeCustomer())) {
                                p.setCustomer(c);
                            }
                        }
                        if (groupByCombo.getSelectionModel().getSelectedItem().equals("Semua")) {
                            listPenjualan.add(p);
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Belum Lunas") && p.getSisaPembayaran() != 0) {
                            listPenjualan.add(p);
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Lunas") && p.getSisaPembayaran() == 0) {
                            listPenjualan.add(p);
                        }

                    }
                    return listPenjualan;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allPenjualan.clear();
            allPenjualan.addAll(task.getValue());
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

    private void searchPenjualan() {
        try {
            filterData.clear();
            for (PenjualanHead temp : allPenjualan) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPenjualan())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglPenjualan())))
                            || checkColumn(temp.getCustomer().getNama())
                            || checkColumn(temp.getCustomer().getAlamat())
                            || checkColumn(df.format(temp.getTotalPenjualan()))
                            || checkColumn(df.format(temp.getPembayaran()))
                            || checkColumn(df.format(temp.getSisaPembayaran()))
                            || checkColumn(temp.getCatatan())) {
                        filterData.add(temp);
                    }
                }
            }
            hitungTotal();
        } catch (Exception e) {
            e.printStackTrace();
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void hitungTotal() {
        double total = 0;
        double terbayar = 0;
        double belumterbayar = 0;
        for (PenjualanHead temp : filterData) {
            total = total + temp.getTotalPenjualan();
            terbayar = terbayar + temp.getPembayaran();
            belumterbayar = belumterbayar + temp.getSisaPembayaran();
        }
        totalPenjualanField.setText(df.format(total));
        sudahTerbayarField.setText(df.format(terbayar));
        belumTerbayarField.setText(df.format(belumterbayar));
    }

    private void showPembayaran(PenjualanHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPembayaran.fxml");
        NewPembayaranController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setPembayaranPenjualan(p.getNoPenjualan());
        controller.saveButton.setOnAction((event) -> {
            double jumlahBayar = Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", ""));
            if (jumlahBayar > p.getSisaPembayaran()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah yang dibayar melebihi dari sisa pembayaran");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Piutang pt = PiutangDAO.getByKategoriAndKeteranganAndStatus(
                                    con, "Piutang Penjualan", p.getNoPenjualan(), "%");
                            pt.setPenjualanHead(p);
                            TerimaPembayaran t = new TerimaPembayaran();
                            t.setNoPiutang(pt.getNoPiutang());
                            t.setJumlahPembayaran(Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", "")));
                            t.setTipeKeuangan(controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            t.setCatatan("");
                            t.setKodeUser(sistem.getUser().getKodeUser());
                            t.setTglBatal("2000-01-01 00:00:00");
                            t.setUserBatal("");
                            t.setStatus("true");
                            t.setPiutang(pt);
                            return Service.newTerimaPembayaranPiutang(con, t);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getPenjualan();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Pembayaran penjualan berhasil disimpan");
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

    private void lihatDetailPemesanan(PenjualanHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPemesanan.fxml");
        NewPemesananController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPemesanan(p.getNoPemesanan());
    }

    private void lihatDetailPenjualan(PenjualanHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPenjualan.fxml");
        NewPenjualanController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPenjualan(p.getNoPenjualan());
    }

    private void detailBebanPenjualan(PenjualanHead p) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, child, "View/Dialog/DetailBebanPenjualan.fxml");
        DetailBebanPenjualanController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, child);
        controller.setDetailBebanPenjualan(p.getNoPenjualan());
    }

    private void showDetailPiutang(PenjualanHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailPiutang.fxml");
        DetailPiutangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setDetailPenjualan(p);
        x.pembayaranPiutangTable.setRowFactory((TableView<TerimaPembayaran> tableView) -> {
            final TableRow<TerimaPembayaran> row = new TableRow<TerimaPembayaran>() {
                @Override
                public void updateItem(TerimaPembayaran item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(null);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem batal = new MenuItem("Batal Terima Pembayaran");
                        batal.setOnAction((ActionEvent e) -> {
                            batalPembayaran(item, stage);
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Batal Terima Pembayaran") && o.isStatus()) {
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

    private void batalPembayaran(TerimaPembayaran pembayaran, Stage stage) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Batal pembayaran " + pembayaran.getNoTerimaPembayaran() + " ?");
        controller.OK.setOnAction((ActionEvent e) -> {
            mainApp.closeMessage();
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.batalTerimaPembayaranPiutang(con, pembayaran);
                    }
                }
            };
            task.setOnRunning((ex) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((WorkerStateEvent ex) -> {
                mainApp.closeLoading();
                getPenjualan();
                if (task.getValue().equals("true")) {
                    mainApp.closeDialog(mainApp.MainStage, stage);
                    mainApp.showMessage(Modality.NONE, "Success", "Data pembayaran berhasil dibatal");
                } else {
                    mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                }
            });
            task.setOnFailed((ex) -> {
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                mainApp.closeLoading();
            });
            new Thread(task).start();
        });
    }

    private void printInvoice(PenjualanHead p) {
        try (Connection con = Koneksi.getConnection()) {
            List<PenjualanDetail> listPenjualan = PenjualanDetailDAO.getAllPenjualanDetail(con, p.getNoPenjualan());
            for (PenjualanDetail d : listPenjualan) {
                d.setPenjualanHead(p);
            }
            PrintOut printOut = new PrintOut();
            printOut.printInvoice(listPenjualan);
        } catch (Exception e) {
            e.printStackTrace();
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
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
                Sheet sheet = workbook.createSheet("Data Penjualan");
                int rc = 0;
                int c = 8;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tanggal : "
                        + tgl.format(tglBarang.parse(tglMulaiPicker.getValue().toString())) + "-"
                        + tgl.format(tglBarang.parse(tglAkhirPicker.getValue().toString())));
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Status : " + groupByCombo.getSelectionModel().getSelectedItem());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Penjualan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Penjualan");
                sheet.getRow(rc).getCell(2).setCellValue("Customer");
                sheet.getRow(rc).getCell(3).setCellValue("Total Penjualan");
                sheet.getRow(rc).getCell(4).setCellValue("Pembayaran");
                sheet.getRow(rc).getCell(5).setCellValue("Sisa Pembayaran");
                rc++;
                double penjualan = 0;
                double pembayaran = 0;
                double sisaPembayaran = 0;
                for (PenjualanHead b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoPenjualan());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglPenjualan())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getCustomer().getNama());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getTotalPenjualan());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getPembayaran());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getSisaPembayaran());
                    rc++;
                    penjualan = penjualan + b.getTotalPenjualan();
                    pembayaran = pembayaran + b.getPembayaran();
                    sisaPembayaran = sisaPembayaran + b.getSisaPembayaran();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(5).setCellValue(penjualan);
                sheet.getRow(rc).getCell(6).setCellValue(pembayaran);
                sheet.getRow(rc).getCell(7).setCellValue(sisaPembayaran);
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
