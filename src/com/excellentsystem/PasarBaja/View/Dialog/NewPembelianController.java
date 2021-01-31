/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.BebanPembelianDAO;
import com.excellentsystem.PasarBaja.DAO.PembelianDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PembelianHeadDAO;
import com.excellentsystem.PasarBaja.DAO.SupplierDAO;
import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.tglLengkap;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.BebanPembelian;
import com.excellentsystem.PasarBaja.Model.PembelianDetail;
import com.excellentsystem.PasarBaja.Model.PembelianHead;
import com.excellentsystem.PasarBaja.Model.Supplier;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class NewPembelianController {

    @FXML
    private TableView<PembelianDetail> pembelianDetailTable;
    @FXML
    private TableColumn<PembelianDetail, String> kodeBarangColumn;
    @FXML
    private TableColumn<PembelianDetail, String> namaBarangColumn;
    @FXML
    private TableColumn<PembelianDetail, String> keteranganColumn;
    @FXML
    private TableColumn<PembelianDetail, String> satuanColumn;
    @FXML
    private TableColumn<PembelianDetail, Number> qtyColumn;
    @FXML
    private TableColumn<PembelianDetail, Number> hargaBeliColumn;
    @FXML
    private TableColumn<PembelianDetail, Number> totalColumn;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label noPembelianField;
    @FXML
    private Label tglPembelianField;

    @FXML
    private TextField namaField;

    @FXML
    public TextArea catatanField;

    @FXML
    private Label totalQtyField;

    @FXML
    public TextField bebanPembelianField;
    @FXML
    public TextField totalPembelianField;
    @FXML
    public TextField grandtotalField;

    @FXML
    private Button addSupplierButton;
    @FXML
    public Button saveButton;
    @FXML
    private Button cancelButton;

    public Supplier supplier;
    public ObservableList<PembelianDetail> allPembelianBarangDetail = FXCollections.observableArrayList();
    public List<BebanPembelian> allBebanPembelian = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().namaBarangProperty());
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().satuanProperty());
        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().qtyProperty());
        hargaBeliColumn.setCellValueFactory(cellData -> cellData.getValue().hargaBeliProperty());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());

        qtyColumn.setCellFactory(col -> Function.getTableCell());
        hargaBeliColumn.setCellFactory(col -> Function.getTableCell());
        totalColumn.setCellFactory(col -> Function.getTableCell());
        pembelianDetailTable.setItems(allPembelianBarangDetail);

        ContextMenu cm = new ContextMenu();
        MenuItem addBahan = new MenuItem("Add New Barang");
        addBahan.setOnAction((ActionEvent e) -> {
            addBarang();
        });
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent e) -> {
            pembelianDetailTable.refresh();
        });
        cm.getItems().addAll(addBahan, refresh);
        pembelianDetailTable.setContextMenu(cm);
        pembelianDetailTable.setRowFactory((TableView<PembelianDetail> tv) -> {
            final TableRow<PembelianDetail> row = new TableRow<PembelianDetail>() {
                @Override
                public void updateItem(PembelianDetail item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(cm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem addBahan = new MenuItem("Add New Barang");
                        addBahan.setOnAction((ActionEvent e) -> {
                            addBarang();
                        });
                        MenuItem delete = new MenuItem("Delete Barang");
                        delete.setOnAction((ActionEvent e) -> {
                            allPembelianBarangDetail.remove(item);
                            hitungTotal();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent e) -> {
                            pembelianDetailTable.refresh();
                        });
                        if (saveButton.isVisible()) {
                            rm.getItems().addAll(addBahan, delete);
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

    @FXML
    private void hitungTotal() {
        double totalQty = 0;
        double totalPembelian = 0;
        double totalBeban = 0;
        for (PembelianDetail d : allPembelianBarangDetail) {
            totalQty = totalQty + d.getQty();
            totalPembelian = totalPembelian + d.getTotal();
        }
        for (BebanPembelian b : allBebanPembelian) {
            totalBeban = totalBeban + b.getJumlahRp();
        }
        totalQtyField.setText(df.format(totalQty));
        bebanPembelianField.setText(df.format(totalBeban));
        totalPembelianField.setText(df.format(totalPembelian));
        grandtotalField.setText(df.format(totalPembelian + totalBeban));
    }

    public void setNewPembelian() {
        noPembelianField.setText("");
        tglPembelianField.setText("");
        allBebanPembelian = new ArrayList<>();
    }

    public void setDetailPembelian(String noPembelian) {
        Task<PembelianHead> task = new Task<PembelianHead>() {
            @Override
            public PembelianHead call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    PembelianHead p = PembelianHeadDAO.get(con, noPembelian);
                    p.setSupplier(SupplierDAO.get(con, p.getKodeSupplier()));
                    p.setListPembelianDetail(PembelianDetailDAO.getAllByNoPembelian(con, noPembelian));
                    p.setListBebanPembelian(BebanPembelianDAO.getAllByNoPembelian(con, noPembelian));
                    return p;
                }
            }
        };
        task.setOnRunning((e) -> {
            mainApp.showLoadingScreen();
        });
        task.setOnSucceeded((ev) -> {
            try {
                mainApp.closeLoading();
                addSupplierButton.setVisible(false);
                catatanField.setDisable(true);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                AnchorPane.setBottomAnchor(gridPane, 0.0);
                List<MenuItem> removeMenu = new ArrayList<>();
                for (MenuItem m : pembelianDetailTable.getContextMenu().getItems()) {
                    if (m.getText().equals("Add New Barang")) {
                        removeMenu.add(m);
                    }
                }
                pembelianDetailTable.getContextMenu().getItems().removeAll(removeMenu);

                PembelianHead p = task.getValue();
                noPembelianField.setText(p.getNoPembelian());
                tglPembelianField.setText(tglLengkap.format(tglSql.parse(p.getTglPembelian())));
                namaField.setText(p.getSupplier().getNama());
                catatanField.setText(p.getCatatan());

                allPembelianBarangDetail.addAll(p.getListPembelianDetail());
                allBebanPembelian.addAll(p.getListBebanPembelian());
                totalPembelianField.setText(df.format(p.getTotalPembelian()));
                bebanPembelianField.setText(df.format(p.getTotalBebanPembelian()));
                grandtotalField.setText(df.format(p.getGrandtotal()));
                hitungTotal();
            } catch (Exception e) {
                e.printStackTrace();
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
    private void close() {
        mainApp.closeDialog(owner, stage);
    }

    @FXML
    private void addBarang() {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/AddBarangPembelian.fxml");
        AddBarangPembelianController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.addButton.setOnAction((ActionEvent event) -> {
            if (controller.barang == null) {
                mainApp.showMessage(Modality.NONE, "Warning", "Barang belum dipilih atau kode barang masih kosong");
            } else if (controller.qtyField.getText().equals("0") || controller.qtyField.getText().equals("")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Qty tidak boleh kosong");
            } else if (controller.totalField.getText().equals("0") || controller.totalField.getText().equals("")) {
                mainApp.showMessage(Modality.NONE, "Warning", "Total tidak boleh kosong");
            } else {
                mainApp.closeDialog(stage, child);
                PembelianDetail d = null;
                for (PembelianDetail temp : allPembelianBarangDetail) {
                    if (temp.getKodeBarang().equals(controller.barang.getKodeBarang())
                            && temp.getKeterangan().equals(controller.keteranganField.getText())
                            && temp.getHargaBeli() == Double.parseDouble(controller.hargaBeliField.getText().replaceAll(",", ""))) {
                        d = temp;
                    }
                }
                if (d == null) {
                    PembelianDetail detail = new PembelianDetail();
                    detail.setKodeBarang(controller.barang.getKodeBarang());
                    detail.setNamaBarang(controller.barang.getNamaBarang());
                    detail.setKeterangan(controller.keteranganField.getText());
                    detail.setSatuan(controller.barang.getSatuan());
                    detail.setQty(Double.parseDouble(controller.qtyField.getText().replaceAll(",", "")));
                    detail.setHargaBeli(Double.parseDouble(controller.hargaBeliField.getText().replaceAll(",", "")));
                    detail.setTotal(Double.parseDouble(controller.totalField.getText().replaceAll(",", "")));
                    allPembelianBarangDetail.add(detail);
                    hitungTotal();
                } else {
                    d.setQty(d.getQty() + Double.parseDouble(controller.qtyField.getText().replaceAll(",", "")));
                    d.setTotal(d.getQty() * d.getHargaBeli());
                    pembelianDetailTable.refresh();
                    hitungTotal();
                }
            }
        });
    }

    @FXML
    private void addBeban() {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/DetailBebanPembelian.fxml");
        DetailBebanPembelianController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.setDetailBebanPembelianCoil(allBebanPembelian);
        if (!saveButton.isVisible()) {
            controller.viewBebanPembelianCoil();
        }
        controller.saveAndCloseButton.setOnAction((ActionEvent event) -> {
            allBebanPembelian.clear();
            allBebanPembelian.addAll(controller.allBebanPembelianCoilDetail);
            hitungTotal();
            mainApp.closeDialog(stage, child);
        });
    }

    @FXML
    private void addSupplier() {
        Stage child = new Stage();
        FXMLLoader loader = mainApp.showDialog(stage, child, "View/Dialog/AddSupplier.fxml");
        AddSupplierController controller = loader.getController();
        controller.setMainApp(mainApp, stage, child);
        controller.supplierTable.setRowFactory(table -> {
            TableRow<Supplier> row = new TableRow<Supplier>() {
            };
            row.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    if (row.getItem() != null) {
                        mainApp.closeDialog(stage, child);
                        supplier = row.getItem();
                        namaField.setText(supplier.getNama());
                    }
                }
            });
            return row;
        });
    }
}
