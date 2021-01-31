/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailBarangController;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;
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
public class DataBarangController {

    @FXML
    private TableView<Barang> barangTable;
    @FXML
    private TableColumn<Barang, String> kodeBarangColumn;
    @FXML
    private TableColumn<Barang, String> namaBarangColumn;
    @FXML
    private TableColumn<Barang, Number> beratColumn;
    @FXML
    private TableColumn<Barang, String> satuanColumn;
    @FXML
    private TableColumn<Barang, Number> hargaJualColumn;
    @FXML
    private TextField searchField;

    private ObservableList<Barang> allBarang = FXCollections.observableArrayList();
    private ObservableList<Barang> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        kodeBarangColumn.setCellFactory(col -> Function.getWrapTableCell(kodeBarangColumn));

        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().namaBarangProperty());
        namaBarangColumn.setCellFactory(col -> Function.getWrapTableCell(namaBarangColumn));

        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().satuanProperty());
        satuanColumn.setCellFactory(col -> Function.getWrapTableCell(satuanColumn));

        beratColumn.setCellValueFactory(cellData -> cellData.getValue().beratProperty());
        beratColumn.setCellFactory(col -> Function.getTableCell());

        hargaJualColumn.setCellValueFactory(cellData -> cellData.getValue().hargaJualProperty());
        hargaJualColumn.setCellFactory(col -> Function.getTableCell());

        final ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Barang");
        addNew.setOnAction((ActionEvent e) -> {
            addNew();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getBarang();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Barang") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        barangTable.setContextMenu(rm);
        barangTable.setRowFactory((TableView<Barang> tableView) -> {
            final TableRow<Barang> row = new TableRow<Barang>() {
                @Override
                public void updateItem(Barang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Barang");
                        addNew.setOnAction((ActionEvent e) -> {
                            addNew();
                        });
                        MenuItem edit = new MenuItem("Edit Barang");
                        edit.setOnAction((ActionEvent e) -> {
                            editBarang(item);
                        });
                        MenuItem hapus = new MenuItem("Delete Barang");
                        hapus.setOnAction((ActionEvent e) -> {
                            hapusBarang(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getBarang();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Barang") && o.isStatus()) {
                                rm.getItems().add(addNew);
                            }
                            if (o.getJenis().equals("Edit Barang") && o.isStatus()) {
                                rm.getItems().add(edit);
                            }
                            if (o.getJenis().equals("Delete Barang") && o.isStatus()) {
                                rm.getItems().add(hapus);
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
                            if (o.getJenis().equals("Edit Barang") && o.isStatus()) {
                                editBarang(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allBarang.addListener((ListChangeListener.Change<? extends Barang> change) -> {
            searchBarang();
        });
        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchBarang();
        });
        filterData.addAll(allBarang);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        getBarang();
        barangTable.setItems(filterData);
    }

    private void getBarang() {
        Task<List<Barang>> task = new Task<List<Barang>>() {
            @Override
            public List<Barang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return BarangDAO.getAllByStatus(con, "true");
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allBarang.clear();
            allBarang.addAll(task.getValue());
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

    private void searchBarang() {
        filterData.clear();
        for (Barang temp : allBarang) {
            if (searchField.getText() == null || searchField.getText().equals("")) {
                filterData.add(temp);
            } else {
                if (checkColumn(temp.getKodeBarang())
                        || checkColumn(temp.getNamaBarang())
                        || checkColumn(df.format(temp.getBerat()))
                        || checkColumn(temp.getSatuan())
                        || checkColumn(df.format(temp.getHargaJual()))) {
                    filterData.add(temp);
                }
            }
        }
    }

    private void addNew() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailBarang.fxml");
        DetailBarangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setBarang(null);
        x.saveButton.setOnAction((ActionEvent event) -> {
            if ("".equals(x.kodeBarangField.getText())) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kode Barang masih kosong");
            } else if (x.beratField.getText().equals("") || x.beratField.getText().equals("0")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Berat masih kosong");
            } else if (x.hargaJualField.getText().equals("") || x.hargaJualField.getText().equals("0")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Harga jual masih kosong");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Barang b = new Barang();
                            b.setKodeBarang(x.kodeBarangField.getText());
                            b.setNamaBarang(x.namaBarangField.getText());
                            b.setSatuan(x.satuanField.getText());
                            b.setBerat(Double.parseDouble(x.beratField.getText().replaceAll(",", "")));
                            b.setHargaJual(Double.parseDouble(x.hargaJualField.getText().replaceAll(",", "")));
                            b.setStatus("true");
                            return Service.newBarang(con, b);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getBarang();
                    if (task.getValue().equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Data barang berhasil disimpan");
                        mainApp.closeDialog(mainApp.MainStage, stage);
                    } else {
                        mainApp.showMessage(Modality.NONE, "Error", task.getValue());
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

    private void editBarang(Barang b) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailBarang.fxml");
        DetailBarangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setBarang(b);
        x.saveButton.setOnAction((ActionEvent event) -> {
            if ("".equals(x.kodeBarangField.getText())) {
                mainApp.showMessage(Modality.NONE, "Warning", "Barang belum dipilih");
            } else if (x.beratField.getText().equals("") || x.beratField.getText().equals("0")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Berat masih kosong");
            } else if (x.hargaJualField.getText().equals("") || x.hargaJualField.getText().equals("0")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Harga jual masih kosong");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            b.setNamaBarang(x.namaBarangField.getText());
                            b.setSatuan(x.satuanField.getText());
                            b.setBerat(Double.parseDouble(x.beratField.getText().replaceAll(",", "")));
                            b.setHargaJual(Double.parseDouble(x.hargaJualField.getText().replaceAll(",", "")));
                            return Service.updateBarang(con, b);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getBarang();
                    if (task.getValue().equals("true")) {
                        mainApp.showMessage(Modality.NONE, "Success", "Data barang berhasil disimpan");
                        mainApp.closeDialog(mainApp.MainStage, stage);
                    } else {
                        mainApp.showMessage(Modality.NONE, "Error", task.getValue());
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

    private void hapusBarang(Barang b) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Delete barang " + b.getKodeBarang() + "-" + b.getNamaBarang() + " ?");
        controller.OK.setOnAction((ActionEvent ev) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.deleteBarang(con, b);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getBarang();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Data barang berhasil dihapus");
                } else {
                    mainApp.showMessage(Modality.NONE, "Error", task.getValue());
                }
            });
            task.setOnFailed((e) -> {
                mainApp.closeLoading();
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            });
            new Thread(task).start();
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
                Sheet sheet = workbook.createSheet("Data Barang");
                int rc = 0;
                int c = 6;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Kode Barang");
                sheet.getRow(rc).getCell(1).setCellValue("Nama Barang");
                sheet.getRow(rc).getCell(2).setCellValue("Satuan");
                sheet.getRow(rc).getCell(3).setCellValue("Berat");
                sheet.getRow(rc).getCell(4).setCellValue("Harga Jual");
                rc++;
                for (Barang b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getKodeBarang());
                    sheet.getRow(rc).getCell(1).setCellValue(b.getNamaBarang());
                    sheet.getRow(rc).getCell(2).setCellValue(b.getSatuan());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getBerat());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getHargaJual());
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
