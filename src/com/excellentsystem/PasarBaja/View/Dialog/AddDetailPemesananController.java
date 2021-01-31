/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author excellent
 */
public class AddDetailPemesananController {

    @FXML
    private TableView<PemesananDetail> pemesananDetailTable;
    @FXML
    private TableColumn<PemesananDetail, String> kodeBarangColumn;
    @FXML
    private TableColumn<PemesananDetail, String> namaBarangColumn;
    @FXML
    private TableColumn<PemesananDetail, String> keteranganColumn;
    @FXML
    private TableColumn<PemesananDetail, String> catatanInternColumn;
    @FXML
    private TableColumn<PemesananDetail, String> satuanColumn;
    @FXML
    private TableColumn<PemesananDetail, Number> qtyColumn;
    @FXML
    private TableColumn<PemesananDetail, Number> qtyTerkirimColumn;

    @FXML
    private TextField kodeBarangField;
    @FXML
    private TextField namaBarangField;
    @FXML
    public TextField qtyField;
    @FXML
    private TextField satuanField;
    @FXML
    public TextField keteranganField;
    @FXML
    public TextField catatanInternField;
    @FXML
    public Button addButton;
    public PemesananDetail pemesananDetail;
    private Main mainApp;
    private Stage stage;
    private Stage owner;
    public ObservableList<PemesananDetail> allPemesananDetail = FXCollections.observableArrayList();

    public void initialize() {
        kodeBarangColumn.setCellValueFactory(cellData -> cellData.getValue().kodeBarangProperty());
        namaBarangColumn.setCellValueFactory(cellData -> cellData.getValue().namaBarangProperty());
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        catatanInternColumn.setCellValueFactory(cellData -> cellData.getValue().catatanInternProperty());
        satuanColumn.setCellValueFactory(cellData -> cellData.getValue().satuanProperty());
        qtyColumn.setCellValueFactory(cellData -> cellData.getValue().qtyProperty());
        qtyColumn.setCellFactory(col -> Function.getTableCell());
        qtyTerkirimColumn.setCellValueFactory(cellData -> cellData.getValue().qtyTerkirimProperty());
        qtyTerkirimColumn.setCellFactory(col -> Function.getTableCell());
        pemesananDetailTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectPemesananDetail(newValue);
        });
        qtyField.setOnKeyReleased((event) -> {
            try {
                String string = qtyField.getText();
                if (string.indexOf(".") > 0) {
                    String string2 = string.substring(string.indexOf(".") + 1, string.length());
                    if (string2.contains(".")) {
                        qtyField.undo();
                    } else if (!string2.equals("") && Double.parseDouble(string2) != 0) {
                        qtyField.setText(df.format(Double.parseDouble(string.replaceAll(",", ""))));
                    }
                } else {
                    qtyField.setText(df.format(Double.parseDouble(string.replaceAll(",", ""))));
                }
                qtyField.end();
            } catch (Exception e) {
                qtyField.undo();
            }
        });
    }

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        pemesananDetailTable.setItems(allPemesananDetail);
        stage.setOnCloseRequest((e) -> {
            mainApp.closeDialog(owner, stage);
        });
        stage.setHeight(mainApp.screenSize.getHeight() * 0.9);
        stage.setWidth(mainApp.screenSize.getWidth() * 0.9);
        stage.setX((mainApp.screenSize.getWidth() - stage.getWidth()) / 2);
        stage.setY((mainApp.screenSize.getHeight() - stage.getHeight()) / 2);
    }

    public void setPemesananDetail(List<PemesananDetail> listPemesanan) {
        allPemesananDetail.clear();
        allPemesananDetail.addAll(listPemesanan);
    }

    private void selectPemesananDetail(PemesananDetail value) {
        pemesananDetail = null;
        kodeBarangField.setText("");
        namaBarangField.setText("");
        satuanField.setText("");
        keteranganField.setText("");
        catatanInternField.setText("");
        if (value != null) {
            pemesananDetail = value;
            kodeBarangField.setText(value.getKodeBarang());
            namaBarangField.setText(value.getNamaBarang());
            satuanField.setText(value.getSatuan());
            keteranganField.setText(value.getKeterangan());
            catatanInternField.setText(value.getCatatanIntern());
            qtyField.setText(df.format(value.getQty()));
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }
}
