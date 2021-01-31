/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanHeadDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Customer;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import com.excellentsystem.PasarBaja.View.Dialog.DetailPiutangController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPenjualanController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
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
public class UntungRugiHPPPenjualanController {

    @FXML
    private TreeTableView<PenjualanDetail> penjualanDetailTable;
    @FXML
    private TreeTableColumn<PenjualanDetail, String> noPenjualanColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, String> tglPenjualanColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, String> namaCustomerColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, String> kodeBarangColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, String> namaBarangColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, String> satuanColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, Number> qtyColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, Number> nilaiColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, Number> totalNilaiColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, Number> hargaJualColumn;
    @FXML
    private TreeTableColumn<PenjualanDetail, Number> totalColumn;

    @FXML
    private Label totalQtyField;
    @FXML
    private Label totalNilaiField;

    private String tglAwal;
    private String tglAkhir;
    final TreeItem<PenjualanDetail> root = new TreeItem<>();
    private ObservableList<PenjualanDetail> allPenjualan = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage owner;
    private Stage stage;

    public void initialize() {
        noPenjualanColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().noPenjualanProperty());
        tglPenjualanColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getValue().getPenjualanHead().getTglPenjualan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPenjualanColumn.setComparator(Function.sortDate(tglLengkap));
        namaCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getPenjualanHead().getCustomer().namaProperty());

        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().namaBarangProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().satuanProperty());

        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().qtyProperty());
        nilaiColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().nilaiProperty());
        totalNilaiColumn.setCellValueFactory(cellData
                -> new SimpleDoubleProperty(cellData.getValue().getValue().getNilai() * cellData.getValue().getValue().getQty()));

        hargaJualColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().hargaJualProperty());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().totalProperty());
        qtyColumn.setCellFactory(col -> Function.getTreeTableCell());
        nilaiColumn.setCellFactory(col -> Function.getTreeTableCell());
        totalNilaiColumn.setCellFactory(col -> Function.getTreeTableCell());
        hargaJualColumn.setCellFactory(col -> Function.getTreeTableCell());
        totalColumn.setCellFactory(col -> Function.getTreeTableCell());

        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getPenjualan(tglAwal, tglAkhir);
        });
        rm.getItems().addAll(export, refresh);
        penjualanDetailTable.setContextMenu(rm);
        penjualanDetailTable.setRowFactory((TreeTableView<PenjualanDetail> tableView) -> {
            final TreeTableRow<PenjualanDetail> row = new TreeTableRow<PenjualanDetail>() {
                @Override
                public void updateItem(PenjualanDetail item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem detail = new MenuItem("Detail Penjualan");
                        detail.setOnAction((ActionEvent e) -> {
                            lihatDetailPenjualan(item.getPenjualanHead());
                        });
                        MenuItem pembayaran = new MenuItem("Detail Pembayaran Penjualan");
                        pembayaran.setOnAction((ActionEvent e) -> {
                            showDetailPiutang(item.getPenjualanHead());
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getPenjualan(tglAwal, tglAkhir);
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Detail Penjualan") && o.isStatus()
                                    && item.getPenjualanHead().getStatus() != null) {
                                rm.getItems().add(detail);
                            }
                            if (o.getJenis().equals("Detail Pembayaran Penjualan") && o.isStatus()
                                    && item.getPenjualanHead().getPembayaran() > 0
                                    && item.getPenjualanHead().getStatus() != null) {
                                rm.getItems().add(pembayaran);
                            }
                        }
                        rm.getItems().addAll(export, refresh);
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
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
        stage.setHeight(mainApp.screenSize.getHeight() * 0.9);
        stage.setWidth(mainApp.screenSize.getWidth() * 0.9);
        stage.setX((mainApp.screenSize.getWidth() - stage.getWidth()) / 2);
        stage.setY((mainApp.screenSize.getHeight() - stage.getHeight()) / 2);
    }

    public void getPenjualan(String tglAwal, String tglAkhir) {
        Task<List<PenjualanDetail>> task = new Task<List<PenjualanDetail>>() {
            @Override
            public List<PenjualanDetail> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<PenjualanHead> penjualan = PenjualanHeadDAO.getAllByDateAndStatus(con, tglAwal, tglAkhir, "true");
                    List<PenjualanDetail> temp = PenjualanDetailDAO.getAllByDateAndStatus(con, tglAwal, tglAkhir, "true");
                    List<Customer> customer = CustomerDAO.getAllByStatus(con, "%");
                    for (PenjualanDetail d : temp) {
                        for (PenjualanHead h : penjualan) {
                            if (d.getNoPenjualan().equals(h.getNoPenjualan())) {
                                d.setPenjualanHead(h);
                            }
                        }
                        for (Customer c : customer) {
                            if (d.getPenjualanHead().getKodeCustomer().equals(c.getKodeCustomer())) {
                                d.getPenjualanHead().setCustomer(c);
                            }
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
            try {
                mainApp.closeLoading();
                this.tglAwal = tglAwal;
                this.tglAkhir = tglAkhir;
                allPenjualan.clear();
                allPenjualan.addAll(task.getValue());
                setTable();
                hitungTotal();
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

    private void hitungTotal() {
        double totalNilai = 0;
        double totalQty = 0;
        for (PenjualanDetail temp : allPenjualan) {
            totalNilai = totalNilai + (temp.getNilai() * temp.getQty());
            totalQty = totalQty + temp.getQty();
        }
        totalNilaiField.setText(df.format(totalNilai));
        totalQtyField.setText(df.format(totalQty));
    }

    private void setTable() throws Exception {
        if (penjualanDetailTable.getRoot() != null) {
            penjualanDetailTable.getRoot().getChildren().clear();
        }
        List<String> groupBy = new ArrayList<>();
        for (PenjualanDetail temp : allPenjualan) {
            if (!groupBy.contains(temp.getPenjualanHead().getCustomer().getNama())) {
                groupBy.add(temp.getPenjualanHead().getCustomer().getNama());
            }
        }
        for (String temp : groupBy) {
            PenjualanDetail head = new PenjualanDetail();
            head.setNoPenjualan(temp);
            head.setPenjualanHead(new PenjualanHead());
            head.getPenjualanHead().setCustomer(new Customer());
            TreeItem<PenjualanDetail> parent = new TreeItem<>(head);
            double totalQty = 0;
            double totalNilai = 0;
            double totalHarga = 0;
            for (PenjualanDetail detail : allPenjualan) {
                if (temp.equals(detail.getPenjualanHead().getCustomer().getNama())) {
                    totalQty = totalQty + detail.getQty();
                    totalNilai = totalNilai + (detail.getNilai() * detail.getQty());
                    totalHarga = totalHarga + detail.getTotal();
                    parent.getChildren().addAll(new TreeItem<>(detail));
                }
            }
            parent.getValue().setQty(totalQty);
            parent.getValue().setNilai(totalNilai / totalQty);
            parent.getValue().setHargaJual(totalHarga / totalQty);
            parent.getValue().setTotal(totalHarga);
            root.getChildren().add(parent);
        }
        penjualanDetailTable.setRoot(root);
    }

    private void lihatDetailPenjualan(PenjualanHead p) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/NewPenjualan.fxml");
        NewPenjualanController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.setDetailPenjualan(p.getNoPenjualan());
    }

    private void showDetailPiutang(PenjualanHead p) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/DetailPiutang.fxml");
        DetailPiutangController x = loader.getController();
        x.setMainApp(mainApp, stage, child);
        x.setDetailPenjualan(p);
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

                Sheet sheet = workbook.createSheet("Laporan Untung Rugi - HPP Penjualan");
                int rc = 0;
                int c = 11;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Penjualan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Penjualan");
                sheet.getRow(rc).getCell(2).setCellValue("Customer");
                sheet.getRow(rc).getCell(3).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(4).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(5).setCellValue("Satuan");
                sheet.getRow(rc).getCell(6).setCellValue("Qty");
                sheet.getRow(rc).getCell(7).setCellValue("Nilai");
                sheet.getRow(rc).getCell(8).setCellValue("Total Nilai");
                sheet.getRow(rc).getCell(9).setCellValue("Harga");
                sheet.getRow(rc).getCell(10).setCellValue("Total Harga");
                rc++;
                List<String> groupBy = new ArrayList<>();
                for (PenjualanDetail temp : allPenjualan) {
                    if (!groupBy.contains(temp.getPenjualanHead().getCustomer().getNama())) {
                        groupBy.add(temp.getPenjualanHead().getCustomer().getNama());
                    }
                }
                double grandtotalQty = 0;
                double grandtotalNilai = 0;
                double grandtotalHarga = 0;
                for (String temp : groupBy) {
                    rc++;
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue(temp);
                    rc++;
                    double totalQty = 0;
                    double totalNilai = 0;
                    double totalHarga = 0;
                    for (PenjualanDetail detail : allPenjualan) {
                        if (temp.equals(detail.getPenjualanHead().getCustomer().getNama())) {
                            createRow(workbook, sheet, rc, c, "Detail");
                            sheet.getRow(rc).getCell(0).setCellValue(detail.getPenjualanHead().getNoPenjualan());
                            sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(detail.getPenjualanHead().getTglPenjualan())));
                            sheet.getRow(rc).getCell(2).setCellValue(detail.getPenjualanHead().getCustomer().getNama());
                            sheet.getRow(rc).getCell(3).setCellValue(detail.getKodeBarang());
                            sheet.getRow(rc).getCell(4).setCellValue(detail.getNamaBarang());
                            sheet.getRow(rc).getCell(5).setCellValue(detail.getSatuan());
                            sheet.getRow(rc).getCell(6).setCellValue(detail.getQty());
                            sheet.getRow(rc).getCell(7).setCellValue(detail.getNilai());
                            sheet.getRow(rc).getCell(8).setCellValue(detail.getNilai() * detail.getQty());
                            sheet.getRow(rc).getCell(9).setCellValue(detail.getHargaJual());
                            sheet.getRow(rc).getCell(10).setCellValue(detail.getTotal());
                            rc++;
                            totalQty = totalQty + detail.getQty();
                            totalNilai = totalNilai + (detail.getNilai() * detail.getQty());
                            totalHarga = totalHarga + detail.getTotal();
                        }
                    }
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue("Total " + temp);
                    sheet.getRow(rc).getCell(6).setCellValue(totalQty);
                    sheet.getRow(rc).getCell(7).setCellValue(totalNilai / totalQty);
                    sheet.getRow(rc).getCell(8).setCellValue(totalNilai);
                    sheet.getRow(rc).getCell(9).setCellValue(totalHarga / totalQty);
                    sheet.getRow(rc).getCell(10).setCellValue(totalHarga);
                    rc++;
                    grandtotalQty = grandtotalQty + totalQty;
                    grandtotalNilai = grandtotalNilai + totalNilai;
                    grandtotalHarga = grandtotalHarga + totalHarga;
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total");
                sheet.getRow(rc).getCell(6).setCellValue(grandtotalQty);
                sheet.getRow(rc).getCell(7).setCellValue(grandtotalNilai / grandtotalQty);
                sheet.getRow(rc).getCell(8).setCellValue(grandtotalNilai);
                sheet.getRow(rc).getCell(9).setCellValue(grandtotalHarga / grandtotalQty);
                sheet.getRow(rc).getCell(10).setCellValue(grandtotalHarga);
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
