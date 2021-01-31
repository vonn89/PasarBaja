/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.KategoriTransaksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yunaz
 */
public class KategoriTransaksiDAO {
    public static List<KategoriTransaksi> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tm_kategori_transaksi";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<KategoriTransaksi> allKategoriTransaksi = new ArrayList<>();
        while(rs.next()){
            KategoriTransaksi k = new KategoriTransaksi();
            k.setKodeKategori(rs.getString(1));
            k.setJenisTransaksi(rs.getString(2));
            k.setStatus(rs.getString(3));
            allKategoriTransaksi.add(k);
        }
        return allKategoriTransaksi;
    }
    public static KategoriTransaksi get(Connection con, String kodeKategori)throws Exception{
        KategoriTransaksi k = null;
        PreparedStatement ps = con.prepareStatement("select * from tm_kategori_transaksi where kode_kategori=?");
        ps.setString(1, kodeKategori);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            k = new KategoriTransaksi();
            k.setKodeKategori(rs.getString(1));
            k.setJenisTransaksi(rs.getString(2));
            k.setStatus(rs.getString(3));
        }
        return k;
    }
    public static void insert(Connection con, KategoriTransaksi k)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_kategori_transaksi values(?,?,?)");
        ps.setString(1, k.getKodeKategori());
        ps.setString(2, k.getJenisTransaksi());
        ps.setString(3, k.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, KategoriTransaksi k)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_kategori_transaksi set jenis_transaksi=?, status=? where kode_kategori=?");
        ps.setString(1, k.getJenisTransaksi());
        ps.setString(2, k.getStatus());
        ps.setString(3, k.getKodeKategori());
        ps.executeUpdate();
    }
}
