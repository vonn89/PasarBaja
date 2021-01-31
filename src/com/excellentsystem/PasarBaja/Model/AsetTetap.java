/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.Model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xtreme
 */
public class AsetTetap {
    private final StringProperty noAset = new SimpleStringProperty();
    private final StringProperty nama = new SimpleStringProperty();
    private final StringProperty kategori = new SimpleStringProperty();
    private final StringProperty keterangan = new SimpleStringProperty();
    private final IntegerProperty masaPakai = new SimpleIntegerProperty();
    private final DoubleProperty nilaiAwal = new SimpleDoubleProperty();
    private final DoubleProperty penyusutan = new SimpleDoubleProperty();
    private final DoubleProperty nilaiAkhir = new SimpleDoubleProperty();
    private final DoubleProperty hargaJual = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty tglJual = new SimpleStringProperty();
    private final StringProperty userJual = new SimpleStringProperty();
    private final StringProperty tglBeli = new SimpleStringProperty();
    private final StringProperty userBeli = new SimpleStringProperty();

    public double getHargaJual() {
        return hargaJual.get();
    }

    public void setHargaJual(double value) {
        hargaJual.set(value);
    }

    public DoubleProperty hargaJualProperty() {
        return hargaJual;
    }
    

    public String getUserJual() {
        return userJual.get();
    }

    public void setUserJual(String value) {
        userJual.set(value);
    }

    public StringProperty userJualProperty() {
        return userJual;
    }
    

    public String getTglJual() {
        return tglJual.get();
    }

    public void setTglJual(String value) {
        tglJual.set(value);
    }

    public StringProperty tglJualProperty() {
        return tglJual;
    }
    

    public String getTglBeli() {
        return tglBeli.get();
    }

    public void setTglBeli(String value) {
        tglBeli.set(value);
    }

    public StringProperty tglBeliProperty() {
        return tglBeli;
    }

    public String getUserBeli() {
        return userBeli.get();
    }

    public void setUserBeli(String value) {
        userBeli.set(value);
    }

    public StringProperty userBeliProperty() {
        return userBeli;
    }
    

    public String getNoAset() {
        return noAset.get();
    }

    public void setNoAset(String value) {
        noAset.set(value);
    }

    public StringProperty noAsetProperty() {
        return noAset;
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

    public String getKeterangan() {
        return keterangan.get();
    }

    public void setKeterangan(String value) {
        keterangan.set(value);
    }

    public StringProperty keteranganProperty() {
        return keterangan;
    }

    public int getMasaPakai() {
        return masaPakai.get();
    }

    public void setMasaPakai(int value) {
        masaPakai.set(value);
    }

    public IntegerProperty masaPakaiProperty() {
        return masaPakai;
    }

    public double getNilaiAwal() {
        return nilaiAwal.get();
    }

    public void setNilaiAwal(double value) {
        nilaiAwal.set(value);
    }

    public DoubleProperty nilaiAwalProperty() {
        return nilaiAwal;
    }

    public double getPenyusutan() {
        return penyusutan.get();
    }

    public void setPenyusutan(double value) {
        penyusutan.set(value);
    }

    public DoubleProperty penyusutanProperty() {
        return penyusutan;
    }

    public double getNilaiAkhir() {
        return nilaiAkhir.get();
    }

    public void setNilaiAkhir(double value) {
        nilaiAkhir.set(value);
    }

    public DoubleProperty nilaiAkhirProperty() {
        return nilaiAkhir;
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
    

    public String getNama() {
        return nama.get();
    }

    public void setNama(String value) {
        nama.set(value);
    }

    public StringProperty namaProperty() {
        return nama;
    }
    
}
