/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class PenjualanDetailDAO {
    public static List<PenjualanDetail> getAllByDateAndStatus(
            Connection con, String tglMulai, String tglAkhir, String status)throws Exception{
        String sql = "select * from tt_penjualan_detail "
            + " where no_penjualan in (select no_penjualan from tt_penjualan_head "
            + " where left(tgl_penjualan,10) between ? and ? ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"' ";
        sql = sql + " )";
        PreparedStatement ps = con.prepareStatement(sql);
        List<PenjualanDetail> allDetail = new ArrayList<>();
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            PenjualanDetail d = new PenjualanDetail();
            d.setNoPenjualan(rs.getString(1));
            d.setNoPemesanan(rs.getString(2));
            d.setNoUrut(rs.getInt(3));
            d.setKodeBarang(rs.getString(4));
            d.setNamaBarang(rs.getString(5));
            d.setKeterangan(rs.getString(6));
            d.setCatatanIntern(rs.getString(7));
            d.setSatuan(rs.getString(8));
            d.setQty(rs.getDouble(9));
            d.setNilai(rs.getDouble(10));
            d.setHargaJual(rs.getDouble(11));
            d.setTotal(rs.getDouble(12));
            allDetail.add(d);
        }
        return allDetail;
    }
    public static List<PenjualanDetail> getAllPenjualanDetail(Connection con, String noPenjualan)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_penjualan_detail where no_penjualan=?");
        ps.setString(1, noPenjualan);
        ResultSet rs = ps.executeQuery();
        List<PenjualanDetail> allDetail = new ArrayList<>();
        while(rs.next()){
            PenjualanDetail d = new PenjualanDetail();
            d.setNoPenjualan(rs.getString(1));
            d.setNoPemesanan(rs.getString(2));
            d.setNoUrut(rs.getInt(3));
            d.setKodeBarang(rs.getString(4));
            d.setNamaBarang(rs.getString(5));
            d.setKeterangan(rs.getString(6));
            d.setCatatanIntern(rs.getString(7));
            d.setSatuan(rs.getString(8));
            d.setQty(rs.getDouble(9));
            d.setNilai(rs.getDouble(10));
            d.setHargaJual(rs.getDouble(11));
            d.setTotal(rs.getDouble(12));
            allDetail.add(d);
        }
        return allDetail;
    }
    public static void insert(Connection con, PenjualanDetail d)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_penjualan_detail values(?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, d.getNoPenjualan());
        ps.setString(2, d.getNoPemesanan());
        ps.setInt(3, d.getNoUrut());
        ps.setString(4, d.getKodeBarang());
        ps.setString(5, d.getNamaBarang());
        ps.setString(6, d.getKeterangan());
        ps.setString(7, d.getCatatanIntern());
        ps.setString(8, d.getSatuan());
        ps.setDouble(9, d.getQty());
        ps.setDouble(10, d.getNilai());
        ps.setDouble(11, d.getHargaJual());
        ps.setDouble(12, d.getTotal());
        ps.executeUpdate();
    }
    public static void update(Connection con, PenjualanDetail d)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_penjualan_detail set "
                + " kode_barang = ?, nama_barang = ?, keterangan = ?, catatan_intern = ?, "
                + " satuan = ?, qty = ?, nilai = ?, harga_jual = ?, total = ? "
                + " where no_penjualan = ? and no_pemesanan = ? and no_urut = ? ");
        ps.setString(1, d.getKodeBarang());
        ps.setString(2, d.getNamaBarang());
        ps.setString(3, d.getKeterangan());
        ps.setString(4, d.getCatatanIntern());
        ps.setString(5, d.getSatuan());
        ps.setDouble(6, d.getQty());
        ps.setDouble(7, d.getNilai());
        ps.setDouble(8, d.getHargaJual());
        ps.setDouble(9, d.getTotal());
        ps.setString(10, d.getNoPenjualan());
        ps.setString(11, d.getNoPemesanan());
        ps.setInt(12, d.getNoUrut());
        ps.executeUpdate();
    }
}
