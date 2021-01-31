/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
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
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.Customer;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import com.excellentsystem.PasarBaja.Model.PemesananHead;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author excellent
 */
public class PermintaanBarangController {

    @FXML
    private TableView<PemesananDetail> permintaanTable;
    @FXML
    private TableColumn<PemesananDetail, Boolean> checkColumn;
    @FXML
    private TableColumn<PemesananDetail, String> noPemesananColumn;
    @FXML
    private TableColumn<PemesananDetail, String> tglPemesananColumn;
    @FXML
    private TableColumn<PemesananDetail, String> namaCustomerColumn;
    @FXML
    private TableColumn<PemesananDetail, String> alamatCustomerColumn;
    @FXML
    private TableColumn<PemesananDetail, String> kodeBarangColumn;
    @FXML
    private TableColumn<PemesananDetail, String> namaBarangColumn;
    @FXML
    private TableColumn<PemesananDetail, String> keteranganColumn;
    @FXML
    private TableColumn<PemesananDetail, String> catatanInternColumn;
    @FXML
    private TableColumn<PemesananDetail, String> satuanColumn;
    @FXML
    private TableColumn<PemesananDetail, Number> qtyColumn;
    @FXML
    private TableColumn<PemesananDetail, Number> qtyTerkirimColumn;
    @FXML
    private TableColumn<PemesananDetail, Number> qtySisaColumn;
    @FXML
    private TableColumn<PemesananDetail, Number> tonaseColumn;

    @FXML
    private CheckBox checkAll;
    @FXML
    private Label totalQtyLabel;
    @FXML
    private Label totalTonaseLabel;
    @FXML
    private TextField searchField;
    @FXML
    private DatePicker tglMulaiPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    @FXML
    private ComboBox<String> groupByCombo;
    private ObservableList<PemesananDetail> allPermintaan = FXCollections.observableArrayList();
    private ObservableList<PemesananDetail> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPemesananColumn.setCellValueFactory(cellData -> cellData.getValue().noPemesananProperty());
        noPemesananColumn.setCellFactory(col -> Function.getWrapTableCell(noPemesananColumn));

        tglPemesananColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getPemesananHead().getTglPemesanan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPemesananColumn.setCellFactory(col -> Function.getWrapTableCell(tglPemesananColumn));
        tglPemesananColumn.setComparator(Function.sortDate(tglLengkap));

        namaCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getPemesananHead().getCustomer().namaProperty());
        namaCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(namaCustomerColumn));

        alamatCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().getPemesananHead().getCustomer().alamatProperty());
        alamatCustomerColumn.setCellFactory(col -> Function.getWrapTableCell(alamatCustomerColumn));

        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        kodeBarangColumn.setCellFactory(col -> Function.getWrapTableCell(kodeBarangColumn));

        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().namaBarangProperty());
        namaBarangColumn.setCellFactory(col -> Function.getWrapTableCell(namaBarangColumn));

        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        keteranganColumn.setCellFactory(col -> Function.getWrapTableCell(keteranganColumn));

        catatanInternColumn.setCellValueFactory(cellData -> cellData.getValue().catatanInternProperty());
        catatanInternColumn.setCellFactory(col -> Function.getWrapTableCell(catatanInternColumn));

        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().satuanProperty());
        satuanColumn.setCellFactory(col -> Function.getWrapTableCell(satuanColumn));

        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().qtyProperty());
        qtyColumn.setCellFactory(col -> Function.getTableCell());

        qtyTerkirimColumn.setCellValueFactory(cellData -> cellData.getValue().qtyTerkirimProperty());
        qtyTerkirimColumn.setCellFactory(col -> Function.getTableCell());

        qtySisaColumn.setCellValueFactory(cellData -> {
            return new SimpleDoubleProperty(cellData.getValue().getQty() - cellData.getValue().getQtyTerkirim());
        });
        qtySisaColumn.setCellFactory(col -> Function.getTableCell());

        tonaseColumn.setCellValueFactory(cellData -> {
            return new SimpleDoubleProperty((cellData.getValue().getQty() - cellData.getValue().getQtyTerkirim()) * cellData.getValue().getBarang().getBerat());
        });
        tonaseColumn.setCellFactory(col -> Function.getTableCell());

        checkColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn((Integer v) -> {
            return permintaanTable.getItems().get(v).statusProperty();
        }));

        tglMulaiPicker.setConverter(Function.getTglConverter());
        tglMulaiPicker.setValue(LocalDate.now().minusYears(1));
        tglMulaiPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPicker));
        final ContextMenu rm = new ContextMenu();
        MenuItem spk = new MenuItem("Print SPK");
        spk.setOnAction((ActionEvent e) -> {
            printSPK();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getPermintaan();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Print SPK") && o.isStatus()) {
                rm.getItems().add(spk);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        permintaanTable.setContextMenu(rm);
        permintaanTable.setRowFactory((TableView<PemesananDetail> tableView) -> {
            final TableRow<PemesananDetail> row = new TableRow<PemesananDetail>() {
            };
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                        && mouseEvent.getClickCount() == 2) {
                    if (row.getItem() != null) {
                        if (row.getItem().isStatus()) {
                            row.getItem().setStatus(false);
                        } else {
                            row.getItem().setStatus(true);
                        }
                        hitungTotal();
                    }
                }
            });
            return row;
        });
        allPermintaan.addListener((ListChangeListener.Change<? extends PemesananDetail> change) -> {
            searchPermintaan();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchPermintaan();
                });
        filterData.addAll(allPermintaan);
        permintaanTable.setItems(filterData);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.clear();
        groupBy.add("Wait");
        groupBy.add("Done");
        groupBy.add("Semua");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().select("Wait");
        getPermintaan();
    }

    private void hitungTotal() {
        double qty = 0;
        double berat = 0;
        for (PemesananDetail d : filterData) {
            if (d.isStatus()) {
                qty = qty + d.getQty() - d.getQtyTerkirim();
                berat = berat + (d.getQty() - d.getQtyTerkirim()) * d.getBarang().getBerat();
            }
        }
        totalQtyLabel.setText(df.format(qty));
        totalTonaseLabel.setText(df.format(berat));
    }

    @FXML
    private void getPermintaan() {
        Task<List<PemesananDetail>> task = new Task<List<PemesananDetail>>() {
            @Override
            public List<PemesananDetail> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<Customer> allCustomer = CustomerDAO.getAllByStatus(con, "%");
                    List<Barang> allBarang = BarangDAO.getAllByStatus(con, "%");
                    List<PemesananDetail> allPemesananDetail = PemesananDetailDAO.getAllByDateAndStatus(con,
                            tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), "open");
                    List<PemesananHead> allPemesanan = PemesananHeadDAO.getAllByDateAndStatus(con,
                            tglMulaiPicker.getValue().toString(), tglAkhirPicker.getValue().toString(), "open");
                    for (PemesananHead p : allPemesanan) {
                        List<PemesananDetail> detail = new ArrayList<>();
                        for (PemesananDetail d : allPemesananDetail) {
                            if (d.getNoPemesanan().equals(p.getNoPemesanan())) {
                                detail.add(d);
                            }
                        }
                        p.setListPemesananDetail(detail);
                    }
                    List<PemesananDetail> listPemesananDetail = new ArrayList<>();
                    for (PemesananDetail d : allPemesananDetail) {
                        for (PemesananHead h : allPemesanan) {
                            if (d.getNoPemesanan().equals(h.getNoPemesanan())) {
                                d.setPemesananHead(h);
                            }
                        }
                        for (Customer c : allCustomer) {
                            if (d.getPemesananHead().getKodeCustomer().equals(c.getKodeCustomer())) {
                                d.getPemesananHead().setCustomer(c);
                            }
                        }
                        for (Barang b : allBarang) {
                            if (d.getKodeBarang().equals(b.getKodeBarang())) {
                                d.setBarang(b);
                            }
                        }
                        if (groupByCombo.getSelectionModel().getSelectedItem().equals("Done") && d.getQty() - d.getQtyTerkirim() == 0) {
                            listPemesananDetail.add(d);
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Wait") && d.getQty() - d.getQtyTerkirim() != 0) {
                            listPemesananDetail.add(d);
                        } else if (groupByCombo.getSelectionModel().getSelectedItem().equals("Semua")) {
                            listPemesananDetail.add(d);
                        }
                    }
                    return listPemesananDetail;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allPermintaan.clear();
            allPermintaan.addAll(task.getValue());
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            task.getException().printStackTrace();
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    @FXML
    private void checkAllHandle() {
        for (PemesananDetail d : allPermintaan) {
            d.setStatus(checkAll.isSelected());
        }
        hitungTotal();
        permintaanTable.refresh();
    }

    private Boolean checkColumn(String column) {
        if (column != null) {
            if (column.toLowerCase().contains(searchField.getText().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void searchPermintaan() {
        try {
            filterData.clear();
            for (PemesananDetail temp : allPermintaan) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPemesanan())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getPemesananHead().getTglPemesanan())))
                            || checkColumn(temp.getPemesananHead().getKodeCustomer())
                            || checkColumn(temp.getPemesananHead().getCustomer().getNama())
                            || checkColumn(temp.getPemesananHead().getCustomer().getAlamat())
                            || checkColumn(df.format(temp.getPemesananHead().getTotalPemesanan()))
                            || checkColumn(df.format(temp.getPemesananHead().getDownPayment()))
                            || checkColumn(temp.getPemesananHead().getCatatan())
                            || checkColumn(temp.getPemesananHead().getStatus())
                            || checkColumn(temp.getPemesananHead().getKodeUser())
                            || checkColumn(temp.getKodeBarang())
                            || checkColumn(temp.getNamaBarang())
                            || checkColumn(temp.getKeterangan())
                            || checkColumn(temp.getCatatanIntern())
                            || checkColumn(df.format(temp.getQty()))
                            || checkColumn(df.format(temp.getQtyTerkirim()))
                            || checkColumn(temp.getSatuan())) {
                        filterData.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
            e.printStackTrace();
        }
    }

    private void printSPK() {
//        try{
//            List<PemesananDetail> spk = new ArrayList<>();
//            for(PemesananDetail b : filterData){
//                if(b.isStatus())
//                    spk.add(b);
//            }
//            if(spk.isEmpty()){
//                mainApp.showMessage(Modality.NONE, "Warning", "Data permintaan barang belum di pilih");
//            }else{
//                Report report = new Report();
//                report.printSPK(spk);
//            }
//        }catch(Exception e){
//            mainApp.showMessage(Modality.NONE, "Error", e.toString());
//        }
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
                Sheet sheet = workbook.createSheet("Data Permintaan Barang");
                int rc = 0;
                int c = 13;
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
                sheet.getRow(rc).getCell(3).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(4).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(5).setCellValue("Satuan");
                sheet.getRow(rc).getCell(6).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(7).setCellValue("Catatan Intern");
                sheet.getRow(rc).getCell(8).setCellValue("Qty Order");
                sheet.getRow(rc).getCell(9).setCellValue("Qty Terkirim");
                sheet.getRow(rc).getCell(10).setCellValue("Qty Sisa");
                sheet.getRow(rc).getCell(11).setCellValue("Tonase");
                rc++;
                double qty = 0;
                double qtyTerkirim = 0;
                double berat = 0;
                for (PemesananDetail b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoPemesanan());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getPemesananHead().getTglPemesanan())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getPemesananHead().getCustomer().getNama());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getKodeBarang());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getNamaBarang());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getSatuan());
                    sheet.getRow(rc).getCell(6).setCellValue(b.getKeterangan());
                    sheet.getRow(rc).getCell(7).setCellValue(b.getKeterangan());
                    sheet.getRow(rc).getCell(8).setCellValue(b.getQty());
                    sheet.getRow(rc).getCell(9).setCellValue(b.getQtyTerkirim());
                    sheet.getRow(rc).getCell(10).setCellValue(b.getQty() - b.getQtyTerkirim());
                    sheet.getRow(rc).getCell(11).setCellValue((b.getQty() - b.getQtyTerkirim()) * b.getBarang().getBerat());
                    rc++;
                    qty = qty + b.getQty();
                    qtyTerkirim = qtyTerkirim + b.getQtyTerkirim();
                    berat = berat + (b.getQty() - b.getQtyTerkirim()) * b.getBarang().getBerat();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(8).setCellValue(qty);
                sheet.getRow(rc).getCell(9).setCellValue(qtyTerkirim);
                sheet.getRow(rc).getCell(10).setCellValue(qty - qtyTerkirim);
                sheet.getRow(rc).getCell(11).setCellValue(berat);
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
