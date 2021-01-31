/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.View.Dialog;

import com.excellentsystem.PasarBaja.DAO.UserDAO;
import static com.excellentsystem.PasarBaja.Function.decrypt;
import com.excellentsystem.PasarBaja.Koneksi;
import com.excellentsystem.PasarBaja.Main;
import static com.excellentsystem.PasarBaja.Main.key;
import static com.excellentsystem.PasarBaja.Main.sistem;
import java.sql.Connection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Xtreme
 */
public class UbahPasswordController {

    @FXML
    public TextField username;
    @FXML
    public PasswordField passwordLama;
    @FXML
    public PasswordField passwordBaru;
    @FXML
    public PasswordField ulangiPasswordBaru;
    @FXML
    public Label warning;
    private Main mainApp;
    private Stage stage;
    private Stage owner;

    public void setMainApp(Main mainApp, Stage owner, Stage stage) {
        this.mainApp = mainApp;
        this.owner = owner;
        this.stage = stage;
        stage.setOnCloseRequest((e) -> {
            mainApp.closeDialog(owner, stage);
        });
        username.setText(sistem.getUser().getKodeUser());
        warning.setText("");
    }

    public void save() {
        try {
            if (passwordLama.getText().equals("")) {
                warning.setText("password lama masih kosong");
            } else if (passwordBaru.getText().equals("")) {
                warning.setText("password baru masih kosong");
            } else if (ulangiPasswordBaru.getText().equals("")) {
                warning.setText("ulangi password baru masih kosong");
            } else if (!passwordLama.getText().equals(decrypt(sistem.getUser().getPassword(), key))) {
                warning.setText("password lama salah");
            } else if (!passwordBaru.getText().equals(ulangiPasswordBaru.getText())) {
                warning.setText("password baru tidak sama");
            } else {
                try (Connection con = Koneksi.getConnection()) {
                    sistem.getUser().setPassword(passwordBaru.getText());
                    UserDAO.update(con, sistem.getUser());
                    mainApp.showMessage(Modality.NONE, "Success", "Password baru berhasil di simpan");
                    close();
                } catch (Exception e) {
                    mainApp.showMessage(Modality.NONE, "Error", e.toString());
                }
            }
        } catch (Exception e) {
            mainApp.showMessage(Modality.NONE, "Error", e.toString());
        }
    }

    public void close() {
        mainApp.closeDialog(owner, stage);
    }

}
