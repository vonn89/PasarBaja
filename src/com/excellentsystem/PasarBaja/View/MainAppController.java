/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View;

import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.sistem;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Duration;

/**
 *
 * @author Xtreme
 */
public class MainAppController {

    @FXML
    public VBox vbox;
    @FXML
    private Accordion accordion;
    @FXML
    private Label title;

    @FXML
    private TitledPane loginButton;
    @FXML
    private MenuButton logoutButton;
    @FXML
    private MenuButton ubahPasswordButton;

    @FXML
    private TitledPane masterPane;
    @FXML
    private VBox masterVbox;
    @FXML
    private MenuButton menuDataCustomer;
    @FXML
    private MenuButton menuDataSupplier;
    @FXML
    private MenuButton menuDataBarang;

    @FXML
    private TitledPane penjualanPane;
    @FXML
    private VBox penjualanVbox;
    @FXML
    private MenuButton menuPemesanan;
    @FXML
    private MenuButton menuPenjualan;

    @FXML
    private TitledPane pembelianPane;

    @FXML
    private TitledPane barangPane;
    @FXML
    private VBox barangVbox;
    @FXML
    private MenuButton menuPermintaanBarang;
    @FXML
    private MenuButton menuPengirimanBarang;

    @FXML
    private TitledPane keuanganPane;
    @FXML
    private VBox keuanganVbox;
    @FXML
    private MenuButton menuKeuangan;
    @FXML
    private MenuButton menuHutang;
    @FXML
    private MenuButton menuPiutang;
    @FXML
    private MenuButton menuModal;
    @FXML
    private MenuButton menuAsetTetap;

    @FXML
    private TitledPane laporanPane;
    @FXML
    private VBox laporanVbox;
    @FXML
    private MenuButton menuLaporanBarang;
    @FXML
    private MenuButton menuLaporanPenjualan;
    @FXML
    private MenuButton menuLaporanPembelian;
    @FXML
    private MenuButton menuLaporanKeuangan;
    @FXML
    private MenuButton menuLaporanManagerial;

