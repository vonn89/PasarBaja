/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.DAO.PiutangDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.Piutang;
import com.excellentsystem.PasarBaja.Model.TerimaPembayaran;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.Dialog.DetailPiutangController;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPembayaranController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPenjualanController;
import com.excellentsystem.PasarBaja.View.Dialog.NewPiutangController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
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
public class PiutangController {

    @FXML
    private TableView<Piutang> piutangTable;
    @FXML
    private TableColumn<Piutang, String> noPiutangColumn;
    @FXML
    private TableColumn<Piutang, String> tglPiutangColumn;
    @FXML
    private TableColumn<Piutang, String> tipePiutangColumn;
    @FXML
    private TableColumn<Piutang, String> keteranganColumn;
    @FXML
    private TableColumn<Piutang, Number> jumlahPiutangColumn;
    @FXML
    private TableColumn<Piutang, Number> pembayaranColumn;
    @FXML
    private TableColumn<Piutang, Number> sisaPiutangColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalPiutangField;
    @FXML
    private Label totalPembayaranField;
    @FXML
    private ComboBox<String> groupByCombo;

    private ObservableList<Piutang> allPiutang = FXCollections.observableArrayList();
    private ObservableList<Piutang> filterData = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        noPiutangColumn.setCellValueFactory(cellData -> cellData.getValue().noPiutangProperty());
        noPiutangColumn.setCellFactory(col -> Function.getWrapTableCell(noPiutangColumn));

        tipePiutangColumn.setCellValueFactory(cellData -> cellData.getValue().kategoriProperty());
        tipePiutangColumn.setCellFactory(col -> Function.getWrapTableCell(tipePiutangColumn));

        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        keteranganColumn.setCellFactory(col -> Function.getWrapTableCell(keteranganColumn));

        tglPiutangColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglPiutang())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglPiutangColumn.setCellFactory(col -> Function.getWrapTableCell(tglPiutangColumn));
        tglPiutangColumn.setComparator(Function.sortDate(tglLengkap));

        jumlahPiutangColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahPiutangProperty());
        jumlahPiutangColumn.setCellFactory(col -> Function.getTableCell());

        pembayaranColumn.setCellValueFactory(cellData -> cellData.getValue().pembayaranProperty());
        pembayaranColumn.setCellFactory(col -> Function.getTableCell());

        sisaPiutangColumn.setCellValueFactory(cellData -> cellData.getValue().sisaPiutangProperty());
        sisaPiutangColumn.setCellFactory(col -> Function.getTableCell());

        ContextMenu rm = new ContextMenu();
        MenuItem addNew = new MenuItem("Add New Piutang");
        addNew.setOnAction((ActionEvent e) -> {
            showNewPiutang();
        });
        MenuItem katPiutang = new MenuItem("Add New Kategori Piutang");
        katPiutang.setOnAction((ActionEvent e) -> {
            mainApp.showKategoriPiutang();
        });
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            getPiutang();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Add New Piutang") && o.isStatus()) {
                rm.getItems().add(addNew);
            }
            if (o.getJenis().equals("Kategori Piutang") && o.isStatus()) {
                rm.getItems().add(katPiutang);
            }
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().add(export);
            }
        }
        rm.getItems().addAll(refresh);
        piutangTable.setContextMenu(rm);
        piutangTable.setRowFactory(tv -> {
            TableRow<Piutang> row = new TableRow<Piutang>() {
                @Override
                public void updateItem(Piutang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        ContextMenu rm = new ContextMenu();
                        MenuItem addNew = new MenuItem("Add New Piutang");
                        addNew.setOnAction((ActionEvent e) -> {
                            showNewPiutang();
                        });
                        MenuItem katPiutang = new MenuItem("Add New Kategori Piutang");
                        katPiutang.setOnAction((ActionEvent e) -> {
                            mainApp.showKategoriPiutang();
                        });
                        MenuItem lihat = new MenuItem("Detail Piutang");
                        lihat.setOnAction((ActionEvent e) -> {
                            showDetailPiutang(item);
                        });
                        MenuItem lihatPenjualan = new MenuItem("Detail Penjualan");
                        lihatPenjualan.setOnAction((ActionEvent e) -> {
                            showDetailPenjualan(item);
                        });
                        MenuItem bayar = new MenuItem("Pembayaran Piutang");
                        bayar.setOnAction((ActionEvent e) -> {
                            showPembayaran(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            getPiutang();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Add New Piutang") && o.isStatus()) {
                                rm.getItems().add(addNew);
                            }
                            if (o.getJenis().equals("Kategori Piutang") && o.isStatus()) {
                                rm.getItems().add(katPiutang);
                            }
                            if (o.getJenis().equals("Detail Piutang") && o.isStatus()) {
                                rm.getItems().add(lihat);
                            }
                            if (o.getJenis().equals("Detail Penjualan") && o.isStatus()
                                    && item.getKategori().equals("Piutang Penjualan")) {
                                rm.getItems().add(lihatPenjualan);
                            }
                            if (o.getJenis().equals("Terima Pembayaran Piutang") && o.isStatus() && item.getStatus().equals("open")) {
                                rm.getItems().add(bayar);
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
                            if (o.getJenis().equals("Detail Piutang") && o.isStatus()) {
                                showDetailPiutang(row.getItem());
                            }
                        }
                    }
                }
            });
            return row;
        });
        allPiutang.addListener((ListChangeListener.Change<? extends Piutang> change) -> {
            searchPiutang();
        });
        searchField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    searchPiutang();
                });
        filterData.addAll(allPiutang);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> groupBy = FXCollections.observableArrayList();
        groupBy.clear();
        groupBy.add("Belum Lunas");
        groupBy.add("Lunas");
        groupByCombo.setItems(groupBy);
        groupByCombo.getSelectionModel().select("Belum Lunas");
        getPiutang();
        piutangTable.setItems(filterData);
    }

    @FXML
    private void getPiutang() {
        Task<List<Piutang>> task = new Task<List<Piutang>>() {
            @Override
            public List<Piutang> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    String status = "close";
                    if (groupByCombo.getSelectionModel().getSelectedItem().equals("Belum Lunas")) {
                        status = "open";
                    }
                    return PiutangDAO.getAllByStatus(con, status);
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            mainApp.closeLoading();
            allPiutang.clear();
            allPiutang.addAll(task.getValue());
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

    private void searchPiutang() {
        try {
            filterData.clear();
            for (Piutang temp : allPiutang) {
                if (searchField.getText() == null || searchField.getText().equals("")) {
                    filterData.add(temp);
                } else {
                    if (checkColumn(temp.getNoPiutang())
                            || checkColumn(df.format(temp.getJumlahPiutang()))
                            || checkColumn(tglLengkap.format(tglSql.parse(temp.getTglPiutang())))
                            || checkColumn(temp.getKategori())
                            || checkColumn(temp.getKeterangan())
                            || checkColumn(df.format(temp.getPembayaran()))
                            || checkColumn(df.format(temp.getSisaPiutang()))) {
                        filterData.add(temp);
                    }
                }
            }
            hitungTotal();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    private void hitungTotal() {
        double belumDibayar = 0;
        double sudahDibayar = 0;
        for (Piutang temp : filterData) {
            belumDibayar = belumDibayar + temp.getSisaPiutang();
            sudahDibayar = sudahDibayar + temp.getPembayaran();
        }
        totalPiutangField.setText(df.format(belumDibayar));
        totalPembayaranField.setText(df.format(sudahDibayar));
    }

    private void showDetailPenjualan(Piutang piutang) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPenjualan.fxml");
        NewPenjualanController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        controller.setDetailPenjualan(piutang.getKeterangan());
    }

    private void showDetailPiutang(Piutang piutang) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/DetailPiutang.fxml");
        DetailPiutangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.setDetail(piutang.getNoPiutang());
        x.pembayaranPiutangTable.setRowFactory((TableView<TerimaPembayaran> tableView) -> {
            final TableRow<TerimaPembayaran> row = new TableRow<TerimaPembayaran>() {
                @Override
                public void updateItem(TerimaPembayaran item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(null);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem batal = new MenuItem("Batal Terima Pembayaran Piutang");
                        batal.setOnAction((ActionEvent e) -> {
                            batalPembayaran(item, stage);
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Batal Terima Pembayaran Piutang") && o.isStatus()) {
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
                getPiutang();
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

    private void showNewPiutang() {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPiutang.fxml");
        NewPiutangController x = loader.getController();
        x.setMainApp(mainApp, mainApp.MainStage, stage);
        x.saveButton.setOnAction((ActionEvent event) -> {
            if ("0".equals(x.jumlahRpField.getText().replaceAll(",", "")) || "".equals(x.jumlahRpField.getText().replaceAll(",", ""))) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah Rp masih kosong");
            } else if (x.kategoriCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Kategori belum dipilih");
            } else if (x.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            Piutang h = new Piutang();
                            h.setKategori(x.kategoriCombo.getSelectionModel().getSelectedItem());
                            h.setKeterangan(x.keteranganField.getText());
                            h.setTipeKeuangan(x.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            h.setJumlahPiutang(Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", "")));
                            h.setPembayaran(0);
                            h.setSisaPiutang(Double.parseDouble(x.jumlahRpField.getText().replaceAll(",", "")));
                            h.setKodeUser(sistem.getUser().getKodeUser());
                            h.setStatus("open");
                            return Service.newPiutang(con, h);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getPiutang();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Data piutang berhasil disimpan");
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

    private void showPembayaran(Piutang p) {
        Stage stage = new Stage();
        FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Dialog/NewPembayaran.fxml");
        NewPembayaranController controller = loader.getController();
        controller.setMainApp(mainApp, mainApp.MainStage, stage);
        if (p.getKategori().equals("Piutang Penjualan")) {
            controller.setPembayaranPenjualan(p.getKeterangan());
        } else {
            controller.setPembayaranPiutang(p);
        }
        controller.saveButton.setOnAction((event) -> {
            double jumlahBayar = Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", ""));
            if (jumlahBayar > p.getSisaPiutang()) {
                mainApp.showMessage(Modality.NONE, "Warning", "Jumlah yang dibayar melebihi dari sisa piutang");
            } else if (controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem() == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Tipe keuangan belum dipilih");
            } else {
                Task<String> task = new Task<String>() {
                    @Override
                    public String call() throws Exception {
                        try (Connection con = Koneksi.getConnection()) {
                            TerimaPembayaran t = new TerimaPembayaran();
                            t.setNoPiutang(p.getNoPiutang());
                            t.setJumlahPembayaran(Double.parseDouble(controller.jumlahPembayaranField.getText().replaceAll(",", "")));
                            t.setTipeKeuangan(controller.tipeKeuanganCombo.getSelectionModel().getSelectedItem());
                            t.setCatatan("");
                            t.setKodeUser(sistem.getUser().getKodeUser());
                            t.setTglBatal("2000-01-01 00:00:00");
                            t.setUserBatal("");
                            t.setStatus("true");
                            t.setPiutang(p);
                            return Service.newTerimaPembayaranPiutang(con, t);
                        }
                    }
                };
                task.setOnRunning((e) -> {
                    mainApp.showLoadingScreen();
                });
                task.setOnSucceeded((e) -> {
                    mainApp.closeLoading();
                    getPiutang();
                    if (task.getValue().equals("true")) {
                        mainApp.closeDialog(mainApp.MainStage, stage);
                        mainApp.showMessage(Modality.NONE, "Success", "Pembayaran piutang berhasil disimpan");
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
                Sheet sheet = workbook.createSheet("Data Piutang");
                int rc = 0;
                int c = 8;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Status : " + groupByCombo.getSelectionModel().getSelectedItem());
                rc++;
                createRow(workbook, sheet, rc, c, "Bold");
                sheet.getRow(rc).getCell(0).setCellValue("Filter : " + searchField.getText());
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Piutang");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Piutang");
                sheet.getRow(rc).getCell(2).setCellValue("Kategori");
                sheet.getRow(rc).getCell(3).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(4).setCellValue("Total Piutang");
                sheet.getRow(rc).getCell(5).setCellValue("Pembayaran");
                sheet.getRow(rc).getCell(6).setCellValue("Sisa Piutang");
                rc++;
                double piutang = 0;
                double pembayaran = 0;
                double sisaPiutang = 0;
                for (Piutang b : filterData) {
                    createRow(workbook, sheet, rc, c, "Detail");
                    sheet.getRow(rc).getCell(0).setCellValue(b.getNoPiutang());
                    sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(b.getTglPiutang())));
                    sheet.getRow(rc).getCell(2).setCellValue(b.getKategori());
                    sheet.getRow(rc).getCell(3).setCellValue(b.getKeterangan());
                    sheet.getRow(rc).getCell(4).setCellValue(b.getJumlahPiutang());
                    sheet.getRow(rc).getCell(5).setCellValue(b.getPembayaran());
                    sheet.getRow(rc).getCell(6).setCellValue(b.getSisaPiutang());
                    rc++;
                    piutang = piutang + b.getJumlahPiutang();
                    pembayaran = pembayaran + b.getPembayaran();
                    sisaPiutang = sisaPiutang + b.getSisaPiutang();
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total :");
                sheet.getRow(rc).getCell(4).setCellValue(piutang);
                sheet.getRow(rc).getCell(5).setCellValue(pembayaran);
                sheet.getRow(rc).getCell(6).setCellValue(sisaPiutang);
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
