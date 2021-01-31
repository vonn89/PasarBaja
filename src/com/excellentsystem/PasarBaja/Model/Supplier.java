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
 * @author Xtreme
 */
public class Supplier {
    private final StringProperty kodeSupplier = new SimpleStringProperty();
    private final StringProperty nama = new SimpleStringProperty();
    private final StringProperty alamat = new SimpleStringProperty();
    private final StringProperty kota = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty kontakPerson = new SimpleStringProperty();
    private final StringProperty noTelp = new SimpleStringProperty();
    private final StringProperty noHandphone = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();


    public String getKodeSupplier() {
        return kodeSupplier.get();
    }

    public void setKodeSupplier(String value) {
        kodeSupplier.set(value);
    }

    public StringProperty kodeSupplierProperty() {
        return kodeSupplier;
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

    public String getAlamat() {
        return alamat.get();
    }

    public void setAlamat(String value) {
        alamat.set(value);
    }

    public StringProperty alamatProperty() {
        return alamat;
    }

    public String getKota() {
        return kota.get();
    }

    public void setKota(String value) {
        kota.set(value);
    }

    public StringProperty kotaProperty() {
        return kota;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getKontakPerson() {
        return kontakPerson.get();
    }

    public void setKontakPerson(String value) {
        kontakPerson.set(value);
    }

    public StringProperty kontakPersonProperty() {
        return kontakPerson;
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

    public String getNoHandphone() {
        return noHandphone.get();
    }

    public void setNoHandphone(String value) {
        noHandphone.set(value);
    }

    public StringProperty noHandphoneProperty() {
        return noHandphone;
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
