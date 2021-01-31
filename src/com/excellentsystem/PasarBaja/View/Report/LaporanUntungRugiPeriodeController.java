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
import static com.excellentsystem.PasarBaja.Main.df;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
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
public class LaporanUntungRugiPeriodeController {

    private int rows = 0;
    private int columns = 0;

    @FXML
    private GridPane mainGridPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane stackPane;
    private GridPane gridPane;

    @FXML
    private ComboBox<String> periodeCombo;
    private String tglAwal = "2000-01-01";
    private String tglAkhir = "2000-01-01";
    private LocalDate localDate;
    private ObservableList<Keuangan> allPenjualan = FXCollections.observableArrayList();
    private ObservableList<Keuangan> allHPP = FXCollections.observableArrayList();
    private ObservableList<Keuangan> allPendapatanBeban = FXCollections.observableArrayList();
    private ObservableList<String> kategoriTransaksi = FXCollections.observableArrayList();
    private Main mainApp;

    public void initialize() {
        final ContextMenu rm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction((ActionEvent event) -> {
            getKeuangan();
        });
        rm.getItems().addAll(refresh);
        stackPane.setOnContextMenuRequested((e) -> {
            rm.show(stackPane, e.getScreenX(), e.getScreenY());
        });
        stackPane.setOnMouseClicked((event) -> {
            rm.hide();
        });
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ObservableList<String> periode = FXCollections.observableArrayList();
        periode.add("This Year");
        periode.add("Last Year");
        periode.add("All");
        periodeCombo.setItems(periode);
        periodeCombo.getSelectionModel().selectFirst();
        getKeuangan();
    }

