/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.Pembayaran;
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
public class PembayaranDAO {
    public static List<Pembayaran> getAllByTglBayar(Connection con, String tglAwal, String tglAkhir, String status)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_pembayaran where left(tgl_pembayaran,10) between ? and ?  and status = ?");
        ps.setString(1, tglAwal);
        ps.setString(2, tglAkhir);
        ps.setString(3, status);
        ResultSet rs = ps.executeQuery();
        List<Pembayaran> allPembayaran = new ArrayList<>();
        while(rs.next()){
            Pembayaran p = new Pembayaran();
            p.setNoPembayaran(rs.getString(1));
            p.setTglPembayaran(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setNoHutang(rs.getString(3));
            p.setJumlahPembayaran(rs.getDouble(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setCatatan(rs.getString(6));
            p.setKodeUser(rs.getString(7));
            p.setTglBatal(rs.getDate(8).toString()+" "+rs.getTime(8).toString());
            p.setUserBatal(rs.getString(9));
            p.setStatus(rs.getString(10));
            allPembayaran.add(p);
        }
        return allPembayaran;
    }
    public static List<Pembayaran> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tt_pembayaran ";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Pembayaran> allPembayaran = new ArrayList<>();
        while(rs.next()){
            Pembayaran p = new Pembayaran();
            p.setNoPembayaran(rs.getString(1));
            p.setTglPembayaran(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setNoHutang(rs.getString(3));
            p.setJumlahPembayaran(rs.getDouble(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setCatatan(rs.getString(6));
            p.setKodeUser(rs.getString(7));
            p.setTglBatal(rs.getDate(8).toString()+" "+rs.getTime(8).toString());
            p.setUserBatal(rs.getString(9));
            p.setStatus(rs.getString(10));
            allPembayaran.add(p);
        }
        return allPembayaran;
    }
    public static List<Pembayaran> getAllByNoHutang(Connection con, String noHutang, String status)throws Exception{
        String sql = "select * from tt_pembayaran where no_pembayaran != '' ";
        if(!noHutang.equals("%"))
            sql = sql + " and no_hutang = '"+noHutang+"' ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Pembayaran> allPembayaran = new ArrayList<>();
        while(rs.next()){
            Pembayaran p = new Pembayaran();
            p.setNoPembayaran(rs.getString(1));
            p.setTglPembayaran(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setNoHutang(rs.getString(3));
            p.setJumlahPembayaran(rs.getDouble(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setCatatan(rs.getString(6));
            p.setKodeUser(rs.getString(7));
            p.setTglBatal(rs.getDate(8).toString()+" "+rs.getTime(8).toString());
            p.setUserBatal(rs.getString(9));
            p.setStatus(rs.getString(10));
            allPembayaran.add(p);
        }
        return allPembayaran;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_pembayaran,3)) from tt_pembayaran where mid(no_pembayaran,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "PB-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "PB-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, Pembayaran p)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_pembayaran values(?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, p.getNoPembayaran());
        ps.setString(2, p.getTglPembayaran());
        ps.setString(3, p.getNoHutang());
        ps.setDouble(4, p.getJumlahPembayaran());
        ps.setString(5, p.getTipeKeuangan());
        ps.setString(6, p.getCatatan());
        ps.setString(7, p.getKodeUser());
        ps.setString(8, p.getTglBatal());
        ps.setString(9, p.getUserBatal());
        ps.setString(10, p.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, Pembayaran p)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_pembayaran set "
                + " tgl_pembayaran=?, no_hutang=?, jumlah_pembayaran=?, tipe_keuangan=?, "
                + " catatan=?, kode_user=?, tgl_batal=?, user_batal=?, status=? where no_pembayaran=?");
        ps.setString(1, p.getTglPembayaran());
        ps.setString(2, p.getNoHutang());
        ps.setDouble(3, p.getJumlahPembayaran());
        ps.setString(4, p.getTipeKeuangan());
        ps.setString(5, p.getCatatan());
        ps.setString(6, p.getKodeUser());
        ps.setString(7, p.getTglBatal());
        ps.setString(8, p.getUserBatal());
        ps.setString(9, p.getStatus());
        ps.setString(10, p.getNoPembayaran());
        ps.executeUpdate();
    }
}
