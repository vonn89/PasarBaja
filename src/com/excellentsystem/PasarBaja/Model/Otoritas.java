/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Xtreme
 */
public class Otoritas {
    private final StringProperty kodeUser = new SimpleStringProperty();
    private final StringProperty jenis = new SimpleStringProperty();
    private final BooleanProperty status = new SimpleBooleanProperty();

    public String getKodeUser() {
        return kodeUser.get();
    }

    public void setKodeUser(String value) {
        kodeUser.set(value);
    }

    public StringProperty kodeUserProperty() {
        return kodeUser;
    }

    public String getJenis() {
        return jenis.get();
    }

    public void setJenis(String value) {
        jenis.set(value);
    }

    public StringProperty jenisProperty() {
        return jenis;
    }

    public boolean isStatus() {
        return status.get();
    }

    public void setStatus(boolean value) {
        status.set(value);
    }

    public BooleanProperty statusProperty() {
        return status;
    }
    
    
    
}
