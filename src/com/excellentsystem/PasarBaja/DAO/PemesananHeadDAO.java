/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.PemesananHead;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class PemesananHeadDAO {
    public static List<PemesananHead> getAllByDateAndStatus(Connection con, 
            String tglMulai, String tglAkhir, String status)throws Exception{
        String sql = "select * from tt_pemesanan_head where left(tgl_pemesanan,10) between ? and ? ";
        if(!status.equals("%")) 
            sql = sql + " and status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        List<PemesananHead> allPemesanan = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            PemesananHead p = new PemesananHead();
            p.setNoPemesanan(rs.getString(1));
            p.setTglPemesanan(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setKodeCustomer(rs.getString(3));
            p.setTotalPemesanan(rs.getDouble(4));
            p.setDownPayment(rs.getDouble(5));
            p.setSisaDownPayment(rs.getDouble(6));
            p.setCatatan(rs.getString(7));
            p.setKodeUser(rs.getString(8));
            p.setTglBatal(rs.getDate(9).toString()+" "+rs.getTime(9).toString());
            p.setUserBatal(rs.getString(10));
            p.setStatus(rs.getString(11));
            allPemesanan.add(p);
        }
        return allPemesanan;
    }
    public static PemesananHead get(Connection con, String noPemesanan)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_pemesanan_head where no_pemesanan = ? ");
        ps.setString(1, noPemesanan);
        PemesananHead p = null;
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            p = new PemesananHead();
            p.setNoPemesanan(rs.getString(1));
            p.setTglPemesanan(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setKodeCustomer(rs.getString(3));
            p.setTotalPemesanan(rs.getDouble(4));
            p.setDownPayment(rs.getDouble(5));
            p.setSisaDownPayment(rs.getDouble(6));
            p.setCatatan(rs.getString(7));
            p.setKodeUser(rs.getString(8));
            p.setTglBatal(rs.getDate(9).toString()+" "+rs.getTime(9).toString());
            p.setUserBatal(rs.getString(10));
            p.setStatus(rs.getString(11));
        }
        return p;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_pemesanan,3)) from tt_pemesanan_head "
                + " where mid(no_pemesanan,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "PI-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "PI-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, PemesananHead p)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_pemesanan_head values(?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, p.getNoPemesanan());
        ps.setString(2, p.getTglPemesanan());
        ps.setString(3, p.getKodeCustomer());
        ps.setDouble(4, p.getTotalPemesanan());
        ps.setDouble(5, p.getDownPayment());
        ps.setDouble(6, p.getSisaDownPayment());
        ps.setString(7, p.getCatatan()); 
        ps.setString(8, p.getKodeUser());
        ps.setString(9, p.getTglBatal());
        ps.setString(10, p.getUserBatal());
        ps.setString(11, p.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, PemesananHead p)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_pemesanan_head set "
                + " tgl_pemesanan=?, kode_customer=?, total_pemesanan=?, down_payment=?, sisa_down_payment=?, "
                + " catatan=?, kode_user=?, tgl_batal=?, user_batal=?, status=? "
                + " where no_pemesanan=?");
        ps.setString(1, p.getTglPemesanan());
        ps.setString(2, p.getKodeCustomer());
        ps.setDouble(3, p.getTotalPemesanan());
        ps.setDouble(4, p.getDownPayment());
        ps.setDouble(5, p.getSisaDownPayment());
        ps.setString(6, p.getCatatan());
        ps.setString(7, p.getKodeUser());
        ps.setString(8, p.getTglBatal());
        ps.setString(9, p.getUserBatal());
        ps.setString(10, p.getStatus());
        ps.setString(11, p.getNoPemesanan());
        ps.executeUpdate();
    }
}