    @FXML
    private TitledPane pengaturanPane;
    @FXML
    private VBox pengaturanVbox;
    @FXML
    private MenuButton menuDataUser;
    @FXML
    private MenuButton menuKategoriHutang;
    @FXML
    private MenuButton menuKategoriPiutang;
    @FXML
    private MenuButton menuKategoriKeuangan;
    @FXML
    private MenuButton menuKategoriTransaksi;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        try {
            this.mainApp = mainApp;
            vbox.setPrefWidth(0);
            vbox.setVisible(false);
            for (Node n : vbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            for (Node n : masterVbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            for (Node n : penjualanVbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            for (Node n : barangVbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            for (Node n : keuanganVbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            for (Node n : laporanVbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            for (Node n : pengaturanVbox.getChildren()) {
                n.managedProperty().bind(n.visibleProperty());
            }
            title.setText("PASAR BAJA");
            setUser();
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
            e.printStackTrace();
        }
    }

    public void setUser() {
        menuDataCustomer.setVisible(false);
        menuDataSupplier.setVisible(false);
        menuDataBarang.setVisible(false);

        menuPemesanan.setVisible(false);
        menuPenjualan.setVisible(false);

        menuPermintaanBarang.setVisible(false);
        menuPengirimanBarang.setVisible(false);

        menuKeuangan.setVisible(false);
        menuHutang.setVisible(false);
        menuPiutang.setVisible(false);
        menuModal.setVisible(false);
        menuAsetTetap.setVisible(false);

        menuLaporanBarang.setVisible(false);
        menuLaporanPenjualan.setVisible(false);
        menuLaporanPembelian.setVisible(false);
        menuLaporanKeuangan.setVisible(false);
        menuLaporanManagerial.setVisible(false);

        menuDataUser.setVisible(false);
        menuKategoriHutang.setVisible(false);
        menuKategoriPiutang.setVisible(false);
        menuKategoriKeuangan.setVisible(false);
        menuKategoriTransaksi.setVisible(false);
        if (sistem.getUser() == null) {
            mainApp.showLoginScene();
        } else {
            logoutButton.setVisible(true);
            ubahPasswordButton.setVisible(true);
            loginButton.setText("User : " + sistem.getUser().getKodeUser());
            for (Otoritas o : sistem.getUser().getOtoritas()) {
                if (o.getJenis().equals("Data Customer")) {
                    menuDataCustomer.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Data Supplier")) {
                    menuDataSupplier.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Data Barang")) {
                    menuDataBarang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Pemesanan")) {
                    menuPemesanan.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Penjualan")) {
                    menuPenjualan.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Pembelian")) {
                    if (o.isStatus()) {
                        pembelianPane.setVisible(true);
                    } else {
                        accordion.getPanes().remove(pembelianPane);
                    }
                } else if (o.getJenis().equals("Permintaan Barang")) {
                    menuPermintaanBarang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Pengiriman Barang")) {
                    menuPengirimanBarang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Keuangan")) {
                    menuKeuangan.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Hutang")) {
                    menuHutang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Piutang")) {
                    menuPiutang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Modal")) {
                    menuModal.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Aset Tetap")) {
                    menuAsetTetap.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Laporan Barang")) {
                    menuLaporanBarang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Laporan Penjualan")) {
                    menuLaporanPenjualan.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Laporan Pembelian")) {
                    menuLaporanPembelian.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Laporan Keuangan")) {
                    menuLaporanKeuangan.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Laporan Managerial")) {
                    menuLaporanManagerial.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Data User")) {
                    menuDataUser.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Kategori Hutang")) {
                    menuKategoriHutang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Kategori Piutang")) {
                    menuKategoriPiutang.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Kategori Keuangan")) {
                    menuKategoriKeuangan.setVisible(o.isStatus());
                } else if (o.getJenis().equals("Kategori Transaksi")) {
                    menuKategoriTransaksi.setVisible(o.isStatus());
                }
            }
            if (menuDataCustomer.isVisible() == false
                    && menuDataSupplier.isVisible() == false
                    && menuDataBarang.isVisible() == false) {
                accordion.getPanes().remove(masterPane);
            }
            if (menuPemesanan.isVisible() == false
                    && menuPenjualan.isVisible() == false) {
                accordion.getPanes().remove(penjualanPane);
            }
            if (menuPermintaanBarang.isVisible() == false
                    && menuPengirimanBarang.isVisible() == false) {
                accordion.getPanes().remove(barangPane);
            }
            if (menuKeuangan.isVisible() == false
                    && menuHutang.isVisible() == false
                    && menuPiutang.isVisible() == false
                    && menuModal.isVisible() == false
                    && menuAsetTetap.isVisible() == false) {
                accordion.getPanes().remove(keuanganPane);
            }
            if (menuLaporanBarang.isVisible() == false
                    && menuLaporanPenjualan.isVisible() == false
                    && menuLaporanPembelian.isVisible() == false
                    && menuLaporanKeuangan.isVisible() == false
                    && menuLaporanManagerial.isVisible() == false) {
                accordion.getPanes().remove(laporanPane);
            }
            if (menuDataUser.isVisible() == false
                    && menuKategoriHutang.isVisible() == false
                    && menuKategoriPiutang.isVisible() == false
                    && menuKategoriKeuangan.isVisible() == false
                    && menuKategoriTransaksi.isVisible() == false) {
                accordion.getPanes().remove(pengaturanPane);
            }
        }

    }

    @FXML
    public void showHideMenu() {
        final Animation hideSidebar = new Transition() {
            {
                setCycleDuration(Duration.millis(10));
            }

            @Override
            protected void interpolate(double frac) {
                final double curWidth = 200 * (1.0 - frac);
                vbox.setPrefWidth(curWidth);
                masterPane.setExpanded(false);
                penjualanPane.setExpanded(false);
                pembelianPane.setExpanded(false);
                barangPane.setExpanded(false);
                keuanganPane.setExpanded(false);
                laporanPane.setExpanded(false);
                pengaturanPane.setExpanded(false);
                loginButton.setExpanded(false);
            }
        };
        hideSidebar.onFinishedProperty().set((EventHandler<ActionEvent>) (ActionEvent actionEvent) -> {
            vbox.setVisible(false);
        });
        final Animation showSidebar = new Transition() {
            {
                setCycleDuration(Duration.millis(10));
            }

            @Override
            protected void interpolate(double frac) {
                final double curWidth = 200 * frac;
                vbox.setPrefWidth(curWidth);
            }
        };
        if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
            if (vbox.isVisible()) {
                hideSidebar.play();
            } else {
                vbox.setVisible(true);
                showSidebar.play();
            }
        }
    }

    public void setTitle(String x) {
        title.setText(x);
    }

    @FXML
    public void menuLogout() {
        mainApp.showLoginScene();
    }

    @FXML
    public void menuExit() {
        System.exit(0);
    }

    @FXML
    public void menushowUbahPassword() {
        mainApp.showUbahPassword();
    }

    @FXML
    public void menuDataCustomer() {
        mainApp.showDataCustomer();
    }

    @FXML
    public void menuDataSupplier() {
        mainApp.showDataSupplier();
    }

    @FXML
    public void menuDataBarang() {
        mainApp.showDataBarang();
    }

    @FXML
    public void menuDataUser() {
        mainApp.showDataUser();
    }

    @FXML
    private void showKategoriHutang() {
        mainApp.showKategoriHutang();
    }

    @FXML
    private void showKategoriPiutang() {
        mainApp.showKategoriPiutang();
    }

    @FXML
    private void showKategoriKeuangan() {
        mainApp.showKategoriKeuangan();
    }

    @FXML
    private void showKategoriTransaksi() {
        mainApp.showKategoriTransaksi();
    }

    @FXML
    public void menuPenjualan() {
        mainApp.showPenjualan();
    }

    @FXML
    public void menuPemesanan() {
        mainApp.showPemesanan();
    }

    @FXML
    public void menuPembelian() {
        mainApp.showPembelian();
    }

    @FXML
    public void menuPermintaanBarang() {
        mainApp.showPermintaanBarang();
    }

    @FXML
    public void menuPengirimanBarang() {
        mainApp.showPengirimanBarang();
    }

    @FXML
    public void menuKeuangan() {
        mainApp.showKeuangan();
    }

    @FXML
    public void menuHutang() {
        mainApp.showHutang();
    }

    @FXML
    public void menuPiutang() {
        mainApp.showPiutang();
    }

    @FXML
    public void menuModal() {
        mainApp.showModal();
    }

    @FXML
    public void menuAsetTetap() {
        mainApp.showAsetTetap();
    }

    @FXML
    public void menuLaporanBarang() {
        mainApp.showLaporanBarang();
    }

    @FXML
    public void menuLaporanPenyesuaianStokBarang() {
        mainApp.showLaporanPenyesuaianStokBarang();
    }

    @FXML
    public void menuLaporanPenjualan() {
        mainApp.showLaporanPenjualan();
    }

    @FXML
    public void menuLaporanBarangTerjual() {
        mainApp.showLaporanBarangTerjual();
    }

    @FXML
    public void menuLaporanPembelian() {
        mainApp.showLaporanPembelian();
    }

    @FXML
    public void menuLaporanBarangDibeli() {
        mainApp.showLaporanBarangDibeli();
    }

    @FXML
    public void menuLaporanKeuangan() {
        mainApp.showLaporanKeuangan();
    }

    @FXML
    public void menuLaporanHutang() {
        mainApp.showLaporanHutang();
    }

    @FXML
    public void menuLaporanPiutang() {
        mainApp.showLaporanPiutang();
    }

    @FXML
    public void menuLaporanUntungRugi() {
        mainApp.showLaporanUntungRugi();
    }

    @FXML
    public void menuLaporanUntungRugiPeriode() {
        mainApp.showLaporanUntungRugiPeriode();
    }

    @FXML
    public void menuLaporanNeraca() {
        mainApp.showLaporanNeraca();
    }
}
