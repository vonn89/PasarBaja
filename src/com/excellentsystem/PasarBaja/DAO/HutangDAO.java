/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.Hutang;
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
public class HutangDAO {
    public static List<Hutang> getAllByTanggalAndKategoriAndStatus(Connection con, 
            String tglMulai, String tglAkhir, String kategori, String status)throws Exception{
        String sql = "select * from tm_hutang where left(tgl_hutang,10) between ? and ?  ";
        if(!kategori.equals("%"))
            sql = sql + " and kategori = '"+kategori+"'";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"'";
        else
            sql = sql + " and status != 'false' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<Hutang> listHutang = new ArrayList<>();
        while(rs.next()){
            Hutang h = new Hutang();
            h.setNoHutang(rs.getString(1));
            h.setTglHutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            h.setKategori(rs.getString(3));
            h.setKeterangan(rs.getString(4));
            h.setTipeKeuangan(rs.getString(5));
            h.setJumlahHutang(rs.getDouble(6));
            h.setPembayaran(rs.getDouble(7));
            h.setSisaHutang(rs.getDouble(8));
            h.setKodeUser(rs.getString(9));
            h.setStatus(rs.getString(10));
            listHutang.add(h);
        }
        return listHutang;
    }
    public static List<Hutang> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tm_hutang ";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        else
            sql = sql + " where status != 'false' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Hutang> allHutang = new ArrayList<>();
        while(rs.next()){
            Hutang h = new Hutang();
            h.setNoHutang(rs.getString(1));
            h.setTglHutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            h.setKategori(rs.getString(3));
            h.setKeterangan(rs.getString(4));
            h.setTipeKeuangan(rs.getString(5));
            h.setJumlahHutang(rs.getDouble(6));
            h.setPembayaran(rs.getDouble(7));
            h.setSisaHutang(rs.getDouble(8));
            h.setKodeUser(rs.getString(9));
            h.setStatus(rs.getString(10));
            allHutang.add(h);
        }
        return allHutang;
    }
    public static List<Hutang> getAllByKategoriAndKeteranganAndStatus(Connection con, 
            String kategori, String keterangan, String status)throws Exception{
        String sql = "select * from tm_hutang where no_hutang !='' ";
        if(!kategori.equals("%"))
            sql = sql + " and kategori = '"+kategori+"' ";
        if(!keterangan.equals("%"))
            sql = sql + " and keterangan = '"+keterangan+"' ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        else
            sql = sql + " and status != 'false' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Hutang> listHutang = new ArrayList<>();
        while(rs.next()){
            Hutang h = new Hutang();
            h.setNoHutang(rs.getString(1));
            h.setTglHutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            h.setKategori(rs.getString(3));
            h.setKeterangan(rs.getString(4));
            h.setTipeKeuangan(rs.getString(5));
            h.setJumlahHutang(rs.getDouble(6));
            h.setPembayaran(rs.getDouble(7));
            h.setSisaHutang(rs.getDouble(8));
            h.setKodeUser(rs.getString(9));
            h.setStatus(rs.getString(10));
            listHutang.add(h);
        }
        return listHutang;
    }
    public static Hutang getByKategoriAndKeteranganAndStatus(Connection con, 
            String kategori, String keterangan, String status)throws Exception{
        String sql = "select * from tm_hutang where no_hutang !='' ";
        if(!kategori.equals("%"))
            sql = sql + " and kategori = '"+kategori+"' ";
        if(!keterangan.equals("%"))
            sql = sql + " and keterangan = '"+keterangan+"' ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        else
            sql = sql + " and status != 'false' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        Hutang h = null;
        while(rs.next()){
            h = new Hutang();
            h.setNoHutang(rs.getString(1));
            h.setTglHutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            h.setKategori(rs.getString(3));
            h.setKeterangan(rs.getString(4));
            h.setTipeKeuangan(rs.getString(5));
            h.setJumlahHutang(rs.getDouble(6));
            h.setPembayaran(rs.getDouble(7));
            h.setSisaHutang(rs.getDouble(8));
            h.setKodeUser(rs.getString(9));
            h.setStatus(rs.getString(10));
        }
        return h;
    }
    public static Hutang get(Connection con, String noHutang)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_hutang where no_hutang=?");
        ps.setString(1, noHutang);
        ResultSet rs = ps.executeQuery();
        Hutang h = null;
        while(rs.next()){
            h = new Hutang();
            h.setNoHutang(rs.getString(1));
            h.setTglHutang(rs.getDate(2).toString()+" "+rs.getTime(2).toString());
            h.setKategori(rs.getString(3));
            h.setKeterangan(rs.getString(4));
            h.setTipeKeuangan(rs.getString(5));
            h.setJumlahHutang(rs.getDouble(6));
            h.setPembayaran(rs.getDouble(7));
            h.setSisaHutang(rs.getDouble(8));
            h.setKodeUser(rs.getString(9));
            h.setStatus(rs.getString(10));
        }
        return h;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_hutang,3)) "
                + " from tm_hutang where mid(no_hutang,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "HT-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "HT-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, Hutang h)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_hutang values(?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, h.getNoHutang());
        ps.setString(2, h.getTglHutang());
        ps.setString(3, h.getKategori());
        ps.setString(4, h.getKeterangan());
        ps.setString(5, h.getTipeKeuangan());
        ps.setDouble(6, h.getJumlahHutang());
        ps.setDouble(7, h.getPembayaran());
        ps.setDouble(8, h.getSisaHutang());
        ps.setString(9, h.getKodeUser());
        ps.setString(10, h.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, Hutang h)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_hutang set "
                + " tgl_hutang=?, kategori=?, keterangan=?, tipe_keuangan=?, "
                + " jumlah_hutang=?, pembayaran=?, sisa_hutang=?, "
                + " kode_user=?, status=? where no_hutang=?");
        ps.setString(1, h.getTglHutang());
        ps.setString(2, h.getKategori());
        ps.setString(3, h.getKeterangan());
        ps.setString(4, h.getTipeKeuangan());
        ps.setDouble(5, h.getJumlahHutang());
        ps.setDouble(6, h.getPembayaran());
        ps.setDouble(7, h.getSisaHutang());
        ps.setString(8, h.getKodeUser());
        ps.setString(9, h.getStatus());
        ps.setString(10, h.getNoHutang());
        ps.executeUpdate();
    }
    public static void delete(Connection con, Hutang h)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tm_hutang where no_hutang=?");
        ps.setString(1, h.getNoHutang());
        ps.executeUpdate();
    }
}
