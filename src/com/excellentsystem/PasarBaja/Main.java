/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja;

import com.excellentsystem.PasarBaja.DAO.KategoriHutangDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriKeuanganDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriPiutangDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriTransaksiDAO;
import com.excellentsystem.PasarBaja.DAO.OtoritasDAO;
import com.excellentsystem.PasarBaja.DAO.SistemDAO;
import com.excellentsystem.PasarBaja.DAO.UserDAO;
import static com.excellentsystem.PasarBaja.Function.createSecretKey;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.Sistem;
import com.excellentsystem.PasarBaja.Model.User;
import com.excellentsystem.PasarBaja.Services.Service;
import com.excellentsystem.PasarBaja.View.AsetTetapController;
import com.excellentsystem.PasarBaja.View.DataBarangController;
import com.excellentsystem.PasarBaja.View.DataCustomerController;
import com.excellentsystem.PasarBaja.View.DataSupplierController;
import com.excellentsystem.PasarBaja.View.DataUserController;
import com.excellentsystem.PasarBaja.View.Dialog.KategoriHutangController;
import com.excellentsystem.PasarBaja.View.Dialog.KategoriKeuanganController;
import com.excellentsystem.PasarBaja.View.Dialog.KategoriPiutangController;
import com.excellentsystem.PasarBaja.View.Dialog.KategoriTransaksiController;
import com.excellentsystem.PasarBaja.View.Dialog.MessageController;
import com.excellentsystem.PasarBaja.View.Dialog.SplashScreenController;
import com.excellentsystem.PasarBaja.View.Dialog.UbahPasswordController;
import com.excellentsystem.PasarBaja.View.HutangController;
import com.excellentsystem.PasarBaja.View.KeuanganController;
import com.excellentsystem.PasarBaja.View.LoginController;
import com.excellentsystem.PasarBaja.View.MainAppController;
import com.excellentsystem.PasarBaja.View.ModalController;
import com.excellentsystem.PasarBaja.View.PembelianController;
import com.excellentsystem.PasarBaja.View.PemesananController;
import com.excellentsystem.PasarBaja.View.PengirimanBarangController;
import com.excellentsystem.PasarBaja.View.PenjualanController;
import com.excellentsystem.PasarBaja.View.PermintaanBarangController;
import com.excellentsystem.PasarBaja.View.PiutangController;
import com.excellentsystem.PasarBaja.View.Report.LaporanBarangController;
import com.excellentsystem.PasarBaja.View.Report.LaporanBarangDibeliController;
import com.excellentsystem.PasarBaja.View.Report.LaporanBarangTerjualController;
import com.excellentsystem.PasarBaja.View.Report.LaporanHutangController;
import com.excellentsystem.PasarBaja.View.Report.LaporanKeuanganController;
import com.excellentsystem.PasarBaja.View.Report.LaporanNeracaController;
import com.excellentsystem.PasarBaja.View.Report.LaporanPembelianController;
import com.excellentsystem.PasarBaja.View.Report.LaporanPenjualanController;
import com.excellentsystem.PasarBaja.View.Report.LaporanPenyesuaianStokBarangController;
import com.excellentsystem.PasarBaja.View.Report.LaporanPiutangController;
import com.excellentsystem.PasarBaja.View.Report.LaporanUntungRugiController;
import com.excellentsystem.PasarBaja.View.Report.LaporanUntungRugiPeriodeController;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Xtreme
 */
public class Main extends Application {

    public static DecimalFormat df = new DecimalFormat("###,##0.##;(###,##0.##)");
//    public static DecimalFormat df = new DecimalFormat("###,##0.##");
    public static DateFormat tglBarang = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat tglSql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat tgl = new SimpleDateFormat("dd MMM yyyy");
    public static DateFormat tglLengkap = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    public static DateFormat yymm = new SimpleDateFormat("yyMM");
    public static DateFormat yymmdd = new SimpleDateFormat("yyMMdd");
    
    public Stage MainStage;
    public Stage loading;
    public Stage message;
    
    public BorderPane mainLayout;
    public Dimension screenSize;
    private MainAppController mainAppController;
    
