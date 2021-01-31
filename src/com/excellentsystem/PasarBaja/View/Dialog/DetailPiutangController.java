/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.PiutangDAO;
import com.excellentsystem.PasarBaja.DAO.TerimaPembayaranDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import com.excellentsystem.PasarBaja.Model.Piutang;
import com.excellentsystem.PasarBaja.Model.TerimaPembayaran;
import java.sql.Connection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yunaz
 */
public class DetailPiutangController {

    @FXML
    public TableView<TerimaPembayaran> pembayaranPiutangTable;
    @FXML
    private TableColumn<TerimaPembayaran, String> noPembayaranColumn;
    @FXML
    private TableColumn<TerimaPembayaran, String> tglPembayaranColumn;
    @FXML
    private TableColumn<TerimaPembayaran, Number> jumlahPembayaranColumn;
    @FXML
    private TableColumn<TerimaPembayaran, String> tipeKeuanganColumn;
    @FXML
    private TableColumn<TerimaPembayaran, String> catatanColumn;

    @FXML
    private TextField noPiutangField;
    @FXML
    private TextField tglPiutangField;
    @FXML
    private TextField kategoriField;
    @FXML
    private TextField keteranganField;
    @FXML
    private TextField jumlahPiutangField;
    @FXML
    private Label terbayarLabel;
    @FXML
    private Label sisaPiutangLabel;
    private ObservableList<TerimaPembayaran> allPembayaran = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        noPembayaranColumn.setCellValueFactory(cellData -> cellData.getValue().noTerimaPembayaranProperty());
        tipeKeuanganColumn.setCellValueFactory(cellData -> cellData.getValue().tipeKeuanganProperty());
        tglPembayaranColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(tglLengkap.format(tglSql.parse(cellData.getValue().getTglTerima())));
            } catch (Exception ex) {
                return null;
            }
        });
        jumlahPembayaranColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahPembayaranProperty());
        jumlahPembayaranColumn.setCellFactory(col -> Function.getTableCell());
        catatanColumn.setCellValueFactory(cellData -> cellData.getValue().catatanProperty());

    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        pembayaranPiutangTable.setItems(allPembayaran);
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    public void setDetail(String noPiutang) {
        Task<Piutang> task = new Task<Piutang>() {
            @Override
            public Piutang call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    Piutang p = PiutangDAO.get(con, noPiutang);
                    p.setListTerimaPembayaran(TerimaPembayaranDAO.getAllByNoPiutangAndStatus(con, p.getNoPiutang(), "true"));
                    return p;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            try {
                mainApp.closeLoading();
                Piutang p = task.getValue();
                noPiutangField.setText(p.getNoPiutang());
                tglPiutangField.setText(tglLengkap.format(tglSql.parse(p.getTglPiutang())));
                kategoriField.setText(p.getKategori());
                keteranganField.setText(p.getKeterangan());
                jumlahPiutangField.setText("Rp " + df.format(p.getJumlahPiutang()));
                terbayarLabel.setText(df.format(p.getPembayaran()));
                sisaPiutangLabel.setText(df.format(p.getSisaPiutang()));
                allPembayaran.clear();
                allPembayaran.addAll(p.getListTerimaPembayaran());
            } catch (Exception ex) {
                mainApp.showMessage(Modality.NONE, "Error", ex.toString());
            }
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    public void setDetailPenjualan(PenjualanHead p) {
        Task<Piutang> task = new Task<Piutang>() {
            @Override
            public Piutang call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    Piutang piutang = PiutangDAO.getByKategoriAndKeteranganAndStatus(
                            con, "Piutang Penjualan", p.getNoPenjualan(), "%");
                    piutang.setListTerimaPembayaran(TerimaPembayaranDAO.getAllByNoPiutangAndStatus(
                            con, piutang.getNoPiutang(), "true"));
                    piutang.setPenjualanHead(p);
                    return piutang;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((e) -> {
            try {
                mainApp.closeLoading();
                Piutang piutang = task.getValue();
                noPiutangField.setText(piutang.getNoPiutang());
                tglPiutangField.setText(tglLengkap.format(tglSql.parse(piutang.getTglPiutang())));
                kategoriField.setText(piutang.getKategori());
                keteranganField.setText(piutang.getKeterangan());
                jumlahPiutangField.setText("Rp " + df.format(piutang.getJumlahPiutang()));
                terbayarLabel.setText(df.format(piutang.getPembayaran()));
                sisaPiutangLabel.setText(df.format(piutang.getSisaPiutang()));
                allPembayaran.clear();
                allPembayaran.addAll(piutang.getListTerimaPembayaran());
            } catch (Exception ex) {
                mainApp.showMessage(Modality.NONE, "Error", ex.toString());
            }
        });
        task.setOnFailed((e) -> {
            mainApp.closeLoading();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
        });
        new Thread(task).start();
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
