/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.KategoriKeuangan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yunaz
 */
public class KategoriKeuanganDAO {
    
    public static List<KategoriKeuangan> getAll(Connection con)throws Exception{
        List<KategoriKeuangan> allTipeKeuangan;
        PreparedStatement ps = con.prepareStatement("select * from tm_kategori_keuangan");
        ResultSet rs = ps.executeQuery();
        allTipeKeuangan = new ArrayList<>();
        while(rs.next()){
            KategoriKeuangan k = new KategoriKeuangan();
            k.setKodeKeuangan(rs.getString(1));
            allTipeKeuangan.add(k);
        }
        return allTipeKeuangan;
    }
    public static void insert(Connection con, KategoriKeuangan k)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_kategori_keuangan values(?)");
        ps.setString(1, k.getKodeKeuangan());
        ps.executeUpdate();
    }
    public static void delete(Connection con, KategoriKeuangan k)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tm_kategori_keuangan where kode_keuangan=?");
        ps.setString(1, k.getKodeKeuangan());
        ps.executeUpdate();
    }
}
