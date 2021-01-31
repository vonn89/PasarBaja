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
 * @author excellent
 */
public class LogBarang {

    private final StringProperty tanggal = new SimpleStringProperty();
    private final StringProperty kodeBarang = new SimpleStringProperty();
    private final StringProperty kategori = new SimpleStringProperty();
    private final StringProperty keterangan = new SimpleStringProperty();
    private final DoubleProperty stokAwal = new SimpleDoubleProperty();
    private final DoubleProperty nilaiAwal = new SimpleDoubleProperty();
    private final DoubleProperty stokMasuk = new SimpleDoubleProperty();
    private final DoubleProperty nilaiMasuk = new SimpleDoubleProperty();
    private final DoubleProperty stokKeluar = new SimpleDoubleProperty();
    private final DoubleProperty nilaiKeluar = new SimpleDoubleProperty();
    private final DoubleProperty stokAkhir = new SimpleDoubleProperty();
    private final DoubleProperty nilaiAkhir = new SimpleDoubleProperty();


    public String getKodeBarang() {
        return kodeBarang.get();
    }

    public void setKodeBarang(String value) {
        kodeBarang.set(value);
    }

    public StringProperty kodeBarangProperty() {
        return kodeBarang;
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

    public double getStokAkhir() {
        return stokAkhir.get();
    }

    public void setStokAkhir(double value) {
        stokAkhir.set(value);
    }

    public DoubleProperty stokAkhirProperty() {
        return stokAkhir;
    }

    public double getNilaiKeluar() {
        return nilaiKeluar.get();
    }

    public void setNilaiKeluar(double value) {
        nilaiKeluar.set(value);
    }

    public DoubleProperty nilaiKeluarProperty() {
        return nilaiKeluar;
    }

    public double getStokKeluar() {
        return stokKeluar.get();
    }

    public void setStokKeluar(double value) {
        stokKeluar.set(value);
    }

    public DoubleProperty stokKeluarProperty() {
        return stokKeluar;
    }

    public double getNilaiMasuk() {
        return nilaiMasuk.get();
    }

    public void setNilaiMasuk(double value) {
        nilaiMasuk.set(value);
    }

    public DoubleProperty nilaiMasukProperty() {
        return nilaiMasuk;
    }

    public double getStokMasuk() {
        return stokMasuk.get();
    }

    public void setStokMasuk(double value) {
        stokMasuk.set(value);
    }

    public DoubleProperty stokMasukProperty() {
        return stokMasuk;
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

    public double getStokAwal() {
        return stokAwal.get();
    }

    public void setStokAwal(double value) {
        stokAwal.set(value);
    }

    public DoubleProperty stokAwalProperty() {
        return stokAwal;
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


    public String getTanggal() {
        return tanggal.get();
    }

    public void setTanggal(String value) {
        tanggal.set(value);
    }

    public StringProperty tanggalProperty() {
        return tanggal;
    }
    
}
