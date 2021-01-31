/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.Model;

import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xtreme
 */
public class PenjualanHead {
    private final StringProperty noPenjualan = new SimpleStringProperty();
    private final StringProperty tglPenjualan = new SimpleStringProperty();
    private final StringProperty noPemesanan = new SimpleStringProperty();
    private final StringProperty kodeCustomer = new SimpleStringProperty();
    private final StringProperty tujuanKirim = new SimpleStringProperty();
    private final StringProperty supir = new SimpleStringProperty();
    private final DoubleProperty totalBebanPenjualan = new SimpleDoubleProperty();
    private final DoubleProperty totalPenjualan = new SimpleDoubleProperty();
    private final DoubleProperty pembayaran = new SimpleDoubleProperty();
    private final DoubleProperty sisaPembayaran = new SimpleDoubleProperty();
    private final StringProperty catatan = new SimpleStringProperty();
    private final StringProperty kodeUser = new SimpleStringProperty();
    private final StringProperty tglBatal = new SimpleStringProperty();
    private final StringProperty userBatal = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private Customer customer;
    private List<PenjualanDetail> listPenjualanDetail;
    private PemesananHead pemesananHead;


    public double getTotalBebanPenjualan() {
        return totalBebanPenjualan.get();
    }

    public void setTotalBebanPenjualan(double value) {
        totalBebanPenjualan.set(value);
    }

    public DoubleProperty totalBebanPenjualanProperty() {
        return totalBebanPenjualan;
    }
    
    public String getTujuanKirim() {
        return tujuanKirim.get();
    }

    public void setTujuanKirim(String value) {
        tujuanKirim.set(value);
    }

    public StringProperty tujuanKirimProperty() {
        return tujuanKirim;
    }

    public String getSupir() {
        return supir.get();
    }

    public void setSupir(String value) {
        supir.set(value);
    }

    public StringProperty supirProperty() {
        return supir;
    }

    public PemesananHead getPemesananHead() {
        return pemesananHead;
    }

    public void setPemesananHead(PemesananHead pemesananHead) {
        this.pemesananHead = pemesananHead;
    }
    
    public List<PenjualanDetail> getListPenjualanDetail() {
        return listPenjualanDetail;
    }

    public void setListPenjualanDetail(List<PenjualanDetail> listPenjualanDetail) {
        this.listPenjualanDetail = listPenjualanDetail;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    

    public String getNoPemesanan() {
        return noPemesanan.get();
    }

    public void setNoPemesanan(String value) {
        noPemesanan.set(value);
    }

    public StringProperty noPemesananProperty() {
        return noPemesanan;
    }

    public String getCatatan() {
        return catatan.get();
    }

    public void setCatatan(String value) {
        catatan.set(value);
    }

    public StringProperty catatanProperty() {
        return catatan;
    }
    

    public String getNoPenjualan() {
        return noPenjualan.get();
    }

    public void setNoPenjualan(String value) {
        noPenjualan.set(value);
    }

    public StringProperty noPenjualanProperty() {
        return noPenjualan;
    }

    public String getTglPenjualan() {
        return tglPenjualan.get();
    }

    public void setTglPenjualan(String value) {
        tglPenjualan.set(value);
    }

    public StringProperty tglPenjualanProperty() {
        return tglPenjualan;
    }

    public String getKodeCustomer() {
        return kodeCustomer.get();
    }

    public void setKodeCustomer(String value) {
        kodeCustomer.set(value);
    }

    public StringProperty kodeCustomerProperty() {
        return kodeCustomer;
    }

    public double getTotalPenjualan() {
        return totalPenjualan.get();
    }

    public void setTotalPenjualan(double value) {
        totalPenjualan.set(value);
    }

    public DoubleProperty totalPenjualanProperty() {
        return totalPenjualan;
    }

    public double getPembayaran() {
        return pembayaran.get();
    }

    public void setPembayaran(double value) {
        pembayaran.set(value);
    }

    public DoubleProperty pembayaranProperty() {
        return pembayaran;
    }

    public double getSisaPembayaran() {
        return sisaPembayaran.get();
    }

    public void setSisaPembayaran(double value) {
        sisaPembayaran.set(value);
    }

    public DoubleProperty sisaPembayaranProperty() {
        return sisaPembayaran;
    }
    
    public String getKodeUser() {
        return kodeUser.get();
    }

    public void setKodeUser(String value) {
        kodeUser.set(value);
    }

    public StringProperty kodeUserProperty() {
        return kodeUser;
    }

    public String getTglBatal() {
        return tglBatal.get();
    }

    public void setTglBatal(String value) {
        tglBatal.set(value);
    }

    public StringProperty tglBatalProperty() {
        return tglBatal;
    }

    public String getUserBatal() {
        return userBatal.get();
    }

    public void setUserBatal(String value) {
        userBatal.set(value);
    }

    public StringProperty userBatalProperty() {
        return userBatal;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public StringProperty statusProperty() {
        return status;
    }
    
}
