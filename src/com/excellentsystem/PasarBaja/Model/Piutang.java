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
public class Piutang {
    private final StringProperty noPiutang = new SimpleStringProperty();
    private final StringProperty tglPiutang = new SimpleStringProperty();
    private final StringProperty kategori = new SimpleStringProperty();
    private final StringProperty keterangan = new SimpleStringProperty();
    private final StringProperty tipeKeuangan = new SimpleStringProperty();
    private final DoubleProperty jumlahPiutang = new SimpleDoubleProperty();
    private final DoubleProperty pembayaran = new SimpleDoubleProperty();
    private final DoubleProperty sisaPiutang = new SimpleDoubleProperty();
    private final StringProperty kodeUser = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private PenjualanHead penjualanHead;
    private List<TerimaPembayaran> listTerimaPembayaran;

    public List<TerimaPembayaran> getListTerimaPembayaran() {
        return listTerimaPembayaran;
    }

    public void setListTerimaPembayaran(List<TerimaPembayaran> listTerimaPembayaran) {
        this.listTerimaPembayaran = listTerimaPembayaran;
    }
    
    public PenjualanHead getPenjualanHead() {
        return penjualanHead;
    }

    public void setPenjualanHead(PenjualanHead penjualanHead) {
        this.penjualanHead = penjualanHead;
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

    public String getTipeKeuangan() {
        return tipeKeuangan.get();
    }

    public void setTipeKeuangan(String value) {
        tipeKeuangan.set(value);
    }

    public StringProperty tipeKeuanganProperty() {
        return tipeKeuangan;
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

    public String getKategori() {
        return kategori.get();
    }

    public void setKategori(String value) {
        kategori.set(value);
    }

    public StringProperty kategoriProperty() {
        return kategori;
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
    
    public double getPembayaran() {
        return pembayaran.get();
    }

    public void setPembayaran(double value) {
        pembayaran.set(value);
    }

    public DoubleProperty pembayaranProperty() {
        return pembayaran;
    }

    public double getSisaPiutang() {
        return sisaPiutang.get();
    }

    public void setSisaPiutang(double value) {
        sisaPiutang.set(value);
    }

    public DoubleProperty sisaPiutangProperty() {
        return sisaPiutang;
    }
    

    public double getJumlahPiutang() {
        return jumlahPiutang.get();
    }

    public void setJumlahPiutang(double value) {
        jumlahPiutang.set(value);
    }

    public DoubleProperty jumlahPiutangProperty() {
        return jumlahPiutang;
    }


    public String getTglPiutang() {
        return tglPiutang.get();
    }

    public void setTglPiutang(String value) {
        tglPiutang.set(value);
    }

    public StringProperty tglPiutangProperty() {
        return tglPiutang;
    }
    

    public String getNoPiutang() {
        return noPiutang.get();
    }

    public void setNoPiutang(String value) {
        noPiutang.set(value);
    }

    public StringProperty noPiutangProperty() {
        return noPiutang;
    }
    
}
