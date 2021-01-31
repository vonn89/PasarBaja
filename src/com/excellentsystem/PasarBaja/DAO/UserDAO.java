/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Function.encrypt;
import static com.excellentsystem.PasarBaja.Main.key;
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
public class UserDAO {
    public static List<User> getAll(Connection con)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tm_user where status = 'true'");
        ResultSet rs = ps.executeQuery();
        List<User> allUser = new ArrayList<>();
        while(rs.next()){
            User user = new User();
            user.setKodeUser(rs.getString(1));
            user.setPassword(rs.getString(2));
            user.setStatus(rs.getString(3));
            allUser.add(user);
        }
        return allUser;
    }
    public static void update(Connection con, User user)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_user set password=?, status=? where kode_user=?");
        ps.setString(1, encrypt(user.getPassword(), key));
        ps.setString(2, user.getStatus());
        ps.setString(3, user.getKodeUser());
        ps.executeUpdate();
    }
    public static void insert(Connection con, User user)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_user values(?,?,?)");
        ps.setString(1, user.getKodeUser());
        ps.setString(2, encrypt(user.getPassword(), key));
        ps.setString(3, user.getStatus());
        ps.executeUpdate();
    }
    public static void delete(Connection con, String kodeUser)throws Exception{
        PreparedStatement ps = con.prepareStatement("delete from tm_user where kode_user=?");
        ps.setString(1, kodeUser);
        ps.executeUpdate();
    }
}
