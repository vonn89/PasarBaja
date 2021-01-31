/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.DAO.PenyesuaianStokBarangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import static com.excellentsystem.PasarBaja.Function.getTableCell;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tgl;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.PenyesuaianStokBarang;
import com.excellentsystem.PasarBaja.View.Dialog.PenyesuaianStokController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.time.LocalDate;
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
public class LaporanPenyesuaianStokBarangController {

    @FXML
    private TableView<PenyesuaianStokBarang> penyesuaianStokTable;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> noPenyesuaianColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> tglPenyesuaianColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> kodeBarangColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> namaBarangColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> statusColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, Number> qtyColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> catatanColumn;
    @FXML
    private TableColumn<PenyesuaianStokBarang, String> kodeUserColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label totalQtyField;
    @FXML
    private DatePicker tglMulaiPenyesuaianPicker;
    @FXML
    private DatePicker tglAkhirPenyesuaianPicker;
    private ObservableList<PenyesuaianStokBarang> allPenyesuaianStok = FXCollections.observableArrayList();
    private ObservableList<PenyesuaianStokBarang> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPenyesuaianColumn.setCellValueFactory(cellData -> cellData.getValue().noPenyesuaianProperty());
        tglPenyesuaianColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglPenyesuaian())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPenyesuaianColumn.setComparator(Function.sortDate(tglLengkap));
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().getBarang().namaBarangProperty());
        statusColumn.setCellValueFactory(cellData -> {
            double qty = cellData.getValue().getQty();
            if (qty < 0) {
                return new SimpleStringProperty("Pengurangan Stok");
            } else {
                return new SimpleStringProperty("Penambahan Stok");
            }
        });
        qtyColumn.setCellValueFactory(cellData -> {
            double qty = cellData.getValue().getQty();
            if (qty < 0) {
                qty = qty * -1;
            }
            return new SimpleDoubleProperty(qty);
        });
        qtyColumn.setCellFactory((c) -> getTableCell());
        catatanColumn.setCellValueFactory(cellData -> cellData.getValue().catatanProperty());
        kodeUserColumn.setCellValueFactory(cellData -> cellData.getValue().kodeUserProperty());

        tglMulaiPenyesuaianPicker.setConverter(Function.getTglConverter());
        tglMulaiPenyesuaianPicker.setValue(LocalDate.now().minusMonths(1));
        tglMulaiPenyesuaianPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPenyesuaianPicker));
        tglAkhirPenyesuaianPicker.setConverter(Function.getTglConverter());
        tglAkhirPenyesuaianPicker.setValue(LocalDate.now());
        tglAkhirPenyesuaianPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglMulaiPenyesuaianPicker));
        allPenyesuaianStok.addListener((ListChangeListener.Change<? extends PenyesuaianStokBarang> change) -> {
            searchPenyesuaianStok();
        });
        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchPenyesuaianStok();
        });
        filterData.addAll(allPenyesuaianStok);
        penyesuaianStokTable.setItems(filterData);
        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getPenyesuaianStok();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        penyesuaianStokTable.setContextMenu(rm);
        penyesuaianStokTable.setRowFactory(ttv -> {
            TableRow<PenyesuaianStokBarang> row = new TableRow<PenyesuaianStokBarang>() {
                @Override
                public void updateItem(PenyesuaianStokBarang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        ContextMenu rm = new ContextMenu();
                        MenuItem lihat = new MenuItem("Lihat Detail Penyesuaian Stok");
                        lihat.setOnAction((ActionEvent event) -> {
                            showDetailPenyesuaianStok(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            getPenyesuaianStok();
                        });
                        rm.getItems().addAll(lihat);
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                                rm.getItems().addAll(export);
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
                        showDetailPenyesuaianStok(row.getItem());
                    }
                }
            });
            return row;
        });
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        getPenyesuaianStok();
    }

    @FXML
    private void getPenyesuaianStok() {
        Task<List<PenyesuaianStokBarang>> task = new Task<List<PenyesuaianStokBarang>>() {
            @Override
            public List<PenyesuaianStokBarang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    List<Barang> listBarang = BarangDAO.getAllByStatus(con, "%");
                    List<PenyesuaianStokBarang> listHead = PenyesuaianStokBarangDAO.getAllByDateAndBarang(con,
                            tglMulaiPenyesuaianPicker.getValue().toString(), tglAkhirPenyesuaianPicker.getValue().toString(), "%", "%");
                    for (PenyesuaianStokBarang d : listHead) {
                        for (Barang b : listBarang) {
                            if (d.getKodeBarang().equals(b.getKodeBarang())) {
                                d.setBarang(b);
                            }
                        }
                    }
                    return listHead;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allPenyesuaianStok.clear();
            allPenyesuaianStok.addAll(task.getValue());
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

    private void searchPenyesuaianStok() {
        try {
            filterData.clear();
            for (PenyesuaianStokBarang temp : allPenyesuaianStok) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPenyesuaian())
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglPenyesuaian())))
                            || checkColumn(temp.getCatatan())
                            || checkColumn(temp.getKodeUser())
                            || checkColumn(temp.getKodeBarang())
                            || checkColumn(temp.getBarang().getNamaBarang())
                            || checkColumn(df.format(temp.getQty()))) {
                        filterData.add(temp);
                    }
                }
            }
            hitungTotal();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void showDetailPenyesuaianStok(PenyesuaianStokBarang p) {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, child, "View/Dialog/Child/PenyesuaianStok.fxml");
        PenyesuaianStokController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, child);
        controller.setPenyesuaianStokBarang(p.getNoPenyesuaian());
    }

    private void hitungTotal() {
        double totalQty = 0;
        for (PenyesuaianStokBarang temp : filterData) {
            totalQty = totalQty + temp.getQty();
        }
        totalQtyField.setText(df.format(totalQty));
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
                Sheet sheet = workbook.createSheet("Laporan Penyesuaian Stok Barang");
                int rc = 0;
                int c = 8;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Tgl Penyesuaian Stok : "
                        + tgl.format(tglBarang.parse(tglMulaiPenyesuaianPicker.getValue().toString())) + " - "
                        + tgl.format(tglBarang.parse(tglAkhirPenyesuaianPicker.getValue().toString())));
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Penyesuaian");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Penyesuaian");
                sheet.getRow(rc).getCell(2).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(3).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(4).setCellValue("Status");
                sheet.getRow(rc).getCell(5).setCellValue("Qty");
                sheet.getRow(rc).getCell(6).setCellValue("Catatan");
                sheet.getRow(rc).getCell(7).setCellValue("Kode User");
                rc++;
                double totalPenambahan = 0;
                double totalPengurangan = 0;
                for (PenyesuaianStokBarang s : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(s.getNoPenyesuaian());
                    sheet.getRow(rc).getCell(1).setCellValue(s.getTglPenyesuaian());
                    sheet.getRow(rc).getCell(2).setCellValue(s.getKodeBarang());
                    sheet.getRow(rc).getCell(3).setCellValue(s.getBarang().getNamaBarang());
                    if (s.getQty() < 0) {
                        sheet.getRow(rc).getCell(4).setCellValue("Pengurangan Stok");
                        sheet.getRow(rc).getCell(5).setCellValue(s.getQty() * -1);
                        totalPengurangan = totalPengurangan + (s.getQty() * -1);
                    } else {
                        sheet.getRow(rc).getCell(4).setCellValue("Penambahan Stok");
                        sheet.getRow(rc).getCell(5).setCellValue(s.getQty());
                        totalPenambahan = totalPenambahan + s.getQty();
                    }
                    sheet.getRow(rc).getCell(6).setCellValue(s.getCatatan());
                    sheet.getRow(rc).getCell(7).setCellValue(s.getKodeUser());
                    rc++;
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(1).setCellValue("Total Penambahan Stok");
                sheet.getRow(rc).getCell(5).setCellValue(totalPenambahan);
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(1).setCellValue("Total Pengurangan Stok");
                sheet.getRow(rc).getCell(5).setCellValue(totalPengurangan);
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
