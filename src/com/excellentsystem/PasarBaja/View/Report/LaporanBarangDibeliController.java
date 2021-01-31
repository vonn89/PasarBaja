/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.PembelianDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PembelianHeadDAO;
import com.excellentsystem.PasarBaja.DAO.SupplierDAO;
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
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PembelianDetail;
import com.excellentsystem.PasarBaja.Model.PembelianHead;
import com.excellentsystem.PasarBaja.Model.Supplier;
import com.excellentsystem.PasarBaja.View.Dialog.DetailHutangController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembelianController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.TextField;
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
public class LaporanBarangDibeliController {

    @FXML
    private TreeTableView<PembelianDetail> pembelianDetailTable;
    @FXML
    private TreeTableColumn<PembelianDetail, String> noPembelianColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, String> tglPembelianColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, String> namaSupplierColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, String> kodeBarangColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, String> namaBarangColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, String> satuanColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, Number> qtyColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, Number> hargaBeliColumn;
    @FXML
    private TreeTableColumn<PembelianDetail, Number> totalColumn;

    @FXML
    private ComboBox<String> groupByCombo;
    @FXML
    private TextField searchField;
    @FXML
    private Label totalQtyField;
    @FXML
    private Label totalPembelianField;
    @FXML
    private DatePicker tglPembelianMulaiPicker;
    @FXML
    private DatePicker tglPembelianAkhirPicker;

