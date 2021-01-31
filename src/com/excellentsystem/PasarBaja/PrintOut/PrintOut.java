/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.PrintOut;

import static com.excellentsystem.PasarBaja.Main.sistem;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author excellent
 */
public class PrintOut {
    public String angka(double satuan){ 
          String[] huruf ={"","Satu","Dua","Tiga","Empat","Lima","Enam","Tujuh","Delapan","Sembilan","Sepuluh","Sebelas"}; 
          String hasil=""; 
          if(satuan<12) 
           hasil=hasil+huruf[(int)satuan]; 
          else if(satuan<20) 
           hasil=hasil+angka(satuan-10)+" Belas"; 
          else if(satuan<100) 
           hasil=hasil+angka(satuan/10)+" Puluh "+angka(satuan%10); 
          else if(satuan<200) 
           hasil=hasil+"Seratus "+angka(satuan-100); 
          else if(satuan<1000) 
           hasil=hasil+angka(satuan/100)+" Ratus "+angka(satuan%100); 
          else if(satuan<2000) 
           hasil=hasil+"Seribu "+angka(satuan-1000); 
          else if(satuan<1000000) 
           hasil=hasil+angka(satuan/1000)+" Ribu "+angka(satuan%1000); 
          else if(satuan<1000000000) 
           hasil=hasil+angka(satuan/1000000)+" Juta "+angka(satuan%1000000); 
          else if(satuan<1000000000000.0) 
           hasil=hasil+angka(satuan/1000000000)+" Milyar "+angka(satuan%1000000000); 
          else if(satuan>=100000000000.0) 
           hasil="Angka terlalu besar, harus kurang dari 1 Triliun!"; 
          return hasil; 
    }
    public void printSuratPemesanan(List<PemesananDetail> detail)throws Exception{
        JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("SuratPemesanan.jrxml"));
        Map parameters = new HashMap<>();
        parameters.put("logo", ImageIO.read(getClass().getResource("logo.jpg")));
        parameters.put("nama", sistem.getNama());
        parameters.put("alamat", sistem.getAlamat());
        parameters.put("noTelp", sistem.getNoTelp());
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(detail);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, beanColDataSource);
        JRViewerFx jrViewerFx = new JRViewerFx(jasperPrint);
    }
    public void printSuratJalan(List<PenjualanDetail> detail)throws Exception{
        JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("SuratJalan.jrxml"));
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(detail);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,null, beanColDataSource);
        JRViewerFx jrViewerFx = new JRViewerFx(jasperPrint);
    }
    public void printInvoice(List<PenjualanDetail> detail)throws Exception{
        JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("Invoice.jrxml"));
        Map parameters = new HashMap<>();
        parameters.put("logo", ImageIO.read(getClass().getResource("logo.jpg")));
        parameters.put("nama", sistem.getNama());
        parameters.put("alamat", sistem.getAlamat());
        parameters.put("noTelp", sistem.getNoTelp());
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(detail);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, beanColDataSource);
        JRViewerFx jrViewerFx = new JRViewerFx(jasperPrint);
    }
}
