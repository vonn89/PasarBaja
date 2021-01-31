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
public class UntungRugiPenjualanController {

    @FXML
    private TreeTableView<PenjualanHead> penjualanTable;
    @FXML
    private TreeTableColumn<PenjualanHead, String> noPenjualanColumn;
    @FXML
    private TreeTableColumn<PenjualanHead, String> tglPenjualanColumn;
    @FXML
    private TreeTableColumn<PenjualanHead, String> namaCustomerColumn;
    @FXML
    private TreeTableColumn<PenjualanHead, Number> totalPenjualanColumn;
    @FXML
    private TreeTableColumn<PenjualanHead, Number> pembayaranColumn;
    @FXML
    private TreeTableColumn<PenjualanHead, Number> sisaPembayaranColumn;

    @FXML
    private Label totalPenjualanField;
    @FXML
    private Label totalPembayaranField;
    @FXML
    private Label sisaPembayaranField;
    private String tglAwal;
    private String tglAkhir;
    final TreeItem<PenjualanHead> root = new TreeItem<>();
    private ObservableList<PenjualanHead> allPenjualan = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage owner;
    private Stage stage;

    public void initialize() {
        noPenjualanColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().noPenjualanProperty());
        namaCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getCustomer().namaProperty());
        tglPenjualanColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getValue().getTglPenjualan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPenjualanColumn.setComparator(Function.sortDate(tglLengkap));
        totalPenjualanColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().totalPenjualanProperty());
        totalPenjualanColumn.setCellFactory(col -> Function.getTreeTableCell());
        pembayaranColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().pembayaranProperty());
        pembayaranColumn.setCellFactory(col -> Function.getTreeTableCell());
        sisaPembayaranColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().sisaPembayaranProperty());
        sisaPembayaranColumn.setCellFactory(col -> Function.getTreeTableCell());

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
        penjualanTable.setContextMenu(rm);
        penjualanTable.setRowFactory((TreeTableView<PenjualanHead> tableView) -> {
            final TreeTableRow<PenjualanHead> row = new TreeTableRow<PenjualanHead>() {
                @Override
                public void updateItem(PenjualanHead item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem detail = new MenuItem("Detail Penjualan");
                        detail.setOnAction((ActionEvent e) -> {
                            lihatDetailPenjualan(item);
                        });
                        MenuItem pembayaran = new MenuItem("Detail Pembayaran Penjualan");
                        pembayaran.setOnAction((ActionEvent e) -> {
                            showDetailPiutang(item);
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
                                    && item.getStatus() != null) {
                                rm.getItems().add(detail);
                            }
                            if (o.getJenis().equals("Detail Pembayaran Penjualan") && o.isStatus()
                                    && item.getPembayaran() > 0 && item.getStatus() != null) {
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
        Task<List<PenjualanHead>> task = new Task<List<PenjualanHead>>() {
            @Override
            public List<PenjualanHead> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<PenjualanHead> allPenjualan = PenjualanHeadDAO.getAllByDateAndStatus(con,
                            tglAwal, tglAkhir, "true");
                    List<PenjualanDetail> allDetail = PenjualanDetailDAO.getAllByDateAndStatus(con,
                            tglAwal, tglAkhir, "true");
                    List<Customer> allCustomer = CustomerDAO.getAllByStatus(con, "%");
                    for (PenjualanHead p : allPenjualan) {
                        for (Customer c : allCustomer) {
                            if (p.getKodeCustomer().equals(c.getKodeCustomer())) {
                                p.setCustomer(c);
                            }
                        }
                        List<PenjualanDetail> detail = new ArrayList<>();
                        for (PenjualanDetail d : allDetail) {
                            if (p.getNoPenjualan().equals(d.getNoPenjualan())) {
                                detail.add(d);
                            }
                        }
                        p.setListPenjualanDetail(detail);
                    }
                    return allPenjualan;
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

    private void setTable() throws Exception {
        if (penjualanTable.getRoot() != null) {
            penjualanTable.getRoot().getChildren().clear();
        }
        List<String> groupBy = new ArrayList<>();
        for (PenjualanHead temp : allPenjualan) {
            if (!groupBy.contains(temp.getCustomer().getNama())) {
                groupBy.add(temp.getCustomer().getNama());
            }
        }
        for (String temp : groupBy) {
            PenjualanHead head = new PenjualanHead();
            head.setNoPenjualan(temp);
            head.setCustomer(new Customer());
            TreeItem<PenjualanHead> parent = new TreeItem<>(head);
            double totalPenjualan = 0;
            double totalPembayaran = 0;
            double sisaPembayaran = 0;
            for (PenjualanHead pj : allPenjualan) {
                if (temp.equals(pj.getCustomer().getNama())) {
                    totalPenjualan = totalPenjualan + pj.getTotalPenjualan();
                    totalPembayaran = totalPembayaran + pj.getPembayaran();
                    sisaPembayaran = sisaPembayaran + pj.getSisaPembayaran();
                    parent.getChildren().addAll(new TreeItem<>(pj));
                }
            }
            parent.getValue().setTotalPenjualan(totalPenjualan);
            parent.getValue().setPembayaran(totalPembayaran);
            parent.getValue().setSisaPembayaran(sisaPembayaran);
            root.getChildren().add(parent);
        }
        penjualanTable.setRoot(root);
    }

    private void hitungTotal() {
        double totalPenjualan = 0;
        double totalPembayaran = 0;
        double sisaPembayaran = 0;
        for (PenjualanHead temp : allPenjualan) {
            totalPenjualan = totalPenjualan + temp.getTotalPenjualan();
            totalPembayaran = totalPembayaran + temp.getPembayaran();
            sisaPembayaran = sisaPembayaran + temp.getSisaPembayaran();
        }
        totalPenjualanField.setText(df.format(totalPenjualan));
        totalPembayaranField.setText(df.format(totalPembayaran));
        sisaPembayaranField.setText(df.format(sisaPembayaran));
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
                Sheet sheet = workbook.createSheet("Laporan Untung Rugi - Penjualan");
                int rc = 0;
                int c = 9;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Penjualan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Penjualan");
                sheet.getRow(rc).getCell(2).setCellValue("Customer");
                sheet.getRow(rc).getCell(3).setCellValue("Sales");
                sheet.getRow(rc).getCell(4).setCellValue("Total Penjualan");
                sheet.getRow(rc).getCell(5).setCellValue("Pembayaran");
                sheet.getRow(rc).getCell(6).setCellValue("Sisa Pembayaran");
                sheet.getRow(rc).getCell(7).setCellValue("Catatan");
                sheet.getRow(rc).getCell(8).setCellValue("Kode User");
                rc++;

                List<String> groupBy = new ArrayList<>();
                for (PenjualanHead temp : allPenjualan) {
                    if (!groupBy.contains(temp.getCustomer().getNama())) {
                        groupBy.add(temp.getCustomer().getNama());
                    }
                }
                double grandtotalPenjualan = 0;
                double grandtotalPembayaran = 0;
                double grandsisaPembayaran = 0;
                for (String temp : groupBy) {
                    rc++;
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue(temp);
                    rc++;
                    double totalPenjualan = 0;
                    double totalPembayaran = 0;
                    double sisaPembayaran = 0;
                    for (PenjualanHead p : allPenjualan) {
                        if (temp.equals(p.getCustomer().getNama())) {
                            createRow(workbook, sheet, rc, c, "Detail");
                            sheet.getRow(rc).getCell(0).setCellValue(p.getNoPenjualan());
                            sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(p.getTglPenjualan())));
                            sheet.getRow(rc).getCell(2).setCellValue(p.getCustomer().getNama());
                            sheet.getRow(rc).getCell(3).setCellValue(p.getTotalPenjualan());
                            sheet.getRow(rc).getCell(5).setCellValue(p.getPembayaran());
                            sheet.getRow(rc).getCell(6).setCellValue(p.getSisaPembayaran());
                            sheet.getRow(rc).getCell(7).setCellValue(p.getCatatan());
                            sheet.getRow(rc).getCell(8).setCellValue(p.getKodeUser());
                            rc++;
                            totalPenjualan = totalPenjualan + p.getTotalPenjualan();
                            totalPembayaran = totalPembayaran + p.getPembayaran();
                            sisaPembayaran = sisaPembayaran + p.getSisaPembayaran();
                        }
                    }
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue("Total " + temp);
                    sheet.getRow(rc).getCell(4).setCellValue(totalPenjualan);
                    sheet.getRow(rc).getCell(5).setCellValue(totalPembayaran);
                    sheet.getRow(rc).getCell(6).setCellValue(sisaPembayaran);
                    rc++;
                    grandtotalPenjualan = grandtotalPenjualan + totalPenjualan;
                    grandtotalPembayaran = grandtotalPembayaran + totalPembayaran;
                    grandsisaPembayaran = grandsisaPembayaran + sisaPembayaran;
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total");
                sheet.getRow(rc).getCell(4).setCellValue(grandtotalPenjualan);
                sheet.getRow(rc).getCell(5).setCellValue(grandtotalPembayaran);
                sheet.getRow(rc).getCell(6).setCellValue(grandsisaPembayaran);
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
