/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.AsetTetapDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.AsetTetap;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.View.Dialog.DetailAsetTetapController;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
 * @author yunaz
 */
public class NeracaAsetTetapController {

    @FXML
    private TreeTableView<Keuangan> keuanganTable;
    @FXML
    private TreeTableColumn<Keuangan, String> noKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> tglKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> deskripsiColumn;
    @FXML
    private TreeTableColumn<Keuangan, Number> jumlahRpColumn;
    @FXML
    private Label saldoAkhirLabel;
    private ObservableList<Keuangan> allKeuangan = FXCollections.observableArrayList();
    private List<AsetTetap> listAsetTetap = new ArrayList<>();
    private Main mainApp;
    private Stage owner;
    private Stage stage;
    final TreeItem<Keuangan> root = new TreeItem<>();

    public void initialize() {
        noKeuanganColumn.setCellValueFactory(param -> param.getValue().getValue().noKeuanganProperty());
        deskripsiColumn.setCellValueFactory(param -> param.getValue().getValue().deskripsiProperty());
        tglKeuanganColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getValue().getTglKeuangan())));
            } catch (Exception ex) {
                return null;
            }
        });
        tglKeuanganColumn.setComparator(Function.sortDate(tglLengkap));
        jumlahRpColumn.setCellValueFactory(param -> param.getValue().getValue().jumlahRpProperty());
        jumlahRpColumn.setCellFactory(col -> Function.getTreeTableCell());
        final ContextMenu rm = new ContextMenu();
        MenuItem export = new MenuItem("Export Excel");
        export.setOnAction((ActionEvent e) -> {
            exportExcel();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            keuanganTable.refresh();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        keuanganTable.setContextMenu(rm);
        keuanganTable.setRowFactory((TreeTableView<Keuangan> tableView) -> {
            final TreeTableRow<Keuangan> row = new TreeTableRow<Keuangan>() {
                @Override
                public void updateItem(Keuangan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem lihat = new MenuItem("Detail Aset Tetap");
                        lihat.setOnAction((ActionEvent e) -> {
                            showDetailAsetTetap(item);
                        });
                        MenuItem export = new MenuItem("Export Excel");
                        export.setOnAction((ActionEvent e) -> {
                            exportExcel();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            keuanganTable.refresh();
                        });
                        for (Otoritas o : sistem.getUser().getOtoritas()) {
                            if (o.getJenis().equals("Detail Aset Tetap") && o.isStatus()) {
                                rm.getItems().add(lihat);
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

    public void setKeuangan(List<Keuangan> temp) {
        Task<List<AsetTetap>> task = new Task<List<AsetTetap>>() {
            @Override
            public List<AsetTetap> call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    return AsetTetapDAO.getAllByStatus(con, "%");
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            mainApp.closeLoading();
            allKeuangan.clear();
            allKeuangan.addAll(temp);
            if (keuanganTable.getRoot() != null) {
                keuanganTable.getRoot().getChildren().clear();
            }
            double saldoAkhir = 0;
            listAsetTetap = task.getValue();
            for (AsetTetap at : listAsetTetap) {
                Keuangan k = new Keuangan();
                k.setNoKeuangan(at.getNoAset() + " - " + at.getNama());
                TreeItem<Keuangan> parent = new TreeItem<>(k);
                double total = 0;
                for (Keuangan keu : allKeuangan) {
                    if (keu.getDeskripsi().matches("(.*)" + at.getNoAset() + "(.*)")) {
                        saldoAkhir = saldoAkhir + keu.getJumlahRp();
                        TreeItem<Keuangan> child = new TreeItem<>(keu);
                        parent.getChildren().addAll(child);
                        total = total + keu.getJumlahRp();
                    }
                }
                k.setJumlahRp(total);
                if (total > 1) {
                    root.getChildren().add(parent);
                }
            }
            saldoAkhirLabel.setText(df.format(saldoAkhir));
            keuanganTable.setRoot(root);
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    private void showDetailAsetTetap(Keuangan k) {
        AsetTetap aset = null;
        for (AsetTetap at : listAsetTetap) {
            if (k.getDeskripsi() != null) {
                if (k.getDeskripsi().matches("(.*)" + at.getNoAset() + "(.*)")) {
                    aset = at;
                }
            } else {
                if (k.getNoKeuangan().matches("(.*)" + at.getNoAset() + "(.*)")) {
                    aset = at;
                }
            }
        }
        if (aset != null) {
            Stage child = new Stage();
            FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/Child/DetailAsetTetap.fxml");
            DetailAsetTetapController x = loader.getController();
            x.setMainApp(mainApp, stage, child);
            x.setDetail(aset);
        }
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
                Sheet sheet = workbook.createSheet("Detail Neraca - Aset Tetap");
                int rc = 0;
                int c = 4;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Keuangan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Keuangan");
                sheet.getRow(rc).getCell(2).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(3).setCellValue("Jumlah Rp");
                rc++;
                double saldoAkhir = 0;
                for (AsetTetap at : listAsetTetap) {
                    double total = 0;
                    for (Keuangan keu : allKeuangan) {
                        if (keu.getDeskripsi().matches("(.*)" + at.getNoAset() + "(.*)")) {
                            total = total + keu.getJumlahRp();
                        }
                    }
                    if (total > 1) {
                        rc++;
                        createRow(workbook, sheet, rc, c, "SubHeader");
                        sheet.getRow(rc).getCell(0).setCellValue(at.getNoAset() + " - " + at.getNama());
                        rc++;
                        for (Keuangan keu : allKeuangan) {
                            if (keu.getDeskripsi().matches("(.*)" + at.getNoAset() + "(.*)")) {
                                createRow(workbook, sheet, rc, c, "Detail");
                                sheet.getRow(rc).getCell(0).setCellValue(keu.getNoKeuangan());
                                sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(keu.getTglKeuangan())));
                                sheet.getRow(rc).getCell(2).setCellValue(keu.getDeskripsi());
                                sheet.getRow(rc).getCell(3).setCellValue(keu.getJumlahRp());
                                rc++;
                            }
                        }
                        createRow(workbook, sheet, rc, c, "SubHeader");
                        sheet.getRow(rc).getCell(0).setCellValue("Total " + at.getNoAset() + " - " + at.getNama());
                        sheet.getRow(rc).getCell(3).setCellValue(total);
                        rc++;
                        saldoAkhir = saldoAkhir + total;
                    }
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Total");
                sheet.getRow(rc).getCell(3).setCellValue(saldoAkhir);
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
