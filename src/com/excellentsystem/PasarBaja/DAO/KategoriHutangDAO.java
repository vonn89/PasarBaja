/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.KategoriHutang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yunaz
 */
public class KategoriHutangDAO {
    public static List<KategoriHutang> getAll(Connection con)throws Exception{
        List<KategoriHutang> allKategoriHutang;
        PreparedStatement ps = con.prepareStatement("select * from tm_kategori_hutang");
        ResultSet rs = ps.executeQuery();
        allKategoriHutang = new ArrayList<>();
        while(rs.next()){
            KategoriHutang k = new KategoriHutang();
            k.setKodeKategori(rs.getString(1));
            allKategoriHutang.add(k);
        }
        return allKategoriHutang;
    }
    public static void insert(Connection con, KategoriHutang k)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_kategori_hutang values(?)");
        ps.setString(1, k.getKodeKategori());
        ps.executeUpdate();
    }
    public static void delete(Connection con, KategoriHutang k)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tm_kategori_hutang where kode_kategori=?");
        ps.setString(1, k.getKodeKategori());
        ps.executeUpdate();
    }
}
