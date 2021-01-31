/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.Model;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xtreme
 */
public class Sistem {

    private final StringProperty nama = new SimpleStringProperty();
    private final StringProperty alamat = new SimpleStringProperty();
    private final StringProperty noTelp = new SimpleStringProperty();
    private final StringProperty version = new SimpleStringProperty();
    private User user;
    private List<KategoriKeuangan> listKategoriKeuangan;
    private List<KategoriTransaksi> listKategoriTransaksi;
    private List<User> listUser;
    private List<KategoriHutang> listKategoriHutang;
    private List<KategoriPiutang> listKategoriPiutang;

    public List<KategoriPiutang> getListKategoriPiutang() {
        return listKategoriPiutang;
    }

    public void setListKategoriPiutang(List<KategoriPiutang> listKategoriPiutang) {
        this.listKategoriPiutang = listKategoriPiutang;
    }
    
    public List<KategoriHutang> getListKategoriHutang() {
        return listKategoriHutang;
    }

    public void setListKategoriHutang(List<KategoriHutang> listKategoriHutang) {
        this.listKategoriHutang = listKategoriHutang;
    }
    
    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }

    public List<KategoriTransaksi> getListKategoriTransaksi() {
        return listKategoriTransaksi;
    }

    public void setListKategoriTransaksi(List<KategoriTransaksi> listKategoriTransaksi) {
        this.listKategoriTransaksi = listKategoriTransaksi;
    }

    public List<KategoriKeuangan> getListKategoriKeuangan() {
        return listKategoriKeuangan;
    }

    public void setListKategoriKeuangan(List<KategoriKeuangan> listKategoriKeuangan) {
        this.listKategoriKeuangan = listKategoriKeuangan;
    }

    public String getNoTelp() {
        return noTelp.get();
    }

    public void setNoTelp(String value) {
        noTelp.set(value);
    }

    public StringProperty noTelpProperty() {
        return noTelp;
    }

    public String getAlamat() {
        return alamat.get();
    }

    public void setAlamat(String value) {
        alamat.set(value);
    }

    public StringProperty alamatProperty() {
        return alamat;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVersion() {
        return version.get();
    }

    public void setVersion(String value) {
        version.set(value);
    }

    public StringProperty versionProperty() {
        return version;
    }

}
