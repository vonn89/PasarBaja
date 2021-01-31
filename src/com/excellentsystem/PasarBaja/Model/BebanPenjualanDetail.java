/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.Model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author ASUS
 */
public class BebanPenjualanDetail {

    private final StringProperty noBebanPenjualan = new SimpleStringProperty();
    private final StringProperty noPenjualan = new SimpleStringProperty();
    private final DoubleProperty jumlahRp = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();
    private PenjualanHead penjualanHead;
    private BebanPenjualanHead bebanPenjualanHead;

    public BebanPenjualanHead getBebanPenjualanHead() {
        return bebanPenjualanHead;
    }

    public void setBebanPenjualanHead(BebanPenjualanHead bebanPenjualanHead) {
        this.bebanPenjualanHead = bebanPenjualanHead;
    }
    
    public PenjualanHead getPenjualanHead() {
        return penjualanHead;
    }

    public void setPenjualanHead(PenjualanHead penjualanHead) {
        this.penjualanHead = penjualanHead;
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

    public double getJumlahRp() {
        return jumlahRp.get();
    }

    public void setJumlahRp(double value) {
        jumlahRp.set(value);
    }

    public DoubleProperty jumlahRpProperty() {
        return jumlahRp;
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

    public String getNoBebanPenjualan() {
        return noBebanPenjualan.get();
    }

    public void setNoBebanPenjualan(String value) {
        noBebanPenjualan.set(value);
    }

    public StringProperty noBebanPenjualanProperty() {
        return noBebanPenjualan;
    }
    
}
