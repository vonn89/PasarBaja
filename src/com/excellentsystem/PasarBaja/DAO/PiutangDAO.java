/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.Piutang;
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
public class PiutangDAO {
    public static List<Piutang> getAllByDateAndKategoriAndStatus(
            Connection con, String tglMulai, String tglAkhir, String kategori, String status)throws Exception{
        String sql = "select * from tm_piutang "
                + " where left(tgl_piutang,10) between ? and ? ";
        if(!kategori.equals("%"))
            sql = sql + " and kategori = '"+kategori+"' ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        else
            sql = sql + " and status != 'false'";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<Piutang> listPiutang = new ArrayList<>();
        while(rs.next()){
            Piutang p = new Piutang();
            p.setNoPiutang(rs.getString(1));
            p.setTglPiutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setKategori(rs.getString(3));
            p.setKeterangan(rs.getString(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setJumlahPiutang(rs.getDouble(6));
            p.setPembayaran(rs.getDouble(7));
            p.setSisaPiutang(rs.getDouble(8));
            p.setKodeUser(rs.getString(9));
            p.setStatus(rs.getString(10));
            listPiutang.add(p);
        }
        return listPiutang;
    }
    public static List<Piutang> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tm_piutang ";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        else
            sql = sql + " where status != 'false'";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Piutang> allPiutang = new ArrayList<>();
        while(rs.next()){
            Piutang p = new Piutang();
            p.setNoPiutang(rs.getString(1));
            p.setTglPiutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setKategori(rs.getString(3));
            p.setKeterangan(rs.getString(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setJumlahPiutang(rs.getDouble(6));
            p.setPembayaran(rs.getDouble(7));
            p.setSisaPiutang(rs.getDouble(8));
            p.setKodeUser(rs.getString(9));
            p.setStatus(rs.getString(10));
            allPiutang.add(p);
        }
        return allPiutang;
    }
    public static Piutang getByKategoriAndKeteranganAndStatus(Connection con, String kategori, String keterangan, String status)throws Exception{
        String sql = "select * from tm_piutang where no_piutang!='' ";
        if(!kategori.equals("%"))
            sql = sql + " and kategori = '"+kategori+"' ";
        if(!keterangan.equals("%"))
            sql = sql + " and keterangan = '"+keterangan+"' ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        else
            sql = sql + " and status != 'false'";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        Piutang p = null;
        while(rs.next()){
            p = new Piutang();
            p.setNoPiutang(rs.getString(1));
            p.setTglPiutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setKategori(rs.getString(3));
            p.setKeterangan(rs.getString(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setJumlahPiutang(rs.getDouble(6));
            p.setPembayaran(rs.getDouble(7));
            p.setSisaPiutang(rs.getDouble(8));
            p.setKodeUser(rs.getString(9));
            p.setStatus(rs.getString(10));
        }
        return p;
    }
    public static Piutang get(Connection con, String noPiutang)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_piutang where no_piutang=?");
        ps.setString(1, noPiutang);
        ResultSet rs = ps.executeQuery();
        Piutang p = null;
        while(rs.next()){
            p = new Piutang();
            p.setNoPiutang(rs.getString(1));
            p.setTglPiutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            p.setKategori(rs.getString(3));
            p.setKeterangan(rs.getString(4));
            p.setTipeKeuangan(rs.getString(5));
            p.setJumlahPiutang(rs.getDouble(6));
            p.setPembayaran(rs.getDouble(7));
            p.setSisaPiutang(rs.getDouble(8));
            p.setKodeUser(rs.getString(9));
            p.setStatus(rs.getString(10));
        }
        return p;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_piutang,3)) from tm_piutang "
                + " where mid(no_piutang,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "PT-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "PT-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, Piutang p)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_piutang values(?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, p.getNoPiutang());
        ps.setString(2, p.getTglPiutang());
        ps.setString(3, p.getKategori());
        ps.setString(4, p.getKeterangan());
        ps.setString(5, p.getTipeKeuangan());
        ps.setDouble(6, p.getJumlahPiutang());
        ps.setDouble(7, p.getPembayaran());
        ps.setDouble(8, p.getSisaPiutang());
        ps.setString(9, p.getKodeUser());
        ps.setString(10, p.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, Piutang p)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_piutang set "
                + "tgl_piutang=?, kategori=?, keterangan=?, tipe_keuangan=?, "
                + "jumlah_piutang=?, pembayaran=?, sisa_piutang=?, "
                + "kode_user=?, status=? where no_piutang=?");
        ps.setString(1, p.getTglPiutang());
        ps.setString(2, p.getKategori());
        ps.setString(3, p.getKeterangan());
        ps.setString(4, p.getTipeKeuangan());
        ps.setDouble(5, p.getJumlahPiutang());
        ps.setDouble(6, p.getPembayaran());
        ps.setDouble(7, p.getSisaPiutang());
        ps.setString(8, p.getKodeUser());
        ps.setString(9, p.getStatus());
        ps.setString(10, p.getNoPiutang());
        ps.executeUpdate();
    }
    public static void delete(Connection con, Piutang p)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tm_piutang where no_piutang=?");
        ps.setString(1, p.getNoPiutang());
        ps.executeUpdate();
    }
}
