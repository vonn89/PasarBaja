/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.Function;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.df;
import com.excellentsystem.PasarBaja.Model.BebanPembelian;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class DetailBebanPembelianController {

    @FXML
    private TableView<BebanPembelian> bebanPembelianTable;
    @FXML
    private TableColumn<BebanPembelian, String> keteranganColumn;
    @FXML
    private TableColumn<BebanPembelian, Number> jumlahRpColumn;

    @FXML
    private TextField keteranganField;
    @FXML
    private TextField jumlahRpField;
    @FXML
    private Label totalBebanField;
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    public Button saveAndCloseButton;
    public ObservableList<BebanPembelian> allBebanPembelianCoilDetail = FXCollections.observableArrayList();
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void initialize() {
        keteranganColumn.setCellValueFactory(cellData -> cellData.getValue().keteranganProperty());
        jumlahRpColumn.setCellValueFactory(cellData -> cellData.getValue().jumlahRpProperty());
        jumlahRpColumn.setCellFactory(col -> Function.getTableCell());

        Function.setNumberField(jumlahRpField);
        final ContextMenu rm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            bebanPembelianTable.refresh();
        });
        rm.getItems().addAll(refresh);
        bebanPembelianTable.setContextMenu(rm);
        bebanPembelianTable.setRowFactory(table -> {
            TableRow<BebanPembelian> row = new TableRow<BebanPembelian>() {
                @Override
                public void updateItem(BebanPembelian item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setContextMenu(rm);
                    } else {
                        final ContextMenu rm = new ContextMenu();
                        MenuItem hapus = new MenuItem("Delete Beban Pembelian");
                        hapus.setOnAction((ActionEvent event) -> {
                            allBebanPembelianCoilDetail.remove(item);
                            hitungTotal();
                        });
                        MenuItem refresh = new MenuItem("Refresh");
                        refresh.setOnAction((ActionEvent event) -> {
                            bebanPembelianTable.refresh();
                        });
                        if (anchorPane.isVisible()) {
                            rm.getItems().addAll(hapus);
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
        bebanPembelianTable.setItems(allBebanPembelianCoilDetail);
        stage.setOnCloseRequest((event) -> {
            mainApp.closeDialog(owner, stage);
        });
    }

    private void hitungTotal() {
        double total = 0;
        for (BebanPembelian temp : allBebanPembelianCoilDetail) {
            total = total + temp.getJumlahRp();
        }
        totalBebanField.setText(df.format(total));
    }

    public void setDetailBebanPembelianCoil(List<BebanPembelian> allBeban) {
        allBebanPembelianCoilDetail.clear();
        allBebanPembelianCoilDetail.addAll(allBeban);
        hitungTotal();
    }

    @FXML
    private void addBeban() {
        if (!jumlahRpField.getText().equals("") && !keteranganField.getText().equals("")) {
            BebanPembelian beban = new BebanPembelian();
            beban.setKeterangan(keteranganField.getText());
            beban.setJumlahRp(Double.parseDouble(jumlahRpField.getText().replaceAll(",", "")));
            allBebanPembelianCoilDetail.add(beban);
            hitungTotal();
            jumlahRpField.setText("0");
            keteranganField.setText("");
        } else {
            mainApp.showMessage(Modality.NONE, "Warning", "Keterangan atau jumlah rp masih kosong");
        }
    }

    public void viewBebanPembelianCoil() {
        gridPane.setPrefHeight(500);
        anchorPane.setVisible(false);
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
