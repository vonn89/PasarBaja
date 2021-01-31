/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Function.createRow;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
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
public class NeracaKeuanganController {

    @FXML
    private TreeTableView<Keuangan> keuanganTable;
    @FXML
    private TreeTableColumn<Keuangan, String> noKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> tglKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> tipeKeuanganColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> kategoriColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> deskripsiColumn;
    @FXML
    private TreeTableColumn<Keuangan, Number> jumlahRpColumn;
    @FXML
    private TreeTableColumn<Keuangan, String> kodeUserColumn;
    @FXML
    private Label saldoAwalLabel;
    @FXML
    private Label saldoAkhirLabel;
    private LocalDate tglAwal;
    private LocalDate tglAkhir;
    private ObservableList<Keuangan> allKeuangan = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage owner;
    private Stage stage;
    final TreeItem<Keuangan> root = new TreeItem<>();

    public void initialize() {
        noKeuanganColumn.setCellValueFactory(param -> param.getValue().getValue().noKeuanganProperty());
        tipeKeuanganColumn.setCellValueFactory(param -> param.getValue().getValue().tipeKeuanganProperty());
        kategoriColumn.setCellValueFactory(param -> param.getValue().getValue().kategoriProperty());
        deskripsiColumn.setCellValueFactory(param -> param.getValue().getValue().deskripsiProperty());
        kodeUserColumn.setCellValueFactory(param -> param.getValue().getValue().kodeUserProperty());
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
            setTable();
        });
        for (Otoritas o : sistem.getUser().getOtoritas()) {
            if (o.getJenis().equals("Export Excel") && o.isStatus()) {
                rm.getItems().addAll(export);
            }
        }
        rm.getItems().addAll(refresh);
        keuanganTable.setContextMenu(rm);
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

    public void setKeuangan(List<Keuangan> temp, LocalDate tglAwal, LocalDate tglAkhir) {
        allKeuangan.clear();
        allKeuangan.addAll(temp);
        this.tglAwal = tglAwal;
        this.tglAkhir = tglAkhir;
        setTable();
    }

    private void setTable() {
        if (keuanganTable.getRoot() != null) {
            keuanganTable.getRoot().getChildren().clear();
        }
        double saldoAwal = 0;
        double saldoAkhir = 0;
        List<String> allKategori = new ArrayList<>();
        for (Keuangan keu : allKeuangan) {
            if (!allKategori.contains(keu.getKategori())) {
                allKategori.add(keu.getKategori());
            }
            saldoAkhir = saldoAkhir + keu.getJumlahRp();
            if (LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isAfter(tglAwal.minusDays(1))
                    && LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isBefore(tglAkhir.plusDays(1))) {
            } else {
                saldoAwal = saldoAwal + keu.getJumlahRp();
            }
        }
        saldoAwalLabel.setText(df.format(saldoAwal));
        saldoAkhirLabel.setText(df.format(saldoAkhir));
        for (String s : allKategori) {
            Keuangan k = new Keuangan();
            k.setNoKeuangan(s);
            TreeItem<Keuangan> parent = new TreeItem<>(k);
            double total = 0;
            for (Keuangan keu : allKeuangan) {
                if (LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isAfter(tglAwal.minusDays(1))
                        && LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isBefore(tglAkhir.plusDays(1))
                        && keu.getKategori().equals(s)) {
                    parent.getChildren().add(new TreeItem<>(keu));
                    total = total + keu.getJumlahRp();
                }
            }
            parent.getValue().setJumlahRp(total);
            if (total != 0) {
                root.getChildren().add(parent);
            }
        }
        keuanganTable.setRoot(root);
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
                Sheet sheet = workbook.createSheet("Detail Neraca - Keuangan");
                int rc = 0;
                int c = 4;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("No Keuangan");
                sheet.getRow(rc).getCell(1).setCellValue("Tgl Keuangan");
                sheet.getRow(rc).getCell(2).setCellValue("Keterangan");
                sheet.getRow(rc).getCell(3).setCellValue("Jumlah Rp");
                rc++;
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Saldo Awal");
                sheet.getRow(rc).getCell(3).setCellValue(Double.parseDouble(saldoAwalLabel.getText().replaceAll(",", "")));
                rc++;
                List<String> allKategori = new ArrayList<>();
                for (Keuangan keu : allKeuangan) {
                    if (LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isAfter(tglAwal.minusDays(1))
                            && LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isBefore(tglAkhir.plusDays(1))) {
                        if (!allKategori.contains(keu.getKategori())) {
                            allKategori.add(keu.getKategori());
                        }
                    }
                }
                for (String s : allKategori) {
                    rc++;
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue(s);
                    rc++;
                    double total = 0;
                    for (Keuangan keu : allKeuangan) {
                        if (LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isAfter(tglAwal.minusDays(1))
                                && LocalDate.parse(keu.getTglKeuangan().substring(0, 10)).isBefore(tglAkhir.plusDays(1))
                                && keu.getKategori().equals(s)) {
                            createRow(workbook, sheet, rc, c, "Detail");
                            sheet.getRow(rc).getCell(0).setCellValue(keu.getNoKeuangan());
                            sheet.getRow(rc).getCell(1).setCellValue(tglLengkap.format(tglSql.parse(keu.getTglKeuangan())));
                            sheet.getRow(rc).getCell(2).setCellValue(keu.getDeskripsi());
                            sheet.getRow(rc).getCell(3).setCellValue(keu.getJumlahRp());
                            rc++;

                            total = total + keu.getJumlahRp();
                        }
                    }
                    createRow(workbook, sheet, rc, c, "SubHeader");
                    sheet.getRow(rc).getCell(0).setCellValue("Total " + s);
                    sheet.getRow(rc).getCell(3).setCellValue(total);
                    rc++;
                }
                createRow(workbook, sheet, rc, c, "Header");
                sheet.getRow(rc).getCell(0).setCellValue("Saldo Akhir");
                sheet.getRow(rc).getCell(3).setCellValue(Double.parseDouble(saldoAkhirLabel.getText().replaceAll(",", "")));
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
