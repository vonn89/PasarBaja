/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymmdd;
import com.excellentsystem.PasarBaja.Model.Keuangan;
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
public class KeuanganDAO {
    public static List<Keuangan> getAllByTanggal(Connection con, String tglMulai,String tglAkhir)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_keuangan "
                + " where left(tgl_keuangan,10) between ? and ? ");
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<Keuangan> allKeuangan = new ArrayList<>();
        while(rs.next()){
            Keuangan k = new Keuangan();
            k.setNoKeuangan(rs.getString(1));
            k.setTglKeuangan(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            k.setTipeKeuangan(rs.getString(3));
            k.setKategori(rs.getString(4));
            k.setDeskripsi(rs.getString(5));
            k.setJumlahRp(rs.getDouble(6));
            k.setKodeUser(rs.getString(7));
            allKeuangan.add(k);
        }
        return allKeuangan;
    }
    public static List<Keuangan> getAllByTipeKeuanganAndTanggal(
            Connection con, String tipeKeuangan,String tglMulai,String tglAkhir)throws Exception{
        String sql = "select * from tt_keuangan where left(tgl_keuangan,10) between ? and ? ";
        if(!tipeKeuangan.equals("%"))
            sql = sql + " and tipe_keuangan = '"+tipeKeuangan+"' ";
        sql = sql + " order by tgl_keuangan ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<Keuangan> allKeuangan = new ArrayList<>();
        while(rs.next()){
            Keuangan k = new Keuangan();
            k.setNoKeuangan(rs.getString(1));
            k.setTglKeuangan(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            k.setTipeKeuangan(rs.getString(3));
            k.setKategori(rs.getString(4));
            k.setDeskripsi(rs.getString(5));
            k.setJumlahRp(rs.getDouble(6));
            k.setKodeUser(rs.getString(7));
            allKeuangan.add(k);
        }
        return allKeuangan;
    }
    public static List<Keuangan> getAllByTipeKeuangan(Connection con, String tipeKeuangan)throws Exception{
        String sql = "select * from tt_keuangan ";
        if(!tipeKeuangan.equals("%"))
            sql = sql + " where tipe_keuangan = '"+tipeKeuangan+"' ";
        sql = sql + " order by tgl_keuangan ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Keuangan> allKeuangan = new ArrayList<>();
        while(rs.next()){
            Keuangan k = new Keuangan();
            k.setNoKeuangan(rs.getString(1));
            k.setTglKeuangan(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            k.setTipeKeuangan(rs.getString(3));
            k.setKategori(rs.getString(4));
            k.setDeskripsi(rs.getString(5));
            k.setJumlahRp(rs.getDouble(6));
            k.setKodeUser(rs.getString(7));
            allKeuangan.add(k);
        }
        return allKeuangan;
    }
    public static Double getSaldoAkhir(Connection con, String tanggal,String tipeKeuangan)throws Exception{
        String sql = "select sum(jumlah_rp) from tt_keuangan "
                + " where left(tgl_keuangan,10) <= ? ";
        if(!tipeKeuangan.equals("%"))
            sql = sql + " and tipe_keuangan = '"+tipeKeuangan+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tanggal);
        ResultSet rs = ps.executeQuery();
        double saldoAwal = 0;
        if(rs.next())
            saldoAwal = rs.getDouble(1);
        return saldoAwal;
    }
    public static Double getSaldoAwal(Connection con, String tanggal,String tipeKeuangan)throws Exception{
        String sql = "select sum(jumlah_rp) from tt_keuangan "
                + " where left(tgl_keuangan,10) < ? ";
        if(!tipeKeuangan.equals("%"))
            sql = sql + " and tipe_keuangan = '"+tipeKeuangan+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tanggal);
        ResultSet rs = ps.executeQuery();
        double saldoAwal = 0;
        if(rs.next())
            saldoAwal = rs.getDouble(1);
        return saldoAwal;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_keuangan,4)) "
                + " from tt_keuangan where mid(no_keuangan,4,6)=? ");
        ps.setString(1, yymmdd.format(date));
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return "KK-"+yymmdd.format(date)+"-" +new DecimalFormat("0000").format(rs.getInt(1)+1);
        else 
            return "KK-"+yymmdd.format(date)+"-"+new DecimalFormat("0000").format(1);
    }
    public static void insert(Connection con, Keuangan k)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_keuangan values (?,?,?,?,?,?,?)");
        ps.setString(1, k.getNoKeuangan());
        ps.setString(2, k.getTglKeuangan());
        ps.setString(3, k.getTipeKeuangan());
        ps.setString(4, k.getKategori());
        ps.setString(5, k.getDeskripsi());
        ps.setDouble(6, k.getJumlahRp());
        ps.setString(7, k.getKodeUser());
        ps.executeUpdate();
    }
    public static void update(Connection con, Keuangan k)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_keuangan set "
                + " tgl_keuangan=?, deskripsi=?, jumlah_rp=?, kode_user=?  "
                + " where no_keuangan=? and tipe_keuangan=? and kategori=?");
        ps.setString(1, k.getTglKeuangan());
        ps.setString(2, k.getDeskripsi());
        ps.setDouble(3, k.getJumlahRp());
        ps.setString(4, k.getKodeUser());
        ps.setString(5, k.getNoKeuangan());
        ps.setString(6, k.getTipeKeuangan());
        ps.setString(7, k.getKategori());
        ps.executeUpdate();
    }
    public static void delete(Connection con, String tipeKeuangan, String kategori, String keterangan)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tt_keuangan "
                + " where tipe_keuangan=? and kategori=? and deskripsi=?");
        ps.setString(1, tipeKeuangan);
        ps.setString(2, kategori);
        ps.setString(3, keterangan);
        ps.executeUpdate();
    }
    public static void deleteByNoKeuangan(Connection con, String noKeuangan)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tt_keuangan "
                + " where no_keuangan=?");
        ps.setString(1, noKeuangan);
        ps.executeUpdate();
    }
}
