/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.BebanPenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.BebanPenjualanHeadDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.BebanPenjualanDetail;
import java.sql.Connection;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class DetailBebanPenjualanController {

    @FXML
    private TableView<BebanPenjualanDetail> bebanPenjualanTable;
    @FXML
    private TableColumn<BebanPenjualanDetail, String> noBebanColumn;
    @FXML
    private TableColumn<BebanPenjualanDetail, String> tglBebanColumn;
    @FXML
    private TableColumn<BebanPenjualanDetail, String> keteranganColumn;
    @FXML
    private TableColumn<BebanPenjualanDetail, Number> jumlahRpColumn;

    @FXML
    private Label totalBebanField;
    public ObservableList<BebanPenjualanDetail> allBebanPenjualan = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        noBebanColumn.setCellValueFactory(cellData -> cellData.getValue().getBebanPenjualanHead().noBebanPenjualanProperty());
        tglBebanColumn.setCellValueFactory(cellData -> cellData.getValue().getBebanPenjualanHead().keteranganProperty());
        tglBebanColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getBebanPenjualanHead().getTglBebanPenjualan())));
            } catch (Exception ex) {
                return null;
            }
        });
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().getBebanPenjualanHead().keteranganProperty());
        jumlahRpColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahRpProperty());
        jumlahRpColumn.setCellFactory(col -> Function.getTableCell());
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        bebanPenjualanTable.setItems(allBebanPenjualan);
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    private void hitungTotal() {
        double total = 0;
        for (BebanPenjualanDetail temp : allBebanPenjualan) {
            total = total + temp.getJumlahRp();
        }
        totalBebanField.setText(df.format(total));
    }

    public void setDetailBebanPenjualan(String noPenjualan) {
        try (Connection con = Koneksi.getConnection()) {
            List<BebanPenjualanDetail> allBeban = BebanPenjualanDetailDAO.getAllNoPenjualanAndStatus(con, noPenjualan, "true");
            for (BebanPenjualanDetail d : allBeban) {
                d.setBebanPenjualanHead(BebanPenjualanHeadDAO.get(con, d.getNoBebanPenjualan()));
            }
            allBebanPenjualan.clear();
            allBebanPenjualan.addAll(allBeban);
            hitungTotal();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
