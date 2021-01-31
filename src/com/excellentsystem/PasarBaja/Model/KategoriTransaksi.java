/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author excellent
 */
public class KategoriTransaksi {

    private final StringProperty kodeKategori = new SimpleStringProperty();
    private final StringProperty jenisTransaksi = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getJenisTransaksi() {
        return jenisTransaksi.get();
    }

    public void setJenisTransaksi(String value) {
        jenisTransaksi.set(value);
    }

    public StringProperty jenisTransaksiProperty() {
        return jenisTransaksi;
    }

    public String getKodeKategori() {
        return kodeKategori.get();
    }

    public void setKodeKategori(String value) {
        kodeKategori.set(value);
    }

    public StringProperty kodeKategoriProperty() {
        return kodeKategori;
    }
    
}
