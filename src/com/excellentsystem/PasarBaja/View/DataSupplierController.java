/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.SupplierDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.sistem;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.Supplier;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailSupplierController;
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
public class DataSupplierController {

    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, String> kodeSupplierColumn;
    @FXML
    private TableColumn<Supplier, String> namaColumn;
    @FXML
    private TableColumn<Supplier, String> alamatColumn;
    @FXML
    private TableColumn<Supplier, String> kotaColumn;
    @FXML
    private TableColumn<Supplier, String> kontakPersonColumn;
    @FXML
    private TableColumn<Supplier, String> noTelpColumn;
    @FXML
    private TableColumn<Supplier, String> noHandphoneColumn;
    @FXML
    private TableColumn<Supplier, String> emailColumn;
    @FXML
    private TextField searchField;

    private ObservableList<Supplier> allSupplier = FXCollections.observableArrayList();
    private ObservableList<Supplier> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        kodeSupplierColumn.setCellValueFactory(cellData -> cellData.getValue().kodeSupplierProperty());
        kodeSupplierColumn.setCellFactory(col -> Function.getWrapTableCell(kodeSupplierColumn));

        namaColumn.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        namaColumn.setCellFactory(col -> Function.getWrapTableCell(namaColumn));

        alamatColumn.setCellValueFactory(cellData -> cellData.getValue().alamatProperty());
        alamatColumn.setCellFactory(col -> Function.getWrapTableCell(alamatColumn));

        kotaColumn.setCellValueFactory(cellData -> cellData.getValue().kotaProperty());
        kotaColumn.setCellFactory(col -> Function.getWrapTableCell(kotaColumn));

        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setCellFactory(col -> Function.getWrapTableCell(emailColumn));

        kontakPersonColumn.setCellValueFactory(cellData -> cellData.getValue().kontakPersonProperty());
        kontakPersonColumn.setCellFactory(col -> Function.getWrapTableCell(kontakPersonColumn));

        noTelpColumn.setCellValueFactory(cellData -> cellData.getValue().noTelpProperty());
        noTelpColumn.setCellFactory(col -> Function.getWrapTableCell(noTelpColumn));

        noHandphoneColumn.setCellValueFactory(cellData -> cellData.getValue().noHandphoneProperty());
        noHandphoneColumn.setCellFactory(col -> Function.getWrapTableCell(noHandphoneColumn));

