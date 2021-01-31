/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.LogBarang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yunaz
 */
public class LogBarangDAO {
    public static LogBarang getLastByBarang(Connection con, String kodeBarang)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_log_barang "
                + "where kode_barang = ? order by tanggal desc limit 1");
        ps.setString(1, kodeBarang);
        ResultSet rs = ps.executeQuery();
        LogBarang l = null;
        while(rs.next()){
            l = new LogBarang();
            l.setTanggal(rs.getString(1));
            l.setKodeBarang(rs.getString(2));
            l.setKategori(rs.getString(3));
            l.setKeterangan(rs.getString(4));
            l.setStokAwal(rs.getDouble(5));
            l.setNilaiAwal(rs.getDouble(6));
            l.setStokMasuk(rs.getDouble(7));
            l.setNilaiMasuk(rs.getDouble(8));
            l.setStokKeluar(rs.getDouble(9));
            l.setNilaiKeluar(rs.getDouble(10));
            l.setStokAkhir(rs.getDouble(11));
            l.setNilaiAkhir(rs.getDouble(12));
        }
        return l;
    }
    public static LogBarang getByTanggalAndBarang(Connection con, String tanggal, String kodeBarang)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_log_barang "
                + "where tanggal<= ? and kode_barang = ? order by tanggal desc limit 1");
        ps.setString(1, tanggal);
        ps.setString(2, kodeBarang);
        ResultSet rs = ps.executeQuery();
        LogBarang l = null;
        while(rs.next()){
            l = new LogBarang();
            l.setTanggal(rs.getString(1));
            l.setKodeBarang(rs.getString(2));
            l.setKategori(rs.getString(3));
            l.setKeterangan(rs.getString(4));
            l.setStokAwal(rs.getDouble(5));
            l.setNilaiAwal(rs.getDouble(6));
            l.setStokMasuk(rs.getDouble(7));
            l.setNilaiMasuk(rs.getDouble(8));
            l.setStokKeluar(rs.getDouble(9));
            l.setNilaiKeluar(rs.getDouble(10));
            l.setStokAkhir(rs.getDouble(11));
            l.setNilaiAkhir(rs.getDouble(12));
        }
        return l;
    }
    public static List<LogBarang> getAllByTanggal(Connection con, String tanggal)throws Exception{
        PreparedStatement ps = con.prepareStatement("SELECT '"+tanggal+"',a.kode_barang,a.kategori,a.keterangan,"+
            " a.stok_awal,a.nilai_awal,a.stok_masuk,a.nilai_masuk,a.stok_keluar,a.nilai_keluar,a.stok_akhir,a.nilai_akhir " +
            " FROM tt_log_barang a " +
            " JOIN ( " +
            " SELECT MAX(tanggal) as tanggal, kode_barang as barang " +
            " FROM tt_log_barang " +
            " WHERE left(tanggal,10) <= '"+tanggal+"' " +
            " GROUP BY barang " +
            ") AS b ON a.kode_barang = b.barang AND a.tanggal = b.tanggal " +
            " where left(a.tanggal,10) <= '"+tanggal+"'");
        ResultSet rs = ps.executeQuery();
        List<LogBarang> log = new ArrayList<>();
        while(rs.next()){
            LogBarang l = new LogBarang();
            l.setTanggal(rs.getString(1));
            l.setKodeBarang(rs.getString(2));
            l.setKategori(rs.getString(3));
            l.setKeterangan(rs.getString(4));
            l.setStokAwal(rs.getDouble(5));
            l.setNilaiAwal(rs.getDouble(6));
            l.setStokMasuk(rs.getDouble(7));
            l.setNilaiMasuk(rs.getDouble(8));
            l.setStokKeluar(rs.getDouble(9));
            l.setNilaiKeluar(rs.getDouble(10));
            l.setStokAkhir(rs.getDouble(11));
            l.setNilaiAkhir(rs.getDouble(12));
            log.add(l);
        }
        return log;
    }
    public static List<LogBarang> getAllByTanggalAndBarang(
            Connection con, String tglMulai, String tglAkhir, String kodeBarang)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_log_barang "
                + " where left(tanggal,10) between ? and ? and kode_barang = ? order by tanggal");
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ps.setString(3, kodeBarang);
        ResultSet rs = ps.executeQuery();
        List<LogBarang> allLogBarang = new ArrayList<>();
        while(rs.next()){
            LogBarang l = new LogBarang();
            l.setTanggal(rs.getString(1));
            l.setKodeBarang(rs.getString(2));
            l.setKategori(rs.getString(3));
            l.setKeterangan(rs.getString(4));
            l.setStokAwal(rs.getDouble(5));
            l.setNilaiAwal(rs.getDouble(6));
            l.setStokMasuk(rs.getDouble(7));
            l.setNilaiMasuk(rs.getDouble(8));
            l.setStokKeluar(rs.getDouble(9));
            l.setNilaiKeluar(rs.getDouble(10));
            l.setStokAkhir(rs.getDouble(11));
            l.setNilaiAkhir(rs.getDouble(12));
            allLogBarang.add(l);
        }
        return allLogBarang;
    }
    public static LogBarang getLastBeforeDateAndBarang(
            Connection con, String tanggal, String kodeBarang)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_log_barang where left(tanggal,10) < ? "
                + " and kode_barang = ? order by tanggal desc limit 1");
        ps.setString(1, tanggal);
        ps.setString(2, kodeBarang);
        ResultSet rs = ps.executeQuery();
        LogBarang l = null;
        while(rs.next()){
            l = new LogBarang();
            l.setTanggal(rs.getString(1));
            l.setKodeBarang(rs.getString(2));
            l.setKategori(rs.getString(3));
            l.setKeterangan(rs.getString(4));
            l.setStokAwal(rs.getDouble(5));
            l.setNilaiAwal(rs.getDouble(6));
            l.setStokMasuk(rs.getDouble(7));
            l.setNilaiMasuk(rs.getDouble(8));
            l.setStokKeluar(rs.getDouble(9));
            l.setNilaiKeluar(rs.getDouble(10));
            l.setStokAkhir(rs.getDouble(11));
            l.setNilaiAkhir(rs.getDouble(12));
        }
        return l;
    }
    public static void update(Connection con, LogBarang l)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_log_barang set "
                + " stok_awal=?, nilai_awal=?, stok_masuk=?, nilai_masuk=?, "
                + " stok_keluar=?, nilai_keluar=?, stok_akhir=?, nilai_akhir=? "
                + " where tanggal = ? and kode_barang = ? and kategori = ? and keterangan = ?");
        ps.setDouble(1, l.getStokAwal());
        ps.setDouble(2, l.getNilaiAwal());
        ps.setDouble(3, l.getStokMasuk());
        ps.setDouble(4, l.getNilaiMasuk());
        ps.setDouble(5, l.getStokKeluar());
        ps.setDouble(6, l.getNilaiKeluar());
        ps.setDouble(7, l.getStokAkhir());
        ps.setDouble(8, l.getNilaiAkhir());
        ps.setString(9, l.getTanggal());
        ps.setString(10, l.getKodeBarang());
        ps.setString(11, l.getKategori());
        ps.setString(12, l.getKeterangan());
        ps.executeUpdate();
    }
    public static void insert(Connection con, LogBarang l)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert tt_log_barang values(?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, l.getTanggal());
        ps.setString(2, l.getKodeBarang());
        ps.setString(3, l.getKategori());
        ps.setString(4, l.getKeterangan());
        ps.setDouble(5, l.getStokAwal());
        ps.setDouble(6, l.getNilaiAwal());
        ps.setDouble(7, l.getStokMasuk());
        ps.setDouble(8, l.getNilaiMasuk());
        ps.setDouble(9, l.getStokKeluar());
        ps.setDouble(10, l.getNilaiKeluar());
        ps.setDouble(11, l.getStokAkhir());
        ps.setDouble(12, l.getNilaiAkhir());
        ps.executeUpdate();
    }
    public static void delete(Connection con, String kodeBarang, String kategori, String keterangan)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tt_log_barang "
                + " where kode_barang = ? and kategori = ? and keterangan = ?");
        ps.setString(1, kodeBarang);
        ps.setString(2, kategori);
        ps.setString(3, keterangan);
        ps.executeUpdate();
    }
}
