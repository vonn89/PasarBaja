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
 * @author ASUS
 */
public class BebanPenjualanHead {

    private final StringProperty noBebanPenjualan = new SimpleStringProperty();
    private final StringProperty tglBebanPenjualan = new SimpleStringProperty();
    private final StringProperty keterangan = new SimpleStringProperty();
    private final DoubleProperty totalBebanPenjualan = new SimpleDoubleProperty();
    private final StringProperty tipeKeuangan = new SimpleStringProperty();
    private final StringProperty kodeUser = new SimpleStringProperty();
    private final StringProperty tglBatal = new SimpleStringProperty();
    private final StringProperty userBatal = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private List<BebanPenjualanDetail> listBebanPenjualanDetail;

    public String getTipeKeuangan() {
        return tipeKeuangan.get();
    }

    public void setTipeKeuangan(String value) {
        tipeKeuangan.set(value);
    }

    public StringProperty tipeKeuanganProperty() {
        return tipeKeuangan;
    }

    public List<BebanPenjualanDetail> getListBebanPenjualanDetail() {
        return listBebanPenjualanDetail;
    }

    public void setListBebanPenjualanDetail(List<BebanPenjualanDetail> listBebanPenjualanDetail) {
        this.listBebanPenjualanDetail = listBebanPenjualanDetail;
    }
    
    public double getTotalBebanPenjualan() {
        return totalBebanPenjualan.get();
    }

    public void setTotalBebanPenjualan(double value) {
        totalBebanPenjualan.set(value);
    }

    public DoubleProperty totalBebanPenjualanProperty() {
        return totalBebanPenjualan;
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

    public String getTglBatal() {
        return tglBatal.get();
    }

    public void setTglBatal(String value) {
        tglBatal.set(value);
    }

    public StringProperty tglBatalProperty() {
        return tglBatal;
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

    public String getKodeUser() {
        return kodeUser.get();
    }

    public void setKodeUser(String value) {
        kodeUser.set(value);
    }

    public StringProperty kodeUserProperty() {
        return kodeUser;
    }

    public String getTglBebanPenjualan() {
        return tglBebanPenjualan.get();
    }

    public void setTglBebanPenjualan(String value) {
        tglBebanPenjualan.set(value);
    }

    public StringProperty tglBebanPenjualanProperty() {
        return tglBebanPenjualan;
    }

    public String getNoBebanPenjualan() {
        return noBebanPenjualan.get();
    }

    public void setNoBebanPenjualan(String value) {
        noBebanPenjualan.set(value);
    }

    public StringProperty noBebanPenjualanProperty() {
        return noBebanPenjualan;
    }

    public String getKeterangan() {
        return keterangan.get();
    }

    public void setKeterangan(String value) {
        keterangan.set(value);
    }

    public StringProperty keteranganProperty() {
        return keterangan;
    }

    
}
