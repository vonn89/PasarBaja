/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class OtoritasDAO {
    public static List<Otoritas> getAll(Connection con)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_otoritas");
        ResultSet rs = ps.executeQuery();
        List<Otoritas> allOtoritas = new ArrayList<>();
        while(rs.next()){
            Otoritas o = new Otoritas();
            o.setKodeUser(rs.getString(1));
            o.setJenis(rs.getString(2));
            o.setStatus(Boolean.parseBoolean(rs.getString(3)));
            allOtoritas.add(o);
        }
        return allOtoritas;
    }
    public static List<Otoritas> getAllByKodeUser(Connection con, String kodeUser)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_otoritas where kode_user=?");
        ps.setString(1, kodeUser);
        ResultSet rs = ps.executeQuery();
        List<Otoritas> allOtoritas = new ArrayList<>();
        while(rs.next()){
            Otoritas otoritas = new Otoritas();
            otoritas.setKodeUser(rs.getString(1));
            otoritas.setJenis(rs.getString(2));
            otoritas.setStatus(Boolean.parseBoolean(rs.getString(3)));
            allOtoritas.add(otoritas);
        }
        return allOtoritas;
    }
    public static void insert(Connection con, Otoritas otoritas)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_otoritas values(?,?,?)");
        ps.setString(1, otoritas.getKodeUser());
        ps.setString(2, otoritas.getJenis());
        ps.setString(3, String.valueOf(otoritas.isStatus()));
        ps.executeUpdate();
    }
    public static void update(Connection con, Otoritas otoritas)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_otoritas set status=? where kode_user=? and jenis=?");
        ps.setString(1, String.valueOf(otoritas.isStatus()));
        ps.setString(2, otoritas.getKodeUser());
        ps.setString(3, otoritas.getJenis());
        ps.executeUpdate();
    }
    public static void delete(Connection con, User user)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tm_otoritas where kode_user=?");
        ps.setString(1, user.getKodeUser());
        ps.executeUpdate();
    }
}
