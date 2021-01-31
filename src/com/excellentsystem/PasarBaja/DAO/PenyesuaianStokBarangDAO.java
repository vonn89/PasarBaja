/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymmdd;
import com.excellentsystem.PasarBaja.Model.PenyesuaianStokBarang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author excellent
 */
public class PenyesuaianStokBarangDAO {
    public static PenyesuaianStokBarang get(Connection con, String noPenyesuaian)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_penyesuaian_stok_barang "
                + "where no_penyesuaian = ?");
        ps.setString(1, noPenyesuaian);
        PenyesuaianStokBarang p = null;
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            p = new PenyesuaianStokBarang();
            p.setNoPenyesuaian(rs.getString(1));
            p.setTglPenyesuaian(rs.getString(2));
            p.setKodeBarang(rs.getString(3));
            p.setQty(rs.getDouble(4));
            p.setNilai(rs.getDouble(5));
            p.setCatatan(rs.getString(6));
            p.setKodeUser(rs.getString(7));
            p.setStatus(rs.getString(8));
        }
        return p;
    }
    public static List<PenyesuaianStokBarang> getAllByDateAndBarang(
            Connection con, String tglMulai, String tglAkhir, String barang, String gudang)throws Exception{
        String sql = "select * from tt_penyesuaian_stok_barang where left(tgl_penyesuaian,10) between ? and ? ";
        if(!barang.equals("%"))
            sql = sql + " and kode_barang = '"+barang+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<PenyesuaianStokBarang> listPenyesuaianStok = new ArrayList<>();
        while(rs.next()){
            PenyesuaianStokBarang p = new PenyesuaianStokBarang();
            p.setNoPenyesuaian(rs.getString(1));
            p.setTglPenyesuaian(rs.getString(2));
            p.setKodeBarang(rs.getString(3));
            p.setQty(rs.getDouble(4));
            p.setNilai(rs.getDouble(5));
            p.setCatatan(rs.getString(6));
            p.setKodeUser(rs.getString(7));
            p.setStatus(rs.getString(8));
            listPenyesuaianStok.add(p);
        }
        return listPenyesuaianStok;
    }
    public static void insert(Connection con, PenyesuaianStokBarang p)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_penyesuaian_stok_barang values(?,?,?,?,?,?,?,?)");
        ps.setString(1, p.getNoPenyesuaian());
        ps.setString(2, p.getTglPenyesuaian());
        ps.setString(3, p.getKodeBarang());
        ps.setDouble(4, p.getQty());
        ps.setDouble(5, p.getNilai());
        ps.setString(6, p.getCatatan());
        ps.setString(7, p.getKodeUser());
        ps.setString(8, p.getStatus());
        ps.executeUpdate();
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_penyesuaian,3)) from tt_penyesuaian_stok_barang "
                + " where mid(no_penyesuaian,4,6) = ?");
        ps.setString(1, yymmdd.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "SR-"+yymmdd.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "SR-"+yymmdd.format(date)+new DecimalFormat("000").format(1);
    }
}
