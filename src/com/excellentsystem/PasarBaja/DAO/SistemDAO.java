/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.Sistem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Xtreme
 */
public class SistemDAO {
    
    public static Sistem getSystem(Connection con)throws Exception{
        PreparedStatement get = con.prepareStatement("select * from tm_system");
        Sistem sistem = null ;
        ResultSet rs = get.executeQuery();
        while(rs.next()){
            sistem = new Sistem();
            sistem.setNama(rs.getString(1));
            sistem.setAlamat(rs.getString(2));
            sistem.setNoTelp(rs.getString(3));
            sistem.setVersion(rs.getString(4));
        }
        return sistem;
    }
    
}
