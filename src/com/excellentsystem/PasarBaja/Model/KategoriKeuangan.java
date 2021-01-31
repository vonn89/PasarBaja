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
public class KategoriKeuangan {

    private final StringProperty kodeKeuangan = new SimpleStringProperty();

    public String getKodeKeuangan() {
        return kodeKeuangan.get();
    }

    public void setKodeKeuangan(String value) {
        kodeKeuangan.set(value);
    }

    public StringProperty kodeKeuanganProperty() {
        return kodeKeuangan;
    }
    
}
