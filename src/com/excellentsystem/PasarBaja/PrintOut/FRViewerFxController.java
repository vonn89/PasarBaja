/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.PrintOut;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

public class FRViewerFxController {

    @SuppressWarnings("rawtypes")
    private JasperPrint jasperPrint;
    @FXML private ImageView imageView;
    @FXML private ComboBox<Integer> pageList;
    @FXML private Slider zoomLevel;
    @FXML protected Node view;
    private Stage parentStage;
    private double zoomFactor;
    private double imageHeight;
    private double imageWidth;
    private List<Integer> pages;

    public void show() {
        zoomFactor = 1d;
        zoomLevel.setValue(100d);
        imageView.setX(0);
        imageView.setY(0);
        imageHeight = jasperPrint.getPageHeight();
        imageWidth = jasperPrint.getPageWidth();
        zoomLevel.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            zoomFactor = newValue.doubleValue() / 100;
            imageView.setFitHeight(imageHeight * zoomFactor);
            imageView.setFitWidth(imageWidth * zoomFactor);
        });
        if(jasperPrint.getPages().size() > 0){
            viewPage(0);
            pages = new ArrayList<>();
            for(int i = 0; i < jasperPrint.getPages().size(); i++){
                pages.add(i + 1);
            }
        }
        pageList.setItems(FXCollections.observableArrayList(pages));
        pageList.getSelectionModel().select(0);
    }
    @FXML
    public boolean save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Document", Arrays.asList("*.pdf", "*.PDF")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG image", Arrays.asList("*.png", "*.PNG")));
        File file = fileChooser.showSaveDialog(parentStage);
        if (fileChooser.getSelectedExtensionFilter() != null && fileChooser.getSelectedExtensionFilter().getExtensions() != null) {
            List<String> selectedExtension = fileChooser.getSelectedExtensionFilter().getExtensions();
            if (selectedExtension.contains("*.pdf")) {
                try {
                    JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
                } catch (JRException e) {
                    e.printStackTrace();
                }
            } else if (selectedExtension.contains("*.png")) {
                for (int i = 0; i < jasperPrint.getPages().size(); i++) {
                    String fileNumber = "0000" + Integer.toString(i + 1);
                    fileNumber = fileNumber.substring(fileNumber.length() - 4, fileNumber.length());
                    WritableImage image = getImage(i);
                    String[] fileTokens = file.getAbsolutePath().split("\\.");
                    String filename = "";

                    //add number to filename
                    if (fileTokens.length > 0) {
                        for (int i2 = 0; i2 < fileTokens.length - 1; i2++) {
                            filename = filename + fileTokens[i2] + ((i2 < fileTokens.length - 2) ? "." : "");
                        }
                        filename = filename +" - "+ fileNumber + "." + fileTokens[fileTokens.length - 1];
                    } else {
                        filename = file.getAbsolutePath() +" - "+ fileNumber;
                    }
                    File imageFile = new File(filename);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Application error - \n" + e);
                        alert.showAndWait();
                    }

                }
            } 
        }
        return false;
    }
    private WritableImage getImage(int pageNumber) {
        BufferedImage image = null;
        try {
            image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, pageNumber, 2);
        } catch (JRException e) {
        }
        WritableImage fxImage = new WritableImage(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
        return SwingFXUtils.toFXImage(image, fxImage);
    }
    private void viewPage(int pageNumber) {
        imageView.setFitHeight(imageHeight * zoomFactor);
        imageView.setFitWidth(imageWidth * zoomFactor);
        imageView.setImage(getImage(pageNumber));
    }
    @FXML
    private void print() {
        try {
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void pageListSelected(final ActionEvent event) {
        viewPage(pageList.getSelectionModel().getSelectedItem() - 1);
    }
    public void close() {
        parentStage.close();
    }
    public void setJasperPrint(JasperPrint jasper){
        jasperPrint = jasper;
    }
}