    final TreeItem<PembelianDetail> root = new TreeItem<>();
    private ObservableList<PembelianDetail> allPembelian = FXCollections.observableArrayList();
    private ObservableList<PembelianDetail> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPembelianColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().noPembelianProperty());
        noPembelianColumn.setCellFactory(col -> Function.getWrapTreeTableCell(noPembelianColumn));

        tglPembelianColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getValue().getPembelianHead().getTglPembelian())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPembelianColumn.setCellFactory(col -> Function.getWrapTreeTableCell(tglPembelianColumn));
        tglPembelianColumn.setComparator(Function.sortDate(tglLengkap));

        namaSupplierColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getPembelianHead().getSupplier().namaProperty());
        namaSupplierColumn.setCellFactory(col -> Function.getWrapTreeTableCell(namaSupplierColumn));

        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().kodeBarangProperty());
        kodeBarangColumn.setCellFactory(col -> Function.getWrapTreeTableCell(kodeBarangColumn));

        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().namaBarangProperty());
        namaBarangColumn.setCellFactory(col -> Function.getWrapTreeTableCell(namaBarangColumn));

        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().satuanProperty());
        satuanColumn.setCellFactory(col -> Function.getWrapTreeTableCell(satuanColumn));

        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().qtyProperty());
        qtyColumn.setCellFactory(col -> Function.getTreeTableCell());

        hargaBeliColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().hargaBeliProperty());
        hargaBeliColumn.setCellFactory(col -> Function.getTreeTableCell());

        totalColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().totalProperty());
        totalColumn.setCellFactory(col -> Function.getTreeTableCell());

        tglPembelianMulaiPicker.setConverter(Function.getTglConverter());
        tglPembelianMulaiPicker.setValue(LocalDate.now().minusMonths(1));
        tglPembelianMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglPembelianAkhirPicker));
        tglPembelianAkhirPicker.setConverter(Function.getTglConverter());
        tglPembelianAkhirPicker.setValue(LocalDate.now());
        tglPembelianAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglPembelianMulaiPicker));

        allPembelian.addListener((ListChangeListener.Change<? extends PembelianDetail> change) -> {
            searchPembelian();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchPembelian();
                });
        filterData.addAll(allPembelian);
        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getPembelian();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        pembelianDetailTable.setContextMenu(rm);
        pembelianDetailTable.setRowFactory((TreeTableView<PembelianDetail> tableView) -> {
            final TreeTableRow<PembelianDetail> row = new TreeTableRow<PembelianDetail>() {
                @Override
                public void updateItem(PembelianDetail item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem detail = new MenuItem("Detail Pembelian");
                        detail.setOnAction((ActionEvent e) -> {
                            lihatDetailPembelian(item.getPembelianHead());
                        });
                        MenuItem pembayaran = new MenuItem("Detail Pembayaran Pembelian");
                        pembayaran.setOnAction((ActionEvent e) -> {
                            showDetailHutang(item.getPembelianHead());
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getPembelian();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Detail Pembelian") && o.isStatus()
                                    && item.getPembelianHead().getStatus() != null) {
                                rm.getItems().add(detail);
                            }
                            if (o.getJenis().equals("Detail Pembayaran Pembelian") && o.isStatus()
                                    && item.getPembelianHead().getPembayaran() > 0
                                    && item.getPembelianHead().getStatus() != null) {
                                rm.getItems().add(pembayaran);
                            }
                            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                                rm.getItems().addAll(export);
                            }
                        }
                        rm.getItems().addAll(refresh);
                        setContextMenu(rm);
                    }
                }
            };
            return row;
        });
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.add("No Pembelian");
        groupBy.add("Supplier");
        groupBy.add("Barang");
        groupBy.add("Tanggal");
        groupBy.add("Bulan");
        groupBy.add("Tahun");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().select("No Pembelian");
        getPembelian();
    }

    @FXML
    private void getPembelian() {
        Task<List<PembelianDetail>> task = new Task<List<PembelianDetail>>() {
            @Override
            public List<PembelianDetail> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<PembelianHead> pembelian = PembelianHeadDAO.getAllByDateAndStatus(con,
                            tglPembelianMulaiPicker.getValue().toString(), tglPembelianAkhirPicker.getValue().toString(), "true");
                    List<PembelianDetail> temp = PembelianDetailDAO.getAllByDateAndStatus(con,
                            tglPembelianMulaiPicker.getValue().toString(), tglPembelianAkhirPicker.getValue().toString(), "true");
                    List<Supplier> supplier = SupplierDAO.getAllByStatus(con, "%");
                    for (PembelianDetail d : temp) {
                        for (PembelianHead h : pembelian) {
                            if (d.getNoPembelian().equals(h.getNoPembelian())) {
                                d.setPembelianHead(h);
                            }
                        }
                        for (Supplier c : supplier) {
                            if (d.getPembelianHead().getKodeSupplier().equals(c.getKodeSupplier())) {
                                d.getPembelianHead().setSupplier(c);
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
            mainApp.closeLoading();
            allPembelian.clear();
            allPembelian.addAll(task.getValue());
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

    private void searchPembelian() {
        try {
            filterData.clear();
            for (PembelianDetail temp : allPembelian) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPembelian())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getPembelianHead().getTglPembelian())))
                            || checkColumn(temp.getPembelianHead().getKodeSupplier())
                            || checkColumn(temp.getPembelianHead().getSupplier().getNama())
                            || checkColumn(temp.getPembelianHead().getPaymentTerm())
                            || checkColumn(temp.getPembelianHead().getCatatan())
                            || checkColumn(temp.getKodeBarang())
                            || checkColumn(df.format(temp.getHargaBeli()))
                            || checkColumn(temp.getNamaBarang())
                            || checkColumn(df.format(temp.getQty()))
                            || checkColumn(temp.getKeterangan())
                            || checkColumn(temp.getSatuan())
                            || checkColumn(df.format(temp.getNilai()))
                            || checkColumn(df.format(temp.getTotal()))
                            || checkColumn(df.format(temp.getNilai() * temp.getQty()))) {
                        filterData.add(temp);
                    }
                }
            }
            setTable();
            hitungTotal();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void hitungTotal() {
        double totalQty = 0;
        double totalJual = 0;
        for (PembelianDetail temp : filterData) {
            totalQty = totalQty + temp.getQty();
            totalJual = totalJual + temp.getTotal();
        }
        totalQtyField.setText(df.format(totalQty));
        totalPembelianField.setText(df.format(totalJual));
    }

    private void setTable() throws Exception {
        if (pembelianDetailTable.getRoot() != null) {
            pembelianDetailTable.getRoot().getChildren().clear();
        }
        List<String> groupBy = new ArrayList<>();
        for (PembelianDetail temp : filterData) {
            String group = "";
            if (groupByCombo.getSelectionModel().getSelectedItem().equals("No Pembelian")) {
                group = temp.getNoPembelian();
            } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Supplier")) {
                group = temp.getPembelianHead().getSupplier().getNama();
            } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Barang")) {
                group = temp.getKodeBarang();
            } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tanggal")) {
                group = tgl.format(tglSql.parse(temp.getPembelianHead().getTglPembelian()));
            } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Bulan")) {
                group = new SimpleDateFormat("MMM yyyy").format(tglSql.parse(temp.getPembelianHead().getTglPembelian()));
            } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tahun")) {
                group = new SimpleDateFormat("yyyy").format(tglSql.parse(temp.getPembelianHead().getTglPembelian()));
            }
            if (!groupBy.contains(group)) {
                groupBy.add(group);
            }
        }
        for (String temp : groupBy) {
            PembelianDetail head = new PembelianDetail();
            head.setNoPembelian(temp);
            head.setPembelianHead(new PembelianHead());
            head.getPembelianHead().setSupplier(new Supplier());
            TreeItem<PembelianDetail> parent = new TreeItem<>(head);
            double totalQty = 0;
            double totalNilai = 0;
            double totalHarga = 0;
            for (PembelianDetail detail : filterData) {
                boolean status = false;
                if (groupByCombo.getSelectionModel().getSelectedItem().equals("No Pembelian") && temp.equals(detail.getNoPembelian())) {
                    status = true;
                } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tanggal")
                        && temp.equals(tgl.format(tglSql.parse(detail.getPembelianHead().getTglPembelian())))) {
                    status = true;
                } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Bulan")
                        && temp.equals(new SimpleDateFormat("MMM yyyy").format(tglSql.parse(detail.getPembelianHead().getTglPembelian())))) {
                    status = true;
                } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tahun")
                        && temp.equals(new SimpleDateFormat("yyyy").format(tglSql.parse(detail.getPembelianHead().getTglPembelian())))) {
                    status = true;
                } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Supplier") && temp.equals(detail.getPembelianHead().getSupplier().getNama())) {
                    status = true;
                } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Barang") && temp.equals(detail.getKodeBarang())) {
                    status = true;
                }
                if (status) {
                    totalQty = totalQty + detail.getQty();
                    totalNilai = totalNilai + (detail.getNilai() * detail.getQty());
                    totalHarga = totalHarga + detail.getTotal();
                    parent.getChildren().addAll(new TreeItem<>(detail));
                }
            }
            parent.getValue().setQty(totalQty);
            parent.getValue().setNilai(totalNilai / totalQty);
            parent.getValue().setHargaBeli(totalHarga / totalQty);
            parent.getValue().setTotal(totalHarga);
            root.getChildren().add(parent);
        }
        pembelianDetailTable.setRoot(root);
    }

    private void lihatDetailPembelian(PembelianHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPembelian.fxml");
        NewPembelianController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPembelian(p.getNoPembelian());
    }

    private void showDetailHutang(PembelianHead p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailHutang.fxml");
        DetailHutangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setDetailPembelian(p);
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

                Sheet sheet = workbook.createSheet("Laporan Barang Terjual");
                int rc = 0;
                int c = 11;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tanggal : "
                        + tgl.format(tglBarang.parse(tglPembelianMulaiPicker.getValue().toString())) + "-"
                        + tgl.format(tglBarang.parse(tglPembelianAkhirPicker.getValue().toString())));
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Pembelian");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Pembelian");
                sheet.getRow(rc).getCell(2).setCellValue("Supplier");
                sheet.getRow(rc).getCell(3).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(4).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(5).setCellValue("Satuan");
                sheet.getRow(rc).getCell(6).setCellValue("Qty");
                sheet.getRow(rc).getCell(7).setCellValue("Harga");
                sheet.getRow(rc).getCell(8).setCellValue("Total Harga");
                rc++;
                List<String> groupBy = new ArrayList<>();
                for (PembelianDetail temp : filterData) {
                    String group = "";
                    if (groupByCombo.getSelectionModel().getSelectedItem().equals("No Pembelian")) {
                        group = temp.getNoPembelian();
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Supplier")) {
                        group = temp.getPembelianHead().getSupplier().getNama();
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Barang")) {
                        group = temp.getNamaBarang();
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tanggal")) {
                        group = tgl.format(tglSql.parse(temp.getPembelianHead().getTglPembelian()));
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Bulan")) {
                        group = new SimpleDateFormat("MMM yyyy").format(tglSql.parse(temp.getPembelianHead().getTglPembelian()));
                    } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tahun")) {
                        group = new SimpleDateFormat("yyyy").format(tglSql.parse(temp.getPembelianHead().getTglPembelian()));
                    }
                    if (!groupBy.contains(group)) {
                        groupBy.add(group);
                    }
                }
                double grandtotalQty = 0;
                double grandtotalHarga = 0;
                for (String temp : groupBy) {
                    rc++;
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue(temp);
                    rc++;
                    double totalQty = 0;
                    double totalHarga = 0;
                    for (PembelianDetail detail : filterData) {
                        boolean status = false;
                        if (groupByCombo.getSelectionModel().getSelectedItem().equals("No Pembelian")
                                && temp.equals(detail.getNoPembelian())) {
                            status = true;
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tanggal")
                                && temp.equals(tgl.format(tglSql.parse(detail.getPembelianHead().getTglPembelian())))) {
                            status = true;
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Bulan")
                                && temp.equals(new SimpleDateFormat("MMM yyyy").format(tglSql.parse(detail.getPembelianHead().getTglPembelian())))) {
                            status = true;
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Tahun")
                                && temp.equals(new SimpleDateFormat("yyyy").format(tglSql.parse(detail.getPembelianHead().getTglPembelian())))) {
                            status = true;
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Supplier")
                                && temp.equals(detail.getPembelianHead().getSupplier().getNama())) {
                            status = true;
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Barang")
                                && temp.equals(detail.getNamaBarang())) {
                            status = true;
                        }
                        if (status) {
                            createRow(workbook, sheet, rc, c, "Detail");
                            sheet.getRow(rc).getCell(0).setCellValue(detail.getPembelianHead().getNoPembelian());
                            sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(detail.getPembelianHead().getTglPembelian())));
                            sheet.getRow(rc).getCell(2).setCellValue(detail.getPembelianHead().getSupplier().getNama());
                            sheet.getRow(rc).getCell(3).setCellValue(detail.getKodeBarang());
                            sheet.getRow(rc).getCell(4).setCellValue(detail.getNamaBarang());
                            sheet.getRow(rc).getCell(5).setCellValue(detail.getSatuan());
                            sheet.getRow(rc).getCell(6).setCellValue(detail.getQty());
                            sheet.getRow(rc).getCell(7).setCellValue(detail.getHargaBeli());
                            sheet.getRow(rc).getCell(8).setCellValue(detail.getTotal());
                            rc++;

                            totalQty = totalQty + detail.getQty();
                            totalHarga = totalHarga + detail.getTotal();
                        }
                    }
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue("Total " + temp);
                    sheet.getRow(rc).getCell(6).setCellValue(totalQty);
                    sheet.getRow(rc).getCell(7).setCellValue(totalHarga / totalQty);
                    sheet.getRow(rc).getCell(8).setCellValue(totalHarga);
                    rc++;
                    grandtotalQty = grandtotalQty + totalQty;
                    grandtotalHarga = grandtotalHarga + totalHarga;
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total");
                sheet.getRow(rc).getCell(6).setCellValue(grandtotalQty);
                sheet.getRow(rc).getCell(7).setCellValue(grandtotalHarga / grandtotalQty);
                sheet.getRow(rc).getCell(8).setCellValue(grandtotalHarga);
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