    @FXML
    private void getKeuangan() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection con = Koneksi.getConnection()) {
                    localDate = LocalDate.parse(tglBarang.format(Function.getServerDate(con)), DateTimeFormatter.ISO_DATE);
                    tglAwal = localDate.toString();
                    tglAkhir = localDate.toString();
                    DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter yyyy = DateTimeFormatter.ofPattern("yyyy");
                    if (periodeCombo.getSelectionModel().getSelectedItem().equals("Last 4 Week")) {
                        tglAwal = localDate.minusWeeks(3).minusDays(localDate.getDayOfWeek().getValue()).format(yyyyMMdd);
                        tglAkhir = localDate.format(yyyyMMdd);
                        columns = 5;
                    } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("This Year")) {
                        tglAwal = localDate.format(yyyy) + "-01-01";
                        tglAkhir = localDate.format(yyyy) + "-12-31";
                        columns = 13;
                    } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("Last Year")) {
                        tglAwal = localDate.minusYears(1).format(yyyy) + "-01-01";
                        tglAkhir = localDate.minusYears(1).format(yyyy) + "-12-31";
                        columns = 13;
                    } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("All")) {
                        ResultSet rs = con.prepareStatement("select tgl_keuangan from tt_keuangan order by tgl_keuangan limit 1").executeQuery();
                        while (rs.next()) {
                            tglAwal = rs.getString(1).substring(0, 10);
                        }
                        tglAkhir = localDate.format(yyyy) + "-12-31";
                        int range = localDate.getYear() - Integer.parseInt(tglAwal.substring(0, 4));
                        columns = range + 2;
                    }
                    allPenjualan.clear();
                    allHPP.clear();
                    allPendapatanBeban.clear();
                    allPenjualan.addAll(KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            "Penjualan", tglAwal, tglAkhir));
                    allHPP.addAll(KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            "HPP", tglAwal, tglAkhir));
                    allPendapatanBeban.addAll(KeuanganDAO.getAllByTipeKeuanganAndTanggal(con,
                            "Pendapatan/Beban", tglAwal, tglAkhir));
                    kategoriTransaksi.clear();
                    for (Keuangan k : allPendapatanBeban) {
                        if (!kategoriTransaksi.contains(k.getKategori())) {
                            kategoriTransaksi.add(k.getKategori());
                        }
                    }
                    rows = 6 + kategoriTransaksi.size();
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
            stackPane.getChildren().clear();
            gridPane = new GridPane();
            gridPane.setHgap(1);
            gridPane.setVgap(1);

            setTopHeader();
            setLeftHeader();

            AnchorPane ap = new AnchorPane();
            ap.setStyle("-fx-background-color: seccolor6");
            ap.setMaxSize(300, 41);
            ap.setMouseTransparent(true);
            Label l = new Label("");
            l.setPrefSize(300, 30);
            l.setStyle("-fx-background-color: seccolor2;"
                    + "-fx-text-fill: seccolor6;"
                    + "-fx-font-weight: bold;");
            AnchorPane.setBottomAnchor(l, 1.0);
            ap.getChildren().add(l);
            GridPane.setHalignment(ap, HPos.LEFT);
            GridPane.setValignment(ap, VPos.TOP);
            GridPane.setMargin(ap, new Insets(1, 0, 1, 12));
            mainGridPane.add(ap, 0, 1);

            if (periodeCombo.getSelectionModel().getSelectedItem().equals("Last 4 Week")) {
                DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate firstDate = LocalDate.parse(tglAwal, yyyyMMdd);
                setDetail(tglBarang.parse(firstDate.format(yyyyMMdd)), tglBarang.parse(firstDate.plusDays(7).format(yyyyMMdd)), 1);
                setDetail(tglBarang.parse(firstDate.plusWeeks(1).format(yyyyMMdd)), tglBarang.parse(firstDate.plusWeeks(1).plusDays(7).format(yyyyMMdd)), 2);
                setDetail(tglBarang.parse(firstDate.plusWeeks(2).format(yyyyMMdd)), tglBarang.parse(firstDate.plusWeeks(2).plusDays(7).format(yyyyMMdd)), 3);
                setDetail(tglBarang.parse(firstDate.plusWeeks(3).format(yyyyMMdd)), tglBarang.parse(firstDate.plusWeeks(3).plusDays(7).format(yyyyMMdd)), 4);
            } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("This Year") || periodeCombo.getSelectionModel().getSelectedItem().equals("Last Year")) {
                DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate firstDate = LocalDate.parse(tglAwal, yyyyMMdd);
                setDetail(tglBarang.parse(firstDate.format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(1).format(yyyyMMdd)), 1);
                setDetail(tglBarang.parse(firstDate.plusMonths(1).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(2).format(yyyyMMdd)), 2);
                setDetail(tglBarang.parse(firstDate.plusMonths(2).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(3).format(yyyyMMdd)), 3);
                setDetail(tglBarang.parse(firstDate.plusMonths(3).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(4).format(yyyyMMdd)), 4);
                setDetail(tglBarang.parse(firstDate.plusMonths(4).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(5).format(yyyyMMdd)), 5);
                setDetail(tglBarang.parse(firstDate.plusMonths(5).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(6).format(yyyyMMdd)), 6);
                setDetail(tglBarang.parse(firstDate.plusMonths(6).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(7).format(yyyyMMdd)), 7);
                setDetail(tglBarang.parse(firstDate.plusMonths(7).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(8).format(yyyyMMdd)), 8);
                setDetail(tglBarang.parse(firstDate.plusMonths(8).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(9).format(yyyyMMdd)), 9);
                setDetail(tglBarang.parse(firstDate.plusMonths(9).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(10).format(yyyyMMdd)), 10);
                setDetail(tglBarang.parse(firstDate.plusMonths(10).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(11).format(yyyyMMdd)), 11);
                setDetail(tglBarang.parse(firstDate.plusMonths(11).format(yyyyMMdd)), tglBarang.parse(firstDate.plusMonths(12).format(yyyyMMdd)), 12);
            } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("All")) {
                SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
                int now = Integer.parseInt(yyyy.format(tglBarang.parse(tglAwal)));
                int last = Integer.parseInt(yyyy.format(tglBarang.parse(tglAkhir)));
                int col = 1;
                for (int i = now; i <= last; i++) {
                    Date startDate = tglBarang.parse(String.valueOf(i) + "-01-01");
                    Date endDate = tglBarang.parse(String.valueOf(i) + "-12-31");
                    setDetail(startDate, endDate, col);
                    col++;
                }
            }

            stackPane.getChildren().add(gridPane);
        } catch (Exception ex) {
            mainApp.showMessage(Modality.NONE, "Error", ex.toString());
        }
    }

    private void setLeftHeader() {
        for (int i = 0; i < rows; i++) {
            gridPane.getRowConstraints().add(new RowConstraints(30, 30, 30));
        }
        ScrollPane sp = new ScrollPane();
        sp.setMouseTransparent(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setPadding(new Insets(6, 5, 6, 5));

        StackPane st = new StackPane();
        st.setMouseTransparent(true);
        st.setStyle("-fx-background-color: seccolor6;");
        st.setPadding(new Insets(0, 0, 0, 2));

        GridPane gp = new GridPane();
        gp.setHgap(1);
        gp.setVgap(1);
        gp.setMaxWidth(200);
        gp.getColumnConstraints().add(new ColumnConstraints(300, 300, 300, Priority.ALWAYS, HPos.LEFT, true));
        for (int i = 0; i < rows; i++) {
            gp.getRowConstraints().add(new RowConstraints(30, 30, 30));
        }
        int i = 1;
        addLeftHeaderText(gp, "Penjualan", 0, i);
        i++;
        addLeftHeaderText(gp, "Harga Pokok Penjualan", 0, i);
        i++;
        addLeftHeaderText(gp, "Untung-Rugi Kotor", 0, i);
        i++;
        gp.getRowConstraints().add(i, new RowConstraints(3, 3, 3));
        gridPane.getRowConstraints().add(i, new RowConstraints(3, 3, 3));
        addLeftHeaderText(gp, "", 0, i);
        i++;
        for (String s : kategoriTransaksi) {
            addLeftHeaderText(gp, s, 0, i);
            i++;
        }
        gp.getRowConstraints().add(i, new RowConstraints(3, 3, 3));
        gridPane.getRowConstraints().add(i, new RowConstraints(3, 3, 3));
        addLeftHeaderText(gp, "", 0, i);
        i++;
        addLeftHeaderText(gp, "Total Pendapatan-Beban", 0, i);
        i++;
        addLeftHeaderText(gp, "Untung-Rugi Bersih", 0, i);
        i++;
        StackPane.setMargin(gp, new Insets(5, 1, 5, 5));
        st.getChildren().add(gp);
        sp.setContent(st);
        mainGridPane.add(sp, 0, 1);

        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            sp.setVvalue(scrollPane.getVvalue());
        });
    }

    private void setTopHeader() throws ParseException {
        gridPane.getColumnConstraints().add(new ColumnConstraints(300, 300, 300, Priority.ALWAYS, HPos.LEFT, true));
        for (int i = 1; i < columns; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(150, 150, Double.MAX_VALUE, Priority.ALWAYS, HPos.RIGHT, true));
        }
        ScrollPane sp = new ScrollPane();
        sp.setMouseTransparent(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(true);
        sp.setPadding(new Insets(6, 6, 1, 5));

        StackPane st = new StackPane();
        st.setMouseTransparent(true);
        st.setStyle("-fx-background-color: seccolor6;");
        st.setPadding(new Insets(0, 0, 0, 1));

        GridPane gp = new GridPane();
        gp.setHgap(1);
        gp.setVgap(1);
        gp.setMinHeight(30);
        gp.setPrefHeight(30);
        gp.setMaxHeight(30);
        gp.getColumnConstraints().add(new ColumnConstraints(300, 300, 300, Priority.ALWAYS, HPos.LEFT, true));
        for (int i = 1; i < columns; i++) {
            gp.getColumnConstraints().add(new ColumnConstraints(150, 150, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true));
        }
        gp.getRowConstraints().add(new RowConstraints(30, 30, 30));
        if (periodeCombo.getSelectionModel().getSelectedItem().equals("Last 4 Week")) {
            DateTimeFormatter ddMMM = DateTimeFormatter.ofPattern("dd MMM");
            DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate firstDate = LocalDate.parse(tglAwal, yyyyMMdd);
            addTopHeaderText(gp, firstDate.format(ddMMM) + "-" + firstDate.plusDays(6).format(ddMMM), 1, 0);
            addTopHeaderText(gp, firstDate.plusWeeks(1).format(ddMMM) + "-" + firstDate.plusWeeks(1).plusDays(6).format(ddMMM), 2, 0);
            addTopHeaderText(gp, firstDate.plusWeeks(2).format(ddMMM) + "-" + firstDate.plusWeeks(2).plusDays(6).format(ddMMM), 3, 0);
            addTopHeaderText(gp, firstDate.plusWeeks(3).format(ddMMM) + "-" + firstDate.plusWeeks(3).plusDays(6).format(ddMMM), 4, 0);
        } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("This Year") || periodeCombo.getSelectionModel().getSelectedItem().equals("Last Year")) {
            addTopHeaderText(gp, "January", 1, 0);
            addTopHeaderText(gp, "February", 2, 0);
            addTopHeaderText(gp, "March", 3, 0);
            addTopHeaderText(gp, "April", 4, 0);
            addTopHeaderText(gp, "May", 5, 0);
            addTopHeaderText(gp, "June", 6, 0);
            addTopHeaderText(gp, "July", 7, 0);
            addTopHeaderText(gp, "August", 8, 0);
            addTopHeaderText(gp, "September", 9, 0);
            addTopHeaderText(gp, "October", 10, 0);
            addTopHeaderText(gp, "November", 11, 0);
            addTopHeaderText(gp, "December", 12, 0);
        } else if (periodeCombo.getSelectionModel().getSelectedItem().equals("All")) {
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            int now = Integer.parseInt(yyyy.format(tglBarang.parse(tglAwal)));
            int last = Integer.parseInt(yyyy.format(tglBarang.parse(tglAkhir)));
            int col = 1;
            for (int i = now; i <= last; i++) {
                addTopHeaderText(gp, String.valueOf(i), col, 0);
                col++;
            }
        }

        StackPane.setMargin(gp, new Insets(5, 5, 1, 5));
        st.getChildren().add(gp);
        sp.setContent(st);
        mainGridPane.add(sp, 0, 1);

        scrollPane.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            sp.setHvalue(scrollPane.getHvalue());
        });
    }

    private void setDetail(Date startDate, Date endDate, int col) throws ParseException {
        int i = 1;

        double totalPenjualan = 0;
        for (Keuangan k : allPenjualan) {
            Date date = tglSql.parse(k.getTglKeuangan());
            if (date.after(startDate) && date.before(endDate)) {
                totalPenjualan = totalPenjualan + k.getJumlahRp();
            }
        }
        addNormalText(df.format(totalPenjualan), col, i);
        i++;

        double totalHPP = 0;
        for (Keuangan k : allHPP) {
            Date date = tglSql.parse(k.getTglKeuangan());
            if (date.after(startDate) && date.before(endDate)) {
                totalHPP = totalHPP - k.getJumlahRp();
            }
        }
        addNormalText(df.format(totalHPP), col, i);
        i++;

        addNormalText(df.format(totalPenjualan + totalHPP), col, i);
        i++;

        addLeftHeaderText(gridPane, "", col, i);
        i++;

        double totalPendapatanBeban = 0;
        for (String s : kategoriTransaksi) {
            double pendapatanBeban = 0;
            List<Keuangan> temp = new ArrayList<>();
            for (Keuangan k : allPendapatanBeban) {
                Date date = tglSql.parse(k.getTglKeuangan());
                if (date.after(startDate) && date.before(endDate)) {
                    if (k.getKategori().equals(s)) {
                        pendapatanBeban = pendapatanBeban + k.getJumlahRp();
                        totalPendapatanBeban = totalPendapatanBeban + k.getJumlahRp();
                        temp.add(k);
                    }
                }
            }
            addHyperLinkPendapatanBebanText(df.format(pendapatanBeban), col, i, temp,
                    LocalDate.parse(tglBarang.format(startDate), DateTimeFormatter.ISO_DATE), LocalDate.parse(tglBarang.format(endDate), DateTimeFormatter.ISO_DATE));
            i++;
        }
        addLeftHeaderText(gridPane, "", col, i);
        i++;
        addNormalText(df.format(totalPendapatanBeban), col, i);
        i++;

        addNormalText(df.format(totalPenjualan + totalHPP + totalPendapatanBeban), col, i);

    }

    private void addLeftHeaderText(GridPane gridPane, String text, int column, int row) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color:seccolor2;");
        gridPane.add(anchorPane, column, row, 1, 1);

        Label label = new Label(text);
        label.setStyle("-fx-font-weight:bold;" + "-fx-text-fill: seccolor6;");
        label.setPadding(new Insets(0, 5, 0, 5));
        gridPane.add(label, column, row);
    }

    private void addTopHeaderText(GridPane gridPane, String text, int column, int row) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color:seccolor1;");
        gridPane.add(anchorPane, column, row, 1, 1);

        Label label = new Label(text);
        label.setStyle("-fx-font-weight:bold;" + "-fx-text-fill: seccolor6;");
        label.setPadding(new Insets(0, 5, 0, 5));
        gridPane.add(label, column, row);
    }

    private void addNormalText(String text, int column, int row) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color:seccolor5;");
        gridPane.add(anchorPane, column, row, 1, 1);

        Label label = new Label(text);
        label.setStyle("-fx-font-weight:bold;"
                + "-fx-text-fill: seccolor2;");
        label.setPadding(new Insets(0, 5, 0, 5));
        gridPane.add(label, column, row);
    }

    private void addBoldText(String text, int column, int row) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color:seccolor4;");
        gridPane.add(anchorPane, column, row, 1, 1);

        Label label = new Label(text);
        label.setStyle("-fx-font-weight:bold;"
                + "-fx-text-fill: seccolor2;");
        label.setPadding(new Insets(0, 5, 0, 5));
        gridPane.add(label, column, row);
    }

    private void addHyperLinkPenjualanText(String text, int column, int row, String tglAwal, String tglAkhir) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setStyle("-fx-font-size:12;"
                + "-fx-text-fill:seccolor3;"
                + "-fx-border-color:transparent;");
        hyperlink.setOnAction((e) -> {
            Stage stage = new Stage();
            FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/UntungRugiPenjualan.fxml");
            UntungRugiPenjualanController x = loader.getController();
            x.setMainApp(mainApp, mainApp.MainStage, stage);
            x.getPenjualan(tglAwal, tglAkhir);
        });
        gridPane.add(hyperlink, column, row);
    }

    private void addHyperLinkHPPPenjualanText(String text, int column, int row, String tglAwal, String tglAkhir) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setStyle("-fx-font-size:12;"
                + "-fx-text-fill:seccolor3;"
                + "-fx-border-color:transparent;");
        hyperlink.setOnAction((e) -> {
            Stage stage = new Stage();
            FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/UntungRugiHPPPenjualan.fxml");
            UntungRugiHPPPenjualanController x = loader.getController();
            x.setMainApp(mainApp, mainApp.MainStage, stage);
            x.getPenjualan(tglAwal, tglAkhir);
        });
        gridPane.add(hyperlink, column, row);
    }

    private void addHyperLinkPendapatanBebanText(String text, int column, int row, List<Keuangan> keuangan, LocalDate tglAwal, LocalDate tglAkhir) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color:seccolor5;");
        gridPane.add(anchorPane, column, row, 1, 1);

//        Label label = new Label(text);
//        label.setStyle("-fx-font-weight:bold;"
//                + "-fx-text-fill: seccolor2;");
//        label.setPadding(new Insets(0, 5, 0, 5));
//        gridPane.add(label, column, row);
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setStyle("-fx-font-size:12;"
                + "-fx-font-weight:bold;"
                + "-fx-text-fill:seccolor2;"
                + "-fx-border-color:transparent;");
        hyperlink.setPadding(new Insets(0, 5, 0, 5));
        hyperlink.setOnAction((e) -> {
            Stage stage = new Stage();
            FXMLLoader loader = mainApp.showDialog(mainApp.MainStage, stage, "View/Report/UntungRugiPendapatanBeban.fxml");
            UntungRugiPendapatanBebanController x = loader.getController();
            x.setMainApp(mainApp, mainApp.MainStage, stage);
            x.setKeuangan(keuangan, tglAwal, tglAkhir);
        });
        gridPane.add(hyperlink, column, row);
    }

}
