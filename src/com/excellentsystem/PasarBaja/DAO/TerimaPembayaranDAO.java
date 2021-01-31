/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.TerimaPembayaran;
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
public class TerimaPembayaranDAO {
    public static List<TerimaPembayaran> getAllByTglTerima(Connection con, String tglAwal, String tglAkhir, String status)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_terima_pembayaran where left(tgl_terima,10) between ? and ?  and status = ?");
        ps.setString(1, tglAwal);
        ps.setString(2, tglAkhir);
        ps.setString(3, status);
        ResultSet rs = ps.executeQuery();
        List<TerimaPembayaran> allPembayaran = new ArrayList<>();
        while(rs.next()){
            TerimaPembayaran t = new TerimaPembayaran();
            t.setNoTerimaPembayaran(rs.getString(1));
            t.setTglTerima(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            t.setNoPiutang(rs.getString(3));
            t.setJumlahPembayaran(rs.getDouble(4));
            t.setTipeKeuangan(rs.getString(5));
            t.setCatatan(rs.getString(6));
            t.setKodeUser(rs.getString(7));
            t.setTglBatal(rs.getDate(8).toString()+" "+rs.getTime(8).toString());
            t.setUserBatal(rs.getString(9));
            t.setStatus(rs.getString(10));
            allPembayaran.add(t);
        }
        return allPembayaran;
    }
    public static List<TerimaPembayaran> getAllByNoPiutangAndStatus(Connection con, String noPiutang, String status)throws Exception{
        String sql = "select * from tt_terima_pembayaran where no_terima_pembayaran !='' ";
        if(!noPiutang.equals("%"))
            sql = sql + " and no_piutang = '"+noPiutang+"' ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<TerimaPembayaran> allPembayaran = new ArrayList<>();
        while(rs.next()){
            TerimaPembayaran t = new TerimaPembayaran();
            t.setNoTerimaPembayaran(rs.getString(1));
            t.setTglTerima(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            t.setNoPiutang(rs.getString(3));
            t.setJumlahPembayaran(rs.getDouble(4));
            t.setTipeKeuangan(rs.getString(5));
            t.setCatatan(rs.getString(6));
            t.setKodeUser(rs.getString(7));
            t.setTglBatal(rs.getDate(8).toString()+" "+rs.getTime(8).toString());
            t.setUserBatal(rs.getString(9));
            t.setStatus(rs.getString(10));
            allPembayaran.add(t);
        }
        return allPembayaran;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_terima_pembayaran,3)) "
                + " from tt_terima_pembayaran where mid(no_terima_pembayaran,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "TB-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "TB-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, TerimaPembayaran t)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_terima_pembayaran values(?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, t.getNoTerimaPembayaran());
        ps.setString(2, t.getTglTerima());
        ps.setString(3, t.getNoPiutang());
        ps.setDouble(4, t.getJumlahPembayaran());
        ps.setString(5, t.getTipeKeuangan());
        ps.setString(6, t.getCatatan());
        ps.setString(7, t.getKodeUser());
        ps.setString(8, t.getTglBatal());
        ps.setString(9, t.getUserBatal());
        ps.setString(10, t.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, TerimaPembayaran t)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_terima_pembayaran set "
                + " tgl_terima=?, no_piutang=?, jumlah_pembayaran=?, tipe_keuangan=?, "
                + " catatan=?, kode_user=?, tgl_batal=?, user_batal=?, status=? "
                + " where no_terima_pembayaran=?");
        ps.setString(1, t.getTglTerima());
        ps.setString(2, t.getNoPiutang());
        ps.setDouble(3, t.getJumlahPembayaran());
        ps.setString(4, t.getTipeKeuangan());
        ps.setString(5, t.getCatatan());
        ps.setString(6, t.getKodeUser());
        ps.setString(7, t.getTglBatal());
        ps.setString(8, t.getUserBatal());
        ps.setString(9, t.getStatus());
        ps.setString(10, t.getNoTerimaPembayaran());
        ps.executeUpdate();
    }
}
