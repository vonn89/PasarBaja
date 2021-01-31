/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanHeadDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tgl;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.Customer;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import com.excellentsystem.PasarBaja.PrintOut.PrintOut;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPengirimanController;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
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
 * @author excellent
 */
public class PengirimanBarangController {

    @FXML
    private TableView<PenjualanHead> pengirimanTable;
    @FXML
    private TableColumn<PenjualanHead, String> noPengirimanColumn;
    @FXML
    private TableColumn<PenjualanHead, String> tglPengirimanColumn;
    @FXML
    private TableColumn<PenjualanHead, String> noPemesananColumn;
    @FXML
    private TableColumn<PenjualanHead, String> namaCustomerColumn;
    @FXML
    private TableColumn<PenjualanHead, String> alamatCustomerColumn;
    @FXML
    private TableColumn<PenjualanHead, String> tujuanKirimColumn;
    @FXML
    private TableColumn<PenjualanHead, String> supirColumn;
    @FXML
    private TableColumn<PenjualanHead, Number> tonaseColumn;

    @FXML
    private TextField searchField;
    @FXML
    private DatePicker tglMulaiPicker;
    @FXML
    private DatePicker tglAkhirPicker;

    private ObservableList<PenjualanHead> allPengiriman = FXCollections.observableArrayList();
    private ObservableList<PenjualanHead> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPengirimanColumn.setCellValueFactory(cellData -> cellData.getValue().noPenjualanProperty());
        noPengirimanColumn.setCellFactory(col -> Function.getWrapTableCell(noPengirimanColumn));

        tglPengirimanColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglPenjualan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPengirimanColumn.setCellFactory(col -> Function.getWrapTableCell(tglPengirimanColumn));
        tglPengirimanColumn.setComparator(Function.sortDate(tglLengkap));

        noPemesananColumn.setCellValueFactory(cellData -> cellData.getValue().noPemesananProperty());
        noPemesananColumn.setCellFactory(col -> Function.getWrapTableCell(noPemesananColumn));

        namaCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().namaProperty());
        namaCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(namaCustomerColumn));

        alamatCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().alamatProperty());
        alamatCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(alamatCustomerColumn));

        tujuanKirimColumn.setCellValueFactory(cellData -> cellData.getValue().tujuanKirimProperty());
        tujuanKirimColumn.setCellFactory(col -> Function.getWrapTableCell(tujuanKirimColumn));

        supirColumn.setCellValueFactory(cellData -> cellData.getValue().supirProperty());
        supirColumn.setCellFactory(col -> Function.getWrapTableCell(supirColumn));

        tonaseColumn.setCellValueFactory(cellData -> {
            double tonase = 0;
            for (PenjualanDetail d : cellData.getValue().getListPenjualanDetail()) {
                tonase = tonase + (d.getQty() * d.getBarang().getBerat());
            }
            return new SimpleDoubleProperty(tonase);
        });
        tonaseColumn.setCellFactory(col -> Function.getTableCell());

        tglMulaiPicker.setConverter(Function.getTglConverter());
        tglMulaiPicker.setValue(LocalDate.now().minusMonths(1));
        tglMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPicker));
        final ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Pengiriman");
        addNew.setOnAction((ActionEvent e) -> {
            newPengiriman();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getPengiriman();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Pengiriman") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        pengirimanTable.setContextMenu(rm);
        pengirimanTable.setRowFactory((TableView<PenjualanHead> tableView) -> {
            final TableRow<PenjualanHead> row = new TableRow<PenjualanHead>() {
                @Override
                public void updateItem(PenjualanHead item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Pengiriman");
                        addNew.setOnAction((ActionEvent e) -> {
                            newPengiriman();
                        });
                        MenuItem detail = new MenuItem("Detail Pengiriman");
                        detail.setOnAction((ActionEvent e) -> {
                            lihatDetailPengiriman(item);
                        });
                        MenuItem batal = new MenuItem("Batal Pengiriman");
                        batal.setOnAction((ActionEvent e) -> {
                            batalPengiriman(item);
                        });
                        MenuItem suratJalan = new MenuItem("Print Surat Jalan");
                        suratJalan.setOnAction((ActionEvent e) -> {
                            printSuratJalan(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getPengiriman();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Pengiriman") && o.isStatus()) {
                                rm.getItems().add(addNew);
                            }
                            if (o.getJenis().equals("Detail Pengiriman") && o.isStatus()) {
                                rm.getItems().add(detail);
                            }
                            if (o.getJenis().equals("Batal Pengiriman") && o.isStatus() && !item.getStatus().equals("false")) {
                                rm.getItems().add(batal);
                            }
                            if (o.getJenis().equals("Print Surat Jalan") && o.isStatus() && !item.getStatus().equals("false")) {
                                rm.getItems().add(suratJalan);
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
                            if (o.getJenis().equals("Detail Pengiriman") && o.isStatus()) {
                                lihatDetailPengiriman(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allPengiriman.addListener((ListChangeListener.Change<? extends PenjualanHead> change) -> {
            searchPengiriman();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchPengiriman();
                });
        filterData.addAll(allPengiriman);
        pengirimanTable.setItems(filterData);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        getPengiriman();
    }

    @FXML
    private void getPengiriman() {
        Task<List<PenjualanHead>> task = new Task<List<PenjualanHead>>() {
            @Override
            public List<PenjualanHead> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<Customer> allCustomer = CustomerDAO.getAllByStatus(con, "%");
                    List<Barang> allBarang = BarangDAO.getAllByStatus(con, "%");
                    List<PenjualanDetail> allPengirimanDetail = PenjualanDetailDAO.getAllByDateAndStatus(con,
                            tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), "true");
                    List<PenjualanHead> allPengiriman = PenjualanHeadDAO.getAllByDateAndStatus(con,
                            tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), "true");
                    for (PenjualanHead h : allPengiriman) {
                        for (Customer c : allCustomer) {
                            if (h.getKodeCustomer().equals(c.getKodeCustomer())) {
                                h.setCustomer(c);
                            }
                        }
                        List<PenjualanDetail> listDetail = new ArrayList<>();
                        for (PenjualanDetail d : allPengirimanDetail) {
                            if (d.getNoPenjualan().equals(h.getNoPenjualan())) {
                                for (Barang b : allBarang) {
                                    if (b.getKodeBarang().equals(d.getKodeBarang())) {
                                        d.setBarang(b);
                                    }
                                }
                                listDetail.add(d);
                            }
                        }
                        h.setListPenjualanDetail(listDetail);
                    }
                    return allPengiriman;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allPengiriman.clear();
            allPengiriman.addAll(task.getValue());
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            task.getException().printStackTrace();
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

    private void searchPengiriman() {
        try {
            filterData.clear();
            for (PenjualanHead temp : allPengiriman) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPenjualan())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglPenjualan())))
                            || checkColumn(temp.getNoPemesanan())
                            || checkColumn(temp.getCustomer().getNama())
                            || checkColumn(temp.getCustomer().getAlamat())
                            || checkColumn(temp.getCustomer().getKota())
                            || checkColumn(temp.getTujuanKirim())
                            || checkColumn(temp.getSupir())
                            || checkColumn(temp.getCatatan())) {
                        filterData.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
            e.printStackTrace();
        }
    }

    private void newPengiriman() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPengiriman.fxml");
        NewPengirimanController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setNewPengiriman();
        controller.saveButton.setOnAction((event) -> {
            if (controller.pemesanan == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Pemesanan belum dipilih");
            } else if (controller.allPenjualanDetail.isEmpty()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Barang masih kosong");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            PenjualanHead pengiriman = new PenjualanHead();
                            pengiriman.setPemesananHead(controller.pemesanan);
                            pengiriman.setNoPemesanan(controller.noPemesananField.getText());
                            pengiriman.setKodeCustomer(controller.pemesanan.getKodeCustomer());
                            pengiriman.setTujuanKirim(controller.alamatKirimField.getText());
                            pengiriman.setSupir(controller.namaSupirField.getText());
                            pengiriman.setCatatan(controller.pemesanan.getCatatan());
                            pengiriman.setKodeUser(sistem.getUser().getKodeUser());
                            pengiriman.setTglBatal("2000-01-01 00:00:00");
                            pengiriman.setUserBatal("");
                            pengiriman.setStatus("true");
                            pengiriman.setTotalBebanPenjualan(0);
                            double total = 0;
                            for (PenjualanDetail temp : controller.allPenjualanDetail) {
                                total = total + temp.getTotal();
                            }
                            pengiriman.setTotalPenjualan(total);
                            pengiriman.setPembayaran(0);
                            pengiriman.setSisaPembayaran(total);
//                            
                            double dp = pengiriman.getPemesananHead().getSisaDownPayment();
                            if (total >= dp) {
                                pengiriman.setPembayaran(dp);
                            } else if (total < dp) {
                                pengiriman.setPembayaran(total);
                            }
                            pengiriman.setSisaPembayaran(pengiriman.getTotalPenjualan() - pengiriman.getPembayaran());

                            pengiriman.setListPenjualanDetail(controller.allPenjualanDetail);
                            return Service.newPenjualan(con, pengiriman);
                        }
                    }
                };
                task.setOnRunning((ex) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((WorkerStateEvent ex) -> {
                    mainApp.closeLoading();
                    getPengiriman();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Data pengiriman barang berhasil disimpan");
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

    private void batalPengiriman(PenjualanHead pengiriman) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Batal pengiriman barang " + pengiriman.getNoPenjualan() + " ?");
        controller.OK.setOnAction((ActionEvent e) -> {
            mainApp.closeMessage();
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.batalPenjualan(con, pengiriman);
                    }
                }
            };
            task.setOnRunning((ex) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((WorkerStateEvent ex) -> {
                mainApp.closeLoading();
                getPengiriman();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Data pengiriman barang berhasil dibatal");
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

    private void lihatDetailPengiriman(PenjualanHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPengiriman.fxml");
        NewPengirimanController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPengiriman(p.getNoPenjualan());
    }

    private void printSuratJalan(PenjualanHead p) {
        try (Connection con = Koneksi.getConnection()) {
            List<PenjualanDetail> listPenjualan = PenjualanDetailDAO.getAllPenjualanDetail(con, p.getNoPenjualan());
            for (PenjualanDetail d : listPenjualan) {
                d.setPenjualanHead(p);
                d.setBarang(BarangDAO.get(con, d.getKodeBarang()));
            }
            PrintOut printOut = new PrintOut();
            printOut.printSuratJalan(listPenjualan);
        } catch (Exception e) {
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
                Sheet sheet = workbook.createSheet("Data Pengiriman Barang");
                int rc = 0;
                int c = 9;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tanggal : "
                        + tgl.format(tglBarang.parse(tglMulaiPicker.getValue().toString())) + "-"
                        + tgl.format(tglBarang.parse(tglAkhirPicker.getValue().toString())));
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Pengiriman");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Pengiriman");
                sheet.getRow(rc).getCell(2).setCellValue("No Pemesanan");
                sheet.getRow(rc).getCell(3).setCellValue("Nama");
                sheet.getRow(rc).getCell(4).setCellValue("Alamat");
                sheet.getRow(rc).getCell(5).setCellValue("Tujuan Kirim");
                sheet.getRow(rc).getCell(6).setCellValue("Supir");
                sheet.getRow(rc).getCell(7).setCellValue("Tonase");
                rc++;
                for (PenjualanHead b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoPenjualan());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglPenjualan())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getNoPemesanan());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getCustomer().getNama());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getCustomer().getAlamat());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getTujuanKirim());
                    sheet.getRow(rc).getCell(6).setCellValue(b.getSupir());
                    double tonase = 0;
                    for (PenjualanDetail d : b.getListPenjualanDetail()) {
                        tonase = tonase + (d.getQty() * d.getBarang().getBerat());
                    }
                    sheet.getRow(rc).getCell(7).setCellValue(tonase);
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
