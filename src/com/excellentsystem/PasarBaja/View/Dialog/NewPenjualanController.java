/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.PemesananHeadDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanHeadDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import java.sql.Connection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class NewPenjualanController {

    @FXML
    private TableView<PenjualanDetail> penjualanDetailTable;
    @FXML
    private TableColumn<PenjualanDetail, String> kodeBarangColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> namaBarangColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> keteranganColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> satuanColumn;
    @FXML
    private TableColumn<PenjualanDetail, Number> qtyColumn;
    @FXML
    private TableColumn<PenjualanDetail, Number> hargaJualColumn;
    @FXML
    private TableColumn<PenjualanDetail, Number> hargaJualPPNColumn;
    @FXML
    private TableColumn<PenjualanDetail, Number> subTotalColumn;

    @FXML
    private Label noPenjualanField;
    @FXML
    private Label tglPenjualanField;

    @FXML
    private TextField namaField;
    @FXML
    private TextArea alamatField;

    @FXML
    private TextArea catatanField;

    @FXML
    private Label totalQtyField;
    @FXML
    private TextField totalPenjualanField;
    @FXML
    private TextField ppnField;
    @FXML
    private TextField grandtotalField;

    private PenjualanHead penjualan;
    private ObservableList<PenjualanDetail> allPenjualanDetail = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().namaBarangProperty());
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().satuanProperty());
        hargaJualColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHargaJual() / 1.1));
        hargaJualPPNColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHargaJual()));
        subTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal() / 1.1));
        qtyColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQty()));
        hargaJualColumn.setCellFactory(col -> Function.getTableCell());
        hargaJualPPNColumn.setCellFactory(col -> Function.getTableCell());
        subTotalColumn.setCellFactory(col -> Function.getTableCell());
        qtyColumn.setCellFactory(col -> Function.getTableCell());
        penjualanDetailTable.setItems(allPenjualanDetail);

        ContextMenu cm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            penjualanDetailTable.refresh();
        });
        cm.getItems().addAll(refresh);
        penjualanDetailTable.setContextMenu(cm);
        penjualanDetailTable.setRowFactory((TableView<PenjualanDetail> tv) -> {
            final TableRow<PenjualanDetail> row = new TableRow<PenjualanDetail>() {
                @Override
                public void updateItem(PenjualanDetail item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(cm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            penjualanDetailTable.refresh();
                        });
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

    public void setDetailPenjualan(String noPenjualan) {
        Task<String> task = new Task<String>() {
            @Override
            public String call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    penjualan = PenjualanHeadDAO.get(con, noPenjualan);
                    penjualan.setPemesananHead(PemesananHeadDAO.get(con, penjualan.getNoPemesanan()));
                    penjualan.setCustomer(CustomerDAO.get(con, penjualan.getKodeCustomer()));
                    penjualan.setListPenjualanDetail(PenjualanDetailDAO.getAllPenjualanDetail(con, noPenjualan));
                    return "true";
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((ev) -> {
            try {
                mainApp.closeLoading();
                noPenjualanField.setText(penjualan.getNoPenjualan());
                tglPenjualanField.setText(tglLengkap.format(tglSql.parse(penjualan.getTglPenjualan())));
                namaField.setText(penjualan.getCustomer().getNama());
                alamatField.setText(penjualan.getCustomer().getAlamat());
                catatanField.setText(penjualan.getCatatan());
                allPenjualanDetail.addAll(penjualan.getListPenjualanDetail());
                totalPenjualanField.setText(df.format(penjualan.getTotalPenjualan() / 1.1));
                ppnField.setText(df.format(penjualan.getTotalPenjualan() / 1.1 * 0.1));
                grandtotalField.setText(df.format(penjualan.getTotalPenjualan()));
                double qty = 0;
                for (PenjualanDetail d : allPenjualanDetail) {
                    qty = qty + d.getQty();
                }
                totalQtyField.setText(df.format(qty));
            } catch (Exception e) {
                mainApp.showMessage(Modality.NONE, "Error", e.toString());
            }
        });
        task.setOnFailed((e) -> {
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    @FXML
    private void backToDataPenjualan() {
        mainApp.closeDialog(owner, stage);
    }
}
