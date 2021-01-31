/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.LogBarangDAO;
import com.excellentsystem.PasarBaja.DAO.PemesananHeadDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanHeadDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.LogBarang;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import com.excellentsystem.PasarBaja.Model.PemesananHead;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author excellent
 */
public class NewPengirimanController {

    @FXML
    private TableView<PenjualanDetail> pengirimanDetailTable;
    @FXML
    private TableColumn<PenjualanDetail, String> kodeBarangColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> namaBarangColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> keteranganColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> catatanInternColumn;
    @FXML
    private TableColumn<PenjualanDetail, String> satuanColumn;
    @FXML
    private TableColumn<PenjualanDetail, Number> qtyColumn;

    @FXML
    public TextField noPemesananField;
    @FXML
    private TextField tglPemesananField;

    @FXML
    private TextField namaField;
    @FXML
    private TextArea alamatField;
    @FXML
    public TextArea alamatKirimField;
    @FXML
    public TextField namaSupirField;

    @FXML
    private Label noPengirimanField;
    @FXML
    private Label tglPengirimanField;
    @FXML
    private Label totalQtyField;
    @FXML
    private Label totalTonaseField;

    @FXML
    private Button addPemesananButton;
    @FXML
    private Button cancelButton;
    @FXML
    public Button saveButton;

