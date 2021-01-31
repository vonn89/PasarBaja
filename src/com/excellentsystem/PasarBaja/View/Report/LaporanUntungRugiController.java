/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Report;

import com.excellentsystem.PasarBaja.DAO.KeuanganDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yunaz
 */
public class LaporanUntungRugiController {

    private DecimalFormat df = new DecimalFormat("###,##0");
    @FXML
    private StackPane pane;
    private GridPane gridPane;
    @FXML
    private DatePicker tglAwalPicker;
    @FXML
    private DatePicker tglAkhirPicker;
    private ObservableList<Keuangan> allPenjualan = FXCollections.observableArrayList();
    private ObservableList<Keuangan> allHPP = FXCollections.observableArrayList();
    private ObservableList<Keuangan> allPendapatanBeban = FXCollections.observableArrayList();
    private ObservableList<String> kategoriTransaksi = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        tglAwalPicker.setConverter(Function.getTglConverter());
        tglAwalPicker.setValue(LocalDate.now().minusMonths(1));
        tglAwalPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellMulai(tglAkhirPicker));
        tglAkhirPicker.setConverter(Function.getTglConverter());
        tglAkhirPicker.setValue(LocalDate.now());
        tglAkhirPicker.setDayCellFactory((final DatePicker datePicker) -> Function.getDateCellAkhir(tglAwalPicker));

        final ContextMenu rm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getKeuangan();
        });
        rm.getItems().addAll(refresh);
        pane.setOnContextMenuRequested((e) -> {
            rm.show(pane, e.getScreenX(), e.getScreenY());
        });
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        getKeuangan();
    }

    public void setTanggal(LocalDate tglAwal, LocalDate tglAkhir) {
        tglAwalPicker.setValue(tglAwal);
        tglAkhirPicker.setValue(tglAkhir);
    }

    @FXML
    private void getKeuangan() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    allPenjualan.clear();
                    allHPP.clear();
                    allPendapatanBeban.clear();
                    allPenjualan.addAll(KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            "Penjualan", tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString()));
                    allHPP.addAll(KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            "HPP", tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString()));
                    allPendapatanBeban.addAll(KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            "Pendapatan/Beban", tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString()));

                    kategoriTransaksi.clear();
                    for (Keuangan k : allPendapatanBeban) {
                        if (!kategoriTransaksi.contains(k.getKategori())) {
                            kategoriTransaksi.add(k.getKategori());
                        }
                    }
                    return null;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            setGridPane();
            mainApp.closeLoading();
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    public void setGridPane() {
        try {
            pane.getChildren().clear();
            gridPane = new GridPane();

            gridPane.getColumnConstraints().add(new ColumnConstraints(10, 100, Double.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true));
            gridPane.getColumnConstraints().add(new ColumnConstraints(200, 200, 250, Priority.ALWAYS, HPos.RIGHT, true));
            gridPane.getColumnConstraints().add(new ColumnConstraints(200, 200, 250, Priority.ALWAYS, HPos.RIGHT, true));

            int row = 17 + kategoriTransaksi.size();
            for (int i = 0; i < row; i++) {
                gridPane.getRowConstraints().add(new RowConstraints(30, 30, 30));
                if (i % 2 == 0) {
                    addBackground(i);
                }
            }

            double totalPenjualan = 0;
            for (Keuangan k : allPenjualan) {
                totalPenjualan = totalPenjualan + k.getJumlahRp();
            }
            double totalHPP = 0;
            for (Keuangan k : allHPP) {
                totalHPP = totalHPP + k.getJumlahRp();
            }
            
            addBoldText("Penjualan", 0, 2);
            addBoldText(df.format(totalPenjualan), 2, 2);

            addBoldText("Total Harga Pokok Penjualan", 0, 4);
            addBoldText(df.format(totalHPP), 2, 4);

            addBoldText("Untung-Rugi Kotor", 0, 6);
            addBoldText(df.format(totalPenjualan - totalHPP), 2, 6);

            int i = 8;
            addBoldText("Pendapatan-Beban", 0, i);
            i = i + 1;
            double totalPendapatanBeban = 0;
            for (String s : kategoriTransaksi) {
                double pendapatanBeban = 0;
                List<Keuangan> temp = new ArrayList<>();
                for (Keuangan k : allPendapatanBeban) {
                    if (k.getKategori().equalsIgnoreCase(s)) {
                        pendapatanBeban = pendapatanBeban + k.getJumlahRp();
                        totalPendapatanBeban = totalPendapatanBeban + k.getJumlahRp();
                        temp.add(k);
                    }
                }
                addHyperLinkPendapatanBebanText(s, 0, i, temp);
                addNormalText(df.format(pendapatanBeban), 1, i);
                i = i + 1;
            }
            addBoldText("Total Pendapatan-Beban", 0, i);
            addBoldText(df.format(totalPendapatanBeban), 2, i);
            i = i + 2;

            addBoldText("Untung-Rugi Bersih", 0, i);
            addBoldText(df.format(totalPenjualan - totalHPP + totalPendapatanBeban), 2, i);

            gridPane.setPadding(new Insets(10));
            pane.getChildren().add(gridPane);
        } catch (Exception ex) {
            mainApp.showMessage(Modality.NONE, "Error", ex.toString());
        }
    }

    private void addBackground(int row) {
        AnchorPane x = new AnchorPane();
        x.setStyle("-fx-background-color:derive(seccolor5,20%);");
        gridPane.add(x, 0, row, GridPane.REMAINING, 1);
    }

    private void addNormalText(String text, int column, int row) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:12;");
        gridPane.add(label, column, row);
    }

    private void addBoldText(String text, int column, int row) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:12;"
                + "-fx-font-weight:bold;");
        gridPane.add(label, column, row);
    }

    private void addHyperLinkPenjualanText(String text, int column, int row) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setStyle("-fx-font-size:12;"
                + "-fx-border-color:transparent;");
        hyperlink.setOnAction((e) -> {
            Stage stage = new Stage();
            FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/UntungRugiPenjualan.fxml");
            UntungRugiPenjualanController x = loader.getController();
            x.setMainApp(mainApp, mainApp.MainStage, stage);
            x.getPenjualan(tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString());
        });
        gridPane.add(hyperlink, column, row);
    }

    private void addHyperLinkHPPPenjualanText(String text, int column, int row) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setStyle("-fx-font-size:12;"
                + "-fx-border-color:transparent;");
        hyperlink.setOnAction((e) -> {
            Stage stage = new Stage();
            FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/UntungRugiHPPPenjualan.fxml");
            UntungRugiHPPPenjualanController x = loader.getController();
            x.setMainApp(mainApp, mainApp.MainStage, stage);
            x.getPenjualan(tglAwalPicker.getValue().toString(), tglAkhirPicker.getValue().toString());
        });
        gridPane.add(hyperlink, column, row);
    }

    private void addHyperLinkPendapatanBebanText(String text, int column, int row, List<Keuangan> keuangan) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setStyle("-fx-font-size:12;"
                + "-fx-border-color:transparent;");
        hyperlink.setOnAction((e) -> {
            Stage stage = new Stage();
            FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/UntungRugiPendapatanBeban.fxml");
            UntungRugiPendapatanBebanController x = loader.getController();
            x.setMainApp(mainApp, mainApp.MainStage, stage);
            x.setKeuangan(keuangan, tglAwalPicker.getValue(), tglAkhirPicker.getValue());
        });
        gridPane.add(hyperlink, column, row);
    }

}
