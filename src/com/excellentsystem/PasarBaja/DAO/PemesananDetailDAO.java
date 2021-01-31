/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class PemesananDetailDAO {
    public static List<PemesananDetail> getAllByDateAndStatus(Connection con, 
            String tglMulai, String tglAkhir, String status)throws Exception{
        String sql = "select * from tt_pemesanan_detail "
                + " where no_pemesanan in (select no_pemesanan from tt_pemesanan_head "
                + " where left(tgl_pemesanan,10) between ? and ? ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        sql = sql + " )";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<PemesananDetail> allDetail = new ArrayList<>();
        while(rs.next()){
            PemesananDetail d = new PemesananDetail();
            d.setNoPemesanan(rs.getString(1));
            d.setNoUrut(rs.getInt(2));
            d.setKodeBarang(rs.getString(3));
            d.setNamaBarang(rs.getString(4));
            d.setKeterangan(rs.getString(5));
            d.setCatatanIntern(rs.getString(6));
            d.setSatuan(rs.getString(7));
            d.setQty(rs.getDouble(8));
            d.setQtyTerkirim(rs.getDouble(9));
            d.setHargaJual(rs.getDouble(10));
            d.setTotal(rs.getDouble(11));
            allDetail.add(d);
        }
        return allDetail;
    }
    public static List<PemesananDetail> getAllByNoPemesanan(Connection con, String noPemesanan)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_pemesanan_detail where no_pemesanan=?");
        ps.setString(1, noPemesanan);
        ResultSet rs = ps.executeQuery();
        List<PemesananDetail> allDetail = new ArrayList<>();
        while(rs.next()){
            PemesananDetail d = new PemesananDetail();
            d.setNoPemesanan(rs.getString(1));
            d.setNoUrut(rs.getInt(2));
            d.setKodeBarang(rs.getString(3));
            d.setNamaBarang(rs.getString(4));
            d.setKeterangan(rs.getString(5));
            d.setCatatanIntern(rs.getString(6));
            d.setSatuan(rs.getString(7));
            d.setQty(rs.getDouble(8));
            d.setQtyTerkirim(rs.getDouble(9));
            d.setHargaJual(rs.getDouble(10));
            d.setTotal(rs.getDouble(11));
            allDetail.add(d);
        }
        return allDetail;
    }
    public static PemesananDetail get(Connection con, String noPemesanan, int noUrut)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_pemesanan_detail where no_pemesanan=? and no_urut=?");
        ps.setString(1, noPemesanan);
        ps.setInt(2, noUrut);
        ResultSet rs = ps.executeQuery();
        PemesananDetail d = null;
        while(rs.next()){
            d = new PemesananDetail();
            d.setNoPemesanan(rs.getString(1));
            d.setNoUrut(rs.getInt(2));
            d.setKodeBarang(rs.getString(3));
            d.setNamaBarang(rs.getString(4));
            d.setKeterangan(rs.getString(5));
            d.setCatatanIntern(rs.getString(6));
            d.setSatuan(rs.getString(7));
            d.setQty(rs.getDouble(8));
            d.setQtyTerkirim(rs.getDouble(9));
            d.setHargaJual(rs.getDouble(10));
            d.setTotal(rs.getDouble(11));
        }
        return d;
    }
    public static void insert(Connection con, PemesananDetail d)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_pemesanan_detail values(?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, d.getNoPemesanan());
        ps.setInt(2, d.getNoUrut());
        ps.setString(3, d.getKodeBarang());
        ps.setString(4, d.getNamaBarang());
        ps.setString(5, d.getKeterangan());
        ps.setString(6, d.getCatatanIntern());
        ps.setString(7, d.getSatuan());
        ps.setDouble(8, d.getQty());
        ps.setDouble(9, d.getQtyTerkirim());
        ps.setDouble(10, d.getHargaJual());
        ps.setDouble(11, d.getTotal());
        ps.executeUpdate();
    }
    public static void update(Connection con, PemesananDetail d)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_pemesanan_detail set "
                + " kode_barang=?, nama_barang=?, keterangan=?, catatan_intern=?, satuan=?, qty=?, qty_terkirim=?, harga_jual=?, total=? "
                + " where no_pemesanan=? and no_urut=? ");
        ps.setString(1, d.getKodeBarang());
        ps.setString(2, d.getNamaBarang());
        ps.setString(3, d.getKeterangan());
        ps.setString(4, d.getCatatanIntern());
        ps.setString(5, d.getSatuan());
        ps.setDouble(6, d.getQty());
        ps.setDouble(7, d.getQtyTerkirim());
        ps.setDouble(8, d.getHargaJual());
        ps.setDouble(9, d.getTotal());
        ps.setString(10, d.getNoPemesanan());
        ps.setInt(11, d.getNoUrut());
        ps.executeUpdate();
    }
    public static void delete(Connection con, String noPemesanan)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tt_pemesanan_detail where no_pemesanan=? ");
        ps.setString(1, noPemesanan);
        ps.executeUpdate();
    }
}
