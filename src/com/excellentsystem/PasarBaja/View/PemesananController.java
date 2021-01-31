/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.PemesananDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PemesananHeadDAO;
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
import com.excellentsystem.PasarBaja.Model.Hutang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import com.excellentsystem.PasarBaja.Model.PemesananHead;
import com.excellentsystem.PasarBaja.PrintOut.PrintOut;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailPembayaranDownPaymentController;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembayaranController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPemesananController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
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
public class PemesananController {

    @FXML
    private TableView<PemesananHead> pemesananTable;
    @FXML
    private TableColumn<PemesananHead, String> noPemesananColumn;
    @FXML
    private TableColumn<PemesananHead, String> tglPemesananColumn;
    @FXML
    private TableColumn<PemesananHead, String> namaCustomerColumn;
    @FXML
    private TableColumn<PemesananHead, String> alamatCustomerColumn;
    @FXML
    private TableColumn<PemesananHead, Number> totalPemesananColumn;
    @FXML
    private TableColumn<PemesananHead, Number> sisaPemesananColumn;
    @FXML
    private TableColumn<PemesananHead, Number> downPaymentColumn;
    @FXML
    private TableColumn<PemesananHead, Number> sisaDownPaymentColumn;
    @FXML
    private TableColumn<PemesananHead, String> statusColumn;
    @FXML
    private TableColumn<PemesananHead, String> catatanColumn;
    @FXML
    private TableColumn<PemesananHead, String> kodeUserColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalPemesananField;
    @FXML
    private Label sisaPemesananField;
    @FXML
    private Label totalDownPaymentField;
    @FXML
    private Label totalSisaDownPaymentField;
    @FXML
    private DatePicker tglMulaiPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    @FXML
    private ComboBox<String> groupByCombo;
    private ObservableList<PemesananHead> allPemesanan = FXCollections.observableArrayList();
    private ObservableList<PemesananHead> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPemesananColumn.setCellValueFactory(cellData -> cellData.getValue().noPemesananProperty());
        noPemesananColumn.setCellFactory(col -> Function.getWrapTableCell(noPemesananColumn));

        namaCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().namaProperty());
        namaCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(namaCustomerColumn));

        alamatCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().alamatProperty());
        alamatCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(alamatCustomerColumn));

        catatanColumn.setCellValueFactory(cellData -> cellData.getValue().catatanProperty());
        catatanColumn.setCellFactory(col -> Function.getWrapTableCell(catatanColumn));

        kodeUserColumn.setCellValueFactory(cellData -> cellData.getValue().kodeUserProperty());
        kodeUserColumn.setCellFactory(col -> Function.getWrapTableCell(kodeUserColumn));

        statusColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStatus().equals("close")) {
                return new SimpleStringProperty("Done");
            } else if (cellData.getValue().getStatus().equals("open")) {
                return new SimpleStringProperty("Wait");
            } else if (cellData.getValue().getStatus().equals("false")) {
                return new SimpleStringProperty("Cancel");
            } else if (cellData.getValue().getStatus().equals("wait")) {
                return new SimpleStringProperty("On Review");
            } else {
                return null;
            }
        });
        statusColumn.setCellFactory(col -> Function.getWrapTableCell(statusColumn));

        tglPemesananColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglPemesanan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPemesananColumn.setCellFactory(col -> Function.getWrapTableCell(tglPemesananColumn));
        tglPemesananColumn.setComparator(Function.sortDate(tglLengkap));

        totalPemesananColumn.setCellValueFactory(celldata -> celldata.getValue().totalPemesananProperty());
        totalPemesananColumn.setCellFactory(col -> Function.getTableCell());

        sisaPemesananColumn.setCellValueFactory(celldata -> {
            double sisaPemesanan = 0;
            for (PemesananDetail d : celldata.getValue().getListPemesananDetail()) {
                sisaPemesanan = sisaPemesanan + ((d.getQty() - d.getQtyTerkirim()) * d.getHargaJual());
            }
            return new SimpleDoubleProperty(sisaPemesanan);
        });
        sisaPemesananColumn.setCellFactory(col -> Function.getTableCell());

        downPaymentColumn.setCellValueFactory(celldata -> celldata.getValue().downPaymentProperty());
        downPaymentColumn.setCellFactory(col -> Function.getTableCell());

        sisaDownPaymentColumn.setCellValueFactory(celldata -> celldata.getValue().sisaDownPaymentProperty());
        sisaDownPaymentColumn.setCellFactory(col -> Function.getTableCell());

        tglMulaiPicker.setConverter(Function.getTglConverter());
        tglMulaiPicker.setValue(LocalDate.now().minusYears(1));
        tglMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPicker));

        final ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Pemesanan");
        addNew.setOnAction((ActionEvent e) -> {
            newPemesanan();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getPemesanan();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Pemesanan") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        pemesananTable.setContextMenu(rm);
        pemesananTable.setRowFactory((TableView<PemesananHead> tableView) -> {
            final TableRow<PemesananHead> row = new TableRow<PemesananHead>() {
                @Override
                public void updateItem(PemesananHead item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Pemesanan");
                        addNew.setOnAction((ActionEvent e) -> {
                            newPemesanan();
                        });
                        MenuItem detail = new MenuItem("Detail Pemesanan");
                        detail.setOnAction((ActionEvent e) -> {
                            lihatDetailPemesanan(item);
                        });
                        MenuItem edit = new MenuItem("Edit Pemesanan");
                        edit.setOnAction((ActionEvent e) -> {
                            editPemesanan(item);
                        });
                        MenuItem batal = new MenuItem("Batal Pemesanan");
                        batal.setOnAction((ActionEvent e) -> {
                            batalPemesanan(item);
                        });
                        MenuItem selesai = new MenuItem("Pemesanan Selesai");
                        selesai.setOnAction((ActionEvent e) -> {
                            selesaiPemesanan(item);
                        });
                        MenuItem bayar = new MenuItem("Terima Pembayaran DP");
                        bayar.setOnAction((ActionEvent e) -> {
                            terimaPembayaranDownPayment(item);
                        });
                        MenuItem detailBayar = new MenuItem("Detail Terima Pembayaran DP");
                        detailBayar.setOnAction((ActionEvent e) -> {
                            lihatTerimaPembayaranDownPayment(item);
                        });
                        MenuItem invoice = new MenuItem("Print Order Confirmation");
                        invoice.setOnAction((ActionEvent e) -> {
                            printProformaInvoice(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getPemesanan();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Pemesanan") && o.isStatus()) {
                                rm.getItems().add(addNew);
                            }
                            if (o.getJenis().equals("Detail Pemesanan") && o.isStatus()) {
                                rm.getItems().add(detail);
                            }
                            if (o.getJenis().equals("Edit Pemesanan") && o.isStatus()) {
                                rm.getItems().add(edit);
                            }
                            if (o.getJenis().equals("Batal Pemesanan") && o.isStatus()
                                    && (item.getStatus().equals("open") || item.getStatus().equals("wait")) && item.getDownPayment() == 0) {
                                rm.getItems().add(batal);
                            }
                            if (o.getJenis().equals("Pemesanan Selesai") && o.isStatus()
                                    && item.getStatus().equals("open") && item.getSisaDownPayment() == 0) {
                                rm.getItems().add(selesai);
                            }
                            if (o.getJenis().equals("Terima Pembayaran DP") && o.isStatus()
                                    && item.getStatus().equals("open")) {
                                rm.getItems().add(bayar);
                            }
                            if (o.getJenis().equals("Detail Terima Pembayaran DP") && o.isStatus()) {
                                rm.getItems().add(detailBayar);
                            }
                            if (o.getJenis().equals("Print Order Confirmation") && o.isStatus()
                                    && item.getStatus().equals("open")) {
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
                            if (o.getJenis().equals("Detail Pemesanan") && o.isStatus()) {
                                lihatDetailPemesanan(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allPemesanan.addListener((ListChangeListener.Change<? extends PemesananHead> change) -> {
            searchPemesanan();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchPemesanan();
                });
        filterData.addAll(allPemesanan);
        pemesananTable.setItems(filterData);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.clear();
        groupBy.add("Wait");
        groupBy.add("Done");
        groupBy.add("Cancel");
        groupBy.add("Semua");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().select("Wait");
        getPemesanan();
    }

    @FXML
    private void getPemesanan() {
        Task<List<PemesananHead>> task = new Task<List<PemesananHead>>() {
            @Override
            public List<PemesananHead> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    String status = "%";
                    if (groupByCombo.getSelectionModel().getSelectedItem().equals("Done")) {
                        status = "close";
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Wait")) {
                        status = "open";
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Cancel")) {
                        status = "false";
                    }
                    List<Customer> allCustomer = CustomerDAO.getAllByStatus(con, "%");
                    List<PemesananHead> allPemesanan = PemesananHeadDAO.getAllByDateAndStatus(
                            con, tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), status);
                    List<PemesananDetail> listPemesananDetail = PemesananDetailDAO.getAllByDateAndStatus(
                            con, tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), status);
                    for (PemesananHead p : allPemesanan) {
                        for (Customer c : allCustomer) {
                            if (p.getKodeCustomer().equals(c.getKodeCustomer())) {
                                p.setCustomer(c);
                            }
                        }
                        List<PemesananDetail> detail = new ArrayList<>();
                        for (PemesananDetail d : listPemesananDetail) {
                            if (p.getNoPemesanan().equals(d.getNoPemesanan())) {
                                detail.add(d);
                            }
                        }
                        p.setListPemesananDetail(detail);
                    }
                    return allPemesanan;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allPemesanan.clear();
            allPemesanan.addAll(task.getValue());
        });
        task.setOnFailed((e) -> {
            task.getException().printStackTrace();
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

    private void searchPemesanan() {
        try {
            filterData.clear();
            for (PemesananHead temp : allPemesanan) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPemesanan())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglPemesanan())))
                            || checkColumn(temp.getCustomer().getNama())
                            || checkColumn(temp.getCustomer().getAlamat())
                            || checkColumn(df.format(temp.getTotalPemesanan()))
                            || checkColumn(df.format(temp.getDownPayment()))
                            || checkColumn(df.format(temp.getSisaDownPayment()))
                            || checkColumn(temp.getCatatan())
                            || checkColumn(temp.getStatus())) {
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
        double sisa = 0;
        double dp = 0;
        double sisaDp = 0;
        for (PemesananHead temp : filterData) {
            total = total + temp.getTotalPemesanan();
            for (PemesananDetail d : temp.getListPemesananDetail()) {
                sisa = sisa + ((d.getQty() - d.getQtyTerkirim()) * d.getHargaJual());
            }
            dp = dp + temp.getDownPayment();
            sisaDp = sisaDp + temp.getSisaDownPayment();
        }
        totalPemesananField.setText(df.format(total));
        sisaPemesananField.setText(df.format(sisa));
        totalDownPaymentField.setText(df.format(dp));
        totalSisaDownPaymentField.setText(df.format(sisaDp));
    }

    private void newPemesanan() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPemesanan.fxml");
        NewPemesananController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setNewPemesanan();
        controller.saveButton.setOnAction((event) -> {
            if (controller.customer == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Customer belum dipilih");
            } else if (controller.allPemesananDetail.isEmpty()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Barang masih kosong");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            PemesananHead pemesanan = new PemesananHead();
                            pemesanan.setKodeCustomer(controller.customer.getKodeCustomer());
                            pemesanan.setTotalPemesanan(Double.parseDouble(controller.grandtotalField.getText().replaceAll(",", "")));
                            pemesanan.setDownPayment(0);
                            pemesanan.setSisaDownPayment(0);
                            pemesanan.setCatatan(controller.catatanField.getText());
                            pemesanan.setKodeUser(sistem.getUser().getKodeUser());
                            pemesanan.setTglBatal("2000-01-01 00:00:00");
                            pemesanan.setUserBatal("");
                            pemesanan.setStatus("open");
                            pemesanan.setListPemesananDetail(controller.allPemesananDetail);
                            return Service.newPemesanan(con, pemesanan);
                        }
                    }
                };
                task.setOnRunning((ex) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent ex) -> {
                    mainApp.closeLoading();
                    getPemesanan();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Data pemesanan berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                    }
                });
                task.setOnFailed((ex) -> {
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                    mainApp.closeLoading();
                });
                new Thread(task).start();
            }
        });
    }

    private void lihatDetailPemesanan(PemesananHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPemesanan.fxml");
        NewPemesananController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPemesanan(p.getNoPemesanan());
    }

    private void editPemesanan(PemesananHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPemesanan.fxml");
        NewPemesananController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.editPemesanan(p.getNoPemesanan());
        controller.saveButton.setOnAction((event) -> {
            if (controller.customer == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Customer belum dipilih");
            } else if (controller.allPemesananDetail.isEmpty()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Barang masih kosong");
            } else if (Double.parseDouble(controller.grandtotalField.getText().replaceAll(",", "")) < p.getSisaDownPayment()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tidak dapat disimpan karena jumlah dp lebih besar dari total penjualan");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            p.setKodeCustomer(controller.customer.getKodeCustomer());
                            p.setTotalPemesanan(Double.parseDouble(controller.grandtotalField.getText().replaceAll(",", "")));
                            p.setCatatan(controller.catatanField.getText());
                            p.setKodeUser(sistem.getUser().getKodeUser());
                            p.setTglBatal("2000-01-01 00:00:00");
                            p.setUserBatal("");
                            p.setStatus("open");
                            int noUrut = 1;
                            for (PemesananDetail temp : controller.allPemesananDetail) {
                                temp.setNoPemesanan(p.getNoPemesanan());
                                temp.setNoUrut(noUrut);
                                noUrut = noUrut + 1;
                            }
                            p.setListPemesananDetail(controller.allPemesananDetail);
                            return Service.editPemesanan(con, p);
                        }
                    }
                };
                task.setOnRunning((ex) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent ex) -> {
                    mainApp.closeLoading();
                    getPemesanan();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Data pemesanan berhasil disimpan");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                    }
                });
                task.setOnFailed((ex) -> {
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                    mainApp.closeLoading();
                });
                new Thread(task).start();
            }
        });
    }

    private void batalPemesanan(PemesananHead p) {
        boolean statusBarang = false;
        for (PemesananDetail d : p.getListPemesananDetail()) {
            if (d.getQtyTerkirim() > 0) {
                statusBarang = true;
            }
        }
        if (statusBarang) {
            mainApp.showMessage(Modality.NONE, "Warning", "Pemesanan tidak dapat dibatalkan, karena sudah ada pengiriman barang");
        } else {
            MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                    "Batal pemesanan " + p.getNoPemesanan() + " , anda yakin ?");
            controller.OK.setOnAction((ActionEvent evt) -> {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            return Service.batalPemesanan(con, p);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((we) -> {
                    mainApp.closeLoading();
                    getPemesanan();
                    if (task.getValue().equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Data pemesanan berhasil dibatal");
                    } else {
                        mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                    }
                });
                task.setOnFailed((e) -> {
                    mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                    mainApp.closeLoading();
                });
                new Thread(task).start();
            });
        }
    }

    private void selesaiPemesanan(PemesananHead p) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Pemesanan " + p.getNoPemesanan() + " telah selesai , anda yakin ?");
        controller.OK.setOnAction((ActionEvent evt) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        p.setStatus("close");
                        return Service.selesaiApprovePemesanan(con, p);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((we) -> {
                mainApp.closeLoading();
                getPemesanan();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Data pemesanan berhasil disimpan");
                } else {
                    mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                }
            });
            task.setOnFailed((e) -> {
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
                mainApp.closeLoading();
            });
            new Thread(task).start();
        });
    }

    private void terimaPembayaranDownPayment(PemesananHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPembayaran.fxml");
        NewPembayaranController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setTerimaPembayaranDownPayment(p);
        controller.saveButton.setOnAction((event) -> {
            if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kategori keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            p.setListPemesananDetail(PemesananDetailDAO.getAllByNoPemesanan(con, p.getNoPemesanan()));
                            double totalDikirim = 0;
                            for (PemesananDetail d : p.getListPemesananDetail()) {
                                totalDikirim = totalDikirim + (d.getQtyTerkirim() * d.getHargaJual());
                            }
                            double jumlahBayar = Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", ""));
                            double sisaPembayaran = p.getTotalPemesanan() - totalDikirim - p.getSisaDownPayment();
                            if (jumlahBayar > sisaPembayaran) {
                                return "Jumlah yang dibayar melebihi dari sisa pembayaran";
                            } else {
                                p.setDownPayment(p.getDownPayment() + jumlahBayar);
                                p.setSisaDownPayment(p.getSisaDownPayment() + jumlahBayar);
                                return Service.newTerimaDownPayment(con, p, jumlahBayar,
                                        controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            }
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getPemesanan();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Terima pembayaran DP berhasil disimpan");
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

    private void lihatTerimaPembayaranDownPayment(PemesananHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailPembayaranDownPayment.fxml");
        DetailPembayaranDownPaymentController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPemesanan(p);
        controller.hutangTable.setRowFactory((TableView<Hutang> tv) -> {
            final TableRow<Hutang> row = new TableRow<Hutang>() {
                @Override
                public void updateItem(Hutang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(null);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem batal = new MenuItem("Batal Terima Pembayaran DP");
                        batal.setOnAction((ActionEvent e) -> {
                            batalPembayaran(p, item, stage);
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Batal Terima Pembayaran DP") && o.isStatus()
                                    && item.getStatus().equals("open") && p.getSisaDownPayment() >= item.getJumlahHutang()) {
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

    private void batalPembayaran(PemesananHead p, Hutang h, Stage stage) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Batal pembayaran " + h.getNoHutang() + " ?");
        controller.OK.setOnAction((ActionEvent e) -> {
            mainApp.closeMessage();
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.batalTerimaDownPayment(con, h);
                    }
                }
            };
            task.setOnRunning((ex) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((WorkerStateEvent ex) -> {
                mainApp.closeLoading();
                getPemesanan();
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

    private void printProformaInvoice(PemesananHead p) {
        try (Connection con = Koneksi.getConnection()) {
            List<PemesananDetail> listDetail = PemesananDetailDAO.getAllByNoPemesanan(con, p.getNoPemesanan());
            for (PemesananDetail d : listDetail) {
                d.setPemesananHead(p);
            }
            PrintOut po = new PrintOut();
            po.printSuratPemesanan(listDetail);
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
                Sheet sheet = workbook.createSheet("Data Pemesanan");
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
                sheet.getRow(rc).getCell(0).setCellValue("No Pemesanan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Pemesanan");
                sheet.getRow(rc).getCell(2).setCellValue("Customer");
                sheet.getRow(rc).getCell(3).setCellValue("Total Pemesanan");
                sheet.getRow(rc).getCell(4).setCellValue("Sisa Pemesanan");
                sheet.getRow(rc).getCell(5).setCellValue("Pembayaran Down Payment");
                sheet.getRow(rc).getCell(6).setCellValue("Sisa Pembayaran Down Payment");
                rc++;
                double pemesanan = 0;
                double sisaPemesanan = 0;
                double pembayaran = 0;
                double sisaPembayaran = 0;
                for (PemesananHead b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoPemesanan());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglPemesanan())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getCustomer().getNama());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getTotalPemesanan());
                    double x = 0;
                    for (PemesananDetail d : b.getListPemesananDetail()) {
                        x = x + ((d.getQty() - d.getQtyTerkirim()) * d.getHargaJual());
                    }
                    sheet.getRow(rc).getCell(4).setCellValue(x);
                    sheet.getRow(rc).getCell(5).setCellValue(b.getDownPayment());
                    sheet.getRow(rc).getCell(6).setCellValue(b.getSisaDownPayment());
                    rc++;
                    pemesanan = pemesanan + b.getTotalPemesanan();
                    sisaPemesanan = sisaPemesanan + x;
                    pembayaran = pembayaran + b.getDownPayment();
                    sisaPembayaran = sisaPembayaran + b.getSisaDownPayment();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(3).setCellValue(pemesanan);
                sheet.getRow(rc).getCell(4).setCellValue(sisaPemesanan);
                sheet.getRow(rc).getCell(5).setCellValue(pembayaran);
                sheet.getRow(rc).getCell(6).setCellValue(sisaPembayaran);
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