    public static Sistem sistem;
    private double x = 0;
    private double y = 0;
    public final String version = "1.0.2";
    public static SecretKeySpec key;
    @Override
    public void start(Stage stage)  {
        MainStage = stage;
        MainStage.setTitle("Pasar Baja");
        MainStage.setMaximized(true);
        MainStage.getIcons().add(new Image(getClass().getResourceAsStream("Resource/icon.png")));
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        ProgressBar progress = new ProgressBar();
        Label updateLabel = new Label();
        Task<String> task = new Task<String>() {
            @Override 
            public String call() throws Exception{
                updateMessage("connecting to server...");
                updateProgress(10, 100);
                try (Connection con = Koneksi.getConnection()) {
                    updateProgress(20, 100);
                    String password = "password";
                    byte[] salt = "12345678".getBytes();
                    key = createSecretKey(password.toCharArray(), salt, 40000, 128);
                    updateProgress(30, 100);
                    updateMessage("initializing system...");
                    sistem = SistemDAO.getSystem(con);
                    sistem.setListKategoriKeuangan(KategoriKeuanganDAO.getAll(con));
                    sistem.setListKategoriTransaksi(KategoriTransaksiDAO.getAllByStatus(con, "true"));
                    sistem.setListKategoriHutang(KategoriHutangDAO.getAll(con));
                    sistem.setListKategoriPiutang(KategoriPiutangDAO.getAll(con));
                    List<User> listUser = UserDAO.getAll(con);
                    List<Otoritas> listOtoritas = OtoritasDAO.getAll(con);
                    for(User u : listUser){
                        List<Otoritas> otoritas = new ArrayList<>();
                        for(Otoritas o : listOtoritas){
                            if(u.getKodeUser().equals(o.getKodeUser()))
                                otoritas.add(o);
                        }
                        u.setOtoritas(otoritas);
                    }
                    sistem.setListUser(listUser);
                    updateProgress(40, 100);
                    updateMessage("checking for updates...");
                    if(!version.equals(sistem.getVersion())){
                        updateMessage("updating software...");
                        updateProgress(50, 100);
                        return Function.downloadUpdateGoogleStorage("Pasar Baja.exe");
                    }
                    Service.setPenyusutanAset(con);
                    
                    updateProgress(70, 100);
                    Thread.sleep(500);
                    updateProgress(80, 100);
                    Thread.sleep(500);
                    updateProgress(90, 100);
                    Thread.sleep(500);
                    updateProgress(100, 100);
                    return "true";
                }
            }
        };
        task.setOnRunning((e) -> {
            showSplashScreen(progress, updateLabel);
        });
        task.setOnSucceeded((e) -> {
            splash.close();
            if(task.getValue().equals("true")){
                showLoginScene();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText(task.getValue());
                alert.showAndWait();
                System.exit(0);
            }
        });
        task.setOnFailed((e) -> {
            task.getException().printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Application error - \n" +task.getException());
            alert.showAndWait();
            System.exit(0);
            splash.close();
        });
        progress.progressProperty().bind(task.progressProperty());
        updateLabel.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
    public void showLoginScene() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("View/Login.fxml"));
            AnchorPane container = (AnchorPane) loader.load();
            