        final ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Supplier");
        addNew.setOnAction((ActionEvent e) -> {
            newSupplier();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getSupplier();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Supplier") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        supplierTable.setContextMenu(rm);
        supplierTable.setRowFactory((TableView<Supplier> tableView) -> {
            final TableRow<Supplier> row = new TableRow<Supplier>() {
                @Override
                public void updateItem(Supplier item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Supplier");
                        addNew.setOnAction((ActionEvent e) -> {
                            newSupplier();
                        });
                        MenuItem edit = new MenuItem("Edit Supplier");
                        edit.setOnAction((ActionEvent e) -> {
                            editSupplier(item);
                        });
                        MenuItem hapus = new MenuItem("Delete Supplier");
                        hapus.setOnAction((ActionEvent e) -> {
                            deleteSupplier(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getSupplier();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Supplier") && o.isStatus()) {
                                rm.getItems().add(addNew);
                            }
                            if (o.getJenis().equals("Edit Supplier") && o.isStatus()) {
                                rm.getItems().add(edit);
                            }
                            if (o.getJenis().equals("Delete Supplier") && o.isStatus()) {
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
                            if (o.getJenis().equals("Edit Supplier") && o.isStatus()) {
                                editSupplier(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allSupplier.addListener((ListChangeListener.Change<? extends Supplier> change) -> {
            searchSupplier();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchSupplier();
                });
        filterData.addAll(allSupplier);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        getSupplier();
        supplierTable.setItems(filterData);
    }

    private void getSupplier() {
        Task<List<Supplier>> task = new Task<List<Supplier>>() {
            @Override
            public List<Supplier> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return SupplierDAO.getAllByStatus(con, "true");
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allSupplier.clear();
            allSupplier.addAll(task.getValue());
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

    private void searchSupplier() {
        filterData.clear();
        for (Supplier temp : allSupplier) {
            if (searchField.getText() == null || searchField.getText().equals("")) {
                filterData.add(temp);
            } else {
                if (checkColumn(temp.getKodeSupplier())
                        || checkColumn(temp.getNama())
                        || checkColumn(temp.getAlamat())
                        || checkColumn(temp.getKota())
                        || checkColumn(temp.getEmail())
                        || checkColumn(temp.getKontakPerson())
                        || checkColumn(temp.getNoTelp())
                        || checkColumn(temp.getNoHandphone())) {
                    filterData.add(temp);
                }
            }
        }
    }

    private void newSupplier() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailSupplier.fxml");
        DetailSupplierController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setSupplierDetail(null);
        x.saveButton.setOnAction((ActionEvent ev) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        Supplier supplier = new Supplier();
                        supplier.setKodeSupplier(SupplierDAO.getId(con));
                        supplier.setNama(x.namaField.getText());
                        supplier.setAlamat(x.alamatField.getText());
                        supplier.setKota(x.kotaField.getText());
                        supplier.setEmail(x.emailField.getText());
                        supplier.setKontakPerson(x.kontakPersonField.getText());
                        supplier.setNoTelp(x.noTelpField.getText());
                        supplier.setNoHandphone(x.noHandphoneField.getText());
                        supplier.setStatus("true");
                        return Service.newSupplier(con, supplier);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getSupplier();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Data supplier berhasil disimpan");
                    mainApp.closeDialog(mainApp.MainStage, stage);
                } else {
                    mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
                }
            });
            task.setOnFailed((e) -> {
                mainApp.closeLoading();
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            });
            new Thread(task).start();
        });
    }

    private void editSupplier(Supplier supplier) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailSupplier.fxml");
        DetailSupplierController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setSupplierDetail(supplier);
        x.saveButton.setOnAction((ActionEvent ev) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        supplier.setNama(x.namaField.getText());
                        supplier.setAlamat(x.alamatField.getText());
                        supplier.setKota(x.kotaField.getText());
                        supplier.setEmail(x.emailField.getText());
                        supplier.setKontakPerson(x.kontakPersonField.getText());
                        supplier.setNoTelp(x.noTelpField.getText());
                        supplier.setNoHandphone(x.noHandphoneField.getText());
                        return Service.updateSupplier(con, supplier);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getSupplier();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Data supplier berhasil disimpan");
                    mainApp.closeDialog(mainApp.MainStage, stage);
                } else {
                    mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
                }
            });
            task.setOnFailed((e) -> {
                mainApp.closeLoading();
                mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            });
            new Thread(task).start();
        });
    }

    private void deleteSupplier(Supplier supplier) {
        MessageController controller = mainApp.showMessage(Modality.WINDOW_MODAL, "Confirmation",
                "Delete supplier " + supplier.getKodeSupplier() + "-" + supplier.getNama() + " ?");
        controller.OK.setOnAction((ActionEvent ec) -> {
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    try (Connection con = Koneksi.getConnection()) {
                        return Service.deleteSupplier(con, supplier);
                    }
                }
            };
            task.setOnRunning((e) -> {
                mainApp.showLoadingScreen();
            });
            task.setOnSucceeded((e) -> {
                mainApp.closeLoading();
                getSupplier();
                if (task.getValue().equals("true")) {
                    mainApp.showMessage(Modality.NONE, "Success", "Data supplier berhasil dihapus");
                } else {
                    mainApp.showMessage(Modality.NONE, "Failed", task.getValue());
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
                Sheet sheet = workbook.createSheet("Data Supplier");
                int rc = 0;
                int c = 15;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Kode Supplier");
                sheet.getRow(rc).getCell(1).setCellValue("Nama");
                sheet.getRow(rc).getCell(2).setCellValue("Alamat");
                sheet.getRow(rc).getCell(3).setCellValue("Kota");
                sheet.getRow(rc).getCell(4).setCellValue("Kontak Person");
                sheet.getRow(rc).getCell(5).setCellValue("No Telp");
                sheet.getRow(rc).getCell(6).setCellValue("No Handphone");
                sheet.getRow(rc).getCell(7).setCellValue("Email");
                rc++;
                for (Supplier b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getKodeSupplier());
                    sheet.getRow(rc).getCell(1).setCellValue(b.getNama());
                    sheet.getRow(rc).getCell(2).setCellValue(b.getAlamat());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getKota());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getKontakPerson());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getNoTelp());
                    sheet.getRow(rc).getCell(6).setCellValue(b.getNoHandphone());
                    sheet.getRow(rc).getCell(7).setCellValue(b.getEmail());
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