    public PemesananHead pemesanan;
    public ObservableList<PenjualanDetail> allPenjualanDetail = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().namaBarangProperty());
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        catatanInternColumn.setCellValueFactory(cellData -> cellData.getValue().catatanInternProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().satuanProperty());
        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().qtyProperty());
        qtyColumn.setCellFactory(col -> Function.getTableCell());
        pengirimanDetailTable.setItems(allPenjualanDetail);

        ContextMenu cm = new ContextMenu();
        MenuItem addBarang = new MenuItem("Add Barang");
        addBarang.setOnAction((ActionEvent e) -> {
            addBarang();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            pengirimanDetailTable.refresh();
        });
        cm.getItems().addAll(addBarang, refresh);
        pengirimanDetailTable.setContextMenu(cm);
        pengirimanDetailTable.setRowFactory((TableView<PenjualanDetail> tv) -> {
            final TableRow<PenjualanDetail> row = new TableRow<PenjualanDetail>() {
                @Override
                public void updateItem(PenjualanDetail item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(cm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addBarang = new MenuItem("Add Barang");
                        addBarang.setOnAction((ActionEvent e) -> {
                            addBarang();
                        });
                        MenuItem delete = new MenuItem("Delete Barang");
                        delete.setOnAction((ActionEvent e) -> {
                            allPenjualanDetail.remove(item);
                            hitungTotal();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            pengirimanDetailTable.refresh();
                        });
                        if (saveButton.isVisible()) {
                            rm.getItems().addAll(addBarang, delete);
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

    private void hitungTotal() {
        double total = 0;
        double totalTonase = 0;
        for (PenjualanDetail d : allPenjualanDetail) {
            total = total + d.getQty();
            totalTonase = totalTonase + d.getQty() * d.getBarang().getBerat();
        }
        totalQtyField.setText(df.format(total));
        totalTonaseField.setText(df.format(totalTonase));
    }

    public void setNewPengiriman() {
        noPengirimanField.setText("");
        tglPengirimanField.setText("");
    }

    public void setDetailPengiriman(String noPenjualan) {
        Task<PenjualanHead> task = new Task<PenjualanHead>() {
            @Override
            public PenjualanHead call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    PenjualanHead pengiriman = PenjualanHeadDAO.get(con, noPenjualan);
                    pengiriman.setPemesananHead(PemesananHeadDAO.get(con, pengiriman.getNoPemesanan()));
                    pengiriman.setCustomer(CustomerDAO.get(con, pengiriman.getKodeCustomer()));
                    pengiriman.setListPenjualanDetail(PenjualanDetailDAO.getAllPenjualanDetail(con, noPenjualan));
                    for (PenjualanDetail d : pengiriman.getListPenjualanDetail()) {
                        d.setBarang(BarangDAO.get(con, d.getKodeBarang()));
                    }
                    return pengiriman;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((ev) -> {
            try {
                mainApp.closeLoading();
                addPemesananButton.setVisible(false);
                alamatKirimField.setDisable(true);
                namaSupirField.setDisable(true);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                List<MenuItem> removeMenu = new ArrayList<>();
                for (MenuItem m : pengirimanDetailTable.getContextMenu().getItems()) {
                    if (m.getText().equals("Add Barang")) {
                        removeMenu.add(m);
                    }
                }
                pengirimanDetailTable.getContextMenu().getItems().removeAll(removeMenu);

                PenjualanHead pengiriman = task.getValue();
                noPengirimanField.setText(pengiriman.getNoPenjualan());
                tglPengirimanField.setText(tglLengkap.format(tglSql.parse(pengiriman.getTglPenjualan())));
                noPemesananField.setText(pengiriman.getNoPemesanan());
                if (pengiriman.getPemesananHead() != null) {
                    tglPemesananField.setText(tglLengkap.format(tglSql.parse(pengiriman.getPemesananHead().getTglPemesanan())));
                }
                namaField.setText(pengiriman.getCustomer().getNama());
                alamatField.setText(pengiriman.getCustomer().getAlamat());
                alamatKirimField.setText(pengiriman.getTujuanKirim());
                namaSupirField.setText(pengiriman.getSupir());
                allPenjualanDetail.addAll(pengiriman.getListPenjualanDetail());
                hitungTotal();
            } catch (Exception e) {
                e.printStackTrace();
                mainApp.showMessage(Modality.NONE, "Error", e.toString());
            }
        });
        task.setOnFailed((e) -> {
            task.getException().printStackTrace();
            mainApp.showMessage(Modality.NONE, "Error", task.getException().toString());
            mainApp.closeLoading();
        });
        new Thread(task).start();
    }

    @FXML
    private void addPemesanan() {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/AddPemesanan.fxml");
        AddPemesananController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.pemesananHeadTable.setRowFactory(table -> {
            TableRow<PemesananHead> row = new TableRow<PemesananHead>() {
            };
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                        && mouseEvent.getClickCount() == 2) {
                    if (row.getItem() != null) {
                        try {
                            mainApp.closeDialog(stage, child);
                            pemesanan = row.getItem();
                            noPemesananField.setText(pemesanan.getNoPemesanan());
                            tglPemesananField.setText(tglLengkap.format(tglSql.parse(pemesanan.getTglPemesanan())));
                            namaField.setText(pemesanan.getCustomer().getNama());
                            alamatField.setText(pemesanan.getCustomer().getAlamat());
                            allPenjualanDetail.clear();
                            hitungTotal();
                        } catch (Exception e) {
                            mainApp.showMessage(Modality.NONE, "Error", e.toString());
                        }
                    }
                }
            });
            return row;
        });
    }

    @FXML
    private void changeGudang() {
        allPenjualanDetail.clear();
        hitungTotal();
    }

    @FXML
    private void close() {
        mainApp.closeDialog(owner, stage);
    }

    @FXML
    private void addBarang() {
        if (pemesanan == null) {
            mainApp.showMessage(Modality.NONE, "Warning", "Pemesanan belum dipilih");
        } else {
            Stage child = new Stage();
            FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/AddDetailPemesanan.fxml");
            AddDetailPemesananController controller = loader.getController();
            controller.setMainApp(mainApp, stage, child);
            controller.setPemesananDetail(pemesanan.getListPemesananDetail());
            controller.addButton.setOnAction((ActionEvent event) -> {
                if (controller.pemesananDetail != null) {
                    mainApp.closeDialog(stage, child);
                    double qty = 0;
                    for (PemesananDetail d : controller.allPemesananDetail) {
                        if (d.getKodeBarang().equals(controller.pemesananDetail.getKodeBarang())
                                && d.getKeterangan().equals(controller.pemesananDetail.getKeterangan())) {
                            qty = qty + (d.getQty() - d.getQtyTerkirim());
                        }
                    }
                    PenjualanDetail d = null;
                    for (PenjualanDetail temp : allPenjualanDetail) {
                        if (temp.getNoPemesanan().equals(controller.pemesananDetail.getNoPemesanan())
                                && temp.getNoUrut() == controller.pemesananDetail.getNoUrut()) {
                            qty = qty - temp.getQty();
                            d = temp;
                        }
                    }
                    if (qty >= Double.parseDouble(controller.qtyField.getText().replaceAll(",", ""))) {
                        if (d == null) {
                            try (Connection con = Koneksi.getConnection()) {
                                LogBarang log = LogBarangDAO.getLastByBarang(con, controller.pemesananDetail.getKodeBarang());
                                PenjualanDetail detail = new PenjualanDetail();
                                detail.setNoPemesanan(controller.pemesananDetail.getNoPemesanan());
                                detail.setNoUrut(controller.pemesananDetail.getNoUrut());
                                detail.setKodeBarang(controller.pemesananDetail.getKodeBarang());
                                detail.setNamaBarang(controller.pemesananDetail.getNamaBarang());
                                detail.setKeterangan(controller.keteranganField.getText());
                                detail.setCatatanIntern(controller.pemesananDetail.getCatatanIntern());
                                detail.setSatuan(controller.pemesananDetail.getSatuan());
                                detail.setQty(Double.parseDouble(controller.qtyField.getText().replaceAll(",", "")));
                                detail.setNilai(log.getNilaiAkhir() / log.getStokAkhir());
                                detail.setHargaJual(controller.pemesananDetail.getHargaJual());
                                detail.setTotal(Double.parseDouble(controller.qtyField.getText().replaceAll(",", ""))
                                        * controller.pemesananDetail.getHargaJual());
                                detail.setBarang(BarangDAO.get(con, controller.pemesananDetail.getKodeBarang()));
                                allPenjualanDetail.add(detail);
                            } catch (Exception e) {
                                mainApp.showMessage(Modality.NONE, "Error", e.toString());
                            }
                        } else {
                            d.setQty(d.getQty() + Double.parseDouble(controller.qtyField.getText().replaceAll(",", "")));
                            d.setTotal(d.getQty() * d.getHargaJual());
                            pengirimanDetailTable.refresh();
                        }
                        hitungTotal();
                    } else {
                        mainApp.showMessage(Modality.NONE, "Warning", "Barang yang akan dikirim melebihi pemesanan");
                    }
                }
            });
        }
    }
}