            Scene scene = new Scene(container);
            MainStage.hide();
            MainStage.setScene(scene);
            MainStage.show();
            LoginController controller = loader.getController();
            controller.setMainApp(this);
        }catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(MainStage);
            alert.setTitle("Error");
            alert.setContentText("Application error - \n" +e);
            alert.showAndWait();
        }
    }
    public void showMainScene(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("View/MainApp.fxml"));
            mainLayout = (BorderPane) loader.load();
            Scene scene = new Scene(mainLayout);
            
            final Animation animationshow = new Transition() {
                { setCycleDuration(Duration.millis(1000)); }
                @Override
                protected void interpolate(double frac) {
                    MainStage.setOpacity(1-frac);
                }
            };
            animationshow.onFinishedProperty().set((EventHandler<ActionEvent>) (ActionEvent actionEvent) -> {
                final Animation animation = new Transition() {
                    { setCycleDuration(Duration.millis(1000)); }
                    @Override
                    protected void interpolate(double frac) {
                        MainStage.setOpacity(frac);
                    }
                };
                animation.play();
                MainStage.hide();
                MainStage.setScene(scene);
                mainAppController = loader.getController();
                
                mainAppController.setMainApp(this);
//                showDashboard();
                
                MainStage.show();
            });
            animationshow.play();
            
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(MainStage);
            alert.setTitle("Error");
            alert.setContentText("Application error - \n" +e);
            alert.showAndWait();
        }
    }
    public DataCustomerController showDataCustomer(){
        FXMLLoader loader = changeStage("View/DataCustomer.fxml");
        DataCustomerController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Data Customer");
        return controller;
    }
    public DataSupplierController showDataSupplier(){
        FXMLLoader loader = changeStage("View/DataSupplier.fxml");
        DataSupplierController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Data Supplier");
        return controller;
    }
    public DataBarangController showDataBarang(){
        FXMLLoader loader = changeStage("View/DataBarang.fxml");
        DataBarangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Data Barang");
        return controller;
    }
    public DataUserController showDataUser(){
        FXMLLoader loader = changeStage("View/DataUser.fxml");
        DataUserController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Data User");
        return controller;
    }
    public PemesananController showPemesanan(){
        FXMLLoader loader = changeStage("View/Pemesanan.fxml");
        PemesananController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Pemesanan");
        return controller;
    }
    public PenjualanController showPenjualan(){
        FXMLLoader loader = changeStage("View/Penjualan.fxml");
        PenjualanController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Penjualan");
        return controller;
    }
    public PembelianController showPembelian(){
        FXMLLoader loader = changeStage("View/Pembelian.fxml");
        PembelianController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Pembelian");
        return controller;
    }
    public PermintaanBarangController showPermintaanBarang(){
        FXMLLoader loader = changeStage("View/PermintaanBarang.fxml");
        PermintaanBarangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Permintaan Barang");
        return controller;
    }
    public PengirimanBarangController showPengirimanBarang(){
        FXMLLoader loader = changeStage("View/PengirimanBarang.fxml");
        PengirimanBarangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Pengiriman Barang");
        return controller;
    }
    public KeuanganController showKeuangan(){
        FXMLLoader loader = changeStage("View/Keuangan.fxml");
        KeuanganController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Keuangan");
        return controller;
    }
    public HutangController showHutang(){
        FXMLLoader loader = changeStage("View/Hutang.fxml");
        HutangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Hutang");
        return controller;
    }
    public PiutangController showPiutang(){
        FXMLLoader loader = changeStage("View/Piutang.fxml");
        PiutangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Piutang");
        return controller;
    }
    public ModalController showModal(){
        FXMLLoader loader = changeStage("View/Modal.fxml");
        ModalController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Modal");
        return controller;
    }
    public AsetTetapController showAsetTetap(){
        FXMLLoader loader = changeStage("View/AsetTetap.fxml");
        AsetTetapController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Aset Tetap");
        return controller;
    }
    public LaporanBarangController showLaporanBarang(){
        FXMLLoader loader = changeStage("View/Report/LaporanBarang.fxml");
        LaporanBarangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Barang");
        return controller;
    }
    public LaporanPenyesuaianStokBarangController showLaporanPenyesuaianStokBarang(){
        FXMLLoader loader = changeStage("View/Report/LaporanPenyesuaianStokBarang.fxml");
        LaporanPenyesuaianStokBarangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Penyesuaian Stok Barang");
        return controller;
    }
    public LaporanPenjualanController showLaporanPenjualan(){
        FXMLLoader loader = changeStage("View/Report/LaporanPenjualan.fxml");
        LaporanPenjualanController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Penjualan");
        return controller;
    }
    public LaporanBarangTerjualController showLaporanBarangTerjual(){
        FXMLLoader loader = changeStage("View/Report/LaporanBarangTerjual.fxml");
        LaporanBarangTerjualController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Barang Terjual");
        return controller;
    }
    public LaporanPembelianController showLaporanPembelian(){
        FXMLLoader loader = changeStage("View/Report/LaporanPembelian.fxml");
        LaporanPembelianController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Pembelian");
        return controller;
    }
    public LaporanBarangDibeliController showLaporanBarangDibeli(){
        FXMLLoader loader = changeStage("View/Report/LaporanBarangDibeli.fxml");
        LaporanBarangDibeliController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Barang Dibeli");
        return controller;
    }
    public LaporanKeuanganController showLaporanKeuangan(){
        FXMLLoader loader = changeStage("View/Report/LaporanKeuangan.fxml");
        LaporanKeuanganController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Keuangan");
        return controller;
    }
    public LaporanHutangController showLaporanHutang(){
        FXMLLoader loader = changeStage("View/Report/LaporanHutang.fxml");
        LaporanHutangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Hutang");
        return controller;
    }
    public LaporanPiutangController showLaporanPiutang(){
        FXMLLoader loader = changeStage("View/Report/LaporanPiutang.fxml");
        LaporanPiutangController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Piutang");
        return controller;
    }
    public LaporanUntungRugiController showLaporanUntungRugi(){
        FXMLLoader loader = changeStage("View/Report/LaporanUntungRugi.fxml");
        LaporanUntungRugiController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Untung Rugi");
        return controller;
    }
    public LaporanUntungRugiPeriodeController showLaporanUntungRugiPeriode(){
        FXMLLoader loader = changeStage("View/Report/LaporanUntungRugiPeriode.fxml");
        LaporanUntungRugiPeriodeController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Untung Rugi Periode");
        return controller;
    }
    public LaporanNeracaController showLaporanNeraca(){
        FXMLLoader loader = changeStage("View/Report/LaporanNeraca.fxml");
        LaporanNeracaController controller = loader.getController();
        controller.setMainApp(this);
        setTitle("Laporan Neraca");
        return controller;
    }
    public KategoriHutangController showKategoriHutang(){
        Stage stage = new Stage();
        FXMLLoader loader = showDialog(MainStage ,stage, "View/Dialog/KategoriHutang.fxml");
        KategoriHutangController controller = loader.getController();
        controller.setMainApp(this, MainStage, stage);
        return controller;
    }
    public KategoriPiutangController showKategoriPiutang(){
        Stage stage = new Stage();
        FXMLLoader loader = showDialog(MainStage ,stage, "View/Dialog/KategoriPiutang.fxml");
        KategoriPiutangController controller = loader.getController();
        controller.setMainApp(this, MainStage, stage);
        return controller;
    }
    public KategoriTransaksiController showKategoriTransaksi(){
        Stage stage = new Stage();
        FXMLLoader loader = showDialog(MainStage ,stage, "View/Dialog/KategoriTransaksi.fxml");
        KategoriTransaksiController controller = loader.getController();
        controller.setMainApp(this, MainStage, stage);
        return controller;
    }
    public KategoriKeuanganController showKategoriKeuangan(){
        Stage stage = new Stage();
        FXMLLoader loader = showDialog(MainStage ,stage, "View/Dialog/KategoriKeuangan.fxml");
        KategoriKeuanganController controller = loader.getController();
        controller.setMainApp(this, MainStage, stage);
        return controller;
    }
    public UbahPasswordController showUbahPassword(){
        Stage stage = new Stage();
        FXMLLoader loader = showDialog(MainStage, stage, "View/Dialog/UbahPassword.fxml");
        UbahPasswordController controller = loader.getController();
        controller.setMainApp(this, MainStage ,stage);
        return controller;
    }
    
    public void setTitle(String title){
        mainAppController.setTitle(title);
        if (mainAppController.vbox.isVisible()) 
            mainAppController.showHideMenu();
    }
    public void showLoadingScreen(){
        try{
            if(loading!=null)
                loading.close();
            loading = new Stage();
            loading.initModality(Modality.WINDOW_MODAL);
            loading.initOwner(MainStage);
            loading.initStyle(StageStyle.TRANSPARENT);
            loading.setOnCloseRequest((event) -> {
                event.consume();
            });
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("View/Dialog/LoadingScreen.fxml"));
            AnchorPane container = (AnchorPane) loader.load();

            Scene scene = new Scene(container);
            scene.setFill(Color.TRANSPARENT);
            
            loading.setOpacity(0.7);
            loading.hide();
            loading.setScene(scene);
            loading.show();
            
            loading.setHeight(MainStage.getHeight());
            loading.setWidth(MainStage.getWidth());
            loading.setX((MainStage.getWidth() - loading.getWidth()) / 2);
            loading.setY((MainStage.getHeight() - loading.getHeight()) / 2);
        }catch(Exception e){
            showMessage(Modality.NONE, "Error", e.toString());
            e.printStackTrace();
        }
    }
    public void closeLoading(){
        loading.close();
    }
    public FXMLLoader changeStage(String URL){
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(URL));
            AnchorPane container = (AnchorPane) loader.load();
            BorderPane border = (BorderPane) mainLayout.getCenter();
            border.setCenter(container);
            return loader;
        }catch(Exception e){
            e.printStackTrace();
            showMessage(Modality.NONE, "Error", e.toString());
            return null;
        }
    }
    private Stage splash;
    public void showSplashScreen(ProgressBar progressBar, Label updateLabel){
        try{
            if(splash!=null)
                splash.close();
            splash = new Stage();
            splash.getIcons().add(new Image(getClass().getResourceAsStream("Resource/icon.png")));
            splash.initModality(Modality.WINDOW_MODAL);
            splash.initOwner(MainStage);
            splash.initStyle(StageStyle.TRANSPARENT);
            splash.setOnCloseRequest((event) -> {
                event.consume();
            });
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("View/Dialog/SplashScreen.fxml"));
            AnchorPane container = (AnchorPane) loader.load();
            SplashScreenController controller = loader.getController();
            progressBar.setPrefWidth(475);
            updateLabel.setStyle("-fx-text-fill: white");
            controller.setSplashScreen(progressBar, updateLabel);

            Scene scene = new Scene(container);
            scene.setFill(Color.TRANSPARENT);
            
            splash.hide();
            splash.setScene(scene);
            splash.show();
            
            splash.setHeight(screenSize.getHeight());
            splash.setWidth(screenSize.getWidth());
            splash.setX((screenSize.getWidth() - splash.getWidth()) / 2);
            splash.setY((screenSize.getHeight() - splash.getHeight()) / 2);
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(MainStage);
            alert.setTitle("Error");
            alert.setContentText("Application error - \n" +e);
            alert.showAndWait();
        }
    }
    public void closeDialog(Stage owner,Stage dialog){
        dialog.close();
        owner.getScene().getRoot().setEffect(new ColorAdjust(0,0,0,0));
    }
    public FXMLLoader showDialog(Stage owner, Stage dialog, String URL){
        try{
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(owner);
            dialog.initStyle(StageStyle.TRANSPARENT);
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(URL));
            AnchorPane container = (AnchorPane) loader.load();

            Scene scene = new Scene(container);
            scene.setFill(Color.TRANSPARENT);
                        
            scene.setOnMousePressed((MouseEvent mouseEvent) -> {
                x = dialog.getX() - mouseEvent.getScreenX();
                y = dialog.getY() - mouseEvent.getScreenY();
            });
            scene.setOnMouseDragged((MouseEvent mouseEvent) -> {
                dialog.setX(mouseEvent.getScreenX() + x);
                dialog.setY(mouseEvent.getScreenY() + y);
            });
            owner.getScene().getRoot().setEffect(new ColorAdjust(0, 0, -0.5, -0.5));
            dialog.hide();
            dialog.setScene(scene);
            dialog.show();
            //set dialog on center parent
            dialog.setX((screenSize.getWidth() - dialog.getWidth()) / 2);
            dialog.setY((screenSize.getHeight() - dialog.getHeight()) / 2);
            return loader;
        }catch(IOException e){
            showMessage(Modality.NONE, "Error", e.toString());
            return null;
        }
    }
    public MessageController showMessage(Modality modal,String type,String content){
        try{
            if(message!=null)
                message.close();
            message = new Stage();
            message.initModality(modal);
            message.initOwner(MainStage);
            message.initStyle(StageStyle.TRANSPARENT);
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("View/Dialog/Message.fxml"));
            AnchorPane container = (AnchorPane) loader.load();

            Scene scene = new Scene(container);
            scene.setFill(Color.TRANSPARENT);
            message.setX(MainStage.getWidth()-450);
            message.setY(MainStage.getHeight()-150);
            final Animation popup = new Transition() {
                { setCycleDuration(Duration.millis(250)); }
                @Override
                protected void interpolate(double frac) {
                    final double curPos = (MainStage.getHeight()-150) * (1-frac);
                    container.setTranslateY(curPos);
                }
            };
            popup.play();
            message.hide();
            message.setScene(scene);
            message.show();
            MessageController controller = loader.getController();
            controller.setMainApp(this,type,content);
            return controller;
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(MainStage);
            alert.setTitle("Error");
            alert.setContentText("Application error - \n" +e);
            alert.showAndWait();
            return null;
        }
    }
    public void closeMessage(){
        message.close();
    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
