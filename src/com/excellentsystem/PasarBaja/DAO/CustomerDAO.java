/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.Customer;
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
public class CustomerDAO {
    public static List<Customer> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tm_customer ";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Customer> allCustomer = new ArrayList<>();
        while(rs.next()){
            Customer c = new Customer();
            c.setKodeCustomer(rs.getString(1));
            c.setNama(rs.getString(2));
            c.setAlamat(rs.getString(3));
            c.setKota(rs.getString(4));
            c.setEmail(rs.getString(5));
            c.setKontakPerson(rs.getString(6));
            c.setNoTelp(rs.getString(7));
            c.setNoHandphone(rs.getString(8));
            c.setStatus(rs.getString(9));
            allCustomer.add(c);
        }
        return allCustomer;
    }
    public static Customer get(Connection con, String kodeCustomer)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_customer where kode_customer = ?");
        ps.setString(1, kodeCustomer);
        ResultSet rs = ps.executeQuery();
        Customer c = null;
        while(rs.next()){
            c = new Customer();
            c.setKodeCustomer(rs.getString(1));
            c.setNama(rs.getString(2));
            c.setAlamat(rs.getString(3));
            c.setKota(rs.getString(4));
            c.setEmail(rs.getString(5));
            c.setKontakPerson(rs.getString(6));
            c.setNoTelp(rs.getString(7));
            c.setNoHandphone(rs.getString(8));
            c.setStatus(rs.getString(9));
        }
        return c;
    }
    public static String getId(Connection con)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(kode_customer,5)) from tm_customer");
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "CS-"+new DecimalFormat("00000").format(rs.getInt(1)+1);
        else
            return "CS-"+new DecimalFormat("00000").format(1);
    }
    public static void insert(Connection con, Customer c)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_customer values(?,?,?,?,?,?,?,?,?)");
        ps.setString(1, c.getKodeCustomer());
        ps.setString(2, c.getNama());
        ps.setString(3, c.getAlamat());
        ps.setString(4, c.getKota());
        ps.setString(5, c.getEmail());
        ps.setString(6, c.getKontakPerson());
        ps.setString(7, c.getNoTelp());
        ps.setString(8, c.getNoHandphone());
        ps.setString(9, c.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, Customer c)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_customer set nama=?, alamat=?, kota=?, "
            + " email=?, kontak_person=?, no_telp=?, no_handphone=?, "
            + " status=? where kode_customer=?");
        ps.setString(1, c.getNama());
        ps.setString(2, c.getAlamat());
        ps.setString(3, c.getKota());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getKontakPerson());
        ps.setString(6, c.getNoTelp());
        ps.setString(7, c.getNoHandphone());
        ps.setString(8, c.getStatus());
        ps.setString(9, c.getKodeCustomer());
        ps.executeUpdate();
    }
}
