/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class SupplierDAO {
    public static List<Supplier> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tm_supplier ";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Supplier> allSupplier = new ArrayList<>();
        while(rs.next()){
            Supplier s = new Supplier();
            s.setKodeSupplier(rs.getString(1));
            s.setNama(rs.getString(2));
            s.setAlamat(rs.getString(3));
            s.setKota(rs.getString(4));
            s.setEmail(rs.getString(5));
            s.setKontakPerson(rs.getString(6));
            s.setNoTelp(rs.getString(7));
            s.setNoHandphone(rs.getString(8));
            s.setStatus(rs.getString(9));
            allSupplier.add(s);
        }
        return allSupplier;
    }
    public static Supplier get(Connection con, String kodeSupplier)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_supplier where kode_supplier=? ");
        ps.setString(1, kodeSupplier);
        ResultSet rs = ps.executeQuery();
        Supplier s = null;
        while(rs.next()){
            s = new Supplier();
            s.setKodeSupplier(rs.getString(1));
            s.setNama(rs.getString(2));
            s.setAlamat(rs.getString(3));
            s.setKota(rs.getString(4));
            s.setEmail(rs.getString(5));
            s.setKontakPerson(rs.getString(6));
            s.setNoTelp(rs.getString(7));
            s.setNoHandphone(rs.getString(8));
            s.setStatus(rs.getString(9));
        }
        return s;
    }
    public static String getId(Connection con)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(kode_supplier,4)) from tm_supplier");
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "SP-"+new DecimalFormat("0000").format(rs.getInt(1)+1);
        else
            return "SP-"+new DecimalFormat("0000").format(1);
    }
    public static void update(Connection con, Supplier s)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_supplier set nama=?, alamat=?, kota=?, "
            + " email=?, kontak_person=?, no_telp=?, no_handphone=?, status=?"
            + " where kode_supplier=?");
        ps.setString(1, s.getNama());
        ps.setString(2, s.getAlamat());
        ps.setString(3, s.getKota());
        ps.setString(4, s.getEmail());
        ps.setString(5, s.getKontakPerson());
        ps.setString(6, s.getNoTelp());
        ps.setString(7, s.getNoHandphone());
        ps.setString(8, s.getStatus());
        ps.setString(9, s.getKodeSupplier());
        ps.executeUpdate();
    }
    public static void insert(Connection con, Supplier s)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_supplier values(?,?,?,?,?,?,?,?,?)");
        ps.setString(1, s.getKodeSupplier());
        ps.setString(2, s.getNama());
        ps.setString(3, s.getAlamat());
        ps.setString(4, s.getKota());
        ps.setString(5, s.getEmail());
        ps.setString(6, s.getKontakPerson());
        ps.setString(7, s.getNoTelp());
        ps.setString(8, s.getNoHandphone());
        ps.setString(9, s.getStatus());
        ps.executeUpdate();
    }
}
